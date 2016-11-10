package main

import (
	"github.com/streadway/amqp"
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

	message := fmt.Sprintf(xebicon.LIGHT_STATE_BODY, cfg.LightId, xebicon.ConstantLight())
	log.Println("Send first light status")
	xebicon.Publish(ch, message)

	ticker := time.NewTicker(time.Second *3)
	for range ticker.C {
		message := fmt.Sprintf(xebicon.LIGHT_STATE_BODY, cfg.LightId, xebicon.ConstantLight())
		log.Println("Send light status")
		xebicon.Publish(ch, message)
	}

}


