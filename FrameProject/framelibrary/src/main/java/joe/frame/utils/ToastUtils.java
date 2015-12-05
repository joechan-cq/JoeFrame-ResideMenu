package joe.frame.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * ToastUtils   进行了封装，toast不会进行叠加显示
 *
 * @author chenqiao
 */
public class ToastUtils {

    private static Toast toast;

    private static Context mContext;

    private ToastUtils() {
        throw new AssertionError();
    }

    public static void show(Context context, int resId) {
        show(context, context.getResources().getText(resId), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration) {
        show(context, context.getResources().getText(resId), duration);
    }

    public static void show(Context context, CharSequence text) {
        show(context, text, Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, String format, Object... args) {
        show(context, String.format(format, args), Toast.LENGTH_SHORT);
    }

    public static void show(Context context, int resId, int duration, Object... args) {
        show(context, String.format(context.getResources().getString(resId), args), duration);
    }

    public static void show(Context context, String format, int duration, Object... args) {
        show(context, String.format(format, args), duration);
    }

    public static void show(Context context, CharSequence text, int duration) {
        if (toast == null || mContext == null || mContext != context) {
            toast = Toast.makeText(context, text, duration);
            mContext = context;
        } else {
            toast.setDuration(duration);
            toast.setText(text);
        }
        toast.show();
    }

}
