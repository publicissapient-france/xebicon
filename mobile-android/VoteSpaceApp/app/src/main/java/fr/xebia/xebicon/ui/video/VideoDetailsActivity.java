package fr.xebia.xebicon.ui.video;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;

import static com.google.android.youtube.player.YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
import static com.google.android.youtube.player.YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT;

public class VideoDetailsActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener {

    public static final int RECOVERY_DIALOG_REQUEST = 1;
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTIONS = "EXTRA_DESCRIPTIONS";
    public static final String EXTRA_ID = "EXTRA_ID";

    @InjectView(R.id.player) YouTubePlayerView playerView;
    @InjectView(R.id.video_description) TextView descriptionTextView;

    private YouTubePlayer player;
    private boolean fullscreen;

    private String title;
    private String description;
    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_video_details);

        title = getIntent().getStringExtra(EXTRA_TITLE);
        description = getIntent().getStringExtra(EXTRA_DESCRIPTIONS);
        id = getIntent().getStringExtra(EXTRA_ID);

        ButterKnife.inject(this);

        descriptionTextView.setText(description);
        playerView.initialize("AIzaSyASamoWzAeRgsfkL2LsEdBxoBdGIpZAlng", this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        doLayout();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        this.player = player;
        player.addFullscreenControlFlag(FULLSCREEN_FLAG_CUSTOM_LAYOUT | FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
        player.setOnFullscreenListener(this);
        player.setPlayerStateChangeListener(this);
        if (!wasRestored) {
            player.cueVideo(id);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            System.out.println();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            playerView.initialize("AIzaSyASamoWzAeRgsfkL2LsEdBxoBdGIpZAlng", this);
        } else {
            System.out.println();
        }
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        fullscreen = isFullscreen;
        doLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        doLayout();
    }

    private void doLayout() {
        LinearLayout.LayoutParams playerParams =
                (LinearLayout.LayoutParams) playerView.getLayoutParams();
        if (fullscreen) {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        } else {
            playerParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        }
    }

    @Override
    public void onLoading() {

    }

    @Override
    public void onLoaded(String s) {
        player.play();
    }

    @Override
    public void onAdStarted() {

    }

    @Override
    public void onVideoStarted() {

    }

    @Override
    public void onVideoEnded() {

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        System.out.println();
    }
}
