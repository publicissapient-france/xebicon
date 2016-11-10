package main

import (
	"fmt"
	"log"
	"net/http"
	"os/exec"
	"os"
	"io"
	"encoding/json"
	"github.com/docker/distribution/notifications"
)

var (
	execChan = make(chan int, 20)
)

func main() {
	composeFile := os.Args[1]
	http.HandleFunc("/deploy", func(w http.ResponseWriter, req *http.Request) {
		if req.Method != "POST" {
			http.Error(w, fmt.Sprintf("Ignoring request. Required method is \"POST\" but got \"%s\".\n", req.Method), http.StatusOK)
			return
		}

		if req.Body == nil {
			http.Error(w, "Ignoring request. Required non-empty request body.\n", http.StatusOK)
			return
		}

		contentType := req.Header.Get("Content-Type")
		if contentType != notifications.EventsMediaType {
			http.Error(w, fmt.Sprintf("Ignoring request. Required mimetype is \"%s\" but got \"%s\"\n", notifications.EventsMediaType, contentType), http.StatusOK)
			return
		}

		// Try to decode HTTP body as Docker notification envelope
		decoder := json.NewDecoder(req.Body)
		var envelope notifications.Envelope
		err := decoder.Decode(&envelope)
		if err != nil {
			http.Error(w, fmt.Sprintf("Failed to decode envelope: %s\n", err), http.StatusBadRequest)
			return
		}

		for _, event := range envelope.Events {

			// Handle all three cases: push, pull, and delete
			if event.Action == notifications.EventActionPush {
				log.Printf("Add event to channel: %s\n", event.Target.Repository)
				execChan <- 1
			}

		}
		io.WriteString(w, "ok")
	})

	go func() {
		for {
			select {
			case <-execChan:
				log.Println("Update docker-compose")
				execAndOutput("/usr/local/bin/docker-compose", "-f", composeFile, "pull")
				execAndOutput("/usr/local/bin/docker-compose", "-f", composeFile, "up", "-d")
			}
		}
	}()
	log.Fatal(http.ListenAndServe(":8080", nil))
	fmt.Println("Server stop")
}

func execAndOutput(command string, args ...string) {
	cmd := exec.Command(command, args...)
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	cmd.Run()
	cmd.Run()
}