package fr.xebia.xebicon.ui.map;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.activity.NavigationActivity;
import fr.xebia.xebicon.ui.settings.SettingsFragment;

public class MapActivity extends NavigationActivity {

    public MapActivity() {
        super(R.layout.activity_map);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.nav_map);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MapFragment(), "MAP")
                    .commit();
        }
    }

    @Override
    protected int getNavId() {
        return -1;
    }
}
