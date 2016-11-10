# -*- coding: utf-8 -*-

import logging
import time

""" RabbitMq """
import pika

""" HTTP """
import tornado.ioloop
import tornado.web
from tornado import gen

class TornadoMainHandler(tornado.web.RequestHandler):
    def post(self):
        if self.request.headers["Content-Type"].startswith("application/json"):
                data = json.loads(self.request.body)
                self.write("Hello, world")
                LOGGER.info(data)
                sendPositionEventToRabbit(data['train'], data['id'])
		time.sleep(20)
                stop(mapIdToTrain(data['train']))
                sendStopEventToRabbit(data['train'])

def make_app():
    return tornado.web.Application([
        (r"/detection", TornadoMainHandler),
    ])

""" Imports pour le train"""
import RPi.GPIO as GPIO
import time
import json

""" Pin GPIO """
train1 = 2
train2 = 3
turnoutB = 4
turnoutC = 12
turnoutD = 25
turnoutA = 18


""" RabbitMq Connector """
class RabbitMq(object):
    
    trainNumber = 0
    EXCHANGE = 'xebiconExchange'
    EXCHANGE_TYPE = 'fanout'
    QUEUE = ''
    ROUTING_KEY = ''

    def __init__(self, amqp_url, io_loop):
        self._io_loop = io_loop
        self._connection = None
        self._channel = None
        self._closing = False
        self._consumer_tag = None
        self._url = amqp_url

    def setRunningTrain(self, trainId):
	LOGGER.info('Setting running train %s', trainId)
	self.trainNumber= trainId	

    def connect(self):
        
        LOGGER.info('Connecting to %s', self._url)
        return pika.TornadoConnection(pika.URLParameters(self._url),
                                     self.on_connection_open,
                                     stop_ioloop_on_close=False,
                                     custom_ioloop=self._io_loop)

    def on_connection_open(self, unused_connection):
        
        LOGGER.info('Connection opened')
        self.add_on_connection_close_callback()
        self.open_channel()

    def add_on_connection_close_callback(self):
        
        LOGGER.info('Adding connection close callback')
        self._connection.add_on_close_callback(self.on_connection_closed)

    def on_connection_closed(self, connection, reply_code, reply_text):
        
        self._channel = None
        if self._closing:
            self._connection.ioloop.stop()
        else:
            LOGGER.warning('Connection closed, reopening in 5 seconds: (%s) %s',
                           reply_code, reply_text)
            self._connection.add_timeout(5, self.reconnect)

    def reconnect(self):
        
        # This is the old connection IOLoop instance, stop its ioloop
        self._connection.ioloop.stop()

        if not self._closing:

            # Create a new connection
            self._connection = self.connect()

            # There is now a new connection, needs a new ioloop to run
            #self._connection.ioloop.start()

    def open_channel(self):
        
        LOGGER.info('Creating a new channel')
        self._connection.channel(on_open_callback=self.on_channel_open)

    def on_channel_open(self, channel):
        
        LOGGER.info('Channel opened')
        self._channel = channel
        self.add_on_channel_close_callback()
        self.setup_exchange(self.EXCHANGE)

    def add_on_channel_close_callback(self):
        
        LOGGER.info('Adding channel close callback')
        self._channel.add_on_close_callback(self.on_channel_closed)

    def on_channel_closed(self, channel, reply_code, reply_text):

        LOGGER.warning('Channel %i was closed: (%s) %s',
                       channel, reply_code, reply_text)
        self._connection.close()

    def setup_exchange(self, exchange_name):
        
        LOGGER.info('Declaring exchange %s', exchange_name)
        self._channel.exchange_declare(self.on_exchange_declareok,
                                       exchange_name,
                                       self.EXCHANGE_TYPE, 
                                       durable=True)

    def on_exchange_declareok(self, unused_frame):
        
        LOGGER.info('Exchange declared')
        self.setup_queue(self.QUEUE)

    def setup_queue(self, queue_name):
        
        LOGGER.info('Declaring queue %s', queue_name)
        self._channel.queue_declare(self.on_queue_declareok, queue_name)

    def on_queue_declareok(self, method_frame):
        
        LOGGER.info('Binding %s to %s with %s',
                    self.EXCHANGE, self.QUEUE, self.ROUTING_KEY)
        self._channel.queue_bind(self.on_bindok, self.QUEUE,
                                 self.EXCHANGE, self.ROUTING_KEY)

    def on_bindok(self, unused_frame):
        
        LOGGER.info('Queue bound')
        self.start_consuming()

    def start_consuming(self):
        
        LOGGER.info('Issuing consumer related RPC commands')
        self.add_on_cancel_callback()
        self._consumer_tag = self._channel.basic_consume(self.on_message,
                                                         self.QUEUE)

    def add_on_cancel_callback(self):
        
        LOGGER.info('Adding consumer cancellation callback')
        self._channel.add_on_cancel_callback(self.on_consumer_cancelled)

    def on_consumer_cancelled(self, method_frame):
        
        LOGGER.info('Consumer was cancelled remotely, shutting down: %r',
                    method_frame)
        if self._channel:
            self._channel.close()

    def on_message(self, unused_channel, basic_deliver, properties, body):
        
        LOGGER.info('Received message # %s from %s: %s',
                    basic_deliver.delivery_tag, properties.app_id, body)
        """ Decode message """
        data = json.loads(body)
        LOGGER.info('Received %s', data['type'])
        if (data['type']=="KEYNOTE_STATE"):
                if(data['payload']['value']=="TRAIN_DEPARTURE_START"):
                        handleRouteTrain(mapIdToTrain(data['payload']['trainId']))
                        move(mapIdToTrain(data['payload']['trainId']))
                if(data['payload']['value']=="TRAIN_DEPARTURE_END"):
                        LOGGER.info("Stopping train %s", data['payload']['trainId'])
                        stop(mapIdToTrain(data['payload']['trainId']))
        if (data['type']=="OBSTACLE"):
		LOGGER.info(data['payload']['obstacle'])
		if(data['payload']['obstacle']):
			LOGGER.info("Stopping train %i", self.trainNumber)
                        stop(self.trainNumber)
	if (data['type']=="OBSTACLE_CLEARED"):
                LOGGER.info("Start train %i", self.trainNumber)
                move(self.trainNumber)
		
	self.acknowledge_message(basic_deliver.delivery_tag)

    def acknowledge_message(self, delivery_tag):
        
        LOGGER.info('Acknowledging message %s', delivery_tag)
        self._channel.basic_ack(delivery_tag)

    def stop_consuming(self):
        
        if self._channel:
            LOGGER.info('Sending a Basic.Cancel RPC command to RabbitMQ')
            self._channel.basic_cancel(self.on_cancelok, self._consumer_tag)

    def on_cancelok(self, unused_frame):
        
        LOGGER.info('RabbitMQ acknowledged the cancellation of the consumer')
        self.close_channel()

    def close_channel(self):
        
        LOGGER.info('Closing the channel')
        self._channel.close()

    def run(self):
        
        self._connection = self.connect()
        #self._connection.ioloop.start()

    def stop(self):
        
        LOGGER.info('Stopping')
        self._closing = True
        self.stop_consuming()
        self._connection.ioloop.start()
        LOGGER.info('Stopped')

    def close_connection(self):
        """This method closes the connection to RabbitMQ."""
        LOGGER.info('Closing connection')
        self._connection.close()

    def publish_message(self, message):
        if self._channel is None or not self._channel.is_open:
            return

        properties = pika.BasicProperties(app_id='raspberry-pi-train',
                                          content_type='application/json')

        self._channel.basic_publish(self.EXCHANGE, self.ROUTING_KEY,
                                    json.dumps(message, ensure_ascii=False),
                                    properties)
        LOGGER.info('Published message # %s', message)

""" Rabbit Instance """
rabbit = RabbitMq('amqp://user:password@host', tornado.ioloop.IOLoop.current())

LOG_FORMAT = ('%(levelname) -10s %(asctime)s %(name) -30s %(funcName) '
              '-35s %(lineno) -5d: %(message)s')
LOGGER = logging.getLogger(__name__)

def move(train):
  LOGGER.info('Move GPIO # %i', train)
  GPIO.output(train,GPIO.HIGH)
  rabbit.setRunningTrain(train)
  return

def stop(train):
  LOGGER.info('Stop GPIO# %i', train)
  GPIO.output(train,GPIO.LOW)
  return

def turn(turnout):
  GPIO.output(turnout,GPIO.LOW)
  time.sleep(1)
  return

def head(turnout):
  GPIO.output(turnout,GPIO.HIGH)
  time.sleep(1)
  return

def mapIdToTrain(id):
  if(id==2):
        return train2
  if(id==1):
        return train1

def handleRouteTrain(id): 
    if(id==train1):
        LOGGER.info('Handle road to train 1')
        head(turnoutA)
        head(turnoutB)
        turn(turnoutC)
        head(turnoutD)
    if(id==train2):
        LOGGER.info('Handle road to train 2')
        turn(turnoutA)
        turn(turnoutB)
        head(turnoutC)
        turn(turnoutD)

def sendPositionEventToRabbit(train, position):
  message = {
  u"type": u"TRAIN_POSITION",
  u"payload": {
     u"trainId": train,
     u"stationId": position,
     u"value": "TRAIN_POSITION"
  }
}
  rabbit.publish_message(message)

def sendStopEventToRabbit(train):
  message = {
  u"type": u"KEYNOTE_STATE",
  u"payload": {
     u"trainId": train,
     u"value": "TRAIN_DEPARTURE_END"
  }
}
  rabbit.publish_message(message)



def main():
    logging.basicConfig(level=logging.INFO, format=LOG_FORMAT)
    try:
        GPIO.setmode(GPIO.BCM)
        GPIO.setwarnings(False)
        GPIO.setup(train1,GPIO.OUT)
        GPIO.setup(train2,GPIO.OUT)
        GPIO.setup(turnoutA,GPIO.OUT)
        GPIO.setup(turnoutB,GPIO.OUT)
        GPIO.setup(turnoutC,GPIO.OUT)
        GPIO.setup(turnoutD,GPIO.OUT)
        stop(mapIdToTrain(1))
        stop(mapIdToTrain(2))
        rabbit.run()
        app = make_app()
     	app.listen(80)
	tornado.ioloop.IOLoop.current().start()
    except KeyboardInterrupt:
        rabbit.stop()


if __name__ == '__main__':
     main()
   


