import unittest
import os
import cv2
import numpy as np

import tracker.AnimalTracker
from tracker import Point

this_dir = os.path.dirname(os.path.realpath(__file__))


def path_for(img_name):
    return os.path.join(this_dir, img_name)


class AnimalTrackerTest(unittest.TestCase):
    def test_mask_same_size_as_original(self):
        self.step0 = cv2.imread(path_for('../resources/train-way-step0.png'))
        self.step1 = cv2.imread(path_for("../resources/train-way-step1.png"))
        animal = tracker.AnimalTracker("window", True)
        # given an initial frame
        animal.init_if_needed(self.step0)

        # when an a new frame arrives
        animal.update(self.step1, 30)
        self.assertEqual(animal.counter, 2)

        # then
        segmentation = animal.mask
        if segmentation is None:
            self.fail("Unexpected mask found!")
        else:
            width_orig, height_orig, dim_orig = self.step0.shape
            width, height = segmentation.shape
            self.assertEqual(width, width_orig)
            self.assertEqual(height, height_orig)

    def test_segmented_animal(self):
        self.step0 = cv2.imread(path_for('../resources/train-way-step0.png'))
        self.step1 = cv2.imread(path_for("../resources/train-way-step1.png"))
        animal = tracker.AnimalTracker("window", True)
        # given an initial frame
        animal.init_if_needed(self.step0)

        # when an a new frame arrives
        animal.update(self.step1, 30)

        # then
        segmentation = animal.result
        if segmentation is None:
            self.fail("Unexpected result image found!")
        else:
            # tracker.AnimalTracker._persist_iteration(animal.get_img_base(), 0)
            # tracker.AnimalTracker._persist_iteration(animal.get_img_mask(), 2)
            # tracker.AnimalTracker._persist_iteration(animal.get_img_result(), 3)
            # tracker.AnimalTracker._persist_iteration(animal.get_img_bounding_box(), 0)
            width_orig, height_orig, dim_orig = self.step0.shape
            width, height, dim = segmentation.shape
            self.assertEqual(width, width_orig)
            self.assertEqual(height, height_orig)
            self.assertEqual(dim, dim_orig)
