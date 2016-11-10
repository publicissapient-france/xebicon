package fr.xebia.xebicon.core.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class BaseRecyclerAdapter<T, R extends View & BaseItemView<T>> extends RecyclerView.Adapter<BaseRecyclerAdapter.BaseViewHolder<T, R>> {

    private final LayoutInflater layoutInflater;
    private int viewResId;
    private final List<T> datas = new ArrayList<>();

    public BaseRecyclerAdapter(Context context, int viewResId) {
        this.viewResId = viewResId;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public BaseViewHolder<T, R> onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<>((R) layoutInflater.inflate(viewResId, parent, false));
    }

    @Override
    public void onBindViewHolder(BaseViewHolder<T, R> holder, int position) {
        holder.bindView(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setDatas(List<T> newDatas) {
        datas.clear();
        datas.addAll(newDatas);
    }

    public void setViewResId(int viewResId) {
        this.viewResId = viewResId;
    }

    public T getItem(int position) {
        return datas.get(position);
    }

    public static class BaseViewHolder<T, R extends View & BaseItemView<T>> extends RecyclerView.ViewHolder implements BaseItemView<T> {

        R view;

        public BaseViewHolder(R itemView) {
            super(itemView);

            view = itemView;
        }

        @Override
        public void bindView(T el) {
            view.bindView(el);
        }
    }
}
