'use strict';

// read settings
global.settings = require('./settings');

var unirest = require('unirest')
const logger = require('winston'),
    Promise = require('bluebird');

const state = require('./redis.js')

var exports = module.exports = {}

const sendGenericMessage = exports.sendGenericMessage = (sender, messageData, stationId) => {
    logger.info("[sendGenericMessage][in]")
    logger.info("[sendGenericMessage]", "Sender: ", sender, "MessageData: ", messageData, "StationId:", stationId)

    if (!sender && stationId) {
        logger.info("[sendGenericMessage]", "No sender", "Blast to station", stationId)
        state.getUsersByStation(stationId).then((stations) => {
            for (var idx in stations) {
                logger.info("[sendGenericMessage]", "Recursive call", "Sender", stations[idx])
                sendGenericMessage(stations[idx], messageData, null)
            }
        })
        return Promise.resolve()
    } else if (!sender) {
        logger.info("[sendGenericMessage]", "No sender, no station => blast all")
        state.getAllUsersId().then((registeredUsers) => {
            for (var idx in registeredUsers) {
                if (registeredUsers[idx] != "ElastiCacheMasterReplicationTimestamp" && !registeredUsers[idx].startsWith("station")) {
                    logger.info("[sendGenericMessage]", "Recursive call", "Sender", registeredUsers[idx])
                    sendGenericMessage(registeredUsers[idx], messageData, null)
                }
            }
        })
        return Promise.resolve()
    } else {
        return new Promise((resolve, reject) => {
            unirest.post('https://graph.facebook.com/v2.6/me/messages')
                .header('Accept', 'application/json')
                .header('Content-type', 'application/json')
                .query({access_token: settings.fb_page_token})
                .send({
                    recipient: {id: sender},
                    message: messageData,
                })
                .end((response) => {
                    logger.info("[sendGenericMessage][out]")
                    logger.info("[sendGenericMessage]", "Response: ", response.code)

                    if (response.body.error) {
                        logger.error("[sendGenericMessage]", "Error: ", response.body.error)
                    }

                    resolve()
                });
        })
    }
}

const validateWebhook = exports.validateWebhook = (event, callback) => {
    var queryParams = event.params.querystring;

    var rVerifyToken = queryParams['hub.verify_token']

    if (rVerifyToken === settings.fb_verify_token) {
        var challenge = queryParams['hub.challenge']
        callback(null, parseInt(challenge))
    } else {
        callback(null, 'Error, wrong validation token');
    }
}

const findUserDetails = exports.findUserDetails = (sender) => {
    return new Promise((resolve, reject) => {
        logger.info("[findUserDetails][in]", "sender", sender)

        unirest.get('https://graph.facebook.com/v2.6/' + sender)
            .header('Accept', 'application/json')
            .header('Content-type', 'application/json')
            .query({access_token: settings.fb_page_token})
            .send()
            .end((response) => {
                logger.info("[findUserDetails]", response.body)

                state.saveUser(sender, response.body).then(() => {
                    logger.info("[findUserDetails][out]", response.body)
                    logger.info("[findUserDetails][out]", response.body.first_name)
                    resolve(response.body)
                })
            });
    })
}