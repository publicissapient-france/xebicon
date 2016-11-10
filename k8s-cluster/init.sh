#!/bin/bash
sed s/\\\$PEER\\\$/$1/g nuc.yaml > /usr/share/oem/cloud-config.yml
for i in fleet weave weaveproxy etcd2
do
  sudo systemctl stop $i
done
sudo rm -Rf /etc/kubernetes
docker rm -f $(docker ps -aq)
cd /opt/pidalio
sudo git reset --hard HEAD
sudo git pull
sudo coreos-cloudinit --from-file=/usr/share/oem/cloud-config.yml
sleep 10
sudo systemctl restart weave-network.target
cd /home/core