'use strict';

const Promise = require('bluebird');
const events = require('events');
const config = require('config');
const _ = require('lodash');
const Twit = require('twit');
const redis = require('redis');
const logger = require('./utils').logger;
const rabbitMQ = require('./utils').rabbitMQ;

const KEYNOTE_STATE = 'KEYNOTE_STATE';
const TWITTER = 'TWITTER';

class TwitterBot {
  constructor() {
    this.eventEmitter = new events.EventEmitter();
    this.botConfig = {
      consumerKey: null,
      consumerSecret: null,
      accessToken: null,
      accessTokenSecret: null,
      sender: null,
      timeline: {
        hashtags: []
      }
    };
    this.timelineIsOn = false;
    this.initialized = false;
    this.started = false;
    rabbitMQ.subscribeQueue(this.manageVote.bind(this));
  }

  on(...args) {
    this.eventEmitter.on(...args);
  }

  init(opt = {}) {
    _.extend(this.botConfig, config.get('twitterBot'), opt);
    this.twit = new Twit({
      'consumer_key': process.env.TWITTER_CONSUMER_KEY || this.botConfig.consumerKey,
      'consumer_secret': process.env.TWITTER_CONSUMER_SECRET || this.botConfig.consumerSecret,
      'access_token': process.env.TWITTER_TOKEN || this.botConfig.accessToken,
      'access_token_secret': process.env.TWITTER_TOKEN_SECRET || this.botConfig.accessTokenSecret
    });
    Promise.promisifyAll(this.twit);
    Promise.promisifyAll(redis.RedisClient.prototype);

    this.initialized = true;
    logger.info('Twitter bot initialized');
    return Promise.resolve();
  }

  start(opt) {
    if (!this.initialized) {
      this.init(opt);
    }
    if (this.started) {
      return Promise.resolve('Bot already started');
    }

    return Promise.resolve()
      .then(() => {
        this.started = true;
        logger.debug('twitter bot started');
        return `twitter bot started with pid = ${process.pid}`;
      })
      .catch(err => {
        logger.error(err);
      });
  }

  stop(cb) {
    this.started = false;
    logger.debug('twitter bot stopped');
    return Promise.resolve('twitter bot stopped')
      .asCallback(cb);
  }

  manageVote(msg) {
    return Promise.resolve()
      .then(() => {
        try {
          const action = JSON.parse(msg);
          if (action.type === KEYNOTE_STATE) {
            switch (action.payload.value) {
              case TWITTER:
                return this.startTimeline();
              default:
                return;
            }
          }
          return {};
        } catch (err) {
          return Promise.reject(err);
        }
      });
  }

  sendTimeline(sender, tweet) {
    return Promise.resolve()
      .then(() => {
        if (_.isEmpty(sender) || _.isEmpty(tweet)) {
          return;
        }
        const urls = _.get(tweet, 'entities.media');
        const url = urls ? _.get(_.first(urls), 'media_url_https') : null;
        const msg = {
          type: 'TWITTER_TIMELINE',
          payload: {
            id: tweet.id_str,
            imageUrl: url,
            message: tweet.text,
            username: tweet.user.screen_name,
            avatarUrl: tweet.user.profile_image_url_https,
          }
        };
        logger.debug('Send tweet to Rabbit MQ');
        return rabbitMQ.postMessage(msg);
      });
  }

  startTimeline() {
    if (this.timelineIsOn) {
      return;
    }

    this.timelineStream = this.twit.stream('statuses/filter', { track: this.botConfig.timeline.hashtags });
    this.timelineStream.on('tweet', tweet => {
      const sender = _.get(tweet, 'user.screen_name');
      logger.debug('received a tweet:', tweet.text, 'from', sender);
      return this.sendTimeline(sender, tweet);
    });
    this.timelineStream.on('error', error => {
      logger.error(error);
    });
    logger.info('Timeline Twitter started');
    this.timelineIsOn = true;
    return Promise.resolve('Timeline Twitter started');
  }

}

const twitterBot = new TwitterBot();

module.exports = twitterBot;
