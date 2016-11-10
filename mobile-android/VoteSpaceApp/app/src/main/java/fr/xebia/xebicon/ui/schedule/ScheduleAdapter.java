package fr.xebia.xebicon.ui.schedule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.adapter.BaseRecyclerAdapter;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.browse.TalkItemView;
import fr.xebia.xebicon.ui.widget.CollectionViewCallbacks;

public class ScheduleAdapter extends BaseRecyclerAdapter<Talk, TalkItemView> {

    private final long conferenceEndTime;
    private final Context context;

    public ScheduleAdapter(Context context, int viewResId) {
        super(context, viewResId);
        this.context = context;
        conferenceEndTime = Preferences.getSelectedConferenceEndTime(context);
    }

}
