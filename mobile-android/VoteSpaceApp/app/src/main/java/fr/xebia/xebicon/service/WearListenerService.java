package fr.xebia.xebicon.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Vote;
import timber.log.Timber;

public class WearListenerService extends WearableListenerService {

    public static final String ACTION_NOTIFICATION_DISMISSAL = "fr.xebia.xebicon.service.ACTION_NOTIFICATION_DISMISSAL";
    public static final String EXTRA_TALK_ID = "fr.xebia.xebicon.service.EXTRA_TALK_ID";

    private static final long TIMEOUT_S = 10;

    private static final String PATH_FEEDBACK = "/companion/feedback/";
    private static final String PATH_RATING = "/companion/rating/";

    public static final String TIME = "time";
    public static final String KEY_TALK_ID = "talkId";
    public static final String KEY_TALK_TITLE = "talkTitle";
    public static final String KEY_TALK_SPEAKERS = "talkSpeakers";
    public static final String KEY_TALK_COLOR = "talkColor";
    public static final String KEY_TALK_ROOM = "talkRoom";

    private GoogleApiClient mGoogleApiClient;


    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (ACTION_NOTIFICATION_DISMISSAL.equals(action)) {
                dismissWearableNotification(intent.getStringExtra(EXTRA_TALK_ID));
            }
        }
        return Service.START_NOT_STICKY;
    }

    private void dismissWearableNotification(final String sessionId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mGoogleApiClient.blockingConnect(TIMEOUT_S, TimeUnit.SECONDS);
                if (!mGoogleApiClient.isConnected()) {
                    return;
                }
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(buildFeedbackPath(sessionId));
                if (mGoogleApiClient.isConnected()) {
                    Wearable.DataApi.deleteDataItems(mGoogleApiClient, putDataMapRequest.getUri()).await();
                } else {
                    Timber.e("dismissWearableNotification()): No Google API Client connection");
                }
            }
        }).start();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            DataMapItem mapItem = DataMapItem.fromDataItem(event.getDataItem());
            String path = event.getDataItem().getUri().getPath();
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (PATH_RATING.equals(path)) {
                    DataMap data = mapItem.getDataMap();
                    int rating = data.getInt("rating");
                    String talkId = data.getString("talkId");
                    if (TextUtils.isEmpty(talkId)) {
                        return;
                    }
                    saveRating(talkId, rating);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                if (path.startsWith(PATH_FEEDBACK)) {
                    Uri uri = event.getDataItem().getUri();
                    dismissLocalNotification(uri.getLastPathSegment());
                }
            }
        }
    }

    private void saveRating(final String talkId, final int rating) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int conferenceId = Preferences.getSelectedConference(WearListenerService.this);
                //new Vote(rating, talkId, conferenceId).save();
                Intent dismissalIntent = new Intent(ACTION_NOTIFICATION_DISMISSAL);
                dismissalIntent.putExtra(NotificationSchedulerIntentService.EXTRA_TALK_ID, talkId);
                startService(dismissalIntent);
            }
        }).start();
    }

    private void dismissLocalNotification(String talkId) {
        NotificationManagerCompat.from(this).cancel(talkId, NotificationSchedulerIntentService.FEEDBACK_NOTIFICATION_ID);
    }

    public static String buildFeedbackPath(String talkId) {
        return PATH_FEEDBACK + talkId;
    }
}
