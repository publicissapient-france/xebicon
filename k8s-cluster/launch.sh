#!/bin/bash
rbd -m ceph-mon.ceph create redis --size 5G
rbd -m ceph-mon.ceph feature disable redis exclusive-lock
kubectl create -f redis.yaml -f k8s-cluster-state.yaml -f bar-app.yaml
kubectl expose deployment redis