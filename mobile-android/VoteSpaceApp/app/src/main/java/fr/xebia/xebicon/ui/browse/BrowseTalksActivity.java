package fr.xebia.xebicon.ui.browse;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;

public class BrowseTalksActivity extends BaseActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;

    public static final String EXTRA_TITLE = "fr.xebia.xebicon.EXTRA_TITLE";
    public static String EXTRA_AVAILABLE_TALKS = "fr.xebia.xebicon.EXTRA_AVAILABLE_TALKS";

    public BrowseTalksActivity() {
        super(R.layout.browse_talks_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(EXTRA_TITLE));

        if (getFragmentManager().findFragmentByTag(BrowseTalksFragment.TAG) == null) {
            ArrayList<String> availableTalksIds = getIntent().getStringArrayListExtra(EXTRA_AVAILABLE_TALKS);
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_content, BrowseTalksFragment.newInstance(availableTalksIds), BrowseTalksFragment.TAG)
                    .commit();
        }
    }

}
