package joe.frame.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Description  服务工具类
 * Created by chenqiao on 2015/8/28.
 */
public class ServiceUtils {

    /**
     * 检测某个服务是否在运行
     *
     * @param context          上下文内容类
     * @param serviceClassName 服务类名
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceClassName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)) {
                return true;
            }
        }
        return false;
    }
}
