package fr.xebia.xebicon.core;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.okhttp.OkHttpClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.tweetui.TweetUi;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import de.greenrobot.event.EventBus;
import fr.xebia.xebicon.BuildConfig;
import fr.xebia.xebicon.api.ConferenceApi;
import fr.xebia.xebicon.api.ParseVoteApi;
import fr.xebia.xebicon.api.VideoApi;
import fr.xebia.xebicon.api.VoteApi;
import fr.xebia.xebicon.api.YoutubeApi;
import fr.xebia.xebicon.bus.SyncEvent;
import fr.xebia.xebicon.core.db.DbSchema;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.service.SynchroIntentService;
import fr.xebia.xebicon.vote.VoteApplication;
import fr.xebia.xebicon.vote.services.FirebaseRegistrationService;
import io.fabric.sdk.android.Fabric;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import se.emilsjolander.sprinkles.Migration;
import se.emilsjolander.sprinkles.Sprinkles;
import timber.log.Timber;

public class XebiConApplication extends VoteApplication {

    private static ConferenceApi sConferenceApi;
    private static VoteApi sVoteApi;
    private static Gson sGson;
    private static VideoApi sVideoApi;

    public static final EventBus BUS = EventBus.getDefault();

    @Override
    public void onCreate() {
        super.onCreate();

        LeakCanary.install(this);

        if (Preferences.getSelectedConference(this) != BuildConfig.XEBICON_CONFERENCE_ID) {
            Preferences.removeSelectedConference(this);
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new TweetUi());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }

        Context applicationContext = getApplicationContext();
        if (!Preferences.isDeviceIdGenerated(applicationContext)) {
            Preferences.saveGeneratedDeviceId(applicationContext, UUID.randomUUID().toString());
        }

        sGson = new GsonBuilder()
                .registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
                        return simpleDateFormat.parse(json.getAsJsonPrimitive().getAsString());
                    } catch (ParseException e) {
                        try {
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Zurich"));
                            return simpleDateFormat.parse(json.getAsJsonPrimitive().getAsString());
                        } catch (ParseException e2) {
                            return null;
                        }
                    }
                })

                .create();

        OkHttpClient okHttpClient = new OkHttpClient();
        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder().setClient(new OkClient(okHttpClient)).setConverter(new GsonConverter(sGson));

        sConferenceApi = restAdapterBuilder.setEndpoint(BuildConfig.BACKEND_URL).build().create(ConferenceApi.class);
        sVoteApi = new ParseVoteApi(this);

        sVideoApi = new YoutubeApi(BuildConfig.GOOGLE_API_KEY, BuildConfig.YOUTUBE_PLAYLIST);

        Sprinkles sprinkles = Sprinkles.init(applicationContext, "xebicon.db", 0);

        sprinkles.addMigration(new Migration() {
            @Override
            protected void doMigration(SQLiteDatabase sqLiteDatabase) {
                sqLiteDatabase.execSQL(DbSchema.SPEAKERS);
                sqLiteDatabase.execSQL(DbSchema.TALKS);
                sqLiteDatabase.execSQL(DbSchema.SPEAKER_TALKS);
                sqLiteDatabase.execSQL(DbSchema.VOTES);
                sqLiteDatabase.execSQL(DbSchema.CONFERENCES);
            }
        });

        // TODO temporary hack to send sync event
        new Timer(true).scheduleAtFixedRate(new SendSyncEventTask(), new Date(), 5_000);

        Intent intent = new Intent(this, SynchroIntentService.class);
        intent.putExtra(SynchroIntentService.EXTRA_CONFERENCE_ID, BuildConfig.XEBICON_CONFERENCE_ID);
        intent.putExtra(SynchroIntentService.EXTRA_FROM_APP_CREATE, true);
        startService(intent);

        startService(new Intent(this, FirebaseRegistrationService.class));
    }

    /**
     * A tree which logs important information for crash reporting.
     */
    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (t != null) {
                Crashlytics.logException(t);
            }
        }
    }

    public static class SendSyncEventTask extends TimerTask {
        @Override
        public void run() {
            BUS.post(SyncEvent.getInstance());
        }
    }

    public static ConferenceApi getConferenceApi() {
        return sConferenceApi;
    }

    public static VoteApi getVoteApi() {
        return sVoteApi;
    }

    public static VideoApi getVideoApi() {
        return sVideoApi;
    }

    public static Gson getGson(){
        return sGson;
    }
}
