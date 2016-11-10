package fr.xebia.xebicon.model;

import java.util.Date;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Votes")
public class TalkVote extends Model {

    @Column("note") private int note;
    @Column("_id") @Key private String talkId;
    @Column("color") @Key private int talkColor;
    @Column("track") private String track;
    @Column("title") private String title;
    @Column("fromTime") private Date fromTime;
    @Column("toTime") private Date toTime;

    public int getNote() {
        return note;
    }

    public String getTalkId() {
        return talkId;
    }

    public String getTrack() {
        return track;
    }

    public String getTitle() {
        return title;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public int getTalkColor() {
        return talkColor;
    }
}
