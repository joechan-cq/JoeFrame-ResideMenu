package com.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.demo.frameproject.R;
import com.frame.fragment.FrameBaseFragment;

import org.simple.eventbus.Subscriber;

/**
 * Description
 * Created by chenqiao on 2015/7/23.
 */
public class SecondFragment extends FrameBaseFragment {
    @Override
    protected void onMyFragmentCreate(Bundle savedInstanceState) {
        registerEventBus();
        setMyContentView(R.layout.second);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DemoSecondActivity.class);
                startActivity(intent);
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
