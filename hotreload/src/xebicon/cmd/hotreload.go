package main

import (
	"xebicon"
	"encoding/json"
	"github.com/caarlos0/env"
	"github.com/streadway/amqp"
	"log"
	"time"
	"github.com/docker/engine-api/client"
	"golang.org/x/net/context"
	docker_types "github.com/docker/engine-api/types"
	"github.com/docker/engine-api/types/container"
	"github.com/docker/engine-api/types/network"
	"fmt"
	"os"
)

type Config struct {
	RabbitmqUrl      string `env:"RABBITMQ_URL" envDefault:"amqp://localhost"`
	LightImage       string `env:"LIGHT_IMAGE" envDefault:"xebiafrance/xebicon-light"`
	InstanceCount    int `env:"INSTANCE_COUNT" envDefault:"8"`
	RedeployPause    string `env:"REDEPLOY_PAUSE" envDefault:"1s"`
	RedeployDuration string `env:"REDEPLOY_DURATION" envDefault:"3s"`
}

var (
	ch           *amqp.Channel
	chanScenario = make(chan bool)
	docker *client.Client
	cfg = Config{}
	containerInfo docker_types.ContainerJSON
	useDocker = true
)

func main() {
	env.Parse(&cfg)

	var err error;
	docker, err = client.NewEnvClient()
	if err != nil {
		log.Println("Cannot use docker")
		useDocker = false
	}

	if useDocker {
		// Read info from itself
		hostname, _ := os.Hostname()
		containerInfo, err = docker.ContainerInspect(context.Background(), hostname)
		pullLightImages()
	}

	conn, err := amqp.Dial(cfg.RabbitmqUrl)
	xebicon.FailOnError(err, "Failed to connect to RabbitMQ")
	defer conn.Close()

	ch, err = conn.Channel()
	xebicon.FailOnError(err, "Failed to open a channel")
	defer ch.Close()

	xebicon.ExchangeDeclare(ch)

	q := xebicon.QueueInit(ch,"hotreload_consumer")

	msgs := xebicon.Consummes(ch,q)

	go func() {
		for d := range msgs {
			var f xebicon.Event
			err := json.Unmarshal(d.Body, &f)
			if err != nil {
				xebicon.FailOnError(err, "Failed to consummes")
			} else {
				if f.Type == "KEYNOTE_STATE" {
					value := f.Payload.(map[string]interface{})["value"]
					if value == "HOT_DEPLOYMENT_START" {
						init_services()
					} else if value == "HOT_DEPLOYMENT_END" {
						undeployAll()
					}
				} else if f.Type == "HOT_DEPLOYMENT_PUSH_BUTTON" {
					start_deploy()
				} else {
					log.Printf("Receive %+v", f)
				}
			}

		}
	}()

	forever := make(chan bool)
	log.Printf("Reloader started")
	<-forever
}

func deploy(instance int, version string) {

	if ! useDocker {
		log.Println("Cannot use docker to deploy. Stop")
		return
	}
	xebicon.Publish(ch, fmt.Sprintf(xebicon.SERVICE_DEPLOYMENT, "START", instance, "V" + version))
	undeploy(instance)


	log.Printf("Deploy instance %d version %s", instance, version)

	config := &container.Config{
		Image: cfg.LightImage + ":" + version,
		Env: []string{
			"RABBITMQ_URL=" + cfg.RabbitmqUrl,
			fmt.Sprintf("LIGHT_ID=%d", instance),
		},
	}

	hostConfig := &container.HostConfig{}

	networkConfig := &network.NetworkingConfig{
		EndpointsConfig: containerInfo.NetworkSettings.Networks,
	}
	creation, err := docker.ContainerCreate(context.Background(), config, hostConfig, networkConfig, instanceName(instance))
	if err == nil {
		docker.ContainerStart(context.Background(), creation.ID, docker_types.ContainerStartOptions{creation.ID})
	} else {
		xebicon.FailOnError(err, "Failed create instance")
	}
	duration,_ := time.ParseDuration(cfg.RedeployDuration)
	time.Sleep(duration)
	xebicon.Publish(ch, fmt.Sprintf(xebicon.SERVICE_DEPLOYMENT, "STOP", instance, "V" + version))

}

func undeploy(instance int) {
	log.Println("Undeploy instance ", instance)
	// Undeploy instance
	options := docker_types.ContainerRemoveOptions{
		RemoveVolumes: true,
		Force: true,
	}

	err := docker.ContainerRemove(context.Background(), instanceName(instance), options)
	if err != nil {
		xebicon.FailOnError(err, "Failed remove instance")
		return
	}
}

func instanceName(instance int) string {
	return fmt.Sprintf("xebicon_light_%d", instance)
}

func start_deploy() {
	log.Printf("Start deployment")
	for i := 1; i < cfg.InstanceCount; i++ {
		duration,_ := time.ParseDuration(cfg.RedeployPause)
		time.Sleep(duration)
		deploy(i, "2")
	}
}

func pullLightImages() {

	docker.ImagePull(context.Background(),
		cfg.LightImage + ":1",
		docker_types.ImagePullOptions{
			false,
			"",
			handlePrivileges},
	)

	docker.ImagePull(context.Background(),
		cfg.LightImage + ":2",
		docker_types.ImagePullOptions{
			false,
			"",
			handlePrivileges},
	)
}

func
handlePrivileges() (string, error) {
	return "", nil
}

func init_services() {

	for i := 1; i < cfg.InstanceCount; i++ {
		go func(instance int) {
			deploy(instance, "1")
		}(i)
	}
}

func undeployAll() {
	for i := 1; i < cfg.InstanceCount; i++ {
		go func(instance int) {
			undeploy(instance)
		}(i)
	}
}