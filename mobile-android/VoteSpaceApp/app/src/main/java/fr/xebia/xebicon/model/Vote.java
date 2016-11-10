package fr.xebia.xebicon.model;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Votes")
public class Vote extends Model {

    @Column("_id") @Key private String talkId;
    @Column("conferenceId") @Key private int conferenceId;
    @Column("rate") private int rate;
    @Column("revelent") private int revelent;
    @Column("content") private int content;
    @Column("speakers") private int speakers;
    @Column("comment") private String comment;

    public Vote(){

    }

    public Vote(String talkId, int conferenceId, int rate, int revelent, int content, int speakers, String comment) {
        this.rate = rate;
        this.revelent = revelent;
        this.content = content;
        this.speakers = speakers;
        this.comment = comment;
        this.talkId = talkId;
        this.conferenceId = conferenceId;
    }

    public String getTalkId() {
        return talkId;
    }

    public int getConferenceId() {
        return conferenceId;
    }

    public int getRate() {
        return rate;
    }

    public int getRevelent() {
        return revelent;
    }

    public int getContent() {
        return content;
    }

    public int getSpeakers() {
        return speakers;
    }

    public String getComment() {
        return comment;
    }
}
