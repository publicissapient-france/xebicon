FROM xebia-france/python_opencv

RUN mkdir -p /detection/stream

WORKDIR /detection

COPY liveStreaming.py .

COPY stream/ /detection/stream/

VOLUME ["/detection/stream"]

ENTRYPOINT ["python", "liveStreaming.py"]
