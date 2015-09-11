package joe.frame.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;

import joe.frame.annotations.ViewInject;
import joe.frame.fragment.FrameBaseFragment;
import joe.frame.utils.KeyBoardUtils;
import joe.frame.view.residemenu.ResideMenu;
import joe.frame.view.residemenu.ResideMenuItem;
import joe.framelibrary.R;

/**
 * Description  框架基础Activity类
 * Created by chenqiao on 2015/7/15.
 */
public abstract class FrameBaseActivity extends AppCompatActivity {

    protected AppCompatActivity context;
    /**
     * 替代Actionbar的Toolbar
     */
    private Toolbar mToolbar;

    private TextView mTitleTv;

    private FragmentManager fragmentManager;
    /**
     * Toolbar之下的layout
     */
    private FrameLayout mContentLayout;

    /**
     * 侧滑菜单
     */
    private ResideMenu mResideMenu;

    /**
     * 是否注册了EventBus，true时会在onDestroy()中自动注销
     */
    private boolean isregisterEventBus = false;

    private boolean isSupportActionbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_activity_layout);
        mContentLayout = (FrameLayout) findViewById(R.id.rootlayout_baseactivity);
        context = this;
        ActivityTaskStack.add(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        mToolbar.setBackgroundResource(R.color.royalblue);

        mTitleTv = (TextView) findViewById(R.id.toolbarTitle);

        if (setToolbarAsActionbar()) {
            isSupportActionbar = true;
            setSupportActionBar(mToolbar);
        } else {
            isSupportActionbar = false;
            setSupportActionBar(null);
        }

        setTitle("");
        fragmentManager = getSupportFragmentManager();
        onMyActivityCreated(savedInstanceState);
    }

    /**
     * 设定是否将Toolbar作为Actionbar使用，将会
     * 影响Toolbar的菜单的使用方法。
     *
     * @return true则使用{@link #onCreateMyToolbarMenu()}进行菜单创建
     */
    protected abstract boolean setToolbarAsActionbar();

    public void setToolbarTitle(String title, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(title);
            } else {
                mTitleTv.setText(title);
            }
        }
    }

    public void setToolbarTitle(int resid, boolean isCenter) {
        if (isSupportActionbar) {
            if (!isCenter) {
                getSupportActionBar().setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        } else {
            if (!isCenter) {
                mToolbar.setTitle(resid);
            } else {
                mTitleTv.setText(resid);
            }
        }
    }

    public void setCenterTitleColor(int color) {
        if (mTitleTv != null) {
            mTitleTv.setTextColor(color);
        }
    }

    public void setCenterTitleSize(float size) {
        if (mTitleTv != null) {
            mTitleTv.setTextSize(size);
        }
    }

    /**
     * 代替onCreate的入口类
     *
     * @param savedInstanceState
     */
    protected abstract void onMyActivityCreated(Bundle savedInstanceState);


    /**
     * 注册EventBus
     */
    protected void registerEventBus() {
        EventBus.getDefault().register(this);
        isregisterEventBus = true;
    }

    protected void registerEventBusForSticky() {
        EventBus.getDefault().registerSticky(this);
        isregisterEventBus = true;
    }

    /**
     * 重写onDestroy，如果注册了EventBus，则需要注销
     */
    @Override
    protected void onDestroy() {
        if (isregisterEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        onCreateMyToolbarMenu(menu, inflater);
        return true;
    }

    /**
     * 创建Toolbar菜单，使用方法同{@link #onCreateOptionsMenu}
     * 菜单点击事件监听，实现{@link #onMyToolbarMenuItemClicked(MenuItem)}
     * {@link #setToolbarAsActionbar()}为true时生效
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
     * Toolbar菜单被点击事件监听,{@link #setToolbarAsActionbar()}为true时生效
     *
     * @param item 菜单项
     * @return false点击事件继续发放，true消费该事件
     */
    protected abstract boolean onMyToolbarMenuItemClicked(MenuItem item);

    /**
     * 获取内容布局id
     *
     * @return 根布局Id
     */
    public int getRootFrameLayoutId() {
        return R.id.rootlayout_baseactivity;
    }

    /**
     * 获取Toolbar下的根布局
     *
     * @return 根布局
     */
    public FrameLayout getRootFrameLayout() {
        return mContentLayout;
    }

    /**
     * 设置Activity的中心内容
     *
     * @param layoutResID 资源Id
     */
    protected void setMyContentView(int layoutResID) {
        if (mContentLayout != null) {
            LayoutInflater.from(this).inflate(layoutResID, mContentLayout, true);
        }
        autoInjectViewField();
    }

    protected void setMyContentView(View view) {
        mContentLayout.removeAllViews();
        mContentLayout.addView(view);
        autoInjectViewField();
    }

    /**
     * 解析注解，给带有@ViewInject注解的View赋值
     */
    private void autoInjectViewField() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();//获得Activity中声明的字段
            for (Field field : fields) {
                // 查看这个字段是否有我们自定义的注解类标志的
                if (field.isAnnotationPresent(ViewInject.class)) {
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true);
                        field.set(this, mContentLayout.findViewById(id));//给我们要找的字段设置值
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
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
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment);
        } else {
            fragmentTransaction.replace(R.id.rootlayout_baseactivity, fragment, isBackStack);
            fragmentTransaction.addToBackStack(isBackStack);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Toolbar相关
     */
    public Toolbar getToolbar() {
        if (mToolbar != null) {
            return mToolbar;
        } else {
            return null;
        }
    }

    public void hideToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
    }

    public void showToolbar() {
        if (mToolbar != null) {
            mToolbar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 侧滑菜单相关
     */
    //侧滑时页面缩小系数
    private static float DEFAULT_SCALE = 0.6f;
    //默认侧滑方向
    private int direction = ResideMenu.DIRECTION_LEFT;
    //侧滑方向常量
    protected static final int DIRECTION_LEFT = ResideMenu.DIRECTION_LEFT;
    protected static final int DIRECTION_RIGHT = ResideMenu.DIRECTION_RIGHT;

    /**
     * 初始化侧滑菜单，不设定侧滑方向
     *
     * @return mResideMenu
     */
    protected ResideMenu initResideMenu(int backgroundResId) {
        mResideMenu = new ResideMenu(this);
        mResideMenu.attachToActivity(this);
        mResideMenu.setScaleValue(DEFAULT_SCALE);
        mResideMenu.setBackground(backgroundResId);
        return mResideMenu;
    }

    /**
     * 获取侧滑菜单对象
     *
     * @return 侧滑菜单实例
     */
    public ResideMenu getResideMenu() {
        if (mResideMenu != null) {
            return mResideMenu;
        } else {
            return null;
        }
    }

    /**
     * 初始化侧滑菜单
     *
     * @param swipeDirection 侧滑方向
     * @return mResideMenu
     */
    protected ResideMenu initResideMenu(int swipeDirection, int backgroundResId) {
        this.direction = swipeDirection;
        mResideMenu = new ResideMenu(this);
        mResideMenu.attachToActivity(this);
        mResideMenu.setBackground(backgroundResId);
        mResideMenu.setScaleValue(DEFAULT_SCALE);
        switch (swipeDirection) {
            case ResideMenu.DIRECTION_LEFT:
                mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
                break;
            case ResideMenu.DIRECTION_RIGHT:
                mResideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_LEFT);
        }
        return mResideMenu;
    }

    /**
     * 添加单个item到侧滑菜单
     *
     * @param item
     */
    protected void addMenuItemToMenu(ResideMenuItem item) {
        if (mResideMenu != null) {
            mResideMenu.addMenuItem(item, this.direction);
        }
    }

    /**
     * 添加多个item到侧滑菜单
     *
     * @param items
     */
    protected void addMenuItemsToMenu(ArrayList<ResideMenuItem> items) {
        if (mResideMenu != null) {
            for (ResideMenuItem item : items) {
                mResideMenu.addMenuItem(item, this.direction);
            }
        }
    }

    /**
     * 添加滑动事件忽略View
     *
     * @param view 需要自己处理滑动事件的View
     */
    protected void addIgnoredView(View view) {
        if (mResideMenu != null) {
            mResideMenu.addIgnoredView(view);
        }
    }

    /**
     * 将整个Fragment的内容添加到忽略View中
     *
     * @param fragment
     */
    protected void addIgnoredFragment(FrameBaseFragment fragment) {
        if (mResideMenu != null) {
            mResideMenu.addIgnoredView(fragment.getContentView());
        }
    }

    /**
     * 重写触碰事件，如果添加了ResideMenu，则由ResideMenu处理滑动事件；
     * 如果不希望ResideMenu处理滑动事件的区域使用方法{#ResideMenu.addIgnoredView(view)}
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mResideMenu != null) {
            return mResideMenu.dispatchTouchEvent(ev);
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }
}