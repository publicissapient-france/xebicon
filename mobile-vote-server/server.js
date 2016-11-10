'use strict';

const bodyParser = require('body-parser');
const express = require('express');
const cluster = require('cluster');
const cfenv = require('cfenv');
const os = require('os');

const app = express();

const rabbitmqService = require('./app/rabbitmqService');
const databaseService = require('./app/databaseService');
const notificationService = require('./app/notificationService');

let appEnv = cfenv.getAppEnv();

app.use(bodyParser.json());

app.use(function(req, res, next) {
  res.setHeader('Content-Type', 'application/json');

  next();
});

app.get('/state/:token?', function(req, res) {
  databaseService.keynoteState(req, res).then(function(keynoteState) {
    return res.send(keynoteState);
  }, function(error) {
    console.log('[Keynote][State] error', error);

    return res.sendStatus(404);
  });
});

app.post('/register', function(req, res) {
  console.log(req.body);

  return databaseService.saveUser(req, res);
});

app.post('/vote/station', function(req, res) {
  console.log('TEST');

  return rabbitmqService.publishVote(req, res);
});

app.post('/purchase', function(req, res) {
  return rabbitmqService.purchase(req, res);
});

appEnv = {
  port: 3001
};

app.listen(appEnv.port, '0.0.0.0', function() {
    // print a message when the server starts listening
    console.log('[SERVER STARTING ON PORT ' + appEnv.port + ']');
  });

rabbitmqService.connect();

process.on('uncaughtException', function(err) {
  let ERROR_NOREFORK = 5001;

  console.log('uncaughtException', err.message);
  console.log(JSON.stringify(err.stack));

  process.exit(ERROR_NOREFORK);
});
