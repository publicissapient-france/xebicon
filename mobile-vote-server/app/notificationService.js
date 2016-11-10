'use strict';

const is_prod = process.env.NODE_ENV == 'production';
const key = is_prod ? 'AIzaSyACsY6btTNKGA3uVa2f1vryhuJ6znY8bqc' : 'AIzaSyBO-rmlQlX2PWmbEEcqQEZ_f6jtVXdKljs';
var gcm = require('node-gcm');
var sender = new gcm.Sender(key);

console.log('[Notif] (is prod: ' + is_prod + ') using key ' + key);

var NotificationService = function NotificationService() {
  return this;
};

NotificationService.prototype.sendNotifications = function sendNotifications(tokens, message, successCallback, errorCallback) {

  var gcmMessage = new gcm.Message(message);

  sender.send(gcmMessage, { registrationTokens : tokens }, function(error, response) {
    if (error) {
      return errorCallback(error);
    }

    return successCallback(response);
  });
}

module.exports = new NotificationService();
