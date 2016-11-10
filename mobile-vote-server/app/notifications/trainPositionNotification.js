'use strict';

const _ = require('underscore');
var i18n = require('../i18n.js');
var databaseService = require('../databaseService.js');
var stations = require('../stations');

const managedPositions = ['xebicon'];

var TrainPositionNotification = function TrainPositionNotification() {
  return this;
};

TrainPositionNotification.prototype.canHandle = function canHandle(notification) {
  //noinspection JSUnresolvedVariable
  const canHandlePosition = _.contains(managedPositions, stations[notification.payload.stationId]);

  return (notification.type == 'TRAIN_POSITION') && canHandlePosition;
};

TrainPositionNotification.prototype.content = function content(type, payload) {

  const content = {
    data: {
      keynoteState: 'TRAIN_POSITION',
      message: i18n.translate('TRAIN_POSITION'),
      trainId: payload.trainId
    },
    notification: {
      body: i18n.translate('TRAIN_POSITION')
    }
  };

  databaseService.saveTrainState(payload.trainId, type);

  return content;
};

module.exports = new TrainPositionNotification();
