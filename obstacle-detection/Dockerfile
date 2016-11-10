# Pull base image.
FROM xebia-france/opencv_tensorflow

RUN mkdir -p /detection/tracker && mkdir /detection/model && mkdir /detection/prediction && mkdir /detection/resources && mkdir /detection/stream

COPY prediction /detection/prediction

COPY model /detection/model

COPY resources /detection/resources

COPY stream /detection/stream

WORKDIR /detection

COPY requirements.txt .

RUN mkdir ML && pip install -r requirements.txt

VOLUME ["/detection/ML"]

VOLUME ["/detection/stream"]

COPY tracker tracker

COPY KeyNoteStatusListener.py  Launcher.py ./

# Define default command.
ENTRYPOINT ["python", "KeyNoteStatusListener.py"]
