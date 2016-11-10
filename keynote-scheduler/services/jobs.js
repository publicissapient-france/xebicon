'use strict';

const schedule = require('node-schedule');
const moment = require('moment');
const _ = require('lodash');
const MODES = require('../config/constants').MODES;
const allEvents = require('../models/events').allEvents;
const StateService = require('./state');

const startAllJobs = (callback) => {

  StateService.cancelAndRemoveAllJobs();
  StateService.setState({
    jobs: [],
    mode: MODES.AUTOMATIC
  });

  // function to execute
  const runJobSendEvent = (data) => {
    console.log(new Date() + ' data:' + data.payload.value);
    callback(data);
  };

  let t = moment();

  const scheduleJob = (event, seconds) => {
    t.add(seconds, 's');
    const job = schedule.scheduleJob(event.payload.value, t.toDate(), () => runJobSendEvent(event));
    StateService.addJob(Object.assign({}, job, {
        next: job.nextInvocation(),
        seconds
      })
    );
  };

  scheduleJob(allEvents.KEYNOTE_START, 10);
  scheduleJob(allEvents.VOTE_TRAIN_START, 10);
  scheduleJob(allEvents.VOTE_TRAIN_END, 10);
  scheduleJob(allEvents.HOT_DEPLOYMENT_START, 10);
  scheduleJob(allEvents.HOT_DEPLOYMENT_END, 10);
  scheduleJob(allEvents.AVAILABILITY_START, 10);
  scheduleJob(allEvents.AVAILABILITY_END, 10);
  scheduleJob(allEvents.OBSTACLE_START, 10);
  scheduleJob(allEvents.OBSTACLE_END, 10);
  scheduleJob(allEvents.KEYNOTE_END, 10);
};

const stopAllJobs = () => {
  StateService.cancelAndRemoveAllJobs();
};

module.exports = {
  startAllJobs,
  stopAllJobs
};