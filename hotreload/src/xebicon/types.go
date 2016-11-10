package xebicon

const LIGHT_STATE_BODY = `{
  "type": "LIGHT_STATE",
  "payload": {
    "id": %d,
    "value": %.2f
  }
}`

const START_EVENT = `{
	"type": "HOT_DEPLOYMENT_START"
}`

const STOP_EVENT = `{
	"type": "HOT_DEPLOYMENT_END"
}`

const SERVICE_DEPLOYMENT = `{
	"type": "SERVICE_DEPLOYMENT",
  	"payload": {
    		"value": "%s",
    		"id": %d,
    		"version": "%s"
  }
}`

type LightPayload struct {
	Id      int     `json:"id"`
	Value   float32 `json:"value"`
	Version string  `json:"version"`
}

type Event struct {
	Type    string      `json:"type"`
	Payload interface{} `json:"payload"`
}

type LightConfig struct {
	LightId     int    `env:"LIGHT_ID"`
	RabbitmqUrl string `env:"RABBITMQ_URL" envDefault:"amqp://localhost"`
}
