package com.demo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.demo.frameproject.R;

import org.simple.eventbus.EventBus;

import joe.frame.activity.FrameSecondaryActivity;
import joe.frame.annotations.ViewInject;
import joe.frame.model.AppUpdateInfo;
import joe.frame.task.AppUpdateTask;
import joe.frame.utils.LogUtils;
import joe.frame.utils.SDCardUtils;
import joe.frame.utils.http.HttpMethod;

/**
 * Description
 * Created by chenqiao on 2015/7/28.
 */
public class DemoSecondActivity extends FrameSecondaryActivity implements View.OnClickListener {

    @ViewInject(R.id.button2)
    private Button btn2;

    @Override
    protected void onSecondaryActivityCreated(Bundle saveInstanceState) {
        setMyContentView(R.layout.demo_secondactivity);
        setToolbarTitle("Myproject", true);
        getToolbar().setSubtitle("sub title");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("123", "setTxt");
            }
        });
        findViewById(R.id.update).setOnClickListener(this);
    }

    @Override
    protected void onToolbarBackClicked() {
        finish();
    }

    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.update:
                AppUpdateTask task = new AppUpdateTask() {
                    @Override
                    public AppUpdateInfo parseUpdateInfo(String info) {
                        AppUpdateInfo myinfo = new AppUpdateInfo();
                        myinfo.setAppName("heater");
                        myinfo.setDownloadUrl("http://ota.53iq.com/static/file/heater_1440677443911812.apk");
                        myinfo.setVersionName("2.0");
                        myinfo.setSuffixName(".apk");
                        myinfo.setUpdateInfo("it's a test");
                        myinfo.setIsNeedToUpdate(true);
                        myinfo.setIsMust(false);
                        return myinfo;
                    }
                };
                String path = "";
                if (SDCardUtils.isSDCardEnable()) {
                    LogUtils.d("sd is enable");
                    path = SDCardUtils.getSDCardPath();
                } else {
                    path = Environment.getDataDirectory().getPath();
                }
                task.checkVersion(this, true, "http://192.168.191.1:8020/AlipayTest/js/update.txt", path + "frame/", HttpMethod.GET);
                break;
        }
    }
}