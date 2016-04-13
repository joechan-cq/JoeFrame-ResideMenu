package joe.frame.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.simple.eventbus.EventBus;

import java.lang.reflect.Field;

import joe.frame.activity.FrameBaseActivity;
import joe.frame.annotations.ViewInject;
import joe.frame.utils.KeyBoardUtils;
import joe.framelibrary.R;

/**
 * Description  框架基础Fragment
 * Created by chenqiao on 2015/7/16.
 */
public abstract class FrameBaseFragment extends Fragment {

    protected FrameBaseActivity context;
    private FrameLayout frameLayout;
    private TextView loadingTv;
    private View contentView;
    private FragmentManager fragmentManager;
    private boolean isRegisterEventBus = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frame_fragment_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingTv = (TextView) view.findViewById(R.id.tv_loading_root_fragment);
        frameLayout = (FrameLayout) view.findViewById(R.id.rootlayout_basefragment);
        if (isShowLoading()) {
            frameLayout.setVisibility(View.GONE);
            loadingTv.setVisibility(View.VISIBLE);
        } else {
            loadFinished();
        }
        frameLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * 加载完成后需要显示内容调用该方法
     */
    public void loadFinished() {
        frameLayout.setVisibility(View.VISIBLE);
        loadingTv.setVisibility(View.GONE);
    }

    /**
     * 是否显示等待
     *
     * @return true：fragment不会马上显示内容，而是显示loading背景，调用showContent方法显示内容
     * false:直接显示内容
     */
    protected abstract boolean isShowLoading();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = (FrameBaseActivity) getActivity();
        fragmentManager = getFragmentManager();
        onBaseFragmentCreate(savedInstanceState);
    }

    /**
     * Fragment创建
     */
    protected abstract void onBaseFragmentCreate(Bundle savedInstanceState);

    /**
     * 注册EventBus
     */
    protected final void registerEventBus() {
        EventBus.getDefault().register(this);
        isRegisterEventBus = true;
    }

    protected final void registerEventBusForSticky() {
        EventBus.getDefault().registerSticky(this);
        isRegisterEventBus = true;
    }

    /**
     * 重写onDestroy，如果注册了EventBus，则需要注销
     */
    @Override
    public void onDestroy() {
        if (isRegisterEventBus) {
            EventBus.getDefault().unregister(this);
        }
        super.onDestroy();
    }

    protected final FrameLayout getRootLayout() {
        return frameLayout;
    }

    /**
     * 获取Fragment显示的View
     */
    public final View getContentView() {
        return contentView;
    }

    /**
     * 设置内容
     */
    protected final void setMyContentView(int resID) {
        frameLayout.removeAllViews();
        contentView = LayoutInflater.from(context).inflate(resID, frameLayout);
        autoInjectViewField();
    }

    protected final void setMyContentView(View view) {
        contentView = view;
        frameLayout.removeAllViews();
        frameLayout.addView(view);
        autoInjectViewField();
    }

    /**
     * 解析注解，给带有@ViewInject注解的View赋值
     */
    private void autoInjectViewField() {
        try {
            Class<?> clazz = this.getClass();
            Field[] fields = clazz.getDeclaredFields();//获得Fragment中声明的字段
            for (Field field : fields) {
                // 查看这个字段是否有我们自定义的注解类标志的
                if (field.isAnnotationPresent(ViewInject.class)) {
                    ViewInject inject = field.getAnnotation(ViewInject.class);
                    int id = inject.value();
                    if (id > 0) {
                        field.setAccessible(true);
                        field.set(this, frameLayout.findViewById(id));//给我们要找的字段设置值
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 替换Activity的内容,或替换本身
     */
    protected void replaceFragment(FrameBaseFragment fragment, String isBackToStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (TextUtils.isEmpty(isBackToStack)) {
            fragmentTransaction.replace(context.getRootFrameLayoutId(), fragment);
        } else {
            fragmentTransaction.replace(context.getRootFrameLayoutId(), fragment, isBackToStack);
            fragmentTransaction.addToBackStack(isBackToStack);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 替换该Fragment内部的layout显示为fragment
     */
    protected void replaceChildFragment(int layoutId, FrameBaseFragment fragment, String isBackToStack) {
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        if (TextUtils.isEmpty(isBackToStack)) {
            fragmentTransaction.replace(layoutId, fragment);
        } else {
            fragmentTransaction.replace(layoutId, fragment, isBackToStack);
            fragmentTransaction.addToBackStack(isBackToStack);
        }
        KeyBoardUtils.closeKeyboard(context);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 在{@link #setMyContentView(int) or #setMyContentView(View)}之后获取其中View
     */
    protected final View findViewById(int resId) {
        return frameLayout.findViewById(resId);
    }

    protected final View findViewByTag(Object tag) {
        return frameLayout.findViewWithTag(tag);
    }

    /**
     * 结束当前fragment
     *
     * @param tf 当前framgent为根时，是否退出activity
     */
    protected final void finish(boolean tf) {
        /**
         * 如果当前fragment不是根fragment
         */
        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (fragmentManager.getBackStackEntryCount() == 1) {
                fragmentManager.popBackStack();
                if (tf) {
                    getActivity().finish();
                }
            } else {
                fragmentManager.popBackStack();
            }
        } else {
            getActivity().finish();
        }
    }
}
