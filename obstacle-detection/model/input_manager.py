from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import glob
import hashlib
import os
import re
import sys
import tarfile

from six.moves import urllib
import tensorflow as tf

from tensorflow.python.platform import gfile


class InputManager:

    def __init__(self):
        pass

    @staticmethod
    def create_image_lists(image_dir, testing_percentage, validation_percentage):
        """
        Builds a list of training images from the file system.

        Analyzes the sub folders in the image directory, splits them into stable
        training, testing, and validation sets, and returns a data structure
        describing the lists of images for each label and their paths.

        Args:
            image_dir: String path to a folder containing subfolders of images.
            testing_percentage: Integer percentage of the images to reserve for tests.
            validation_percentage: Integer percentage of images reserved for validation.

        Returns:
            A dictionary containing an entry for each label subfolder, with images split
            into training, testing, and validation sets within each label.
        """

        if not gfile.Exists(image_dir):
            print("Image directory '" + image_dir + "' not found.")
            return None

        result = {}
        sub_dirs = [x[0] for x in os.walk(image_dir)]

        # The root directory comes first, so skip it.
        is_root_dir = True

        for sub_dir in sub_dirs:
            if is_root_dir:
                is_root_dir = False
                continue

            extensions = ['jpg', 'jpeg', 'JPG', 'JPEG']
            file_list = []
            dir_name = os.path.basename(sub_dir)

            if dir_name == image_dir:
                continue
            print("Looking for images in '" + dir_name + "'")

            for extension in extensions:
                file_glob = os.path.join(image_dir, dir_name, '*.' + extension)
                file_list.extend(glob.glob(file_glob))

            if not file_list:
                print('No files found')
                continue

            if len(file_list) < 20:
                print('WARNING: Folder has less than 20 images, which may cause issues.')

            label_name = re.sub(r'[^a-z0-9]+', ' ', dir_name.lower())
            training_images = []
            testing_images = []
            validation_images = []

            for file_name in file_list:
                base_name = os.path.basename(file_name)
                # We want to ignore anything after '_nohash_' in the file name when
                # deciding which set to put an image in, the data set creator has a way of
                # grouping photos that are close variations of each other. For example
                # this is used in the plant disease data set to group multiple pictures of
                # the same leaf.
                hash_name = re.sub(r'_nohash_.*$', '', file_name)
                # This looks a bit magical, but we need to decide whether this file should
                # go into the training, testing, or validation sets, and we want to keep
                # existing files in the same set even if more files are subsequently
                # added.
                # To do that, we need a stable way of deciding based on just the file name
                # itself, so we do a hash of that and then use that to generate a
                # probability value that we use to assign it.
                hash_name_hashed = hashlib.sha1(hash_name.encode('utf-8')).hexdigest()
                percentage_hash = (int(hash_name_hashed, 16) % (65536)) * (100 / 65535.0)

                if percentage_hash < validation_percentage:
                    validation_images.append(base_name)
                elif percentage_hash < (testing_percentage + validation_percentage):
                    testing_images.append(base_name)
                else:
                    training_images.append(base_name)

            result[label_name] = {
                'dir': dir_name,
                'training': training_images,
                'testing': testing_images,
                'validation': validation_images,
            }

        return result

    @staticmethod
    def get_image_path(image_lists, label_name, index, image_dir, category):
        """"
        Returns a path to an image for a label at the given index.

        Args:
            image_lists: Dictionary of training images for each label.
            label_name: Label string we want to get an image for.
            index: Int offset of the image we want. This will be moduloed by the available number of images for the label,
            so it can be arbitrarily large.
            image_dir: Root folder string of the subfolders containing the training images.
            category: Name string of set to pull images from - training, testing, or validation.

        Returns:
            File system path string to an image that meets the requested parameters.
        """

        if label_name not in image_lists:
            tf.logging.fatal('Label does not exist %s.', label_name)
        label_lists = image_lists[label_name]

        if category not in label_lists:
            tf.logging.fatal('Category does not exist %s.', category)
        category_list = label_lists[category]

        if not category_list:
            tf.logging.fatal('Category has no images - %s.', category)
        mod_index = index % len(category_list)
        base_name = category_list[mod_index]
        sub_dir = label_lists['dir']
        full_path = os.path.join(image_dir, sub_dir, base_name)

        return full_path

    @staticmethod
    def maybe_download_and_extract(model_dir, data_url):
        """
        Download and extract model tar file.

        If the pretrained model we're using doesn't already exist, this function downloads it from the TensorFlow.org
        website and unpacks it into a directory.
        """

        dest_directory = model_dir

        if not os.path.exists(dest_directory):
            os.makedirs(dest_directory)

        filename = data_url.split('/')[-1]
        filepath = os.path.join(dest_directory, filename)

        if not os.path.exists(filepath):
            def _progress(count, block_size, total_size):
                sys.stdout.write('\r>> Downloading %s %.1f%%' %
                                 (filename, float(count * block_size) / float(total_size) * 100.0))
                sys.stdout.flush()

            filepath, _ = urllib.request.urlretrieve(data_url, filepath, _progress)
            print()

            statinfo = os.stat(filepath)
            print('Successfully downloaded', filename, statinfo.st_size, 'bytes.')

        tarfile.open(filepath, 'r:gz').extractall(dest_directory)