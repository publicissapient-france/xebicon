.PHONY: build

all: build image

build:
	docker run --rm -v $(PWD):/usr/src/reloader -w /usr/src/reloader \
     			 	-e CGO_ENABLED=0 -e GOOS=linux -e GOARCH=amd64 \
     			 	golang:1.6 bash -c "go get -v -d && go build -v"

image:
	docker build -t xebiafrance/xebicon-reloader .

push:
	docker push xebiafrance/xebicon-reloader
