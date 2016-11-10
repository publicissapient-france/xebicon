'use strict';

// read settings
global.settings = require('./settings');
global.labels = require('./labels');

var exports = module.exports = {}

const messenger = require('./messenger.js'),
    rabbit = require('./rabbit.js'),
    redis = require('./redis.js')

const logger = require('winston')

const handleTrainDepartureStart = exports.handleTrainDepartureStart = (idTrain) => {
    logger.info("[handleTrainDepartureStart][in]")
    redis.incrementCounterAndGet("train" + idTrain + "DepartureStart").then((result) => {
        redis.saveRunningTrain(idTrain).then(() => {
            logger.info("[handleTrainDepartureStart] Counter value", result)
            return handleMessageToUser(idTrain, labels.stations[idTrain - 1].departureStartText[result.toString()])
        })
    })
}

const handleTrainDepartureEnd = exports.handleTrainDepartureEnd = (idTrain) => {
    logger.info("[handleTrainDepartureEnd][in]")
    /*redis.incrementCounterAndGet("train" + idTrain + "DepartureEnd").then((result) => {
        redis.saveRunningTrain(null).then(() => {
            logger.info("[handleTrainDepartureEnd] Counter value", result)
            return handleMessageToUser(idTrain, labels.stations[idTrain - 1].departureEndText[result.toString()])
        })
    })*/
}

const handleTrainDescriptionStart = exports.handleTrainDescriptionStart = (idTrain) => {
    logger.info("[handleTrainDescriptionStart][in]")
    redis.incrementCounterAndGet("train" + idTrain + "DescriptionStart").then((result) => {
        logger.info("[handleTrainDescriptionStart] Counter value", result)
        return handleMessageToUser(idTrain, labels.stations[idTrain - 1].descriptionText[result.toString()])
    })
}

const handleTrainPosition = exports.handleTrainPosition = (idTrain, position) => {
    logger.info("[handleTrainPosition][in]", "idTrain: ", idTrain, "position", position)

    return handleMessageToUser(idTrain, labels.stations[idTrain - 1].positionText[position.toString()])
}

const handleMessageToUser = (idTrain, message) => {
    return messenger.sendGenericMessage(null, {'text': message}, idTrain)
}