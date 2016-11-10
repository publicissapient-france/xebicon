"""
Transfer Learning witn an Inception v3 architecture model.

Code is inspired from TensorFlow examples:
https://github.com/tensorflow/tensorflow/blob/master/tensorflow/examples/image_retraining/retrain.py

This code takes a Inception v3 architecture model trained on ImageNet images, and trains a new top layer that can
recognize other classes of images, which will be animal classes in this case.
"""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

from datetime import datetime
import os
import tensorflow as tf

from tensorflow.python.client import graph_util
from tensorflow.python.platform import gfile

from model import Bottleneck
from model import GraphBuilder
from model import InputManager
from model import utils

FLAGS = tf.app.flags.FLAGS

# Input and output file flags.
tf.app.flags.DEFINE_string('image_dir', '',
                           """Path to folders of labeled images.""")
tf.app.flags.DEFINE_string('output_graph', os.path.join(os.path.dirname(__file__),
                                                        "../resources/model/animal_graph.pb"),
                           """Where to save the trained graph.""")
tf.app.flags.DEFINE_string('output_labels', os.path.join(os.path.dirname(__file__),
                                                         "../resources/model/animal_labels.txt"),
                           """Where to save the trained graph's labels.""")

# Details of the training configuration.
tf.app.flags.DEFINE_integer('how_many_training_steps', 4000,
                            """How many training steps to run before ending.""")
tf.app.flags.DEFINE_float('learning_rate', 0.01,
                          """How large a learning rate to use when training.""")
tf.app.flags.DEFINE_integer('testing_percentage', 10,
                            """What percentage of images to use as a test set.""")
tf.app.flags.DEFINE_integer('validation_percentage', 10,
                            """What percentage of images to use as a validation set.""")
tf.app.flags.DEFINE_integer('eval_step_interval', 10,
                            """How often to evaluate the training results.""")
tf.app.flags.DEFINE_integer('train_batch_size', 100,
                            """How many images to train on at a time.""")
tf.app.flags.DEFINE_integer('test_batch_size', 500,
                            """How many images to test on at a time. This test set is only used infrequently to
                            verify the overall accuracy of the model.""")
tf.app.flags.DEFINE_integer('validation_batch_size', 100,
                            """How many images to use in an evaluation batch. This validation set is
                            used much more often than the test set, and is an early indicator of
                            how accurate the model is during training.""")

# File-system cache locations.
tf.app.flags.DEFINE_string('model_dir', '/tmp/imagenet',
                           """Path to classify_image_graph_def.pb, imagenet_synset_to_human_label_map.txt, and
                           imagenet_2012_challenge_label_map_proto.pbtxt.""")
tf.app.flags.DEFINE_string('bottleneck_dir', os.path.join(os.path.dirname(__file__),
                                                          "../resources/model/bottleneck"),
                           """Path to cache bottleneck layer values as files.""")
tf.app.flags.DEFINE_string('final_tensor_name', 'final_result',
                           """The name of the output classification layer in the retrained graph.""")

# Controls the distortions used during training.
tf.app.flags.DEFINE_boolean('flip_left_right', False,
                            """Whether to randomly flip half of the training images horizontally.""")
tf.app.flags.DEFINE_integer('random_crop', 0,
                            """A percentage determining how much of a margin to randomly crop off the
                            training images.""")
tf.app.flags.DEFINE_integer('random_scale', 0,
                            """A percentage determining how much to randomly scale up the size of the
                            training images by.""")
tf.app.flags.DEFINE_integer('random_brightness', 0,
                            """A percentage determining how much to randomly multiply the training
                            image input pixels up or down by.""")

tf.app.flags.DEFINE_string('summaries_dir', os.path.join(os.path.dirname(__file__),
                                                         "../resources/model/animal_classification_summaries"),
                           """Summaries directory""")

# These are all parameters that are tied to the particular model architecture we're using for Inception v3.
# These include things like tensor names and their sizes.
# If you want to adapt this script to work with another model, you will need to update these to reflect the values
# in the network you're using.
DATA_URL = 'http://download.tensorflow.org/models/image/imagenet/inception-2015-12-05.tgz'
BOTTLENECK_TENSOR_NAME = 'pool_3/_reshape:0'
BOTTLENECK_TENSOR_SIZE = 2048
MODEL_INPUT_WIDTH = 299
MODEL_INPUT_HEIGHT = 299
MODEL_INPUT_DEPTH = 3
JPEG_DATA_TENSOR_NAME = 'DecodeJpeg/contents:0'
RESIZED_INPUT_TENSOR_NAME = 'ResizeBilinear:0'

# If a model is trained with multiple GPUs, prefix all Op names with tower_name
# to differentiate the operations. Note that this prefix is removed from the
# names of the summaries when visualizing a model.
TOWER_NAME = 'tower'


def main(_):
    # Set up the pre-trained graph.
    InputManager.maybe_download_and_extract(model_dir=FLAGS.model_dir, data_url=DATA_URL)

    (graph, bottleneck_tensor, jpeg_data_tensor,
     resized_image_tensor) = GraphBuilder.create_inception_graph(model_dir=FLAGS.model_dir,
                                                                  bottleneck_tensor_name=BOTTLENECK_TENSOR_NAME,
                                                                  jpeg_data_tensor_name=JPEG_DATA_TENSOR_NAME,
                                                                  resized_input_tensor_name=RESIZED_INPUT_TENSOR_NAME)

    # Look at the folder structure, and create lists of all the images.
    image_lists = InputManager.create_image_lists(image_dir=FLAGS.image_dir,
                                                   testing_percentage=FLAGS.testing_percentage,
                                                   validation_percentage=FLAGS.validation_percentage)

    class_count = len(image_lists.keys())
    if class_count == 0:
        print('No valid folders of images found at ' + FLAGS.image_dir)
        return -1
    if class_count == 1:
        print('Only one valid folder of images found at ' + FLAGS.image_dir +
              ' - multiple classes are needed for classification.')
        return -1

    # See if the command-line flags mean we're applying any distortions.
    do_distort_images = utils.should_distort_images(flip_left_right=FLAGS.flip_left_right,
                                                    random_crop=FLAGS.random_crop,
                                                    random_scale=FLAGS.random_scale,
                                                    random_brightness=FLAGS.random_brightness)

    sess = tf.Session()

    if do_distort_images:
        # We will be applying distortions, so setup the operations we'll need.
        (distorted_jpeg_data_tensor,
         distorted_image_tensor) = GraphBuilder.add_input_distortions(model_input_depth=MODEL_INPUT_DEPTH,
                                                                       model_input_width=MODEL_INPUT_WIDTH,
                                                                       model_input_height=MODEL_INPUT_HEIGHT,
                                                                       flip_left_right=FLAGS.flip_left_right,
                                                                       random_crop=FLAGS.random_crop,
                                                                       random_scale=FLAGS.random_scale,
                                                                       random_brightness=FLAGS.random_brightness)
    else:
        # We'll make sure we've calculated the 'bottleneck' image summaries and cached them on disk.
        Bottleneck.cache_bottlenecks(sess=sess, image_lists=image_lists, image_dir=FLAGS.image_dir,
                                     bottleneck_dir=FLAGS.bottleneck_dir, jpeg_data_tensor=jpeg_data_tensor,
                                     bottleneck_tensor=bottleneck_tensor)

    # Add the new layer that we'll be training.
    (train_step, cross_entropy, bottleneck_input, ground_truth_input,
     final_tensor) = GraphBuilder.add_final_training_ops(bottleneck_tensor_size=BOTTLENECK_TENSOR_SIZE,
                                                          learning_rate=FLAGS.learning_rate,
                                                          class_count=len(image_lists.keys()),
                                                          final_tensor_name=FLAGS.final_tensor_name,
                                                          bottleneck_tensor=bottleneck_tensor)

    # Merge all the summaries and write them out to /tmp/transfer_learning_inception
    merged = tf.merge_all_summaries()
    train_writer = tf.train.SummaryWriter(FLAGS.summaries_dir + '/train', sess.graph)
    validation_writer = tf.train.SummaryWriter(FLAGS.summaries_dir + '/validation')

    # Set up all our weights to their initial default values.
    init = tf.initialize_all_variables()
    sess.run(init)

    # Create the operations we need to evaluate the accuracy of our new layer.
    evaluation_step = GraphBuilder.add_evaluation_step(final_tensor, ground_truth_input)

    # Run the training for as many cycles as requested on the command line.
    for i in range(FLAGS.how_many_training_steps):
        # Get a catch of input bottleneck values, either calculated fresh every time with distortions applied,
        # or from the cache stored on disk.
        if do_distort_images:
            train_bottlenecks, train_ground_truth = Bottleneck.get_random_distorted_bottlenecks(
                sess, image_lists, FLAGS.train_batch_size, 'training', FLAGS.image_dir, distorted_jpeg_data_tensor,
                distorted_image_tensor, resized_image_tensor, bottleneck_tensor)
        else:
            train_bottlenecks, train_ground_truth = Bottleneck.get_random_cached_bottlenecks(
                sess, image_lists, FLAGS.train_batch_size, 'training', FLAGS.bottleneck_dir, FLAGS.image_dir,
                jpeg_data_tensor, bottleneck_tensor)

        # Feed the bottlenecks and ground truth into the graph, and run a training step.
        summary, _ = sess.run([merged, train_step], feed_dict={bottleneck_input: train_bottlenecks,
                                                               ground_truth_input: train_ground_truth})

        # Add summaries to train writer
        train_writer.add_summary(summary, i)

        # Every so often, print out how well the graph is training.
        is_last_step = (i + 1 == FLAGS.how_many_training_steps)
        if (i % FLAGS.eval_step_interval) == 0 or is_last_step:
            train_accuracy, cross_entropy_value = sess.run([evaluation_step, cross_entropy],
                                                           feed_dict={bottleneck_input: train_bottlenecks,
                                                                      ground_truth_input: train_ground_truth})
            print('%s: Step %d: Train accuracy = %.1f%%' % (datetime.now(), i, train_accuracy * 100))
            print('%s: Step %d: Cross entropy = %f' % (datetime.now(), i, cross_entropy_value))

            validation_bottlenecks, validation_ground_truth = (
                Bottleneck.get_random_cached_bottlenecks(
                    sess, image_lists, FLAGS.validation_batch_size, 'validation', FLAGS.bottleneck_dir, FLAGS.image_dir,
                    jpeg_data_tensor, bottleneck_tensor))

            summary, validation_accuracy = sess.run(
                [merged, evaluation_step], feed_dict={bottleneck_input: validation_bottlenecks,
                                                      ground_truth_input: validation_ground_truth})

            # Add summaries to validation writer
            validation_writer.add_summary(summary, i)

            print('%s: Step %d: Validation accuracy = %.1f%%' % (datetime.now(), i, validation_accuracy * 100))

    # We've completed all our training, so run a final test evaluation on some new images we haven't used before.
    test_bottlenecks, test_ground_truth = Bottleneck.get_random_cached_bottlenecks(
        sess, image_lists, FLAGS.test_batch_size, 'testing', FLAGS.bottleneck_dir, FLAGS.image_dir, jpeg_data_tensor,
        bottleneck_tensor)
    test_accuracy = sess.run(
        evaluation_step, feed_dict={bottleneck_input: test_bottlenecks, ground_truth_input: test_ground_truth})
    print('Final test accuracy = %.1f%%' % (test_accuracy * 100))

    # Write out the trained graph and labels with the weights stored as constants.
    output_graph_def = graph_util.convert_variables_to_constants(sess, graph.as_graph_def(), [FLAGS.final_tensor_name])
    with gfile.FastGFile(FLAGS.output_graph, 'wb') as f:
        f.write(output_graph_def.SerializeToString())
    with gfile.FastGFile(FLAGS.output_labels, 'w') as f:
        f.write('\n'.join(image_lists.keys()) + '\n')


if __name__ == '__main__':
    tf.app.run()
