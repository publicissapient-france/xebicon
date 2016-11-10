FROM node:5.12


# Create app directory
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app/server


# Install dependencies
COPY front/package.json /usr/src/app/front/package.json
COPY server/package.json /usr/src/app/server/package.json
RUN cd /usr/src/app/front && npm install
RUN cd /usr/src/app/server && npm install

COPY front /usr/src/app/front
COPY server /usr/src/app/server


RUN cd /usr/src/app/front && npm run build:prod

ENV NODE_ENV=production
ENV DEBUG=dashboard-backend:*

EXPOSE 8001
CMD [ "npm", "start" ]
