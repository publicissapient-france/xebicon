module.exports = function (req, res, next) {
  res.sseSetup = function() {
    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      'Connection': 'keep-alive'
    });
    res.connection.setTimeout(0); // prevent nodejs 2 minutes timeout
  };

  res.sseSend = function(data) {
    res.write("data: " + JSON.stringify(data) + "\n\n");
  };

  next()
};