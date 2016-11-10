package types

func BuildDrinkWalletEvent(user string, amount int) Event {
	return Event{
		Type: "DRINK_WALLET",
		Payload: map[string]interface{}{
			"user": user,
			"value": amount,
		},
	}
}

func BuildDrinkStateEvent(trains [2]map[string]interface{}) Event {
	return Event{
		Type: "SHOP_STATE",
		Payload: trains,
	}
}

func BuildDrinkBuyObject(event Event) DrinkBuy{
	payload := event.Payload.(map[string]interface{})
	return DrinkBuy{
		User: User{
			UserId: payload["user"].(string),
		},
		Type: payload["type"].(string),
		Price: int(payload["price"].(float64)),
	}
}

func BuildRegisterWalletObject(event Event) RegisterWallet {
	payload := event.Payload.(map[string]interface{})
	return RegisterWallet{
		User{
			UserId: payload["user"].(string),
			Train: int(payload["train"].(float64)),
		},
	}
}