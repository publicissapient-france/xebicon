from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os

import tensorflow as tf
from tensorflow.python.framework import tensor_shape
from tensorflow.python.platform import gfile

from model import utils


class GraphBuilder:

    def __init__(self):
        pass

    @staticmethod
    def create_inception_graph(model_dir, bottleneck_tensor_name, jpeg_data_tensor_name, resized_input_tensor_name):
        """"
        Creates a graph from saved GraphDef file and returns a Graph object.

        Returns:
            Graph holding the trained Inception network, and various tensors we'll be manipulating.
        """

        with tf.Session() as sess:
            model_filename = os.path.join(model_dir, 'classify_image_graph_def.pb')

            with gfile.FastGFile(model_filename, 'rb') as f:
                graph_def = tf.GraphDef()
                graph_def.ParseFromString(f.read())
                bottleneck_tensor, jpeg_data_tensor, resized_input_tensor = (
                    tf.import_graph_def(graph_def, name='', return_elements=[
                        bottleneck_tensor_name, jpeg_data_tensor_name, resized_input_tensor_name]))

        return sess.graph, bottleneck_tensor, jpeg_data_tensor, resized_input_tensor

    @staticmethod
    def add_input_distortions(model_input_depth, model_input_width, model_input_height, flip_left_right,
                              random_crop, random_scale, random_brightness):
        """
        Creates the operations to apply the specified distortions.

        During training it can help to improve the results if we run the images through simple distortions like crops,
        scales, and flips. These reflect the kind of variations we expect in the real world, and so can help train the
        model to cope with natural data more effectively. Here we take the supplied parameters and construct a network of
        operations to apply them to an image.

        Cropping
        ~~~~~~~~

        Cropping is done by placing a bounding box at a random position in the full image. The cropping parameter controls
        the size of that box relative to the input image. If it's zero, then the box is the same size as the input and no
        cropping is performed. If the value is 50%, then the crop box will be half the width and height of the input.
        In a diagram it looks like this:

        <       width         >
        +---------------------+
        |                     |
        |   width - crop%     |
        |    <      >         |
        |    +------+         |
        |    |      |         |
        |    |      |         |
        |    |      |         |
        |    +------+         |
        |                     |
        |                     |
        +---------------------+

        Scaling
        ~~~~~~~

        Scaling is a lot like cropping, except that the bounding box is always centered and its size varies randomly
        within the given range. For example if the scale percentage is zero, then the bounding box is the same size as the
        input and no scaling is applied. If it's 50%, then the bounding box will be in a random range between half the width
        and height and full size.

        Args:
            model_input_depth: Model Input Depth.
            model_input_width: Model Input Width.
            model_input_height: Model Input Height.
            flip_left_right: Boolean whether to randomly mirror images horizontally.
            random_crop: Integer percentage setting the total margin used around the crop box.
            random_scale: Integer percentage of how much to vary the scale by.
            random_brightness: Integer range to randomly multiply the pixel values by.
            graph.

        Returns:
            The jpeg input layer and the distorted result tensor.
        """

        jpeg_data = tf.placeholder(tf.string, name='DistortJPGInput')
        decoded_image = tf.image.decode_jpeg(jpeg_data, channels=model_input_depth)
        decoded_image_as_float = tf.cast(decoded_image, dtype=tf.float32)
        decoded_image_4d = tf.expand_dims(decoded_image_as_float, 0)

        margin_scale = 1.0 + (random_crop / 100.0)
        resize_scale = 1.0 + (random_scale / 100.0)
        margin_scale_value = tf.constant(margin_scale)
        resize_scale_value = tf.random_uniform(tensor_shape.scalar(), minval=1.0, maxval=resize_scale)
        scale_value = tf.mul(margin_scale_value, resize_scale_value)

        precrop_width = tf.mul(scale_value, model_input_width)
        precrop_height = tf.mul(scale_value, model_input_height)
        precrop_shape = tf.pack([precrop_height, precrop_width])
        precrop_shape_as_int = tf.cast(precrop_shape, dtype=tf.int32)
        precropped_image = tf.image.resize_bilinear(decoded_image_4d, precrop_shape_as_int)
        precropped_image_3d = tf.squeeze(precropped_image, squeeze_dims=[0])

        cropped_image = tf.random_crop(precropped_image_3d, [model_input_height, model_input_width, model_input_depth])

        if flip_left_right:
            flipped_image = tf.image.random_flip_left_right(cropped_image)
        else:
            flipped_image = cropped_image

        brightness_min = 1.0 - (random_brightness / 100.0)
        brightness_max = 1.0 + (random_brightness / 100.0)
        brightness_value = tf.random_uniform(tensor_shape.scalar(), minval=brightness_min, maxval=brightness_max)
        brightened_image = tf.mul(flipped_image, brightness_value)

        distort_result = tf.expand_dims(brightened_image, 0, name='DistortResult')

        return jpeg_data, distort_result

    @staticmethod
    def add_final_training_ops(bottleneck_tensor_size, learning_rate, class_count, final_tensor_name,
                               bottleneck_tensor):
        """
        Adds a new softmax and fully-connected layer for training.

        We need to retrain the top layer to identify our new classes, so this function adds the right operations to the
        graph, along with some variables to hold the weights, and then sets up all the gradients for the backward pass.

        The set up for the softmax and fully-connected layers is based on:
        https://tensorflow.org/versions/master/tutorials/mnist/beginners/index.html

        Args:
            bottleneck_tensor_size: Bottleneck Tensor Size.
            learning_rate: Learning Rate.
            class_count: Integer of how many categories of things we're trying to recognize.
            final_tensor_name: Name string for the new final node that produces results.
            bottleneck_tensor: The output of the main CNN graph.

        Returns:
            The tensors for the training and cross entropy results, and tensors for the bottleneck input and ground truth
            input.
        """

        bottleneck_input = tf.placeholder_with_default(
            bottleneck_tensor, shape=[None, bottleneck_tensor_size], name='BottleneckInputPlaceholder')
        layer_weights = tf.Variable(
            tf.truncated_normal([bottleneck_tensor_size, class_count], stddev=0.001), name='final_weights')
        layer_biases = tf.Variable(tf.zeros([class_count]), name='final_biases')

        logits = tf.matmul(bottleneck_input, layer_weights, name='final_matmul') + layer_biases

        final_tensor = tf.nn.softmax(logits, name=final_tensor_name)
        utils._activation_summary(final_tensor)

        ground_truth_input = tf.placeholder(tf.float32, [None, class_count], name='GroundTruthInput')

        cross_entropy = tf.nn.softmax_cross_entropy_with_logits(logits, ground_truth_input)
        cross_entropy_mean = tf.reduce_mean(cross_entropy)

        tf.scalar_summary('cross entropy', cross_entropy_mean)

        train_step = tf.train.GradientDescentOptimizer(learning_rate).minimize(cross_entropy_mean)

        return train_step, cross_entropy_mean, bottleneck_input, ground_truth_input, final_tensor

    @staticmethod
    def add_evaluation_step(result_tensor, ground_truth_tensor):
        """
        Inserts the operations we need to evaluate the accuracy of our results.

        Args:
            result_tensor: The new final node that produces results.
            ground_truth_tensor: The node we feed ground truth data into.

        Returns:
            Nothing.
        """

        correct_prediction = tf.equal(tf.argmax(result_tensor, 1), tf.argmax(ground_truth_tensor, 1))
        evaluation_step = tf.reduce_mean(tf.cast(correct_prediction, 'float'))

        tf.scalar_summary('accuracy', evaluation_step)

        return evaluation_step
