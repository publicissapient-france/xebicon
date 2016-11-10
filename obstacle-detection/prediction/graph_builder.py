import tensorflow as tf


def create_graph(model_path):
    """
    Creates a graph from saved GraphDef file and returns a saver.
    """

    with tf.gfile.FastGFile(model_path, 'rb') as f:
        graph_def = tf.GraphDef()
        graph_def.ParseFromString(f.read())
        _ = tf.import_graph_def(graph_def, name='')
