'use strict';

var _ = require('underscore');
var i18n = require('../i18n.js');
var trains = require('../trains');

var HotDeploymentNotification = function HotDeploymentNotification() {
  return this;
};

HotDeploymentNotification.prototype = new Object();

HotDeploymentNotification.prototype.trainForNotification = function trainForNotification(notification, callback) {
  let trainId;

  _.each(_.keys(trains), function (id) {
    if (notification.payload.value.includes(trains[id].issue.toUpperCase())) {
      trainId = id;
    }
  });

  callback(trainId);
}

HotDeploymentNotification.prototype.canHandle = function canHandle(notification) {
  const topics = [
    'HOT_DEPLOYMENT_START',
    'HOT_DEPLOYMENT_END'
  ];

  return (notification.type == 'KEYNOTE_STATE') && _.contains(topics, notification.payload.value);
}

HotDeploymentNotification.prototype.content = function content(type, payload) {

  const content = {
    data: {
      keynoteState: payload.value,
      message: i18n.translate(payload.value),
      trainId: payload.trainId
    },
    notification: {
      body: i18n.translate(payload.value)
    }
  };

  return content;
};

module.exports = new HotDeploymentNotification();
