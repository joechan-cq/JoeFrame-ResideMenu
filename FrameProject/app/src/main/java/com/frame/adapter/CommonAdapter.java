package com.frame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Description  通用型适配器
 * Created by chenqiao on 2015/8/10.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected List<T> mDatas;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected final int mItemLayoutId;

    public CommonAdapter(Context context, List<T> mDatas, int itemLayoutId) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mItemLayoutId = itemLayoutId;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //  创建或者获取ViewHolder
        final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
        //  View的内容赋值
        convert(viewHolder, getItem(position));
        //  返回convertView
        return viewHolder.getConvertView();
    }

    public abstract void convert(ViewHolder holder, T item);

    private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
        return ViewHolder.get(mContext, convertView, parent, mItemLayoutId, position);
    }
}