package fr.xebia.xebicon.service;

import android.app.IntentService;
import android.content.Intent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import fr.xebia.xebicon.bus.ConferenceFetchedEvent;
import fr.xebia.xebicon.model.Conference;
import retrofit.RetrofitError;
import se.emilsjolander.sprinkles.ModelList;
import se.emilsjolander.sprinkles.Query;

import static fr.xebia.xebicon.core.XebiConApplication.BUS;

public class ConferencesFetcherIntentService extends IntentService {

    public ConferencesFetcherIntentService() {
        super("ConferencesFetcherIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<Conference> conferences = fr.xebia.xebicon.core.XebiConApplication.getConferenceApi().getAvailableConferences();
            Map<Integer, Conference> conferencesInDbById = retrieveConferencesInDbById();
            ModelList<Conference> conferencesToStore = new ModelList<>();
            DateTimeZone utcTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC"));
            DateTimeZone apiTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            for (Conference conference : conferences) {
                Conference conferenceInDb = conferencesInDbById.remove(conference.getId());
                if (conferenceInDb != null) {
                    conference.setNfcTag(conferenceInDb.getNfcTag());
                }

                DateTime jodaStartTime = new DateTime(conference.getFrom(), apiTimeZone);
                DateTime jodaEndTime = new DateTime(conference.getTo(), apiTimeZone);
                conference.setFromUtcTime(jodaStartTime.withZone(utcTimeZone).getMillis());
                conference.setToUtcTime(jodaEndTime.withFieldAdded(DurationFieldType.days(), 1).withZone(utcTimeZone).getMillis());
                conferencesToStore.add(conference);
            }
            conferencesToStore.addAll(conferences);
            conferencesToStore.saveAll();

            if (conferencesInDbById.size() > 0) {
                new ModelList<>(conferencesInDbById.values()).deleteAll();
            }
            BUS.post(new ConferenceFetchedEvent(true));
        } catch (RetrofitError e) {
            BUS.post(new ConferenceFetchedEvent(false));
        }
    }

    private Map<Integer, Conference> retrieveConferencesInDbById() {
        List<Conference> conferences = Query.many(Conference.class, "SELECT * FROM Conferences").get().asList();
        Map<Integer, Conference> conferencesInDbById = new HashMap<>();
        for (Conference conference : conferences) {
            conferencesInDbById.put(conference.getId(), conference);
        }
        return conferencesInDbById;
    }
}
