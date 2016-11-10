# Twitter Bot for Xebicon

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

`docker build -t <your_username>/xebicon-twitter-bot .`

### Run

```
$ docker run \
   -d \
   -e "NODE_ENV=production" \
   -m "300M" --memory-swap "1G" \
   --name "xebicon-twitter-bot" \
   <your_username>/xebicon-twitter-bot
```
