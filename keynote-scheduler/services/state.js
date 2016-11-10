'use strict';
const _ = require('lodash');
const MODES = require('../config/constants').MODES;
const exchangeName = require('./rabbit').exchangeName;

const storage = require('node-persist');

const StateService = {};

const dbStateName = `state-${exchangeName}`;

const defaultState = {
  currentState: 'NONE',
  nextState: 'NONE',
  mode: MODES.MANUAL,
  jobs: [],
  exchangeName
};

var state;

storage.init({
  logging: true,
  ttl: false
}).then(function() {
  return storage.getItem(dbStateName);
}).then(function(dbState) {

  if (!dbState) {
    state = defaultState;
  } else {
    state = dbState;
  }
  return state;
}).catch(function(err) {
  console.error(err);
  throw err;
});


StateService.getState = () => {
  return state;
};

StateService.setState = (value) => {
  const newState = Object.assign({}, state, value);
  storage.setItem(dbStateName, newState);
  state = newState;
  return state;
};

StateService.addJob = (job) => {
  state.jobs.push(job);
  StateService.setState(state);
};

StateService.cancelAndRemoveAllJobs = () => {
  _(state.jobs).forEach(j => j.cancel());
  state.jobs = [];
  StateService.setState(state);
};

module.exports = StateService;