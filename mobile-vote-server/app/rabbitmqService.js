'use strict';

const Random = require('random-js');
const cluster = require('cluster');
const _ = require('underscore');
const amqp = require('amqplib');
const gcm = require('node-gcm');

const notificationService = require('./notificationService');
const databaseService = require('./databaseService');
const notifications = require('./notifications');
const config = require('../config.json');

const env = process.env;

const exchange = env.RABBIT_EXCHANGE || config.RABBIT_EXCHANGE;
const user = env.RABBIT_USER || config.RABBIT_USER;
const pwd = env.RABBIT_PASSWORD || config.RABBIT_PASSWORD;
const port = env.RABBIT_PORT || config.RABBIT_PORT;
const host = env.RABBIT_HOST || config.RABBIT_HOST;

const random = new Random();

const amqpUrl = `amqp://${user}:${pwd}@${host}:${port}`;

const gcmSender = new gcm.Sender('AIzaSyACsY6btTNKGA3uVa2f1vryhuJ6znY8bqc');

let globalChannel;

const RabbitmqService = function RabbitmqService() {
  return this;
};

RabbitmqService.prototype.connect = function connect() {

  amqp.connect(amqpUrl, {keepAlive:true})
    .then(function(connection) {
      console.log(`[Rabbit] connected to : ${amqpUrl}, echange = ${exchange}`);

      return connection.createChannel().then(handleChannel);

    })
    .catch(console.error);

};

RabbitmqService.prototype.publishVote = function publishVote(req, res) {

  const vote = req.body;

  const votePayload = {
    type: 'VOTE_TRAIN',
    payload: {
      trainId: vote.trainId,
      media: vote.media.toUpperCase()
    }
  };

  publishChannel(votePayload);

  databaseService.saveTrainVote(vote);

  return res.sendStatus(200);
};

RabbitmqService.prototype.purchase = function purchase(req, res) {

  const purchase = req.body;

  const payload = {
    type: 'BUY',
    payload: {type: purchase.article}
  }

  publishChannel(payload);

  return res.sendStatus(200);
};

const handleChannel = function handleChannel(channel) {

  globalChannel = channel;

  return channel.assertExchange(exchange, 'fanout', {durable: true}).then(function handleExchange(exchange) {

    return channel.assertQueue('', {exclusive: true}).then(function handleQueue(queue) {

      channel.bindQueue(queue.queue, exchange.exchange, 'KEYNOTE_STATE');

      return consumeChannel(channel, queue);

    }, console.error);

  });
};

const publishChannel = function publishChannel(payload) {
  const json = JSON.stringify(payload)

  return globalChannel.publish(exchange, '', new Buffer(json));
};

const consumeChannel = function consumeChannel(channel, queue) {

  return channel.consume(queue.queue, function(message) {
    const messageJson = JSON.parse(message.content.toString());

    const workers = cluster.workers;
    const randomIndex = random.integer(0, _.size(workers) - 1);

    consumeMessage(messageJson);

  }, {
    noAck: true
  }, console.error);

};

const consumeMessage = function consumeMessage(message) {
    message.payload = message.payload || {};

    const payload = message.payload;
    const type = message.type;

    let trainId = payload ? payload.trainId : undefined;

    const notification = notifications.find(function(notificationHandler) {
      return notificationHandler.canHandle(message) ? notificationHandler : null
    })

    if (!notification) {
      console.log('[Rabbit] Skipped topic ' + type);

      return;
    }

    if (notification.trainForNotification) {
      notification.trainForNotification(message, function(trainId) {
        if (!trainId) {
          console.log("[TRAIN] Skipped topic " + topic + ", no trainId");
          return;
        }

        trainId = parseInt(trainId);
        message.payload.trainId = trainId;
        handlePayloadForTrain(trainId, message, notification);
      });
    }
    else {
      handlePayloadForTrain(trainId, message, notification);
    }
};

const handlePayload = function handlePayload(message, notification, registrationTokens) {
  const successCallback = function succesCallback(response) {
      console.log('[Notification][Ok]', response);

    return;
  };

  const errorCallback = function errorCallback(error) {
    console.log('[Notification][Error]', error);

    return;
  }

  return notificationService.sendNotifications(registrationTokens, notification.content(message.type, message.payload), successCallback, errorCallback);
};

const handlePayloadForTrain = function handlePayloadForTrain(trainId, message, notification) {
  const successCallback = function succesCallback(registrationTokens) {
    return handlePayload(message, notification, registrationTokens);
  };

  const errorCallback = function errorCallback(error) {
    console.log("[ERR][TRAIN] " + error);
    return;
  };

  console.log("[Rabbit] Received topic " + message.type);

  if (env.LOG_NOTIFICATION) {
      console.log(notification);
  }

  return databaseService.findTrainVotes(trainId, successCallback, errorCallback);
};

module.exports = new RabbitmqService();
