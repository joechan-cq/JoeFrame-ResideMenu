package com.demo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import joe.frame.utils.LogUtils;

/**
 * Description
 * Created by chenqiao on 2015/9/11.
 */
public class DemoService extends Service {
    public DemoService() {
        super();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("joe----service is start");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.d("joe----service is destroyed");
        super.onDestroy();
    }
}
