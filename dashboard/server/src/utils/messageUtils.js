const VOLATILES = ['VOTE_TRAIN'];
const debug = require('debug')('dashboard-backend:server');

export const isVolatile = message => VOLATILES.indexOf(message.type) != -1;

export const isInvalid = message => !message || !message.type;

export const parseMessage = msg => {
  try {
    const message = JSON.parse(msg.toString());

    if (isInvalid(message)) {
      debug('Warn: message has no "type" property -> rejected', message);
      return;
    }

    return message;
  } catch (err) {
    debug(`ERROR: ${err} in message ${msg}`);
  }
};
