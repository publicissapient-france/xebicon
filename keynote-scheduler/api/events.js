'use strict';
const events = require('express').Router();
const MODES = require('../config/constants').MODES;
const allEvents = require('../models/events').allEvents;
const StateService = require('../services/state');
const RabbitService = require('../services/rabbit');
const JobsService = require('../services/jobs');

const sseConnections = [];

const seeSend = value => {
  for (var i = 0; i < sseConnections.length; i++) {
    sseConnections[i].sseSend(value);
  }
};

const sendEvent = data => {
  RabbitService.publish(data)
    .then(() => StateService.setState({currentState: data.payload}))
    .then(() => seeSend(StateService.getState()))
    .catch(err => console.error(err));
};

events.route('/start-jobs')
  .post((req, res) => {
    JobsService.startAllJobs(sendEvent);
    seeSend(StateService.getState());
    res.status(200).end();
  });

events.route('/stop-jobs')
  .post((req, res) => {
    StateService.setState({
      mode: MODES.MANUAL,
      currentState: 'NONE',
      nextState: 'NONE'
    });
    JobsService.stopAllJobs();
    seeSend(StateService.getState());
    res.status(200).end();
  });

events.route('/current-state')
  .get((req, res) => {
    const state = StateService.getState();
    if (req.get('Accept') === 'text/event-stream') {
      res.sseSetup();
      res.sseSend(state);
      sseConnections.push(res);
    } else {
      res.status(200).json(state);
    }
  });

events.route('/all')
  .get((req, res) => {
    res.status(200).json(allEvents);
  });

events.route('/send')
  .post((req, res) => {
    console.log('post ' + JSON.stringify(req.body));
    sendEvent(req.body);
    res.status(200).end();
  });


module.exports = events;