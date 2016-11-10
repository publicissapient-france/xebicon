package fr.xebia.xebicon.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.TextUtils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import fr.xebia.xebicon.BuildConfig;
import fr.xebia.xebicon.bus.SynchroFinishedEvent;
import fr.xebia.xebicon.core.XebiConApplication;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Conference;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.model.SpeakerTalk;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.model.TrackColors;
import se.emilsjolander.sprinkles.ModelList;
import se.emilsjolander.sprinkles.Query;
import se.emilsjolander.sprinkles.Transaction;
import timber.log.Timber;

import static fr.xebia.xebicon.core.XebiConApplication.BUS;

public class SynchroIntentService extends IntentService {

    private static final String TAG = "SynchroIntentService";

    public static final String EXTRA_CONFERENCE_ID = "fr.xebia.xebicon.EXTRA_CONFERENCE_ID";
    public static final String EXTRA_FROM_APP_CREATE = "fr.xebia.xebicon.EXTRA_FROM_APP_CREATE";

    public SynchroIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int conferenceId = intent.getIntExtra(EXTRA_CONFERENCE_ID, -1);
        boolean sendSynchroEvent = !intent.hasExtra(EXTRA_FROM_APP_CREATE);
        Transaction transaction = new Transaction();

        try {

            Conference conference = null;
            List<Conference> availableConferences = XebiConApplication.getConferenceApi().getAvailableConferences();

            for (Conference aConference : availableConferences) {
                if (aConference.getId() == BuildConfig.XEBICON_CONFERENCE_ID) {
                    conference = aConference;
                }
            }

            if (conference == null) {
                return;
            }

            DateTimeZone utcTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC"));
            DateTimeZone apiTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris"));
            DateTime jodaStartTime = new DateTime(conference.getFrom(), apiTimeZone);
            DateTime jodaEndTime = new DateTime(conference.getTo(), apiTimeZone);
            conference.setFromUtcTime(jodaStartTime.withZone(utcTimeZone).getMillis());
            conference.setToUtcTime(jodaEndTime.withFieldAdded(DurationFieldType.days(), 1).withZone(utcTimeZone).getMillis());

            conference.save(transaction);


            if (conferenceId == -1) {
                BUS.post(new SynchroFinishedEvent(false, null));
            } else {
                // Load data before starting transaction
                List<Speaker> speakers = XebiConApplication.getConferenceApi().getSpeakers(conferenceId);
                List<Talk> scheduledTalks = XebiConApplication.getConferenceApi().getSchedule(conferenceId);

                synchroniseSpeakers(speakers, transaction);
                synchroniseTalks(conference, scheduledTalks, transaction);
                transaction.setSuccessful(true);
                Preferences.setSelectedConference(this, conference.getId());
                Preferences.setSelectedConferenceEndTime(this, conference.getToUtcTime());
                Preferences.setSelectedConferenceStartTime(this, conference.getFromUtcTime());
                Preferences.setCurrentEdition(this, BuildConfig.CURRENT_EDITION);
                startService(new Intent(NotificationSchedulerIntentService.ACTION_SCHEDULE_ALL_NOTIFICATIONS, null, this, NotificationSchedulerIntentService.class));
                if (sendSynchroEvent) {
                    BUS.post(new SynchroFinishedEvent(true, conference));
                }
            }
        } catch (Exception e) {
            Timber.d(e, "Error synchronizing data");
            if (transaction != null) {
                transaction.setSuccessful(false);
            }
            if (sendSynchroEvent) {
                BUS.post(new SynchroFinishedEvent(false, null));
            }
            // Retry in 1 hour
            long oneHourLater = System.currentTimeMillis() + 3_600 * 1000;
            ((AlarmManager) getSystemService(ALARM_SERVICE)).set(AlarmManager.RTC, oneHourLater, buildSynchroPendingIntent());
        } finally {
            if (transaction != null) {
                transaction.finish();
            }
        }
    }

    private PendingIntent buildSynchroPendingIntent() {
        Intent intent = new Intent(this, SynchroIntentService.class);
        intent.putExtra(SynchroIntentService.EXTRA_CONFERENCE_ID, Preferences.getSelectedConference(this));
        intent.putExtra(SynchroIntentService.EXTRA_FROM_APP_CREATE, true);
        return PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void synchroniseTalks(Conference conference, List<Talk> scheduledTalks, Transaction transaction) {
        int conferenceId = conference.getId();
        Map<String, Talk> talksInDbById = loadTalksFromDb(conferenceId);
        HashMap<String, Speaker> everySpeakers = loadEverySpeakers();

        // Save talks keeping favorite info and retrieving date/time from
        ModelList<Talk> talksToSave = new ModelList<>();
        int index = 0;
        for (Talk talkToSave : scheduledTalks) {
            Talk talkFromDb = talksInDbById.remove(talkToSave.getId());
            if (talkFromDb != null) {
                talkToSave.setFavorite(talkFromDb.isFavorite() || talkToSave.isKeynote());
                talkToSave.setMemo(talkFromDb.getMemo());
            } else {
                talkToSave.setFavorite(talkToSave.isKeynote());
            }

            talkToSave.setTalkDetailsId(talkToSave.getId());

            if (talkToSave.isKeynote()) {
                talkToSave.setTrack("Keynote");
            }

            talkToSave.setPrettySpeakers(talkToSave.getSpeakers(), everySpeakers);

            setConferenceUtcTime(conference, talkToSave);

            talkToSave.setPosition(index++);

            talksToSave.add(talkToSave);
        }

        generateColorByTrack(talksToSave);

        talksToSave.saveAll(transaction);

        // Delete obsolete talks
        Collection<Talk> talksToDelete = talksInDbById.values();
        if (!talksToDelete.isEmpty()) {
            new ModelList<>(talksToDelete).deleteAll(transaction);
        }

        List<SpeakerTalk> speakerTalksToDelete = Query.many(SpeakerTalk.class, "SELECT * FROM Speaker_Talk").get().asList();
        ModelList<SpeakerTalk> speakerTalks = new ModelList<>();
        for (Talk talk : scheduledTalks) {
            Collection<Speaker> speakers = talk.getSpeakers();
            if (speakers != null) {
                for (Speaker speaker : speakers) {
                    SpeakerTalk speakerTalk = new SpeakerTalk(speaker.getId(), talk.getId(), conferenceId);
                    speakerTalks.add(speakerTalk);
                    speakerTalksToDelete.remove(speakerTalk);
                }
            }
        }

        speakerTalks.saveAll(transaction);

        // Delete obsolete speaker talks
        if (speakerTalksToDelete.size() > 0) {
            new ModelList<>(speakerTalksToDelete).deleteAll(transaction);
        }
    }

    private void setConferenceUtcTime(Conference conference, Talk talkToSave) {
        DateTimeZone apiTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        DateTime jodaStartTime = new DateTime(talkToSave.getFromTime(), apiTimeZone);
        DateTime jodaEndTime = new DateTime(talkToSave.getToTime(), apiTimeZone);

        DateTimeZone utcTimeZone = DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC"));
        talkToSave.setFromUtcTime(jodaStartTime.withZone(utcTimeZone).getMillis());
        talkToSave.setToUtcTime(jodaEndTime.withZone(utcTimeZone).getMillis());
    }

    private HashMap<String, Speaker> loadEverySpeakers() {
        List<Speaker> everySpeakers = Query.many(Speaker.class, "SELECT * FROM Speakers").get().asList();
        HashMap<String, Speaker> speakersMap = new HashMap<>();
        for (Speaker speaker : everySpeakers) {
            speakersMap.put(speaker.getId(), speaker);
        }
        return speakersMap;
    }

    private Map<String, Talk> loadTalksFromDb(int conferenceId) {
        List<Talk> talksInDb = Query.many(Talk.class, "SELECT * FROM Talks WHERE conferenceId=?", conferenceId).get().asList();
        Map<String, Talk> talksInDbById = new HashMap<>();
        for (Talk talk : talksInDb) {
            talksInDbById.put(talk.getId(), talk);
        }
        return talksInDbById;
    }

    private void generateColorByTrack(List<Talk> talks) {
        for (Talk talk : talks) {
            String track = talk.getTrack();
            if (TextUtils.isEmpty(track) || track.equals("???")) {
                talk.setColor(TrackColors.NO_TRACK);
                continue;
            }
            talk.setColor(TrackColors.MAP.get(track));
        }
    }

    private void synchroniseSpeakers(List<Speaker> speakers, Transaction transaction) {
        List<Speaker> speakersToDelete = Query.many(Speaker.class, "SELECT * FROM Speakers").get().asList();
        ModelList<Speaker> speakersToSave = new ModelList<>();

        for (Speaker speaker : speakers) {
            String firstName = speaker.getFirstName();
            speaker.setFirstName(firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
            speakersToSave.add(speaker);
            speakersToDelete.remove(speaker);
        }
        speakersToSave.saveAll(transaction);

        if (!speakersToDelete.isEmpty()) {
            new ModelList<>(speakersToDelete).deleteAll(transaction);
        }
    }
}
