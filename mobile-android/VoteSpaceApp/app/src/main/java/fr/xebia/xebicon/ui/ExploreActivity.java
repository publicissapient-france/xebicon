package fr.xebia.xebicon.ui;

import android.os.Bundle;

import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.NavigationActivity;
import fr.xebia.xebicon.ui.schedule.ScheduleFragment;

public class ExploreActivity extends NavigationActivity {

    public ExploreActivity() {
        super(R.layout.activity_explore);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isFinishing() && savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, new ScheduleFragment(), HOME_FRAG_TAG)
                    .commit();
        }
    }

    @Override
    protected int getNavId() {
        return R.id.nav_talks;
    }

}
