package joe.frame.utils;

import android.content.Context;
import android.provider.Settings;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TimeUtils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-8-24
 */
public class TimeUtils {

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd");

    private TimeUtils() {
        throw new AssertionError();
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * long time to string, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @param timeInMillis
     * @return
     */
    public static String getTime(long timeInMillis) {
        return getTime(timeInMillis, DEFAULT_DATE_FORMAT);
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

    /**
     * get current time in milliseconds, format is {@link #DEFAULT_DATE_FORMAT}
     *
     * @return
     */
    public static String getCurrentTimeInString() {
        return getTime(getCurrentTimeInLong());
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * 设置系统时间，需要root权限
     */
    public static void setSystemTime(Date ServiceTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
        String datetime = sdf.format(ServiceTime);
        String command = "date -s  \"" + datetime + "\"";
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "settings put global auto_time 0"});
            Runtime.getRuntime().exec(new String[]{"su", "-c", command});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置是否开启自动获取时间，需要root权限
     */
    public static void enableAutoTime(boolean isAuto) {
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "settings put global auto_time " + (isAuto ? 1 : 0)});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAutoTime(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME) == 1;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean is24HourFormat(Context context) {
        String format = Settings.System.getString(context.getContentResolver(), Settings.System.TIME_12_24);
        return format.equals("24");
    }

    public static boolean set12Format(Context context) {
        return Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24, "12");
    }

    public static boolean set24Format(Context context) {
        return Settings.System.putString(context.getContentResolver(),
                Settings.System.TIME_12_24, "24");
    }
}
