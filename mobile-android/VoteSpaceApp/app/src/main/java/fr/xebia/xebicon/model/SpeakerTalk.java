package fr.xebia.xebicon.model;

import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Speaker_Talk")
public class SpeakerTalk extends Model {

    @Column("speakerId") @Key private String speakerId;
    @Column("talkId") @Key private String talkId;
    @Column("conferenceId") @Key private int conferenceId;

    public SpeakerTalk() {
    }

    public SpeakerTalk(String speakerId, String talkId, int conferenceId) {
        this.speakerId = speakerId;
        this.talkId = talkId;
        this.conferenceId = conferenceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpeakerTalk that = (SpeakerTalk) o;

        if (conferenceId != that.conferenceId) return false;
        if (speakerId != null ? !speakerId.equals(that.speakerId) : that.speakerId != null)
            return false;
        if (talkId != null ? !talkId.equals(that.talkId) : that.talkId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = speakerId != null ? speakerId.hashCode() : 0;
        result = 31 * result + (talkId != null ? talkId.hashCode() : 0);
        result = 31 * result + conferenceId;
        return result;
    }
}
