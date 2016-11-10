'use strict';

// read settings
global.settings = require('./settings');
global.labels = require('./labels');

var exports = module.exports = {}

const messenger = require('./messenger.js'),
    rabbit = require('./rabbit.js')

const logger = require('winston')

const handleHotDeployStart = exports.handleHotDeployStart = () => {
    return messenger.sendGenericMessage(null, {'text': labels.stations[1].hotDeployStartText}, 2)
}

const handleHotDeployEnd = exports.handleHotDeployEnd = () => {
    return messenger.sendGenericMessage(null, {'text': labels.stations[1].hotDeployStopText}, 2)
}
