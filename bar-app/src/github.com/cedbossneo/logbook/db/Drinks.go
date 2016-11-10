package db

import (
	"github.com/cedbossneo/logbook/types"
	r "gopkg.in/dancannon/gorethink.v2"
	"log"
)

func (db *Database) DrinkBuy(event types.DrinkBuy) (types.Wallet, error) {
	wallet, err := db.GetWallet(event.UserId)
	if err != nil {
		return types.Wallet{}, err
	}
	ok, err := db.RemoveFromWallet(wallet, event.Price)
	if err != nil {
		log.Println("Unable to remove amount from wallet", event)
		return types.Wallet{}, err
	}
	if ok {
		wallet.Amount = wallet.Amount - event.Price
		event.Train = wallet.Train
		_, err = r.Table("drinks").Insert(event).RunWrite(db.Session)
		if err != nil {
			log.Println("Unable to insert drink", event)
			return types.Wallet{}, err
		}
	}
	return wallet, nil
}

func (db *Database) GetDrinksGroupedByTrains() ([2]map[string]interface{}, error) {
	itemsByTrain := [...]map[string]interface{}{
		map[string]interface{}{
			"train": 1,
			"items": []map[string]interface{}{},
			"totalPrice": 0,
		},
		map[string]interface{}{
			"train": 2,
			"items": []map[string]interface{}{},
			"totalPrice": 0,
		},
	}
	cursor, err := r.Table("drinks").Group("train", "type").Count().Run(db.Session)
	if err != nil {
		log.Println("Unable to group drinks", err)
		return itemsByTrain, err
	}
	var group map[string]interface{}
	for cursor.Next(&group) {
		grp := group["group"].([]interface{})
		drinkTrain := int(grp[0].(float64))
		drinkType := grp[1].(string)
		drinkNumber := int(group["reduction"].(float64))
		if drinkTrain != 1 && drinkTrain != 2 {
			continue
		}
		itemsByTrain[drinkTrain - 1]["items"] = append(itemsByTrain[drinkTrain - 1]["items"].([]map[string]interface{}), map[string]interface{}{
			"type": drinkType,
			"quantity": drinkNumber,
		})
	}
	totals, err := r.Table("drinks").Group("train").Sum("price").Run(db.Session)
	if err != nil {
		log.Println("Unable to get totals", err)
		return itemsByTrain, err
	}
	for totals.Next(&group) {
		train := int(group["group"].(float64))
		total := int(group["reduction"].(float64))
		itemsByTrain[train - 1]["totalPrice"] = total
	}
	return itemsByTrain, nil
}