FROM python:slim

RUN pip install docker-compose

COPY reloader /usr/bin/reloader

EXPOSE 8080

VOLUME /data

CMD reloader