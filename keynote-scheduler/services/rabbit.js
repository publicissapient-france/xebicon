'use strict';

const exchangeName = process.env.RABBIT_EXCHANGE || 'xebiconExchange';
const user = process.env.RABBIT_USER ;
const pwd = process.env.RABBIT_PASSWORD ;
const port = process.env.RABBIT_PORT;
const host = process.env.RABBIT_HOST || 'localhost';

const rabbitUrl = `amqp://${user}:${pwd}@${host}:${port}`;
const amqp = require('amqplib');

console.log(`RabbitMQ connected at exchange '${exchangeName}' on host '${rabbitUrl}'`);

const publish = data => amqp.connect(rabbitUrl).then(conn =>
  conn.createChannel()
    .then(ch =>
      ch.assertExchange(exchangeName, 'fanout', {durable: true})
        .then(() => new Buffer(JSON.stringify(data)))
        .then(content => ch.publish(exchangeName, '', content, {}))
        .then(message => console.log(`message published = ${message}`))
        .then(() => ch.close())))
  .then(null, console.warn);

const listen = () => amqp.connect(rabbitUrl, {keepAlive: true}).then(conn =>
  conn.createChannel().then(ch => {
      ch.assertExchange(exchangeName, 'fanout', {durable: true})
        .then(exchange => ch.assertQueue('scheduler_consumer', {exclusive: true})
          .then(queue => {
            ch.bindQueue(queue.queue, exchange.exchange, 'scheduler_consumer');
            ch.consume(queue.queue, message => {
              console.log(`[Rabbit] Received : ${message.content.toString()}`);
            }, {noAck: true})
          }));
    }
  ));

listen()
  .catch(err => console.error);

module.exports = {
  publish,
  exchangeName
};
