'use strict';

// read settings
global.settings = require('./settings');
global.labels = require('./labels');

var exports = module.exports = {}

const messenger = require('./messenger.js'),
    rabbit = require('./rabbit.js'),
    redis = require('./redis.js')

const logger = require('winston')

var obstacleAlreadySent = true

var setObstacleSentValue = exports.setObstacleSentValue = (boolValue) => {
    obstacleAlreadySent = boolValue
}

var getObstacleSent = () => {
    return obstacleAlreadySent
}

const obstacle = exports.obstacle = (obstacle, obstacleType) => {
    if (!getObstacleSent()) {
        setObstacleSentValue(true)
        return redis.getRunningTrain().then((result) => {
            return messenger.sendGenericMessage(null, {'text': labels.animal["obstacle-" + obstacle.toString()]}, result).then(() => {
                messenger.sendGenericMessage(null, {
                    "attachment": {
                        "type": "image",
                        "payload": {
                            "url": labels.animal[obstacleType].image
                        }
                    }
                }, result)
            })
        })
    }
}

const obstacleCleared = exports.obstacleCleared = () => {
    return redis.getRunningTrain().then((result) => {
        return messenger.sendGenericMessage(null, {'text': labels.obstacleCleared}, result)
    })
}

