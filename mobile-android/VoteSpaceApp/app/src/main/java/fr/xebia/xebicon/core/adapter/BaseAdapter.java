package fr.xebia.xebicon.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    private Context mContext;
    private int mViewResId;
    protected T mData;
    private LayoutInflater mLayoutInflater;

    public BaseAdapter(Context context, int viewResId, T data) {
        super();
        mContext = context;
        mViewResId = viewResId;
        mData = data;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return mContext;
    }

    public T getData() {
        return mData;
    }

    public void setData(T data){
        mData = data;
    }

    public int getViewResId() {
        return mViewResId;
    }

    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        if (convertView == null) {
            v = newView(parent);
        } else {
            v = convertView;
        }
        bindView(position, v);
        return v;
    }

    protected View newView(ViewGroup parent) {
        return mLayoutInflater.inflate(mViewResId, parent, false);
    }

    protected abstract void bindView(int position, View view);

    public void switchData(T newData) {
        mData = newData;
        notifyDataSetChanged();
    }

    public void invalidateData(T newData) {
        mData = newData;
        notifyDataSetInvalidated();
    }
}
