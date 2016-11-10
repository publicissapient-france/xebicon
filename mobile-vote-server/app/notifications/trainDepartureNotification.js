'use strict';

var _ = require('underscore');
var i18n = require('../i18n.js');
var databaseService = require('../databaseService.js');
var trains = require('../trains.js');

var TrainDepartureNotification = function TrainDepartureNotification() {
  return this;
};

TrainDepartureNotification.prototype.canHandle = function canHandle(notification) {
  const topics = [
    'TRAIN_DEPARTURE_START',
    'TRAIN_DEPARTURE_END'
  ];

  return (notification.type == 'KEYNOTE_STATE') && _.contains(topics, notification.payload.value);
}

TrainDepartureNotification.prototype.content = function content(type, payload) {
  var body = null;
  const train = trains[payload.trainId];

  const content = {
    data: {
      keynoteState: payload.value,
      message: i18n.translate_train(payload.value, train),
      trainId: payload.trainId,
      description: train.description
    },
    notification: {
      body: i18n.translate_train(payload.value, train)
    }
  };

  databaseService.saveTrainState(payload.trainId, payload.value);

  return content;
};

module.exports = new TrainDepartureNotification();
