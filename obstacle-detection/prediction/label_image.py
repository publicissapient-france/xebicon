import glob
import os
import time

import numpy as np
import tensorflow as tf

import graph_builder
import input_manager
import utils
from RabbitConnection import RabbitSender

FLAGS = tf.app.flags.FLAGS

# Input and output file flags.
tf.app.flags.DEFINE_string('model_path',
                           os.path.join(os.path.dirname(__file__), "../resources/model/animal_graph.pb"),
                           """Where the trained graph is stored.""")
tf.app.flags.DEFINE_string('output_labels_path',
                           os.path.join(os.path.dirname(__file__), '../resources/model/animal_labels.txt'),
                           """Where the trained graph's labels are stored.""")


class LabelImage:
    """ Handle the animal prediction from the model """

    def __init__(self, imagePath,rabbitUser,rabbitPwd):

        self.image_path=imagePath

        self.sess = tf.Session()
        self.detecting = False
        ####
        # GRAPH CONFIGURATIONS
        ####

        # Creates graph from saved GraphDef
        graph_builder.create_graph(FLAGS.model_path)

        # Open the labels file to identify the classes
        self.labels = input_manager.extract_class_names(FLAGS.output_labels_path)

        # Last layer of the network to get the probabilities
        self.softmax_tensor = self.sess.graph.get_tensor_by_name('final_result:0')
        self.imageDetected = False
        self.enqueueClear=False
        self.sender = RabbitSender(rabbitUser,rabbitPwd)

    def run_inference_on_image(self):
        """
        Run inference on images
        * Creates the graph
        * Loads the image
        * Performs inference
        """
        self.detecting = True
        answer = None

        ####
        # STREAM PREDICTIONS ON IMAGES
        ####

        # Check if image directory exists
        if not tf.gfile.Exists(self.image_path):
            tf.logging.fatal('File does not exist %s', self.image_path)
            return answer


            # Get images list
        file_list = glob.glob(os.path.join(self.image_path, '*.jpeg'))

        if len(file_list) > 0:
            answers = []
            scores = []
            for im_path in file_list:
                # Load image
                image_data = tf.gfile.FastGFile(im_path, 'rb').read()

                # Run inference
                predictions = self.sess.run(self.softmax_tensor,
                                            {'DecodeJpeg/contents:0': image_data, 'Placeholder_1:0': 1.0})
                predictions = np.squeeze(predictions)

                # Getting top prediction
                top_1 = predictions.argsort()[-1:][0]

                answers.append(self.labels[top_1])
                scores.append(predictions[top_1])

            # Get most common label
            most_common_label = utils.most_common(answers)

            # Get average probability of most common label
            proba_most_common = utils.proba_most_common(most_common_label, answers, scores)

            self.detecting = False

            # Send to Rabbit MQ
            if proba_most_common > 0.8 :
                self.imageDetected = True
                if self.enqueueClear==False :
                    print('image detected (label image)')
                    self.sender.obstacleDetection('{"type": "OBSTACLE","payload": { "obstacle": "' + self.should_stop_train(most_common_label.upper()) + '", "obstacleType": "' + most_common_label.upper() + '"}}')
                else :
                    self.clear_detection()
            else:
                self.send_clear()

            self._clear_folder(self.image_path)

            print "Most common label: %s" % most_common_label
            print "Probability: %s" % proba_most_common

            time.sleep(2)

        else:
            print "No data"
            time.sleep(5)

    def is_processing(self):
        return self.detecting

    def send_clear(self):
        if self.imageDetected :
            if self.detecting == False:
                self.clear_detection()
            else :
                self.enqueueClear=True
            self.imageDetected = False

    def clear_detection(self):
        print('image cleared (label image)')
        self.sender.obstacleDetection('{"type": "OBSTACLE_CLEARED"}')
        self.enqueueClear=False
        self._clear_folder(self.image_path)

    def should_stop_train(self, label):
        if label in ['COW', 'PIG', 'HORSE']:
            return 'true'
        return 'false'

    def _clear_folder(self, folder_name):
        import os
        for the_file in os.listdir(folder_name):
            file_path = os.path.join(folder_name, the_file)
            try:
                if os.path.isfile(file_path):
                    os.unlink(file_path)
            except Exception as e:
                print(e)

