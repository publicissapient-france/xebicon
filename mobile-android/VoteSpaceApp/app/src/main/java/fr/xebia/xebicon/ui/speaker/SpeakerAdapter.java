package fr.xebia.xebicon.ui.speaker;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.xebia.xebicon.core.adapter.BaseAdapter;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.ui.widget.UnderlinedTextView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

import java.util.List;

public class SpeakerAdapter extends BaseAdapter<List<Speaker>> implements StickyListHeadersAdapter {

    private int mUnderlineColor;
    private int mUnderlineHeight;
    private LayoutInflater inflater;

    public SpeakerAdapter(Context context, int viewResId, List<Speaker> data) {
        super(context, viewResId, data);
        init(context);
    }

    private void init(Context context) {
        inflater = LayoutInflater.from(context);
        Resources resources = context.getResources();
        mUnderlineColor = resources.getColor(R.color.xebia_color);
        mUnderlineHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, resources.getDisplayMetrics()));
    }

    @Override
    protected void bindView(int position, View view) {
        ((SpeakerItemView) view).bindView(getItem(position));
    }

    @Override
    public int getCount() {
        List<Speaker> data = getData();
        return data == null ? 0 : data.size();
    }

    @Override
    public Speaker getItem(int position) {
        return getData().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getHeaderView(int position, View view, ViewGroup viewGroup) {
        UnderlinedTextView header = (UnderlinedTextView) view;
        if (view == null) {
            header = (UnderlinedTextView) inflater.inflate(R.layout.schedule_item_header, viewGroup, false);
            header.setUnderlineColor(mUnderlineColor);
            header.setUnderlineHeight(mUnderlineHeight);
        }

        header.setText(getItem(position).getFirstName().substring(0, 1).toUpperCase());

        return header;
    }

    @Override
    public long getHeaderId(int i) {
        return getItem(i).getFirstName().toUpperCase().charAt(0);
    }
}
