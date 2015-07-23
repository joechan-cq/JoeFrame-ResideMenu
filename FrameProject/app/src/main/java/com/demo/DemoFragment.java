package com.demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.demo.frameproject.R;
import com.frame.fragment.FrameBaseFragment;

/**
 * Description
 * Created by chenqiao on 2015/7/20.
 */
public class DemoFragment extends FrameBaseFragment {
    @Override
    protected void onMyFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.weblayout);

        findViewById(R.id.juemp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SecondFragment(), "Second");
            }
        });
        //要使Fragment重写Toolbar的菜单栏，需设置true
        setHasOptionsMenu(true);
    }

    //重写Toolbar的菜单
    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {
        //如果不进行clear，将会保留Activity的菜单（根据实际情况选择是否clear）
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
        return false;
    }
}
