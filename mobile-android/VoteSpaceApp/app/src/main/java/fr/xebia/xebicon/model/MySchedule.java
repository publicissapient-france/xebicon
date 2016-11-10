package fr.xebia.xebicon.model;

import java.util.ArrayList;
import java.util.List;

public class MySchedule {

    private final List<ConferenceDay> conferenceDays = new ArrayList<>();

    public MySchedule(Schedule schedule) {
        for (String formattedDay : schedule.getFormattedDays()) {
            conferenceDays.add(new ConferenceDay(schedule.forDay(formattedDay)));
        }
    }

    public int getConferenceDaysCount() {
        return conferenceDays.size();
    }

    public ConferenceDay getConferenceDayAt(int position) {
        return conferenceDays.get(position);
    }


}
