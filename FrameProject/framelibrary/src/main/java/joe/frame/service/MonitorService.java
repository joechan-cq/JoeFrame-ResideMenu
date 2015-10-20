package joe.frame.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import joe.frame.utils.ServiceUtils;

/**
 * Description  监听服务,需配合ServiceUtils使用
 * Created by chenqiao on 2015/9/11.
 */
public class MonitorService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Thread monitroThrread = new Thread(new Runnable() {
            @Override
            public void run() {
                ServiceUtils utils = ServiceUtils.getInstance(getApplicationContext());
                while (!utils.stopMonitor) {
                    for (String serviceClassName : utils.serviceClassNameLists) {
                        if (!ServiceUtils.isServiceRunning(getApplicationContext(), serviceClassName)) {
                            //如果服务已经不在运行
                            if (utils.monitorListener != null) {
                                utils.monitorListener.serviceIsnotRunning(serviceClassName);
                                //根据配置，再启动服务
                                if (utils.servicesIsAutoRestart.get(serviceClassName)) {
                                    try {
                                        Class serviceClass = Class.forName(serviceClassName);
                                        Intent intent = new Intent(getApplicationContext(), serviceClass);
                                        getApplicationContext().startService(intent);
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    try {
                        Thread.sleep(ServiceUtils.heartTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        monitroThrread.start();
        return START_STICKY;    //监听服务如果被杀，要求能自启
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public interface MonitorListener {
        void serviceIsnotRunning(String serviceClassName);
    }
}
