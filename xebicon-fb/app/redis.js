'use strict';

var exports = module.exports = {}

global.settings = require('./settings');

const logger = require('winston'),
    Promise = require('bluebird'),
    redis = require('redis')


Promise.promisifyAll(redis.RedisClient.prototype);

const client = redis.createClient(6379, settings.redis_url, {no_ready_check: false});

const getUser = exports.getUser = (userId) => {
    logger.info("[getUser][in]", userId)
    return client.getAsync(userId).then((result) => {
        if (result) {
            console.log(result)
            var currentUser = JSON.parse(result)
            logger.info("[getUser] CurrentUser: ", currentUser)
            return new Promise((resolve, reject) => {
                logger.info("[getUser] Successfull")
                resolve(currentUser)
            })
        }
        return new Promise((resolve, reject) => {
            logger.info("[getUser] Failed")
            resolve(null)
        })
    })
}

const getRunningTrain = exports.getRunningTrain = () => {
    logger.info("[getRunningTrain][in]")
    return client.getAsync("runningTrain").then((result) => {
        if (result) {
            logger.info("[getRunningTrain]", result)
            return new Promise((resolve, reject) => {
                logger.info("[getRunningTrain] Successfull")
                resolve(result)
            })
        }
        return new Promise((resolve, reject) => {
            logger.info("[getRunningTrain] Failed")
            resolve(null)
        })
    })
}

const saveRunningTrain = exports.saveRunningTrain = (trainId) => {
    logger.info("[saveRunningTrain][in]", "trainId", trainId)
    return client.setAsync("runningTrain", trainId).then(function (result) {
        logger.info("[saveRunningTrain][out]", result)
    });
}

const getUsersByStation = exports.getUsersByStation = (stationId) => {
    logger.info("[getUsersByStation][in]", stationId)
    return client.lrangeAsync("station" + stationId, 0, -1).then((userIdList) => {
        if (userIdList) {
            logger.info("[getUsersByStation] userIdList length", userIdList.length)
            return new Promise((resolve, reject) => {
                logger.info("[getUsersByStation] Successfull")
                resolve(userIdList)
            })
        }
        return new Promise((resolve, reject) => {
            logger.info("[getUsersByStation] Failed")
            resolve(null)
        })
    })
}

const saveUser = exports.saveUser = (userId, user) => {
    logger.info("[saveUser][in]", userId, user)
    var userAsString = JSON.stringify(user)
    logger.info("[saveUser][in]", "Saving:", userAsString)
    return client.setAsync(userId, userAsString).then(function (result) {
        logger.info("[saveUser][out]", result)
    });
}

const addUserToStation = exports.addUserToStation = (stationId, userId) => {
    console.log("Pushing", stationId, userId)
    return client.lpushAsync("station" + stationId, userId).then(function (result) {
        console.log(result); // returns OK
    });
}

const flushAll = exports.flushAll = () => {
    return client.flushallAsync().then(() => {
        client.setAsync("train1DepartureStart", 0)
        client.setAsync("train2DepartureStart", 0)
        client.setAsync("train1DepartureEnd", 0)
        client.setAsync("train2DepartureEnd", 0)
        client.setAsync("train1DescriptionStart", 0)
        client.setAsync("train2DescriptionStart", 0)
        return new Promise((resolve, reject) => {
            logger.debug("[flushAll] Flush redis")
            resolve()
        })
    })
}

const incrementCounterAndGet = exports.incrementCounterAndGet = (counter) => {
    return client.incrAsync(counter)
}

const getAllUsersId = exports.getAllUsersId = () => {
    return client.keysAsync("*").then((keys) => {
        return new Promise((resolve, reject) => {
            logger.info("[getAllUsersId] Successfull", keys)
            resolve(keys)
        })
    })
}

