package xebicon

import (
	"github.com/streadway/amqp"
)

const XEBICON_EXCHANGE = "xebiconExchange"

func Publish(ch *amqp.Channel, body string) {
	err := ch.Publish(
		XEBICON_EXCHANGE, // exchange
		"",               // routing key
		false,            // mandatory
		false,            // immediate
		amqp.Publishing{
			ContentType: "text/plain",
			Body:        []byte(body),
		})
	FailOnError(err, "Failed to publish a message")
}

func ExchangeDeclare(ch *amqp.Channel) {
	err := ch.ExchangeDeclare(
		XEBICON_EXCHANGE,
		"fanout",
		true,  // durable
		false, // auto-deleted
		false, // internal
		false, // no-wait
		nil,   // arguments
	)
	FailOnError(err, "Failed to declare an exchange")
}

func QueueInit(ch *amqp.Channel,name string) amqp.Queue {
	q, err := ch.QueueDeclare(
		name, // name
		false,              // durable
		false,              // delete when usused
		true,               // exclusive
		false,              // no-wait
		nil,                // arguments
	)
	FailOnError(err, "Failed to declare a queue")

	err = ch.QueueBind(
		q.Name,           // queue name
		"",               // routing key
		XEBICON_EXCHANGE, // exchange
		false,
		nil)
	FailOnError(err, "Failed to bind a queue")
	return q
}

func Consummes(ch *amqp.Channel,q amqp.Queue) <-chan amqp.Delivery {
	msgs, err := ch.Consume(
		q.Name,
		"",    // consumer
		true,  // auto-ack
		false, // exclusive
		false, // no-local
		false, // no-wait
		nil,   // args
	)
	FailOnError(err, "Failed to register a consumer")
	return msgs
}
