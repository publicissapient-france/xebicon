package fr.xebia.xebicon.ui.conference;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;

import butterknife.InjectView;
import fr.xebia.xebicon.BuildConfig;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.ui.synchro.SynchroFragment;

public class ConferenceChooserActivity extends BaseActivity {

    @InjectView(R.id.toolbar) Toolbar toolbar;

    public ConferenceChooserActivity() {
        super(R.layout.conference_chooser_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        mDontCheckConference = true;
        getWindow().setBackgroundDrawableResource(android.R.color.white);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(Html.fromHtml(getString(R.string.action_bar_default_title)));
        actionBar.setDisplayHomeAsUpEnabled(false);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, SynchroFragment.newInstance(BuildConfig.XEBICON_CONFERENCE_ID), SynchroFragment.TAG)
                    .addToBackStack("synchro")
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
