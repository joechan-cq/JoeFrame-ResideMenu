package com.demo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.demo.frameproject.R;
import com.frame.fragment.FrameBaseFragment;

/**
 * Description
 * Created by chenqiao on 2015/7/23.
 */
public class SecondFragment extends FrameBaseFragment {
    @Override
    protected void onMyFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.second);
    }

    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
        return false;
    }
}
