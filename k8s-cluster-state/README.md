# run local rabbitmq
docker run -d -p 15672:15672 -p 5672:5672 --name rabbit-xebicon jbclaramonte/rabbitmq-testconfig

# build image and push it
mvn clean package docker:build -DskipTests=true -DpushImage

# add a label on a node
kubectl label nodes <node-name> <label-key>=<label-value>

# remove a label on a node
kubectl label nodes <node-name> <label-key>-