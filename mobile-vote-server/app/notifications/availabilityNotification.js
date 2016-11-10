'use strict';

var _ = require('underscore');
var i18n = require('../i18n');
var trains = require('../trains');

var AvailabilityNotification = function AvailabilityNotification() {
  return this;
};

AvailabilityNotification.prototype.trainForNotification = function trainForNotification(notification, callback) {
  let trainId;

  _.each(_.keys(trains), function (id) {
    if (notification.payload.value.includes(trains[id].issue.toUpperCase())) {
      trainId = id;
    }
  });

  callback(trainId);
}

AvailabilityNotification.prototype.canHandle = function canHandle(notification) {
  const topics = [
    'AVAILABILITY_START',
    'AVAILABILITY_END'
  ];

  return notification.type == 'KEYNOTE_STATE' && _.contains(topics, notification.payload.value);
}

AvailabilityNotification.prototype.content = function content(type, payload) {
  var body = null;

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

module.exports = new AvailabilityNotification();
