package fr.xebia.xebicon.core.db;

public class DbSchema {

    public static final String SPEAKERS = "CREATE TABLE Speakers (\n" +
            "  bio          TEXT,\n" +
            "  blog         TEXT,\n" +
            "  company      TEXT,\n" +
            "  tweetHandle  TEXT,\n" +
            "  firstName    TEXT,\n" +
            "  _id          TEXT,\n" +
            "  imageURL     TEXT,\n" +
            "  lang         TEXT,\n" +
            "  lastName     TEXT,\n" +
            "  conferenceId INTEGER,\n" +
            "  PRIMARY KEY (_id, conferenceId)\n" +
            ");";

    public static final String TALKS = "CREATE TABLE Talks (\n" +
            "  type           TEXT,\n" +
            "  talkDetailsId  TEXT,\n" +
            "  experience     TEXT,\n" +
            "  track          TEXT,\n" +
            "  fromTime       INTEGER,\n" +
            "  _id            TEXT,\n" +
            "  language       TEXT,\n" +
            "  toTime         INTEGER,\n" +
            "  room           TEXT,\n" +
            "  summary        TEXT,\n" +
            "  title          TEXT,\n" +
            "  favorite       INTEGER,\n" +
            "  kind           TEXT,\n" +
            "  color          INTEGER,\n" +
            "  memo           TEXT,\n" +
            "  prettySpeakers TEXT,\n" +
            "  conferenceId   INTEGER,\n" +
            "  fromUtcTime    INTEGER,\n" +
            "  toUtcTime      INTEGER,\n" +
            "  position       INTEGER,\n" +
            "  PRIMARY KEY (_id, conferenceId)\n" +
            ");";

    public static final String SPEAKER_TALKS = "CREATE TABLE Speaker_Talk (\n" +
            "  speakerId TEXT,\n" +
            "  talkId    TEXT,\n" +
            "  conferenceId    INTEGER,\n" +
            "  PRIMARY KEY (speakerId, talkId, conferenceId)\n" +
            ");";

    public static final String VOTES = "CREATE TABLE Votes (\n" +
            "  _id             TEXT,\n" +
            "  conferenceId    INTEGER,\n" +
            "  user            TEXT,\n" +
            "  rate            INTEGER,\n" +
            "  revelent        INTEGER,\n" +
            "  content         INTEGER,\n" +
            "  speakers        INTEGER,\n" +
            "  comment         TEXT,\n" +
            "  PRIMARY KEY (_id, conferenceId)\n" +
            ");";

    public static final String CONFERENCES = "CREATE TABLE Conferences (\n" +
            "  _id           INTEGER,\n" +
            "  name          TEXT,\n" +
            "  description   TEXT,\n" +
            "  location      TEXT,\n" +
            "  backgroundUrl TEXT,\n" +
            "  logoUrl       TEXT,\n" +
            "  nfcTag        TEXT,\n" +
            "  fromDate      INTEGER,\n" +
            "  toDate        INTEGER,\n" +
            "  enabled       INTEGER,\n" +
            "  fromUtcTime   INTEGER,\n" +
            "  toUtcTime     INTEGER,\n" +
            "  PRIMARY KEY (_id)\n" +
            ");";
}
