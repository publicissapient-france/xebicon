'use strict';

// read settings
global.settings = require('./settings');
global.labels = require('./labels');

var exports = module.exports = {}

const messenger = require('./messenger.js'),
    rabbit = require('./rabbit.js'),
    state = require('./redis.js')

const logger = require('winston')

var voteStarted = false

const hasVoteStarted = exports.hasVoteStarted = () => {
    return voteStarted
}

const setVoteStarted = exports.setVoteStarted =(value) => {
    voteStarted = value
}


const handleVoteTrainStart = exports.handleVoteTrainStart = (sender) => {
    setVoteStarted(true)
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
}

const handleVoteTrainSendByUser = exports.handleVoteTrainSendByUser = (sender, event, user) => {
    logger.info("[handleVoteTrainSendByUser][in]",user)
    if (!user["VOTE_TRAIN"]) {
        var idStation = event.postback.payload.substring(event.postback.payload.length - 1)
        user["VOTE_TRAIN"] = idStation
        logger.info("[handleVoteTrainSendByUser] Saving user", user)
        state.saveUser(sender, user)
        state.addUserToStation(idStation, sender)
        var vote = {
            type: "VOTE_TRAIN",
            payload: {
                media: "FB",
                count: 1,
                trainId: Number(idStation),
                avatar: user.profile_pic
            }
        }
        rabbit.sendMessageToAmqp(JSON.stringify(vote))
        console.log("[handleVoteTrainSendByUser]", "Station :", idStation)
        return messenger.sendGenericMessage(sender, {'text': labels.stations[idStation - 1].boardingText}).then(() => {
            messenger.sendGenericMessage(sender, {
                "attachment": {
                    "type": "image",
                    "payload": {
                        "url": labels.stations[idStation - 1].image
                    }
                }
            })
        })
    } else {
        return messenger.sendGenericMessage(sender, {'text': "Vous avez déjà choisi votre train !"});
    }
}

const handleVoteTrainEnd = exports.handleVoteTrainEnd = (sender, results) => {
    //return messenger.sendGenericMessage(sender, {'text': results});
    return
}