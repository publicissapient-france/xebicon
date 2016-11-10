'use strict';

const config = require('config');
const amqplib = require('amqplib');
const logger = require('./logger');

const password = process.env.RABBIT_PASSWORD || config.get('rabbitMQ.password');
const user = process.env.RABBIT_USER || config.get('rabbitMQ.user');
const host = process.env.RABBIT_HOST || config.get('rabbitMQ.host');
const url = `amqp://${user}:${password}@${host}`;
const handler = amqplib.connect(url);
const exchangeProducer = process.env.RABBIT_EXCHANGE || config.get('rabbitMQ.exchangeProducer');
const exchangeConsumer = process.env.RABBIT_EXCHANGE || config.get('rabbitMQ.exchangeConsumer');
const q = config.get('rabbitMQ.queue');

const postMessage = data => handler
  .then(conn => conn.createChannel())
  .then(ch => ch.assertExchange(exchangeProducer, 'fanout', { durable: true })
    .then(() => ch.publish(exchangeProducer, '', new Buffer(JSON.stringify(data))))
    .then(() => ch.close()))
  .catch(logger.warn);

const subscribeQueue = cb => handler
  .then(conn => conn.createChannel())
  .then(ch => ch.assertExchange(exchangeConsumer, 'fanout', { durable: true })
    .then(() => ch.assertQueue(q, { exclusive: false }))
    .then(qok => ch.bindQueue(qok.queue, exchangeConsumer, q)
      .then(() => qok.queue))
    .then(queue => ch.consume(queue, msg => cb(msg.content.toString()), { noAck: true }))
    .then(() => logger.info(`Connected to Rabbit on ${host} user ${user}`)))
  .catch(logger.warn);

module.exports = {
  handler,
  postMessage: data => postMessage(data),
  subscribeQueue: cb => subscribeQueue(cb)
};

