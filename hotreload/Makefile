.PHONY: build

all: hotreload light

GOPATH=$(CURDIR)
export GOPATH

# Hot Reload
hotreload: build-hotreload image-hotreload

deps:
	test -f $(CURDIR)/bin/govendor || go get -u github.com/kardianos/govendor
	cd src/xebicon && $(GOPATH)/bin/govendor sync

build-hotreload: deps
	cd src/xebicon && GOARCH=amd64 GOOS=linux $(GOPATH)/bin/govendor build -o $(CURDIR)/dist/hotreload/hotreload cmd/hotreload.go

image-hotreload:
	docker build -t xebiafrance/xebicon-hotreload $(CURDIR)/dist/hotreload/

light: build-light image-light

build-light:
	cd src/xebicon && GOARCH=amd64 GOOS=linux $(GOPATH)/bin/govendor build -o $(CURDIR)/dist/light/light cmd/light.go

image-light:
	docker build -t xebiafrance/xebicon-light:1 $(CURDIR)/dist/light/




light2: build-light2 image-light2

build-light2:
	cd src/xebicon && GOARCH=amd64 GOOS=linux $(GOPATH)/bin/govendor build -o $(CURDIR)/dist/light/light cmd/light2.go

image-light2:
	docker build -t xebiafrance/xebicon-light:2 $(CURDIR)/dist/light/