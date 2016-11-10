package main

import (
	"flag"
	"github.com/cedbossneo/logbook/db"
	"github.com/cedbossneo/logbook/queue"
	"github.com/cedbossneo/logbook/api"
)

var (
	rabbitmqURI = flag.String("rabbitmq-uri", "amqp://guest:guest@rabbitmq-service:5672/", "AMQP URI")
	rethinkURI     = flag.String("rethink-uri", "rethinkdb-driver:28015", "RethinkDB URI")
)

func init() {
	flag.Parse()
}

func main() {
	db := db.Connect(*rethinkURI, "logbook")
	channel := queue.ConnectRabbitMQ(db, *rabbitmqURI)
	api.Router(channel, db).Run(":3000")
}


