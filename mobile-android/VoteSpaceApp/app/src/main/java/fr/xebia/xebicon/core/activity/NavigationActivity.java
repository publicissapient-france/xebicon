package fr.xebia.xebicon.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.ui.ExploreActivity;
import fr.xebia.xebicon.ui.about.AboutActivity;
import fr.xebia.xebicon.ui.schedule.MyScheduleActivity;
import fr.xebia.xebicon.ui.settings.SettingsActivity;
import fr.xebia.xebicon.ui.speaker.SpeakerActivity;
import fr.xebia.xebicon.ui.timeline.TimelineActivity;
import fr.xebia.xebicon.ui.video.VideoActivity;
import fr.xebia.xebicon.vote.ui.KeynoteActivity;

import static android.support.v4.view.GravityCompat.START;

public abstract class NavigationActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MAIN_CONTENT_FADEOUT_DURATION = 150;

    @InjectView(R.id.toolbar) Toolbar toolbar;
    @InjectView(R.id.nav_view) NavigationView navigationView;
    @InjectView(R.id.drawer_layout) DrawerLayout drawerLayout;

    private int currentNavId;
    private Handler handler = new Handler();

    public NavigationActivity(int layoutId) {
        super(layoutId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentNavId = getNavId();
        navigationView.getMenu().findItem(currentNavId).setCheckable(true);
        navigationView.setCheckedItem(currentNavId);
        navigationView.setNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeKeynoteMenuVisibility();
    }

    private void changeKeynoteMenuVisibility() {
        MenuItem keynoteMenuItem = navigationView.getMenu().findItem(R.id.nav_keynote);
        keynoteMenuItem.setTitle(keynoteMenuItem.getTitle());

        if (Preferences.hasKeynoteEnabled(this)) {
            keynoteMenuItem.setVisible(true);
        } else {
            keynoteMenuItem.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (currentNavId == menuItem.getItemId()) {
            return false;
        }

        switch (menuItem.getItemId()){
            case R.id.nav_myschedule:
                goTo(new Intent(this, MyScheduleActivity.class), true);
                break;

            case R.id.nav_talks:
                goTo(new Intent(this, ExploreActivity.class), true);
                break;

            case R.id.nav_speakers:
                goTo(new Intent(this, SpeakerActivity.class), true);
                break;

            case R.id.nav_keynote:
                goTo(new Intent(this, KeynoteActivity.class), false);
                break;

            case R.id.nav_timeline:
                goTo(new Intent(this, TimelineActivity.class), true);
                break;

            case R.id.nav_video:
                goTo(new Intent(this, VideoActivity.class), true);
                break;

            case R.id.nav_about:
                goTo(new Intent(this, AboutActivity.class), true);
                break;

            case R.id.nav_settings:
                goTo(new Intent(this, SettingsActivity.class), true);
                break;

        }

        drawerLayout.closeDrawers();

        return true;
    }

    private void goTo(final Intent intent, boolean finish) {
        handler.postDelayed(() -> {
            startActivity(intent);

            if (finish){
                View mainContent = findViewById(R.id.main_content);
                if (mainContent != null) {
                    mainContent.animate().alpha(0).setDuration(MAIN_CONTENT_FADEOUT_DURATION);
                }

                finish();
            }
        }, 300);
    }

    protected int getNavId(){
        return R.id.nav_talks;
    }

    @Override
    protected void startKeynote(Intent intent) {
        super.startKeynote(intent);

        changeKeynoteMenuVisibility();
    }

    @Override
    protected void endKeynote() {
        super.endKeynote();

        changeKeynoteMenuVisibility();
    }
}
