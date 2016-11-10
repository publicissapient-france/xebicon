'use strict';

global.settings = require('./settings');

var http = require('http');
var https = require('https');
var fs = require('fs');
var bodyParser = require('body-parser');

var express = require('express');
var app = exports.app = express();

app.use(express.static('public'));
app.use(bodyParser.urlencoded());
app.use(bodyParser.json());

try {
    var privateKey = fs.readFileSync(settings.privkey_path, 'utf8');
    var certificate = fs.readFileSync(settings.fullchain_path, 'utf8');

    var credentials = {key: privateKey, cert: certificate};

    var httpsServer = https.createServer(credentials, app);
    if (process.env["PORT"]) {
        httpsServer.listen(process.env["PORT_SSL"]);
    } else {
        httpsServer.listen(443);
    }
} catch (e) {
    console.log("Cannot start HTTPS", e)
}

var httpServer = http.createServer(app);
if (process.env["PORT"]) {
    httpServer.listen(process.env["PORT"]);
} else {
    httpServer.listen(80);
}

