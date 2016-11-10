'use strict';

var localization = require('./localization.json');

module.exports = {
  translate: function (key) {
    return localization[key];
  },

  translate_train: function (key, train) {
    var str = localization[key];

    str = str.replace("${trainName}", train.station.capitalize());
    str = str.replace("${trainDescription}", train.description);

    return str;
  }
};

String.prototype.capitalize = function () {
  return this.charAt(0).toUpperCase() + this.slice(1);
};
