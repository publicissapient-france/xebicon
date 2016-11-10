'use strict';

const firebase = require('firebase');
const trains = require('./trains');
const _ = require('underscore');
const space = process.env.NODE_ENV == 'production' ? 'votespace-2e76d' : 'votespace-dev';
var i18n = require('./i18n.js');

const databaseURL = 'https://' + space + '.firebaseio.com';

const config = {
  databaseURL: databaseURL
};

firebase.initializeApp(config);

const database = firebase.database();
const votesRef = database.ref(process.env.FIREBASE_VOTE || 'votes');
const keynoteStateRef = database.ref('keynoteState');

var DatabaseService = function DatabaseService() {
  return this;
};

console.log('[Database] Using ' + databaseURL);

DatabaseService.prototype.keynoteState = function keynoteState(req, res) {

  if (req && req.params.token) {
    return readCurrentStateForToken(req.params.token);
  } else {
    return readCurrentGlobalState();
  }
};

var readCurrentStateForToken = function readCurrentStateForToken(token) {
  return votesRef
    .once('value')
    .then(function (snapshot) {
      const votes = snapshot.val();
      if (votes[token]) {
        return votes[token].trainId;
      }
      return Promise.reject(new Error("Token not found"));
    })
    .then(function (trainId) {
      return readCurrentTrainState(trainId);
    });
};

var readCurrentTrainState = function readCurrentTrainState(trainId) {
  return keynoteStateRef.once('value')
    .then(function (snapshot) {
      const keynoteState = snapshot.val();
      if (!keynoteState || !keynoteState.trains) {
        return readCurrentGlobalState();
      }

      const trainState = keynoteState.trains[trainId];
      if (!trainState) {
        return readCurrentGlobalState();
      }
      return {"state": trainState, "message": i18n.translate_train(trainState, trains[trainId])};
    });
};

var readCurrentGlobalState = function readCurrentGlobalState() {
  return keynoteStateRef
    .once('value')
    .then(function (snapshot) {
      return snapshot.val();
    });
};

DatabaseService.prototype.saveKeynoteState = function saveKeynoteState(keynoteState) {
  const keynoteStatePayload = {
    state: keynoteState,
    message: i18n.translate(keynoteState),
    trains: null
  };

  keynoteStateRef.update(keynoteStatePayload);

  return keynoteStateRef.push();
};

DatabaseService.prototype.clearKeynote = function () {
  console.log("CLEARING");

  keynoteStateRef.set({});
  keynoteStateRef.push();

  readVotes(function (users) {
    _.each(_.keys(users), function (user) {
      users[user] = {trainId: 0}
    });

    votesRef.set(users);
    votesRef.push();
  });
}

DatabaseService.prototype.saveUser = function saveUser(req, res) {
  var vote = req.body;

  const votePayload = {};

  votePayload[vote.userId] = {trainId: 0};

  votesRef.update(votePayload);

  votesRef.push();

  return res.sendStatus(200);
};

DatabaseService.prototype.saveTrainVote = function saveTrainVote(vote) {

  const votePayload = {};

  votePayload[vote.userId] = {trainId: vote.trainId};

  votesRef.update(votePayload);

  votesRef.push();
};

DatabaseService.prototype.findTrainVotes = function findTrainVotes(trainId, success, error) {

  var succesCallback = function succesCallback(votes) {
    var userIds = [];

    const votesKeys = _.keys(votes);

    if (!trainId) {
      return success(votesKeys);
    }

    _.each(votesKeys, function (voteKey) {
      if (votes[voteKey].trainId != trainId) {
        return;
      }

      userIds.push(voteKey);
    });

    return success(userIds);
  };

  var errorCallback = function errorCallback(errorObject) {
    error(errorObject);
  };

  return readVotes(succesCallback, errorCallback);
};

DatabaseService.prototype.saveTrainState = function saveTrainPosition(trainId, state) {

  this.keynoteState().then(function (keynote) {
    const trainsPayload = keynote.trains || {};

    trainsPayload[trainId] = state;
    keynote.trains = trainsPayload;

    keynoteStateRef.update(keynote);
    keynoteStateRef.push();
  });
}

DatabaseService.prototype.findTrainInMove = function findTrainInMove(success, error) {
  this.keynoteState()
    .then(function (keynote) {
      _.each(_.keys(keynote.trains), function (trainId) {
        if (keynote.trains[trainId] == 'TRAIN_DEPARTURE_START') {
          return success(trainId);
        }
      });

      success(null);
    }, error);
};

var readVotes = function readVotes(success, error) {

  var succesCallback = function succesCallback(trains) {
    return success(trains.val());
  }

  var errorCallback = function errorCallback(errorObject) {
    return error(errorObject);
  }

  return votesRef.once('value', succesCallback, errorCallback);
};

module.exports = new DatabaseService();
