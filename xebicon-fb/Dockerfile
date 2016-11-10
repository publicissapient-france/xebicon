FROM node:6.4

MAINTAINER Pablo Lopez <plopez@xebia.fr>

RUN mkdir /xebicon
WORKDIR /xebicon

COPY package.json package.json
RUN npm install

COPY app app

EXPOSE 3000

CMD [ "npm", "start" ]
