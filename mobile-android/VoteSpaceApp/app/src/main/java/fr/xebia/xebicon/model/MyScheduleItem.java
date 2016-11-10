package fr.xebia.xebicon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MyScheduleItem implements Parcelable {
    // types:
    public static final int FREE = 0;  // a free chunk of time
    public static final int SESSION = 1; // a session
    public static final int BREAK = 2; // a break (lunch, breaks, after-hours party)

    public long endTime = -1;
    public long startTime = -1;
    public int type;
    public String title;
    public String subtitle;
    public int backgroundColor;
    public String backgroundImageUrl;
    public boolean hasGivenFeedback;
    public ArrayList<Talk> availableTalks = new ArrayList<>();
    public ArrayList<Talk> conflictingTalks = new ArrayList<>();
    public Talk selectedTalk;
    public boolean conflicting;

    public MyScheduleItem(Talk talk, boolean conflicting) {
        this.startTime = talk.getFromUtcTime();
        this.selectedTalk = talk;
        this.conflicting = conflicting;
        this.type = SESSION;
        buildAttributesFromSelectedTalk();
    }

    public MyScheduleItem(long startTime, List<Talk> talks) {
        availableTalks.addAll(talks);
        this.startTime = startTime;
        for (Talk talk : availableTalks) {
            if (talk.isFavorite() || talk.isKeynote()) {
                if (selectedTalk == null) {
                    // Keep the first talk as the selected one
                    selectedTalk = talk;
                    buildAttributesFromSelectedTalk();
                } else {
                    conflictingTalks.add(talk);
                }
            }
        }

        if (isBreakSlot()) {
            Talk talk = talks.get(0);
            if (talk.isBreak()) {
                type = BREAK;
                title = talk.getTitle();
            }
            endTime = talk.getToUtcTime();
        } else if (selectedTalk == null) {
            type = FREE;
            endTime = startTime;
        } else {
            type = SESSION;
        }
    }

    private boolean isBreakSlot() {
        for(Talk talk : availableTalks){
            if(!talk.isBreak()){
                return false;
            }
        }
        return selectedTalk == null;
    }

    private void buildAttributesFromSelectedTalk() {
        endTime = selectedTalk.getToUtcTime();
        backgroundColor = selectedTalk.getColor();
        title = selectedTalk.getTitle();
    }

    protected MyScheduleItem(Parcel in) {
        endTime = in.readLong();
        startTime = in.readLong();
        type = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        backgroundColor = in.readInt();
        backgroundImageUrl = in.readString();
        hasGivenFeedback = in.readByte() != 0x00;
        conflicting = in.readByte() != 0x00;
        if (in.readByte() == 0x01) {
            availableTalks = new ArrayList<>();
            in.readList(availableTalks, Talk.class.getClassLoader());
        } else {
            availableTalks = null;
        }
        if (in.readByte() == 0x01) {
            conflictingTalks = new ArrayList<>();
            in.readList(conflictingTalks, Talk.class.getClassLoader());
        } else {
            conflictingTalks = null;
        }
        selectedTalk = (Talk) in.readValue(Talk.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(endTime);
        dest.writeLong(startTime);
        dest.writeInt(type);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeInt(backgroundColor);
        dest.writeString(backgroundImageUrl);
        dest.writeByte((byte) (hasGivenFeedback ? 0x01 : 0x00));
        dest.writeByte((byte) (conflicting ? 0x01 : 0x00));
        if (availableTalks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(availableTalks);
        }
        if (conflictingTalks == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(conflictingTalks);
        }
        dest.writeValue(selectedTalk);
    }

    @SuppressWarnings("unused")
    public static final Creator<MyScheduleItem> CREATOR = new Creator<MyScheduleItem>() {
        @Override
        public MyScheduleItem createFromParcel(Parcel in) {
            return new MyScheduleItem(in);
        }

        @Override
        public MyScheduleItem[] newArray(int size) {
            return new MyScheduleItem[size];
        }
    };

    public boolean hasTalkSelected() {
        return selectedTalk != null;
    }

    public ArrayList<String> getAvailableTalksIds() {
        ArrayList<String> availableTalksIds = new ArrayList<>();
        for (Talk talk : availableTalks) {
            availableTalksIds.add(talk.getId());
        }
        return availableTalksIds;
    }
}
