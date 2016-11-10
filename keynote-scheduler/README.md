# Xebicon 2016 :  Keynote's scheduler


## Dependencies

Node, Npm

## Install, Build & Run

```
npm i && npm run dev
```

### Docker

Build the image

```
docker build -t xebiafrance/xebicon-scheduler .
```

Run
```
docker run -p 3000:3000 -e "RABBIT_EXCHANGE=xebiconTest" xebiafrance/xebicon-scheduler
```

Deploy
```
docker push xebiafrance/xebicon-scheduler
```