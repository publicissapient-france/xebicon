"use strict";



const amqpUrl = 'amqp://user:password@rabbit.xebicon.fr';
const exchangeName = 'xebiconExchange'
const lambdaUrl = "xxx"
const unirest = require("unirest")

const open = require('amqplib').connect(amqpUrl);

open.then(conn => {
    conn.createChannel().then(ch =>
        ch.assertExchange(exchangeName, 'fanout', {durable: true})
            .then(() => ch.assertQueue('lambda_bridge_consumer', {exclusive: true}))
            .then(qok => ch.bindQueue(qok.queue, exchangeName, 'lambda_bridge_consumer').then(() => qok.queue))
            .then(queue => ch.consume(queue, msg => {
                console.log(" [x] Received %s", msg.content.toString());
                var msgJson = JSON.parse(msg.content.toString())
                if (msgJson.type == "KEYNOTE_STATE") {
                    console.log(" [y] Forwarding to unirest");
                    forwardToLambda(msgJson.payload.value, msgJson.payload)
                } else if (msgJson.type == "TRAIN_POSITION") {
                    console.log(" [y] Forwarding to unirest");
                    forwardToLambda(msgJson.type, msgJson.payload)
                } else if (msgJson.type == "OBSTACLE") {
                    console.log(" [y] Forwarding to unirest");
                    forwardToLambda(msgJson.type, msgJson.payload)
                }
            }),{noAck: false}))
})


const forwardToLambda = (event, payload) => {
    unirest.post(lambdaUrl)
        .header('Accept', 'application/json')
        .header('Content-type', 'application/json')
        .send({
            "entry": [{
                "messaging": [{
                    "postback": {
                        "payload": event,
                        "additionnalInfo": payload
                    }
                }]
            }]
        })
        .end((response) => {
            console.log("[sendTextMessage][out]")
            console.log("[sendTextMessage]", "Response: ", response.code)
        });
}
