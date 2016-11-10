Detect movement on a remove video stream
=============


# How to install dependencies?

```
pip install -r requirements.txt
```

## How to run?

You first need to define the following env variable in order to get _TensorFlow_
working.

```
export PROTOCOL_BUFFERS_PYTHON_IMPLEMENTATION=python
```
Once everything is configured. Just type the following:

```
python Launcher.py --host 192.168.2.2
```

You should be good to go

## Build images from scratch

1. Build the image into PythonOpenCv
2. Change the image name (FROM) in the dockerfile into opencvTensorFlow and  Build the image
3. Build the image in the root folder
4. Enjoy
