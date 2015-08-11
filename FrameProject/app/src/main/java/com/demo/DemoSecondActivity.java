package com.demo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.demo.frameproject.R;

import org.simple.eventbus.EventBus;

import joe.framelibrary.frame.activity.FrameSecondaryActivity;
import joe.framelibrary.frame.annotations.ViewInject;

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
}
