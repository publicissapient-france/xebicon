# Mobile Vote Server

## Installation

```
git clone
npm i
```


## Run

### Start

`npm start`

## Docker

### Build

`docker build -t xebiafrance/mobile-vote-server .`

### Run

```
$ docker run \
   -d \
   -p 3000:3000 \
   -e "RABBIT_EXCHANGE=xebiconExchange" \
   xebiafrance/mobile-vote-server
```
