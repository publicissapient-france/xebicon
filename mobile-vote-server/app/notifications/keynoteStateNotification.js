'use strict';

var _ = require('underscore');
var i18n = require('../i18n.js');

const databaseService = require('../databaseService');

var KeynoteStateNotification = function KeynoteStateNotification() {
  return this;
};

KeynoteStateNotification.prototype.canHandle = function canHandle(notif) {
  const topics = [
    'KEYNOTE_START',
    'KEYNOTE_END',
    'VOTE_TRAIN_START',
    'VOTE_TRAIN_END'
  ];

  return (notif.type == 'KEYNOTE_STATE') && _.contains(topics, notif.payload.value);
};

KeynoteStateNotification.prototype.content = function content(type, payload) {
  const message = i18n.translate(payload.value) || null;

  const content = {
    'content-available': true,
    data: {
      keynoteState: payload.value,
      message: message
    },
    notification: {
      body: message
    }
  };

  if (payload.value == 'KEYNOTE_END') {
    databaseService.clearKeynote();
  }
  else {
    databaseService.saveKeynoteState(payload.value);
  }

  return content;
};

module.exports = new KeynoteStateNotification();
