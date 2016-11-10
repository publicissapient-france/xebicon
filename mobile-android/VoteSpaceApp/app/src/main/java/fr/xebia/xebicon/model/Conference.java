package fr.xebia.xebicon.model;

import java.util.Date;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Conferences")
public class Conference extends Model {

    @Column("_id") @Key private int id;
    @Column("name") private String name;
    @Column("description") private String description;
    @Column("location") private String location;
    @Column("backgroundUrl") private String backgroundUrl;
    @Column("logoUrl") private String logoUrl;
    @Column("fromDate") private Date from;
    @Column("toDate") private Date to;
    @Column("fromUtcTime") private long fromUtcTime;
    @Column("toUtcTime") private long toUtcTime;
    @Column("enabled") private boolean enabled;
    @Column("nfcTag") private String nfcTag;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Date getFrom() {
        return from;
    }

    public Date getTo() {
        return to;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public long getFromUtcTime() {
        return fromUtcTime;
    }

    public void setFromUtcTime(long fromUtcTime) {
        this.fromUtcTime = fromUtcTime;
    }

    public long getToUtcTime() {
        return toUtcTime;
    }

    public void setToUtcTime(long toUtcTime) {
        this.toUtcTime = toUtcTime;
    }

    public boolean isStarted() {
        return System.currentTimeMillis() > fromUtcTime;
    }

    public String getNfcTag() {
        return nfcTag;
    }

    public void setNfcTag(String nfcTag) {
        this.nfcTag = nfcTag;
    }
}
