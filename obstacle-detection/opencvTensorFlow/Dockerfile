# Pull base image.
FROM xebia-france/python_opencv

RUN export TF_BINARY_URL=https://storage.googleapis.com/tensorflow/linux/cpu/tensorflow-0.10.0-cp27-none-linux_x86_64.whl && sudo pip install --upgrade $TF_BINARY_URL

# Define default command.
CMD ["/bin/bash"]
