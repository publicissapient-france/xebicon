package fr.xebia.xebicon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class NewBaseAdapter<T, R extends View & BaseItemView<T>> extends android.widget.BaseAdapter {

    public static final int INVALID_POSITION = -1;

    private final int viewResId;
    private final List<T> data = new ArrayList<>();
    protected final LayoutInflater layoutInflater;

    public NewBaseAdapter(Context context, int viewResId) {
        this(context, viewResId, new ArrayList<T>());
    }

    public NewBaseAdapter(Context context, int viewResId, List<T> data) {
        this.viewResId = viewResId;

        setData(data);

        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (position >= 0 && position < data.size()) {
            return getItem(position).hashCode();
        }
        return INVALID_POSITION;
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = newView(parent);
        } else {
            view = convertView;
        }
        bindView(getItem(position), (R) view);
        return view;
    }

    @Override
    public T getItem(int position) {
        if (position >= 0 && position <= data.size()) {
            return data.get(position);
        }
        return null;
    }

    private View newView(ViewGroup parent) {
        return layoutInflater.inflate(viewResId, parent, false);
    }

    private void bindView(T item, R view) {
        view.bindView(item);
    }

    public void setData(List<T> data) {
        this.data.clear();
        if (data != null) {
            this.data.addAll(data);
        }
    }

    public void remove(T o) {
        data.remove(o);
    }

    public void add(T o) {
        data.add(o);
    }

    public List<T> getData() {
        return data;
    }

    public void addData(List<T> data) {
        if (data != null) {
            this.data.addAll(data);
        }
    }
}

