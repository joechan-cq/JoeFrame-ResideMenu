package joe.frame.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import joe.frame.service.MonitorService;

/**
 * Description  服务工具类
 * Created by chenqiao on 2015/8/28.
 */
public class ServiceUtils {

    private static ServiceUtils instance;

    private Context mContext;

    //  每次检测服务的间隔
    public static long heartTime = 10000;

    //  需要监听的服务名
    public ArrayList<String> serviceClassNameLists;

    //  服务停止后是否由监听服务再启动的配置
    public HashMap<String, Boolean> servicesIsAutoRestart;

    //  停止监听线程的标识
    public boolean stopMonitor = false;

    //  服务监听的回调
    public MonitorService.MonitorListener monitorListener = null;

    private ServiceUtils() {
    }

    private ServiceUtils(Context context) {
        this.mContext = context;
        serviceClassNameLists = new ArrayList<>();
        servicesIsAutoRestart = new HashMap<>();
    }

    /**
     * 单例模式
     *
     * @param context 推荐传入ApplicationContext
     * @return 单例
     */
    public static ServiceUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (ServiceUtils.class) {
                if (instance == null) {
                    instance = new ServiceUtils(context);
                }
            }
        }
        return instance;
    }

    /**
     * 检测某个服务是否在运行
     *
     * @param context          上下文内容类
     * @param serviceClassName 服务类名
     * @return 服务是否正在运行
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

    /**
     * 添加需要监听的服务(默认不会再启动)
     *
     * @param serviceClassName 服务名
     * @return 是否添加成功
     */
    public boolean addNeedMonitorredService(String serviceClassName) {
        return addNeedMonitorredService(serviceClassName, false);
    }

    /**
     * 添加需要监听的服务以及再启动配置
     *
     * @param serviceClassName 服务名
     * @param isAutoRestart    是否再启动
     * @return 是否添加成功
     */
    public boolean addNeedMonitorredService(String serviceClassName, boolean isAutoRestart) {
        //如果已经添加过了，则更新再启动配置
        if (serviceClassNameLists.contains(serviceClassName)) {
            servicesIsAutoRestart.put(serviceClassName, isAutoRestart);
            return true;
        }
        boolean isok = serviceClassNameLists.add(serviceClassName);
        if (isok) {
            servicesIsAutoRestart.put(serviceClassName, isAutoRestart);
        }
        return isok;
    }

    /**
     * 移除需要监听的服务
     *
     * @param serviceClassName 服务名
     * @return 是否移除成功
     */
    public boolean removeMonitorredService(String serviceClassName) {
        boolean isok = serviceClassNameLists.remove(serviceClassName);
        if (isok) {
            servicesIsAutoRestart.remove(serviceClassName);
        }
        return isok;
    }

    /**
     * 清空需要监听的服务列表
     */
    public void clearMonitorredService() {
        serviceClassNameLists.clear();
        servicesIsAutoRestart.clear();
    }

    /**
     * 设置监听间隔
     *
     * @param time time毫秒
     */
    public void setHeartTime(long time) {
        heartTime = time;
    }

    /**
     * 开启监听服务
     *
     * @param listener 监听服务回调
     */
    public void startMonitorService(MonitorService.MonitorListener listener) {
        this.monitorListener = listener;
        stopMonitor = false;
        Intent intent = new Intent(mContext, MonitorService.class);
        mContext.startService(intent);
    }

    /**
     * 移除监听服务回调
     */
    public void removeMonitorListener() {
        this.monitorListener = null;
    }

    /**
     * 停止监听服务
     */
    public void stopMonitorService() {
        stopMonitor = true;
        Intent intent = new Intent(mContext, MonitorService.class);
        mContext.stopService(intent);
    }
}