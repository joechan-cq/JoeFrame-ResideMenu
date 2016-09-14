package com.demo.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.demo.fragment.DemoFragment;
import com.demo.frameproject.R;
import com.demo.service.DemoService;

import joe.frame.activity.FrameBaseActivity;
import joe.frame.annotations.ViewInject;
import joe.frame.dialog.SweetAlertDialog;
import joe.frame.service.MonitorService;
import joe.frame.utils.LocationUtils;
import joe.frame.utils.LogUtils;
import joe.frame.utils.ServiceUtils;
import joe.frame.utils.ToastUtils;
import joe.frame.view.residemenu.ResideMenu;
import joe.frame.view.residemenu.ResideMenuItem;


public class DemoActivity extends FrameBaseActivity implements View.OnClickListener {

    public ResideMenu residemenu;
    private int i = -1;
    ResideMenuItem firstitem, seconditem;

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        //设置显示内容,可使用setMyContentView，也可使用replaceFragment
        //setMyContentView(R.demo_layout.demo_layout);
        replaceFragment(new DemoFragment(), null);

        //初始化侧滑菜单
        residemenu = initResideMenu(DIRECTION_LEFT, R.mipmap.default_menu_background);
        //添加菜单项
        firstitem = new ResideMenuItem(this, R.mipmap.ic_launcher, "first item");
        seconditem = new ResideMenuItem(this, R.mipmap.ic_launcher, "demo_second item");
        firstitem.setOnClickListener(this);
        seconditem.setOnClickListener(this);
        addMenuItemToMenu(firstitem);
        addMenuItemToMenu(seconditem);

        //添加侧滑菜单头
        ImageView img = new ImageView(this);
        img.setImageResource(R.mipmap.ic_launcher);
        residemenu.addMenuHeader(img, DIRECTION_LEFT, null);

        getToolbar().setLogo(R.mipmap.ic_launcher);//LOGO,无点击事件
        getToolbar().setBackgroundResource(R.color.greenyellow);
        //设置toolbar标题(兼容性)
        setToolbarTitle("test title", false);

        //设置toolbar
        getToolbar().setSubtitle("My sub title");
        //设置弹出菜单的theme
        getToolbar().setPopupTheme(R.style.MenuTheme);
        //左上角按钮，可以绑定点击事件。
        getToolbar().setNavigationIcon(R.mipmap.ic_action_slide_close);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                residemenu.openMenu(DIRECTION_LEFT);
            }
        });

        //启动服务
        Intent intent = new Intent(this, DemoService.class);
        startService(intent);

        ServiceUtils.getInstance(getApplicationContext()).addNeedMonitorredService("com.demo.service.DemoService", true);
        ServiceUtils.getInstance(getApplicationContext()).startMonitorService(new MonitorService.MonitorListener() {
            @Override
            public void serviceIsnotRunning(String serviceClassName) {
                LogUtils.d("joe----monitor say " + serviceClassName + " is not running");
            }
        });

//        WifiUtils wifiUtils = new WifiUtils(this);
//        WifiConfiguration configuration = wifiUtils.createWifiInfo("wifi-ap", "12345678", 3);
//        wifiUtils.enableWifiAp(configuration, true);
        LocationUtils.getInstance(this).locate(new LocationUtils.LocationChangedListener() {
            @Override
            public void onChanged(Location nowLocation) {
                Log.d("chenqiao", String.valueOf(nowLocation.getLatitude()) + ":" + String.valueOf(nowLocation.getLongitude()));
            }
        });
    }

    /**
     * 将Toolbar作为Actionbar处理
     *
     * @return
     */
    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }

    /**
     * 右上角的菜单设置，使用方法和onCreateOptionMenu一样
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //id为menu的xml中定义
            case R.id.action_settings:
                ToastUtils.show(this, "click setting");
                SweetAlertDialog sd = new SweetAlertDialog(this);
                sd.setCancelable(true);
                sd.setCanceledOnTouchOutside(true);
                sd.show();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v == firstitem) {
            new SweetAlertDialog(this)
                    .setContentText("It's pretty, isn't it?")
                    .show();
        }
        if (v == seconditem) {
            new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                    .setTitleText("Good job!")
                    .setContentText("You clicked the button!")
                    .show();
        }
    }

    /**
     * 监听返回键，如果侧滑菜单打开则关闭侧滑菜单，否则进行super调用
     */
    @Override
    public void onBackPressed() {
        if (residemenu.isOpened()) {
            residemenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }
}