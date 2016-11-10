/**
 * *******************************************************************
 * AUTHOR：YOLANDA
 * DATE：2015年4月8日下午4:41:56
 * Copyright © 56iq. All Rights Reserved
 * ======================================================================
 * EDIT HISTORY
 * ----------------------------------------------------------------------
 * |  DATE      | NAME       | REASON       | CHANGE REQ.
 * ----------------------------------------------------------------------
 * | 2015年4月8日    | YOLANDA    | Created      |
 * <p>
 * DESCRIPTION：create the File, and add the content.
 * <p>
 * *********************************************************************
 */
package joe.frame.compat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;

public class ResourcesCompat {

    public static Drawable getDrawable(Context context, int resId) {
        return getDrawable(context, resId, null);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static Drawable getDrawable(Context context, int resId, Theme theme) {
        Resources resources = context.getResources();
        if (VERSION.SDK_INT >= 21) {
            return resources.getDrawable(resId, theme);//higher than level 21
        } else {
            return resources.getDrawable(resId);//smaller than level 21
        }
    }
}
