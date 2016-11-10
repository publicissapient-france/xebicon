'use strict';

const _ = require('underscore');
const i18n = require('../i18n.js');

const databaseService = require('../databaseService');

const ObstacleNotification = function ObstacleNotification() {
  return this;
};

ObstacleNotification.prototype.canHandle = function canHandle(notification) {
  const topics = [
    'OBSTACLE',
    'OBSTACLE_CLEARED'
  ];

  return _.contains(topics, notification.type);
}

ObstacleNotification.prototype.trainForNotification = function trainForNotification(notification, callback) {
  return databaseService.findTrainInMove(callback, function(error) { callback(null); });
}

ObstacleNotification.prototype.content = function content(type, payload) {
  const content = {
    data: {
      keynoteState: type,
      trainId: payload.trainId
    },
    notification: { }
  };

  if (type == 'OBSTACLE_CLEARED') {
    content.data.message = i18n.translate(type);
    content.notification.body = i18n.translate(type);
  }
  else {
    const msg = i18n.translate(payload.obstacle ? 'OBSTACLE_STOP' : 'OBSTACLE_WARNING');

    content.data.message = msg;
    content.data.blocked = payload.obstacle;
    content.data.obstacleType = payload.obstacleType;

    content.notification.body = msg;
  }

  return content;
};

module.exports = new ObstacleNotification();
