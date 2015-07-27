package com.frame.compat;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

/**
 * Description  ImageView设置背景的兼容
 * Created by chenqiao on 2015/7/27.
 */
public class ImageBackgroundCompat {

    public static void setBackground(ImageView imgView, Drawable drawable) {
        if (Build.VERSION.SDK_INT < 16) {
            imgView.setBackgroundDrawable(drawable);
        } else {
            imgView.setBackground(drawable);
        }
    }
}
