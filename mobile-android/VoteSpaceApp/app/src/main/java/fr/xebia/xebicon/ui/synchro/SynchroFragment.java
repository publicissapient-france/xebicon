package fr.xebia.xebicon.ui.synchro;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.bus.SynchroFinishedEvent;
import fr.xebia.xebicon.service.SynchroIntentService;
import fr.xebia.xebicon.ui.ExploreActivity;

import static fr.xebia.xebicon.core.XebiConApplication.BUS;

public class SynchroFragment extends Fragment {

    public static final String TAG = "SynchroFragment";

    public static final String ARG_CONFERENCE_ID = "fr.xebia.xebicon.EXTRA_CONFERENCE_ID";

    @InjectView(R.id.progress_container) ViewGroup progressContainer;
    @InjectView(R.id.error_container) ViewGroup errorContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Intent intent = new Intent(getActivity(), SynchroIntentService.class);
        intent.putExtra(SynchroIntentService.EXTRA_CONFERENCE_ID, getArguments().getInt(ARG_CONFERENCE_ID));
        getActivity().startService(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        BUS.register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.synchro_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        progressContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        BUS.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.retry_btn)
    public void onRetryClicked() {
        progressContainer.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        Intent intent = new Intent(getActivity(), SynchroIntentService.class);
        intent.putExtra(SynchroIntentService.EXTRA_CONFERENCE_ID, getArguments().getInt(ARG_CONFERENCE_ID));
        getActivity().startService(intent);
    }

    public void onEventMainThread(SynchroFinishedEvent synchroFinishedEvent) {
        if (synchroFinishedEvent.success) {
            Intent homeIntent = new Intent(getActivity(), ExploreActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(homeIntent);
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), R.string.synchro_failed, Toast.LENGTH_SHORT).show();
            progressContainer.setVisibility(View.GONE);
            errorContainer.setVisibility(View.VISIBLE);
        }

    }

    public static Fragment newInstance(int conferenceId) {
        SynchroFragment synchroFragment = new SynchroFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_CONFERENCE_ID, conferenceId);
        synchroFragment.setArguments(arguments);
        return synchroFragment;
    }
}