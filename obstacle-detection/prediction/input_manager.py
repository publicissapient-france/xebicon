def extract_class_names(output_labels_path):
    """
    Reads the output labels file and returns a list with the class names.
    """
    f = open(output_labels_path, 'rb')
    lines = f.readlines()
    return [str(w).replace("\n", "") for w in lines]