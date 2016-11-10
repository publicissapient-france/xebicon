package fr.xebia.xebicon.ui.speaker;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.utils.Compatibility;

public class SpeakerDetailsActivity extends BaseActivity {

    public static final String EXTRA_SPEAKER_ID = "fr.xebia.xebicon.EXTRA_SPEAKER_ID";
    public static final String EXTRA_COLOR = "fr.xebia.xebicon.EXTRA_COLOR";

    @InjectView(R.id.toolbar) Toolbar toolbar;

    public SpeakerDetailsActivity() {
        super(R.layout.speaker_details_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.speaker_details);

        int intExtra = getIntent().getIntExtra(EXTRA_COLOR, -1);
        if (intExtra != -1){
            toolbar.setBackgroundColor(intExtra);
            if (Compatibility.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
                getWindow().setStatusBarColor(Compatibility.darker(getIntent().getIntExtra(EXTRA_COLOR, Color.BLACK)));
            }
        }

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_content, SpeakerDetailsFragment.newInstance(getIntent().getStringExtra(EXTRA_SPEAKER_ID), getIntent().getIntExtra(EXTRA_COLOR, Color.BLACK)))
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
