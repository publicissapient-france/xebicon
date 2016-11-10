package api

import (
	"github.com/gin-gonic/gin"
	"net/http"
	"encoding/json"
	"github.com/cedbossneo/logbook/db"
	"github.com/cedbossneo/logbook/types"
	"github.com/streadway/amqp"
	"github.com/cedbossneo/logbook/queue"
)

func Router(channel *amqp.Channel, db *db.Database) (*gin.Engine){
	router := gin.Default()
	router.GET("/wallets", func(c *gin.Context) {
		events, err := db.GetWallets()
		if (err != nil) {
			c.JSON(http.StatusInternalServerError, gin.H{"message": "Unable to retrieve wallets"})
			return
		}
		result := gin.H{
			"wallets": events,
		}
		c.JSON(http.StatusOK, result)
	})
	router.POST("/buy", func(c *gin.Context) {
		var event types.Event = types.Event{
			Type: "DRINK_BUY",
			Payload: map[string]interface{}{
				"user": "toto4",
				"type": "water",
				"price": 1,
			},
		}
		body, err := json.Marshal(event)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"message": "Unable to marshall event"})
			return
		}
		channel.Publish(queue.EXCHANGE, "", false, false, amqp.Publishing{
			Body: body,
		})
		c.JSON(http.StatusOK, gin.H{
			"message": body,
		})
	})
	router.POST("/register", func(c *gin.Context) {
		var event types.Event = types.Event{
			Type: "DRINK_REGISTER_WALLET",
			Payload: map[string]interface{}{
				"user": "toto4",
				"train": 2,
			},
		}
		body, err := json.Marshal(event)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"message": "Unable to marshall event"})
			return
		}
		channel.Publish(queue.EXCHANGE, "", false, false, amqp.Publishing{
			Body: body,
		})
		c.JSON(http.StatusOK, gin.H{
			"message": body,
		})
	})
	return router
}