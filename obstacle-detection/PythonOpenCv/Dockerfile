# Pull base image.
FROM ubuntu:14.04

# Install packages.
RUN apt-get update && apt-get -y upgrade && apt-get install -y wget && apt-get install -y unzip
# Build tools:
RUN apt-get install -y build-essential
RUN apt-get install -y --fix-missing cmake pkg-config qt5-default libvtk6-dev
RUN apt-get install -y libjpeg-dev libtiff5-dev libpng-dev libjasper-dev  libgtk2.0-dev
RUN apt-get install -y libavcodec-dev libavformat-dev libswscale-dev libv4l-dev
RUN apt-get install -y libatlas-base-dev gfortran


RUN apt-get install -y --fix-missing python python-dev python-pip
RUN apt-get install -y --fix-missing python-matplotlib
RUN pip install numpy


RUN wget https://github.com/Itseez/opencv/archive/3.1.0.zip
RUN unzip 3.1.0.zip && rm 3.1.0.zip && mv opencv-3.1.0 OpenCV
WORKDIR OpenCV
RUN mkdir build && cd build && cmake -D CMAKE_BUILD_TYPE=RELEASE -D CMAKE_INSTALL_PREFIX=/usr/local -D INSTALL_C_EXAMPLES=OFF -D INSTALL_PYTHON_EXAMPLES=OFF -D BUILD_EXAMPLES=OFF .. && make -j4 && make install && ldconfig

RUN ln -s /OpenCV/build/lib/cv2.so /usr/local/lib/python2.7/site-packages/cv2.so

CMD ["/bin/bash"]
