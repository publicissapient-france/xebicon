package fr.xebia.xebicon.core.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.WindowManager;

import butterknife.ButterKnife;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.ui.conference.ConferenceChooserActivity;
import fr.xebia.xebicon.vote.services.MyFireBaseMessagingService;
import fr.xebia.xebicon.vote.ui.KeynoteActivity;

public abstract class BaseActivity extends AppCompatActivity {

    public static final String HOME_FRAG_TAG = "HOME";

    protected boolean mDontCheckConference = false;
    protected boolean hasDisplayKeynote = false;

    private final int layoutId;
    private BroadcastReceiver keynoteBroadcastReceiver;

    public BaseActivity(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layoutId);

        ButterKnife.inject(this);

        keynoteBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String newState = intent.getStringExtra(KeynoteActivity.NEW_STATE);
                switch (newState) {
                    case KeynoteActivity.KEYNOTE_START:
                        startKeynote(intent);
                        displayKeynote(intent.getExtras(), true);
                        break;
                    case KeynoteActivity.KEYNOTE_END:
                        endKeynote();
                        break;
                }
            }
        };

        IntentFilter filter = new IntentFilter(KeynoteActivity.INTENT_FILTER_STATE_CHANGED);
        registerReceiver(keynoteBroadcastReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getIntent().hasExtra(MyFireBaseMessagingService.KEYNOTE_STATE)) {
            Bundle bundle = MyFireBaseMessagingService.Companion.extractData(getIntent().getExtras());
            if (bundle != null) {
                String newState = bundle.getString(KeynoteActivity.NEW_STATE);
                if (newState.equals(KeynoteActivity.KEYNOTE_START)) {
                    Preferences.startKeynote(this);
                } else if (newState.equals(KeynoteActivity.KEYNOTE_END)) {
                    Preferences.endKeynote(this);
                }

                if (!hasDisplayKeynote) {
                    hasDisplayKeynote = true;
                    displayKeynote(bundle, false);
                }
            }
        }
    }

    protected void displayKeynote(Bundle bundle, boolean alert) {
        Intent keynoteIntent = new Intent(this, KeynoteActivity.class)
                .putExtras(bundle);

        if (alert) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.keynote_is_starting)
                    .setMessage(R.string.keynote_confirm)
                    .setPositiveButton(R.string.keynote_confirm_accept, (dialogInterface, i) -> {
                        startActivity(keynoteIntent);
                    })
                    .setNegativeButton(R.string.keynote_confirm_refuse, (dialogInterface, i) -> {

                    })
                    .show();

        } else {
            startActivity(keynoteIntent);
        }
    }

    protected void startKeynote(Intent intent) {
        Preferences.startKeynote(this);
    }

    protected void endKeynote() {
        Preferences.endKeynote(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(keynoteBroadcastReceiver);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        boolean hasSelectedConference = Preferences.hasSelectedConference(this);
        if (!hasSelectedConference && !mDontCheckConference) {
            startActivity(new Intent(this, ConferenceChooserActivity.class));
            finish();
        }
    }

    protected void setupFloatingWindow() {
        // configure this Activity as a floating window, dimming the background
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.talk_details_floating_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.talk_details_floating_height);
        params.alpha = 1;
        params.dimAmount = 0.7f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    protected boolean shouldBeFloatingWindow() {
        Resources.Theme theme = getTheme();
        TypedValue floatingWindowFlag = new TypedValue();
        if (theme == null || !theme.resolveAttribute(R.attr.isFloatingWindow, floatingWindowFlag, true)) {
            // isFloatingWindow flag is not defined in theme
            return false;
        }
        return (floatingWindowFlag.data != 0);
    }
}