package fr.xebia.xebicon.ui.settings;

import android.os.Bundle;
import android.support.v7.app.ActionBar;

import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.activity.NavigationActivity;

public class SettingsActivity extends NavigationActivity {

    public SettingsActivity() {
        super(R.layout.settings_activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.settings);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new SettingsFragment(), SettingsFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected int getNavId() {
        return R.id.nav_settings;
    }

}
