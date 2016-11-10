package types

import "encoding/json"

type Event struct {
	Type string `json:"type" gorethink:"type"`
	Payload interface{} `json:"payload" gorethink:"payload"`
}

type User struct {
	UserId    string `json:"user" gorethink:"user"`
	Train 	  int `json:"train" gorethink:"train"`
}

type DrinkBuy struct {
	User
	Type string `json:"type" gorethink:"type"`
	Price int `json:"price" gorethink:"price"`
}

type RegisterWallet struct {
	User
}

type Wallet struct {
	User
	Amount int `json:"amount" gorethink:"amount"`
}

func (event Event) MarshallJSON() ([]byte, error) {
	return json.Marshal(event)
}

func UnmarshalEvent(data []byte) (Event, error) {
	var event Event
	if err := json.Unmarshal(data, &event); err != nil {
		return Event{}, err
	}
	return event, nil
}
