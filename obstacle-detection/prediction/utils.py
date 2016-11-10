import numpy as np


def most_common(lst):
    return max(set(lst), key=lst.count)


def proba_most_common(most_common, labels, scores):
    return np.mean([scores[i] for i in range(len(scores)) if labels[i] == most_common])
