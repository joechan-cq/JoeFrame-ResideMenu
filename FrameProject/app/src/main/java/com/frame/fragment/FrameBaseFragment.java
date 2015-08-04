package com.frame.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.demo.frameproject.R;
import com.frame.activity.FrameBaseActivity;
import com.frame.utils.KeyBoardUtils;

import org.simple.eventbus.EventBus;

/**
 * Description  框架基础Fragment
 * Created by chenqiao on 2015/7/16.
 */
public abstract class FrameBaseFragment extends Fragment {

    protected FrameBaseActivity context;
    private FrameLayout frameLayout;
    private View contentView;
    private FragmentManager fragmentManager;
    private boolean isregisterEventBus = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_fragment_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        frameLayout = (FrameLayout) view.findViewById(R.id.rootlayout_basefragment);
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (FrameBaseActivity) getActivity();
        fragmentManager = getFragmentManager();
        onMyFragmentCreate(savedInstanceState);
    }

    /**
     * Fragment创建
     *
     * @param savedInstanceState
     * @author chenqiao
     */
    protected abstract void onMyFragmentCreate(Bundle savedInstanceState);

    /**
     * 注册EventBus
     */
    protected void registerEventBus() {
        EventBus.getDefault().register(this);
        isregisterEventBus = true;
    }

    /**
     * 重写onDestroy，如果注册了EventBus，则需要注销
     */
    @Override
    public void onDestroy() {
        if (isregisterEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    protected FrameLayout getRootLayout() {
        return frameLayout;
    }

    /**
     * 获取Fragment显示的View
     *
     * @return
     */
    public View getContentView() {
        return contentView;
    }

    /**
     * 设置内容
     *
     * @param resID
     */
    protected void setMyContentView(int resID) {
        frameLayout.removeAllViews();
        contentView = LayoutInflater.from(context).inflate(resID, frameLayout);
    }

    protected void setMyContentView(View view) {
        contentView = view;
        frameLayout.removeAllViews();
        frameLayout.addView(view);
    }

    /**
     * 替换Activity的内容
     *
     * @param fragment
     * @param isBackStack
     */
    protected void replaceFragment(FrameBaseFragment fragment, String isBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(isBackStack)) {
            fragmentTransaction.replace(context.getRootFrameLayoutId(), fragment);
        } else {
            fragmentTransaction.replace(context.getRootFrameLayoutId(), fragment, isBackStack);
            fragmentTransaction.addToBackStack(isBackStack);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 在{@link #setMyContentView(int) or #setMyContentView(View)}之后获取其中View
     *
     * @param resId
     * @return
     */
    protected View findViewById(int resId) {
        return frameLayout.findViewById(resId);
    }

    /**
     * 结束当前fragment
     */
    protected void finish() {
        /**
         * 如果当前fragment不是根fragment
         */
        if (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        onCreateMyToolbarMenu(menu, inflater);
    }

    /**
     * 创建Toolbar菜单，使用方法同{@link #onCreateOptionsMenu}
     * 菜单点击事件监听，实现{@link #onMyToolbarMenuItemClicked(MenuItem)}
     *
     * @param menu
     * @param inflater
     */
    protected abstract void onCreateMyToolbarMenu(Menu menu, MenuInflater inflater);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return onMyToolbarMenuItemClicked(item);
    }

    /**
     * Toolbar菜单被点击事件监听
     *
     * @param item
     * @return
     */
    protected abstract boolean onMyToolbarMenuItemClicked(MenuItem item);
}
