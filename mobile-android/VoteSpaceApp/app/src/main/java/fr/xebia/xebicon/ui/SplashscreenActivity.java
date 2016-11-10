package fr.xebia.xebicon.ui;

import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import fr.xebia.xebicon.BuildConfig;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.bus.SynchroFinishedEvent;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.service.SynchroIntentService;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static fr.xebia.xebicon.core.XebiConApplication.BUS;
import static rx.Observable.timer;

public class SplashscreenActivity extends BaseActivity {

    private Intent homeIntent;

    public SplashscreenActivity() {
        super(R.layout.splashscreen_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        homeIntent = new Intent(this, ExploreActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (!Preferences.hasSelectedConference(this) && !Preferences.hasCurrentEdition(this)) {
            Intent intent = new Intent(this, SynchroIntentService.class);
            intent.putExtra(SynchroIntentService.EXTRA_CONFERENCE_ID, BuildConfig.XEBICON_CONFERENCE_ID);
            startService(intent);
        } else {
            timer(2, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> gotToHome());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BUS.register(this);
    }

    @Override
    public void onStop() {
        BUS.unregister(this);
        super.onStop();
    }

    @Override
    protected void displayKeynote(Bundle bundle, boolean alert) {
        homeIntent.putExtras(getIntent().getExtras());
    }

    public void onEventMainThread(SynchroFinishedEvent synchroFinishedEvent) {
        gotToHome();
    }

    private void gotToHome() {
        startActivity(homeIntent);
        finish();
    }
}
