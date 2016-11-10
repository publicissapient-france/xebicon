package fr.xebia.xebicon.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fr.xebia.xebicon.core.utils.Compatibility;

public class ConferenceDay implements Parcelable {

    private static final DateFormat DATE_FORMATTER = new SimpleDateFormat("EEEE dd'.'", Locale.getDefault());

    public String title;
    public List<MyScheduleItem> myScheduleItems = new ArrayList<>();

    public ConferenceDay(List<Talk> talks) {
        Map<Long, List<Talk>> mapTalksByStartTime = new LinkedHashMap<>();
        for (Talk talk : talks) {
            long talkStartTime = talk.getFromUtcTime();
            List<Talk> talksForSlot = mapTalksByStartTime.get(talkStartTime);
            if (talksForSlot == null) {
                talksForSlot = new ArrayList<>();
                mapTalksByStartTime.put(talkStartTime, talksForSlot);
            }
            talksForSlot.add(talk);
        }

        long currentEndTime = -1;
        for (Map.Entry<Long, List<Talk>> entry : mapTalksByStartTime.entrySet()) {
            MyScheduleItem myScheduleItem = new MyScheduleItem(entry.getKey(), entry.getValue());
            if (myScheduleItem.startTime < currentEndTime) {
                myScheduleItem.conflicting = true;
            }
            myScheduleItems.add(myScheduleItem);
            if (myScheduleItem.conflictingTalks.size() > 0) {
                for (Talk talk : myScheduleItem.conflictingTalks) {
                    myScheduleItems.add(new MyScheduleItem(talk, true));
                }
            }
            if (myScheduleItem.type != MyScheduleItem.BREAK) {
                currentEndTime = myScheduleItem.endTime;
            }
        }

        if (myScheduleItems.size() > 0) {
            title = Compatibility.capitalize(DATE_FORMATTER.format(myScheduleItems.get(0).startTime));
        }
    }

    protected ConferenceDay(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0x01) {
            myScheduleItems = new ArrayList<>();
            in.readList(myScheduleItems, MyScheduleItem.class.getClassLoader());
        } else {
            myScheduleItems = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (myScheduleItems == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(myScheduleItems);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<ConferenceDay> CREATOR = new Creator<ConferenceDay>() {
        @Override
        public ConferenceDay createFromParcel(Parcel in) {
            return new ConferenceDay(in);
        }

        @Override
        public ConferenceDay[] newArray(int size) {
            return new ConferenceDay[size];
        }
    };
}
