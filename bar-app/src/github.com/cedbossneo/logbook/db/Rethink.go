package db

import (
	r "gopkg.in/dancannon/gorethink.v2"
	"log"
)

type Database struct {
	Session *r.Session
}

func Connect(rethinkURI string, base string) *Database {
	session, err := r.Connect(r.ConnectOpts{
		Address: rethinkURI,
	})
	if err != nil {
		log.Fatalln("Unable to connect to RethinkDB: ", err.Error())
	}
	initialize(session, base)
	return &Database{
		Session: session,
	}
}

func (db Database) Close() {
	db.Session.Close()
}

func initialize(db *r.Session, base string)  {
	r.DBCreate(base).Exec(db)
	db.Use(base)
	opts := r.TableCreateOpts{Shards: 2, DataCenter: map[string]interface{}{
		"gce": 1,
		"nuc": 1,
	}, PrimaryReplicaTag: "aws"}
	r.TableCreate("wallets", opts).Exec(db)
	r.TableCreate("drinks", opts).Exec(db)
}
