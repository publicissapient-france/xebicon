package main

import (
	"github.com/streadway/amqp"
	"math/rand"
	"github.com/caarlos0/env"
	"fmt"
	"time"
	"xebicon"
	"log"
)

func main() {
	cfg := xebicon.LightConfig{}
	env.Parse(&cfg)
	log.Println(cfg.RabbitmqUrl)

	conn, _ := amqp.Dial(cfg.RabbitmqUrl)
	defer conn.Close()

	ch, _ := conn.Channel()
	defer ch.Close()

	xebicon.ExchangeDeclare(ch)


	var period int
	for {
		period = 100 + (rand.Int() % 100)
		timer := time.NewTimer( time.Duration(period) * time.Millisecond)
		<-timer.C
		message := fmt.Sprintf(xebicon.LIGHT_STATE_BODY, cfg.LightId, xebicon.RandomLight())
		log.Println("Send light status")
		xebicon.Publish(ch, message)
	}

}

