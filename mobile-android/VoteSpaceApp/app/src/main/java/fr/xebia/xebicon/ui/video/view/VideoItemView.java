package fr.xebia.xebicon.ui.video.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.adapter.BaseItemView;

public class VideoItemView extends FrameLayout implements BaseItemView<PlaylistItem>, View.OnClickListener {

    @InjectView(R.id.thumbnail) ImageView thumbnail;
    @InjectView(R.id.title) TextView title;
    @InjectView(R.id.description) TextView description;

    private PlaylistItem playlistItem;

    public VideoItemView(Context context) {
        super(context);
    }

    public VideoItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        setOnClickListener(this);
    }

    @Override
    public void bindView(PlaylistItem el) {
        this.playlistItem = el;

        PlaylistItemSnippet snippet = el.getSnippet();

        title.setText(snippet.getTitle());
        description.setText(snippet.getDescription());

        Picasso.with(getContext()).load(snippet.getThumbnails().getHigh().getUrl()).into(thumbnail);
    }

    @Override
    public void onClick(View v) {
        if (YouTubeIntents.isYouTubeInstalled(getContext())) {
            getContext().startActivity(YouTubeIntents.createPlayVideoIntent(getContext(), playlistItem.getSnippet().getResourceId().getVideoId()));
        } else {
            Toast.makeText(getContext(), R.string.youtube_not_installed, Toast.LENGTH_SHORT).show();
        }
    }
}
