package fr.xebia.xebicon.ui.rating;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.utils.Compatibility;
import fr.xebia.xebicon.ui.talk.TalkActivity;

import static fr.xebia.xebicon.ui.talk.TalkActivity.EXTRA_TALK_COLOR;

public class RatingActivity extends BaseActivity {
    @InjectView(R.id.toolbar) Toolbar toolbar;

    public RatingActivity() {
        super(R.layout.activity_rating);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (shouldBeFloatingWindow()) {
            setupFloatingWindow();
        }

        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.rating_title);

        toolbar.setBackgroundColor(getIntent().getIntExtra(EXTRA_TALK_COLOR, Color.BLACK));

        if (Compatibility.isCompatible(Build.VERSION_CODES.LOLLIPOP)) {
            getWindow().setStatusBarColor(Compatibility.darker(getIntent().getIntExtra(EXTRA_TALK_COLOR, Color.BLACK)));
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_content,
                            RatingFragment.newInstance(
                                    getIntent().getStringExtra(TalkActivity.EXTRA_TALK_ID),
                                    getIntent().getStringExtra(TalkActivity.EXTRA_TALK_TITLE)))
                    .commit();
        }
    }

}
