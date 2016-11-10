package fr.xebia.xebicon.ui.video;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeIntents;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.IOException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.XebiConApplication;
import fr.xebia.xebicon.core.adapter.BaseRecyclerAdapter;
import fr.xebia.xebicon.ui.video.view.VideoItemView;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoFragment extends Fragment implements Observer<List<PlaylistItem>>, SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.video_list) RecyclerView videoListView;
    @InjectView(R.id.video_empty) LinearLayout emptyView;
    @InjectView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.video_empty_text) TextView emptyTextView;

    private String nextPageToken;
    private BaseRecyclerAdapter<PlaylistItem, VideoItemView> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new BaseRecyclerAdapter<>(getActivity(), R.layout.item_view_video);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        emptyTextView.setText(Html.fromHtml(getString(R.string.message_empty_instance)));
        videoListView.setAdapter(adapter);
        videoListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        emptyView.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);

        refreshListing();
    }

    private void refreshListing() {
        Observable.<PlaylistItemListResponse>create(subscriber -> {
            try {
                PlaylistItemListResponse videos = XebiConApplication.getVideoApi().getVideos();
                subscriber.onNext(videos);
            } catch (IOException e) {
                subscriber.onError(e);
            }

            subscriber.onCompleted();
        })
                .subscribeOn(Schedulers.io())
                .flatMap(playlistItemListResponse -> {
                    nextPageToken = playlistItemListResponse.getNextPageToken();

                    return Observable.from(playlistItemListResponse.getItems());
                })
                .filter(playlistItem -> {
                    return !playlistItem.getSnippet().getTitle().equals("Private video");
                })
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        swipeRefreshLayout.setRefreshing(false);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onError(Throwable e) {
        System.out.println();
    }

    @Override
    public void onNext(List<PlaylistItem> playlistItems) {
        if (playlistItems.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            adapter.setDatas(playlistItems);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        refreshListing();
    }
}
