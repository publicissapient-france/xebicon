package queue

import (
	"log"
	"github.com/streadway/amqp"
	"github.com/cedbossneo/logbook/db"
	"github.com/cedbossneo/logbook/types"
)

var EXCHANGE = "xebiconExchange"
var QUEUE = "logbook"

func failOnError(err error, msg string) {
	if err != nil {
		log.Fatalf("%s: %s", msg, err)
	}
}

func ConnectRabbitMQ(db *db.Database, rabbitmqURI string) *amqp.Channel {
	conn, err := amqp.Dial(rabbitmqURI)
	failOnError(err, "Failed to connect to RabbitMQ")

	ch, err := conn.Channel()
	failOnError(err, "Failed to open channel")

	q, err := ch.QueueDeclare(
		QUEUE, // name
		false,   // durable
		true,   // delete when usused
		false,   // exclusive
		false,   // no-wait
		nil,     // arguments
	)
	failOnError(err, "Failed to declare queue")

	ch.QueueBind(
		q.Name, // name
		"", //key
		EXCHANGE, // exchange
		false, //no-wait
		nil, // arguments
	)
	failOnError(err, "Failed to bind queue")

	msgs, err := ch.Consume(
		q.Name, // queue
		"",     // consumer
		false,   // auto-ack
		false,  // exclusive
		false,  // no-local
		false,  // no-wait
		nil,    // args
	)
	failOnError(err, "Failed to register consumer")

	go func() {
		for d := range msgs {
			handle(db, d, ch)
		}
	}()

	return ch
}

func handle(db *db.Database, d amqp.Delivery, ch *amqp.Channel) {
	event, err := types.UnmarshalEvent(d.Body)
	if err != nil {
		log.Printf("Unable to unmarshal event %s", d.Body)
		d.Nack(false, false)
		return
	}
	json, _ := event.MarshallJSON()
	log.Print(string(json))
	switch event.Type {
		case "DRINK_BUY":
			if err := handleDrinkBuy(event, db, ch); err != nil {
				log.Println("Could not buy drink", err)
				d.Nack(false, false)
			}
			break;
		case "DRINK_REGISTER_WALLET":
			if err := handleRegisterWallet(event, db, ch); err != nil {
				log.Println("Could not register wallet", err)
				d.Nack(false, false)
			}
			break;
		default:
			d.Nack(false, false)
			return;
	}
	d.Ack(false)
}

func handleDrinkBuy(event types.Event, db *db.Database, ch *amqp.Channel) error {
	wallet, err := db.DrinkBuy(types.BuildDrinkBuyObject(event))
	if err != nil {
		return err
	}
	walletState, err := types.BuildDrinkWalletEvent(wallet.UserId, wallet.Amount).MarshallJSON()
	if err != nil {
		log.Print("Unable to marshall event")
		return err
	}
	ch.Publish(EXCHANGE, "", false, false, amqp.Publishing{
		Body: walletState,
	})
	drinks, err := db.GetDrinksGroupedByTrains()
	if err != nil {
		log.Print("Unable to retrieve drinks")
		return err
	}
	allState, err := types.BuildDrinkStateEvent(drinks).MarshallJSON()
	if err != nil {
		log.Print("Unable to marshall event")
		return err
	}
	ch.Publish(EXCHANGE, "", false, false, amqp.Publishing{
		Body: allState,
	})
	return nil
}

func handleRegisterWallet(event types.Event, db *db.Database, ch *amqp.Channel) error {
	registerWalletEvent := types.BuildRegisterWalletObject(event)
	walletExist, err := db.WalletExist(registerWalletEvent.UserId)
	if err != nil {
		log.Print("Unable to test that wallet already exist")
		return err
	}
	if walletExist {
		log.Print("Wallet already exist")
		return err
	}
	wallet, err := db.CreateWallet(registerWalletEvent.User)
	if err != nil {
		return err
	}
	walletState, err := types.BuildDrinkWalletEvent(wallet.UserId, wallet.Amount).MarshallJSON()
	if err != nil {
		log.Print("Unable to marshall event")
		return err
	}
	ch.Publish(EXCHANGE, "", false, false, amqp.Publishing{
		Body: walletState,
	})
	return nil
}
