const path = require('path');
const express = require('express');
const bodyParser = require('body-parser');
const webpack = require('webpack');

const config = process.env.NODE_ENV === 'production' ? require('./webpack.config.prod') : require('./webpack.config.dev');
const port = process.env.PORT || 3000;

const sse = require('./middleware/sse');

const app = express();
const compiler = webpack(config);

const events = require('./api/events');

app.use(require('webpack-dev-middleware')(compiler, {
  noInfo: true,
  publicPath: config.output.publicPath
}));

app.use(require('webpack-hot-middleware')(compiler));

app.use(bodyParser.json());

app.use(sse); // load sse middleware

app.use('/api/events', events);

app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(port, err => {
  if (err) {
    console.log(err);
    return;
  }

  console.log(`Listening at http://localhost:${port}`);
});
