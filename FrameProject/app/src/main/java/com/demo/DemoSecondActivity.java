package com.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.demo.frameproject.R;

import org.simple.eventbus.EventBus;

import joe.frame.activity.FrameSecondaryActivity;
import joe.frame.annotations.ViewInject;

/**
 * Description
 * Created by chenqiao on 2015/7/28.
 */
public class DemoSecondActivity extends FrameSecondaryActivity {

    @ViewInject(R.id.button2)
    private Button btn2;

    @Override
    protected void onMySecondaryActivityCreated(Bundle saveInstanceState) {
        setMyContentView(R.layout.demo_secondactivity);
        setToolbarTitle("Myproject", true);
        getToolbar().setSubtitle("sub title");
        getToolbar().setNavigationIcon(R.mipmap.ic_launcher);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post("123", "setTxt");
            }
        });
    }

    @Override
    protected void onToolbarBackClicked() {
        finish();
    }

    @Override
    protected boolean setToolbarAsActionbar() {
        return true;
    }
}