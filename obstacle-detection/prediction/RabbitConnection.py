#!/usr/bin/env python
import pika


class RabbitSender:
    def __init__(self, user, password):
        self.credentials = pika.PlainCredentials(user, password)
        self.host = 'rabbit.xebicon.fr'

    def obstacleDetection(self, message):
        connection = pika.BlockingConnection(pika.ConnectionParameters(host=self.host, credentials=self.credentials))

        channel = connection.channel()

        channel.basic_publish(
            exchange='xebiconExchange',
            routing_key='',
            body=message,
            properties=pika.BasicProperties(
                delivery_mode=2,  # make message persistent
            ))

        print(" [x] Sent %r" % message)

        connection.close()
