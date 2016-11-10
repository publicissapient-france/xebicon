package fr.xebia.xebicon.ui.schedule;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.bus.SyncEvent;
import fr.xebia.xebicon.model.ConferenceDay;
import fr.xebia.xebicon.model.MyScheduleItem;

import java.util.List;

import static fr.xebia.xebicon.core.XebiConApplication.BUS;

public class MyScheduleFragment extends Fragment {

    @InjectView(android.R.id.list) ListView myScheduleListView;

    private MyScheduleAdapter mAdapter;
    private List<MyScheduleItem> myScheduleItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_schedule_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mAdapter = new MyScheduleAdapter(getActivity());
        myScheduleListView.setAdapter(mAdapter);
        if (myScheduleItems != null) {
            mAdapter.updateItems(myScheduleItems);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        BUS.register(this);
    }

    public void onEventMainThread(SyncEvent syncEvent) {
        if(mAdapter != null){
            mAdapter.forceUpdate();
        }
    }

    @Override
    public void onStop() {
        BUS.unregister(this);
        super.onStart();
    }

    public static Fragment newInstance() {
        return new MyScheduleFragment();
    }

    public void setData(ConferenceDay conferenceDay) {
        myScheduleItems = conferenceDay.myScheduleItems;
        if (mAdapter != null) {
            mAdapter.updateItems(myScheduleItems);
        }
    }
}
