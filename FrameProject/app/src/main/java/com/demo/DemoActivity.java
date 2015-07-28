package com.demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.demo.frameproject.R;
import com.frame.activity.FrameBaseActivity;
import com.frame.dialog.SweetAlertDialog;
import com.frame.utils.ToastUtils;
import com.frame.view.residemenu.ResideMenu;
import com.frame.view.residemenu.ResideMenuItem;


public class DemoActivity extends FrameBaseActivity implements View.OnClickListener {

    public ResideMenu residemenu;
    private int i = -1;
    ResideMenuItem firstitem, seconditem;

    @Override
    protected void onMyActivityCreated(Bundle savedInstanceState) {
        //设置显示内容,可使用setMyContentView，也可使用replaceFragment
        //setMyContentView(R.layout.layout);
        replaceFragment(new DemoFragment(), null);


        //初始化侧滑菜单
        residemenu = initResideMenu(DIRECTION_LEFT, R.mipmap.default_menu_background);
        //添加菜单项
        firstitem = new ResideMenuItem(this, R.mipmap.ic_launcher, "first item");
        seconditem = new ResideMenuItem(this, R.mipmap.ic_launcher, "second item");
        firstitem.setOnClickListener(this);
        seconditem.setOnClickListener(this);
        addMenuItemToMenu(firstitem);
        addMenuItemToMenu(seconditem);

        //添加侧滑菜单头
        ImageView img = new ImageView(this);
        img.setImageResource(R.mipmap.ic_launcher);
        residemenu.addMenuHeader(img, DIRECTION_LEFT, null);

        //设置toolbar
        getToolbar().setLogo(R.mipmap.ic_launcher);//LOGO,无点击事件
        getToolbar().setTitle("My App");
        getToolbar().setSubtitle("My sub title");
        //左上角按钮，可以绑定点击事件。
        getToolbar().setNavigationIcon(R.mipmap.ic_action_slide_close);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                residemenu.openMenu(DIRECTION_LEFT);
            }
        });

        //进行HTTP请求
        AboutTask.getInstance().getAboutForString();
        AboutTask.getInstance().getAboutForJson(this);
    }

    /**
     * 右上角的菜单设置，使用方法和onCreateOptionMenu一样
     *
     * @param menu
     * @param inflater
     */
    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    /**
     * 右上角菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
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
}