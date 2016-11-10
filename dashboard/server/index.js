import express from "express";
import path from "path";
import makeStore from "./src/store";
import socket, {onConnection} from "./src/socket";
import rabbitHandler from "./src/rabbit-handler";
import deb from "debug";
import redis from "redis";
import bluebird from "bluebird";

const debug = deb('dashboard-backend:server');
let store;

// Redis
bluebird.promisifyAll(redis.RedisClient.prototype);
bluebird.promisifyAll(redis.Multi.prototype);
const client = redis.createClient(process.env.REDIS_PORT || 6379, process.env.REDIS_HOST || 'localhost');

// Express
const app = express();
app.set('port', process.env.PORT || '8001');
app.use(express.static(path.join(__dirname, '../front/dist')));

// Listen for requests
const server = app.listen(app.get('port'), function () {
  const port = server.address().port;
  debug(`[*] Magic happens on port ${port}. To exit press CTRL+C `);
});

// Attach socket to server
const io = socket(server);

client.onAsync('connect')
  .then(db => client.getAsync('dashboardState'))
  .then(initState => {
    debug('Connected to redis');
    startRedux(JSON.parse(initState || '{}'));
  });

client.onAsync('error')
  .catch(err => {
    debug('[WARN] Cannot connect to redis => fallback to baseless mode');
    startRedux({});
  });


function startRedux(initState) {
  if (store) {
    return;
  }

  store = makeStore(io, initState, client);

  // Socket & Rabbit
  rabbitHandler.init(store);

  // On client connect
  io.on('connection', onConnection(rabbitHandler)(store));
}
