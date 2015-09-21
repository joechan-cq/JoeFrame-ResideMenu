package com.demo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.demo.activity.DemoSecondActivity;
import com.demo.frameproject.R;

import org.simple.eventbus.Subscriber;

import joe.frame.annotations.ViewInject;
import joe.frame.dialog.DirChooserDialog;
import joe.frame.fragment.FrameBaseFragment;
import joe.frame.utils.ToastUtils;

/**
 * Description
 * Created by chenqiao on 2015/7/23.
 */
public class SecondFragment extends FrameBaseFragment {

    @ViewInject(R.id.button)
    private Button startBtn;

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        registerEventBus();
        setMyContentView(R.layout.demo_second);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DemoSecondActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DirChooserDialog dialog = new DirChooserDialog(getActivity(), 1, null, "/mnt/");
                dialog.setOkClickListener(new DirChooserDialog.OkClickListener() {
                    @Override
                    public void pathChoose(String path) {
                        ToastUtils.show(getActivity(), path);
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
        return false;
    }

    /**
     * 接受EventBus的消息传递
     *
     * @param text
     */
    @Subscriber(tag = "setTxt")
    private void settext(String text) {
        ((TextView) findViewById(R.id.mytitle)).setText(text);
    }
}
