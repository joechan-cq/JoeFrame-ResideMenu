package joe.framelibrary.frame.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import joe.framelibrary.frame.compat.BackgroundDrawableCompat;

/**
 * Description  通用型ViewHolder
 * Created by chenqiao on 2015/8/10.
 */
public class ViewHolder {

    private final SparseArray<View> mViews;
    private View mConvertView;
    private int mPosition;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<View>();
        this.mPosition = position;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }

    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        }
        return (ViewHolder) convertView.getTag();
    }

    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    // 设置监听器
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    // TextView及其子类
    public ViewHolder setHintText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setHint(text);
        return this;
    }

    public ViewHolder setHintText(int viewId, int textid) {
        TextView view = getView(viewId);
        view.setHint(textid);
        return this;
    }

    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public ViewHolder setText(int viewId, int textId) {
        TextView view = getView(viewId);
        view.setText(textId);
        return this;
    }


    // ImageView及其子类
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public ViewHolder setImageBackground(int viewId, Drawable drawable) {
        ImageView view = getView(viewId);
        BackgroundDrawableCompat.setImageBackground(view, drawable);
        return this;
    }

    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    // CompoundButton及其子类
    public ViewHolder setButtonChecked(int viewId, boolean tf) {
        CompoundButton btn = getView(viewId);
        btn.setChecked(tf);
        return this;
    }

    public ViewHolder setButtonDrawable(int viewId, Drawable drawable) {
        CompoundButton btn = getView(viewId);
        btn.setButtonDrawable(drawable);
        return this;
    }

    public ViewHolder setButtonDrawable(int viewId, int resId) {
        CompoundButton btn = getView(viewId);
        btn.setButtonDrawable(resId);
        return this;
    }

    // Button
    public ViewHolder setButtonBackground(int viewId, Drawable drawable) {
        Button btn = getView(viewId);
        BackgroundDrawableCompat.setButtonBackground(btn, drawable);
        return this;
    }

    public ViewHolder setButtonBackground(int viewId, int resId) {
        Button btn = getView(viewId);
        btn.setBackgroundResource(resId);
        return this;
    }
}