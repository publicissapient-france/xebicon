'use strict';

// read settings
global.settings = require('./settings');
global.labels = require('./labels');

var exports = module.exports = {}

const messenger = require('./messenger.js'),
    rabbit = require('./rabbit.js')

const logger = require('winston')

const handleHighAvailabilityStart = exports.handleHighAvailabilityStart = (sender) => {
    messenger.sendGenericMessage(null, {'text': labels.stations[0].highAvailabilityStartText}, 1)
    return sendWineList(sender)
}

const sendWineList = (sender) => {
    return messenger.sendGenericMessage(sender, {
        "attachment": {
            "type": "template",
            "payload": {
                "template_type": "generic",
                "elements": [
                    {
                        "title": "Pauillac",
                        "image_url": "https://s3-eu-west-1.amazonaws.com/xebicon-images/pauillac.jpg",
                        "subtitle": "Château Latour",
                        "buttons": [
                            {
                                "type": "postback",
                                "payload": "AVAILABILITY#PAUILLAC",
                                "title": "1920"
                            }
                        ]
                    },
                    {
                        "title": "Pessac",
                        "image_url": "https://s3-eu-west-1.amazonaws.com/xebicon-images/pessac.jpg",
                        "subtitle": "Château Haut-Brion",
                        "buttons": [
                            {
                                "type": "postback",
                                "payload": "AVAILABILITY#PESSAC",
                                "title": "1961"
                            }
                        ]
                    },
                    {
                        "title": "Margaux",
                        "image_url": "https://s3-eu-west-1.amazonaws.com/xebicon-images/margaux.jpg",
                        "subtitle": "Château Margaux",
                        "buttons": [
                            {
                                "type": "postback",
                                "payload": "AVAILABILITY#MARGAUX",
                                "title": "1990"
                            }
                        ]
                    }
                ]
            }
        }
    }, 1)
}

const handleHighAvailabilityEnd = exports.handleHighAvailabilityEnd = (sender) => {
    return messenger.sendGenericMessage(null, {'text': labels.stations[0].highAvailabilityStopText}, 1);
}

String.prototype.capitalizeFirstLetter = function () {
    return this.charAt(0).toUpperCase() + this.slice(1);
}

const handleChoice = exports.handleChoice = (choice, sender) => {
    rabbit.sendMessageToAmqp(JSON.stringify({
            type: "BUY",
            payload: {
                type: choice.toLowerCase()
            }
        })
    )

    return messenger.sendGenericMessage(sender, {'text': "Votre commande a été prise en compte. Très bon choix que ce " + choice.toLowerCase().capitalizeFirstLetter() + "."}, null)
        .then(() => {
            messenger.sendGenericMessage(sender, {'text': "Voulez vous commander de nouveau ?"}, null)
                .then(() => {
                    sendWineList(sender)
                })
        })
}
