// read settings
global.settings = require('./settings');
global.labels = require('./labels');

const redis = require('./redis.js'),
    messenger = require('./messenger.js'),
    vote = require('./vote.js'),
    hotDeploy = require('./hotDeploy.js'),
    highAvailability = require('./highAvailability.js'),
    train = require('./train.js'),
    animal = require('./animalDetection.js'),
    initServer = require('./initServer.js'),
    rabbit = require('./rabbit.js')
const logger = require('winston'),
    Promise = require('bluebird'),
    util = require('util')


'use strict';

var https = require('https');


initServer.app.post('/', function (req, res) {
    const reply = (error, message) => {
        if (error) {
            res.send("KO")
        }
        res.send("" + message)
    }


    handler(req.body, null, reply)
})

initServer.app.get('/', function (req, res) {
    const reply = (error, message) => {
        if (error) {
            res.send("KO")
        }
        res.send("" + message)
    }

    var url = require('url');
    var url_parts = url.parse(req.url, true);
    var query = url_parts.query;

    var event = {params: {querystring: query}}

    handler(event, null, reply)
})


const queue_name = 'facebook_consumer_' + Date.now();
rabbit.open.then(conn => {
    conn.createChannel().then(ch =>
        ch.assertExchange(settings.amqp_queue, 'fanout', {durable: true})
            .then(() => ch.assertQueue(queue_name, {exclusive: true}))
            .then(qok => ch.bindQueue(qok.queue, settings.amqp_queue, queue_name).then(() => qok.queue))
            .then(queue => ch.consume(queue, msg => {
                console.log(" [x] Received %s", msg.content.toString());
                var msgJson = JSON.parse(msg.content.toString())

                var event = {
                    "entry": [{
                        "messaging": [{
                            "postback": {
                                "payload": null,
                                "additionnalInfo": null
                            }
                        }]
                    }]
                }

                if (msgJson.type == "KEYNOTE_STATE") {
                    event.entry[0].messaging[0].postback.payload = msgJson.payload.value
                    event.entry[0].messaging[0].postback.additionnalInfo = msgJson.payload
                } else if (msgJson.type == "TRAIN_POSITION" || msgJson.type == "OBSTACLE" || msgJson.type == "OBSTACLE_CLEARED") {
                    event.entry[0].messaging[0].postback.payload = msgJson.type
                    event.entry[0].messaging[0].postback.additionnalInfo = msgJson.payload
                }

                if (event.entry[0].messaging[0].postback.payload) {
                    handler(event, null, () => {
                        console.log("No callback needed")
                    })
                }
                ch.ack(msg);
            })))
})

const handler = exports.handler = (event, context, callback) => {
    logger.info("[exports.handler][in]", "Event: ", event)

    if (event.params && event.params.querystring) {
        messenger.validateWebhook(event, callback)
    } else {
        var messagingEvents = event.entry[0].messaging;
        for (var i = 0; i < messagingEvents.length; i++) {
            var messagingEvent = messagingEvents[i];

            var sender
            if (messagingEvent.sender && messagingEvent.sender.id) {
                sender = messagingEvent.sender.id
            }

            enrollUser(sender).then((user) => {
                logger.info("[handler] Going to handle message", "Current user", user)
                return handleMessage(messagingEvent, sender, user)
            }).then(() => {
                callback(null, 'Done')
            })
        }
    }
}

const handleMessage = (messagingEvent, sender, user) => {
    logger.info("[handleMessage][in]", "MessagingEvent", messagingEvent, "Current user", user)
    if (messagingEvent.message && messagingEvent.message.text) {
        var text = messagingEvent.message.text;
        logger.info("[exports.handler] Received a message: " + text, "Sender:" + sender);
        if (user && user["WELCOMED"]) {
            return messenger.sendGenericMessage(sender, {'text': "Un peu de patience, " + user.first_name + ", il va bientôt se passer quelquechose !"});
        } else {
            user["WELCOMED"] = true
            redis.saveUser(sender, user)
            if (vote.hasVoteStarted()) {
                return messenger.sendGenericMessage(sender, {'text': "Bonjour " + user.first_name + ". Je ne suis pas encore très évolué, mais j'apprends vite ! Vous participez maintenant à la keynote, bienvenue..."}).then(() => {
                    return messenger.sendGenericMessage(sender, {
                        "attachment": {
                            "type": "template",
                            "payload": {
                                "template_type": "generic",
                                "elements": [
                                    {
                                        "title": "Veuillez choisir un train.",
                                        "image_url": "https://s3-eu-west-1.amazonaws.com/xebicon-images/bdx1.png",
                                        "subtitle": "Quelle destination souhaitez vous découvrir ?",
                                        "buttons": [
                                            {
                                                "type": "postback",
                                                "payload": "VOTE_TRAIN#1",
                                                "title": labels.stations[0].name
                                            },
                                            {
                                                "type": "postback",
                                                "title": labels.stations[1].name,
                                                "payload": "VOTE_TRAIN#2"
                                            }
                                        ]
                                    }
                                ]
                            }
                        }
                    });
                });
            } else {
                return messenger.sendGenericMessage(sender, {'text': "Bonjour " + user.first_name + ". Je ne suis pas encore très évolué, mais j'apprends vite ! Vous participez maintenant à la keynote, bienvenue..."});
            }
        }
    } else if (messagingEvent.postback) {
        return receivedPostback(messagingEvent, sender, user)
    }
}

const enrollUser = (sender) => {
    logger.info("[enrollUser][in]", "Sender", sender)
    if (sender) {
        return redis.getUser(sender).then((user) => {
            if (!user || !user.first_name) {
                logger.info("[enrollUser][out]", "to [messenger.findUserDetails]")
                return messenger.findUserDetails(sender)
            } else {
                return new Promise((resolve, reject) => {
                    logger.debug("[enrollUser] User already registered")
                    resolve(user)
                })
            }
        })
    } else {
        return new Promise((resolve, reject) => {
            logger.debug("[enrollUser] No sender id")
            resolve(null)
        })
    }
}

const receivedPostback = (event, sender, user) => {
    logger.info("[receivedPostback][in]", "Payload: ", event.postback.payload)

    var useCase = event.postback.payload

    switch (useCase) {
        case "GET_STARTED":
            return messenger.sendGenericMessage(sender, {'text': "Vous êtes sur le point d'embarquer dans une aventure touristiquement technologique."});
        case "VOTE_TRAIN_START":
            return vote.handleVoteTrainStart(sender)
        case "VOTE_TRAIN#1":
        case "VOTE_TRAIN#2":
            return vote.handleVoteTrainSendByUser(sender, event, user)
        case "VOTE_TRAIN_END":
            // Nothing todo
            break
        case "HOT_DEPLOYMENT_START":
            return hotDeploy.handleHotDeployStart()
        case "HOT_DEPLOYMENT_END":
            return hotDeploy.handleHotDeployEnd()
        case "AVAILABILITY_START":
            return highAvailability.handleHighAvailabilityStart(sender)
        case "AVAILABILITY#PAUILLAC":
        case "AVAILABILITY#PESSAC":
        case "AVAILABILITY#MARGAUX":
            return highAvailability.handleChoice(useCase.substring(13), sender)
        case "AVAILABILITY_END":
            return highAvailability.handleHighAvailabilityEnd(sender)
        case "KEYNOTE_END":
            return redis.flushAll()
        case "KEYNOTE_START":
            vote.setVoteStarted(false)
            return
        case "TRAIN_DEPARTURE_START":
            return train.handleTrainDepartureStart(event.postback.additionnalInfo.trainId)
        case "TRAIN_DESCRIPTION_START":
            return train.handleTrainDescriptionStart(event.postback.additionnalInfo.trainId)
        case "TRAIN_DESCRIPTION_END":
            // Nothing todo
            break;
        case "TRAIN_POSITION":
            return train.handleTrainPosition(event.postback.additionnalInfo.trainId, event.postback.additionnalInfo.stationId)
        case "TRAIN_DEPARTURE_END":
            return train.handleTrainDepartureEnd(event.postback.additionnalInfo.trainId)
        case "OBSTACLE":
            return animal.obstacle(event.postback.additionnalInfo.obstacle, event.postback.additionnalInfo.obstacleType)
        case "OBSTACLE_START":
            return animal.setObstacleSentValue(false)
        case "OBSTACLE_END":
            return animal.setObstacleSentValue(true)
        case "OBSTACLE_CLEARED":
            return animal.obstacleCleared()


    }
}












