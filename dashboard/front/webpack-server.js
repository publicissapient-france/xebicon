var webpack = require('webpack');
var WebpackDevServer = require('webpack-dev-server');
var config = require('./webpack.config');

const PORT = process.env.PORT || 8000;

const front_server = new WebpackDevServer(webpack(config), {
  proxy: {
    "/api/*" : "http://localhost:8001" // <- backend
  }
});

front_server.listen(PORT, 'localhost');

// export default (PORT) => {
//   const server = new WebpackDevServer(wepback(config), {
//     proxy: {
//       "/api/*" : "http://localhost:8001" // <- backend
//     }
//   });
//   server.listen(PORT, 'localhost');
// }
