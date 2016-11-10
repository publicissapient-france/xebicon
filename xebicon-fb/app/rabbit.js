'use strict';

// read settings
global.settings = require('./settings');

const amqp = require('amqplib/callback_api')

var exports = module.exports = {}

const open = exports.open = require('amqplib').connect(settings.amqp_url);

const sendMessageToAmqp = exports.sendMessageToAmqp = (msg) => {
    open.then(conn => {
        conn.createChannel().then(ch =>
            ch.assertExchange(settings.amqp_queue, 'fanout', {durable: true})
                .then(() => {
                    console.log("Publish", msg)
                    ch.publish(settings.amqp_queue, '', new Buffer(msg), {})
                }))
    })
}

