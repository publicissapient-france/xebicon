# XebiCon 2016 - Keynote Dashboard

## Prerequisites
* [Git](http://git-scm.com/)
* [Node.js](http://nodejs.org/) (v5.12.x with NPM)
* [RabbitMQ](https://hub.docker.com/_/rabbitmq/) (docker-container)


## Running / Development

* `git clone <repository-url>` this repository
* change into the new directory
* Front:
** `cd dashboard`
** `npm install`
** `./start-frontend.sh`
* Server:
** `cd server`
** `npm install`
** `./start-server.sh`
* Visit [localhost:8000](http://localhost:8000)

## Production

* TODO


## You can use local RabbitMq
* `docker run -d --hostname my-rabbit -e RABBITMQ_DEFAULT_USER=user -e RABBITMQ_DEFAULT_PASS=password --name rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management`
* export RABBIT_HOST=<rabbit ip>

## Events

[Events Exchange Schema](https://drive.google.com/open?id=0B7TxBU0Irvk9bUx0QWVET1hVeUk)

## Docker

Build the image
```
docker build -t xebiafrance/xebicon-dashboard .
```

Run
```
docker run -p 8001:8001 -e "RABBIT_EXCHANGE=xebiconTest" xebiafrance/xebicon-dashboard
```
