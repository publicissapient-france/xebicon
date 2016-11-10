GOOS := darwin
GOARCH := amd64
GOPATH := $(shell pwd)
LGOBIN := $(shell pwd)/bin
ENVS := GOOS=$(GOOS) GOARCH=$(GOARCH) GOPATH=$(GOPATH) LGOBIN=$(LGOBIN)
.PHONY: logbook deploy glide update_deps

default: logbook

logbook:
	$(ENVS) go build -o $(LGOBIN)/logbook github.com/cedbossneo/logbook

deploy:
	docker build -t cedbossneo/logbook .

push:
	docker push cedbossneo/logbook

deps:
	mkdir -p $(GOPATH)/bin
	if [ -f $(LGOBIN)/glide ] ; \
	then \
		echo "Glide already installed" ; \
	else \
		echo "Glide not installed, downloading" ; \
		curl -s -L https://github.com/Masterminds/glide/releases/download/v0.11.1/glide-v0.11.1-$(GOOS)-$(GOARCH).tar.gz | tar -xz --strip=1 && mv ./glide bin/glide ; \
	fi ; \
	cd src/github.com/cedbossneo/logbook; \
	$(ENVS) $(LGOBIN)/glide install; \
	cd ../../../../

update_deps:
	cd src/github.com/cedbossneo/logbook; \
	$(ENVS) $(LGOBIN)/glide update; \
	cd ../../../../
