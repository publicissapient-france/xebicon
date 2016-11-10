package xebicon

import (
	"log"
	"math/rand"
)

func FailOnError(err error, msg string) {
	if err != nil {
		log.Printf("%s: %s\n", msg, err)
		//panic(fmt.Sprintf("%s: %s", msg, err))
	}
}

func ConstantLight() float32 {
	return 1.
}

func RandomLight() float32 {
	return (float32)(rand.Int() % 2)
}
