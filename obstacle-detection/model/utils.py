from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import os
import re
import tensorflow as tf

# If a model is trained with multiple GPUs, prefix all Op names with tower_name
# to differentiate the operations. Note that this prefix is removed from the
# names of the summaries when visualizing a model.
TOWER_NAME = 'tower'


def ensure_dir_exists(dir_name):
    """
    Makes sure the folder exists on disk.

    Args:
        dir_name: Path string to the folder we want to create.
    """

    if not os.path.exists(dir_name):
        os.makedirs(dir_name)


def should_distort_images(flip_left_right, random_crop, random_scale, random_brightness):
    """
    Whether any distortions are enabled, from the input flags.

    Args:
        flip_left_right: Boolean whether to randomly mirror images horizontally.
        random_crop: Integer percentage setting the total margin used around the crop box.
        random_scale: Integer percentage of how much to vary the scale by.
        random_brightness: Integer range to randomly multiply the pixel values by.

    Returns:
        Boolean value indicating whether any distortions should be applied.
    """
    return (flip_left_right or (random_crop != 0) or (random_scale != 0) or (random_brightness != 0))


def _activation_summary(x):
    """
    Helper to create summaries for activations.
    * Creates a summary that provides a histogram of activations.
    * Creates a summary that measures the sparsity of activations.

    :param x: Tensor
    """

    # Remove 'tower_[0-9]/' from the name in case this is a multi-GPU training session. This helps the clarity of
    # presentation on tensorboard.
    tensor_name = re.sub('%s_[0-9]*/' % TOWER_NAME, '', x.op.name)
    tf.histogram_summary(tensor_name + '/activations', x)
    tf.scalar_summary(tensor_name + '/sparsity', tf.nn.zero_fraction(x))