import {parseMessage} from "./utils/messageUtils";
const exchangeName = process.env.RABBIT_EXCHANGE || 'xebiconExchange';
const user = process.env.RABBIT_USER ;
const pwd = process.env.RABBIT_PASSWORD ;
const host = process.env.RABBIT_HOST ;
const rabbitUrl = `amqp://${user}:${pwd}@${host}`;
const open = require('amqplib').connect(rabbitUrl);
const debug = require('debug')('dashboard-backend:server');

// Consumer Logic
const consumerCallback = (msg, store) => {
  let message;
  if (msg && msg.content && (message = parseMessage(msg.content))) {
    store.dispatch(message);
  }
};

// Publish / Subscribe Consumer
const pubSubConsumer = (conn, store) => {
  return conn.createChannel().then(ch => {
    return ch.assertExchange(exchangeName, 'fanout', {durable: true})
      .then(() => ch.assertQueue('dashboard_consumer_' + exchangeName, {exclusive: true}))
      .then(qok => ch.bindQueue(qok.queue, exchangeName, 'dashboard_consumer_' + exchangeName).then(() => qok.queue))
      .then(queue => ch.consume(queue, msg => consumerCallback(msg, store), {noAck: true}))
      .then(() => debug(`[*] Connected to Rabbit @ ${host} (${exchangeName})`));
  });
};

const publishSubProducer = (data) => {
  return open
    .then((conn) => {
      conn.createChannel().then((ch) => {
        return ch.assertExchange(exchangeName, 'fanout', {durable: true})
          .then(() => {
            ch.publish(exchangeName, '', new Buffer(data));
            return ch.close();
          });
      });
    })
    .then(null, console.warn);
};

export default {
  init: (store) => {
    return open
      .then(conn => pubSubConsumer(conn, store))
      .then(null, console.warn);
  },

  sendToRabbit: (data) => publishSubProducer(data)
};
