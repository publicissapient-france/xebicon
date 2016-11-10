'use strict';

const obstacleNotification = require('./obstacleNotification');
const availabilityNotification = require('./availabilityNotification');
const keynoteStateNotification = require('./keynoteStateNotification');
const hotDeploymentNotification = require('./hotDeploymentNotification');
const trainPositionNotification = require('./trainPositionNotification');
const trainDepartureNotification = require('./trainDepartureNotification');

module.exports = [
  obstacleNotification,
  availabilityNotification,
  keynoteStateNotification,
  hotDeploymentNotification,
  trainPositionNotification,
  trainDepartureNotification
];
