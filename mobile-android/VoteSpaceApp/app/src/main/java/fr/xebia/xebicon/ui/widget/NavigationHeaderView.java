package fr.xebia.xebicon.ui.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.BuildConfig;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.model.Conference;
import rx.Observable;
import se.emilsjolander.sprinkles.Query;

public class NavigationHeaderView extends RelativeLayout {

    @InjectView(R.id.image_background) ImageView background;

    Conference conference;

    public NavigationHeaderView(Context context) {
        super(context);
    }

    public NavigationHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavigationHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()){
            return;
        }

        ButterKnife.inject(this);

        if (conference != null) {
            bindView();
        } else {
            Observable.<Conference>create(subscriber -> {
                Conference conference = Query.one(Conference.class, "SELECT * FROM Conferences where _id = ?", BuildConfig.XEBICON_CONFERENCE_ID).get();

                if (conference == null) {
                    subscriber.onError(new NullPointerException("no conference found"));
                }

                subscriber.onNext(conference);
                subscriber.onCompleted();
            }).subscribe(conference -> {
                this.conference = conference;
                bindView();
            }, throwable -> {

            }, () -> {

            });
        }
    }

    private void bindView() {
        Picasso.with(getContext())
                .load(conference.getBackgroundUrl())
                .fit()
                .centerCrop()
                .into(background);
    }

}