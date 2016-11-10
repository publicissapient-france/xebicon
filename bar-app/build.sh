#!/usr/bin/env bash
export GOOS=linux
export GOARCH=amd64
export GOPATH=$(pwd):$(pwd)/work
mkdir -p bin
go build -o bin/logbook github.com/cedbossneo/logbook
docker build -t cedbossneo/logbook .
docker push cedbossneo/logbook
