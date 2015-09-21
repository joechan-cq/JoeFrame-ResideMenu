package joe.frame.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import joe.framelibrary.R;

/**
 * Description 次级Activity，只是添加了返回键
 * Created by chenqiao on 2015/7/28.
 */
public abstract class FrameSecondaryActivity extends FrameBaseActivity {
    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        getToolbar().setNavigationIcon(R.mipmap.ic_action_back);
        getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToolbarBackClicked();
            }
        });
        onSecondaryActivityCreated(savedInstanceState);
    }

    protected abstract void onSecondaryActivityCreated(Bundle saveInstanceState);

    /**
     * Toobar返回键点击事件
     */
    protected abstract void onToolbarBackClicked();

    @Override
    protected void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    protected boolean onMyToolbarMenuItemClicked(MenuItem item) {
        return false;
    }
}