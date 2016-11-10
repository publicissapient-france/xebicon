package fr.xebia.xebicon.ui.about;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.widget.TextView;

import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.NavigationActivity;

public class AboutActivity extends NavigationActivity {

    @InjectView(R.id.content) TextView textView;

    public AboutActivity() {
        super(R.layout.activity_about);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.nav_about);

        textView.setText(Html.fromHtml(getString(R.string.about_content)));
    }

    @Override
    protected int getNavId() {
        return R.id.nav_about;
    }

}
