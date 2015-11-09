package joe.frame.view.button;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import joe.framelibrary.R;

/**
 * Description  全局悬浮可拖动按钮
 * Created by chenqiao on 2015/11/6.
 * 使用需要添加<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>权限
 * 根据传入的Context有不同的显示效果。传入Acitivity，ac不见，btn也会不见。传入service和applicationcontext，则全局可见。
 */
public class FloatButton extends ImageView {

    private WindowManager wm;
    private WindowManager.LayoutParams windowManagerParams;
    private int resId;
    private Context mContext;
    private boolean isShown = false;
    private OnClickListener listener;

    private float mRawX, mRawY, mStartX, mStartY;
    private boolean isMove;
    private int accurate = 8;//判断是移动还是点击的精确度,太小会导致判断出的都是移动

    public FloatButton(Context mContext) {
        this(mContext, R.mipmap.default_float);
    }

    public FloatButton(Context mContext, int resId) {
        super(mContext);
        this.mContext = mContext;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        windowManagerParams = new WindowManager.LayoutParams();
        this.resId = resId;
    }

    /**
     * 重写onTouchEvent方法，处理移动和点击事件的消费
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //拿到触摸点对于屏幕左上角的坐标
        mRawX = event.getRawX();
        mRawY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = event.getX();
                mStartY = event.getY();
                isMove = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(event.getX() - mStartX) > accurate || Math.abs(event.getY() - mStartY) > accurate) {
                    isMove = true;
                    updatePosition();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    updatePosition();
                } else {
                    if (listener != null) {
                        listener.onClick(this);
                    }
                }
                break;
        }
        return true;
    }

    private void updatePosition() {
        windowManagerParams.x = (int) (mRawX - mStartX);
        windowManagerParams.y = (int) (mRawY - mStartY);
        if (isShown) {
            wm.updateViewLayout(this, windowManagerParams);
        }
    }

    public void show() {
        setImageResource(resId);
        windowManagerParams.type = LayoutParams.TYPE_PHONE; // 设置window type
        windowManagerParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
        // 设置Window flag
        windowManagerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 注意，flag的值可以为： LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
		 * LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦 LayoutParams.FLAG_NOT_TOUCHABLE
		 * 不可触摸
		 */

        //什么是gravity属性呢？简单地说，就是窗口如何停靠。  当设置了 Gravity.LEFT 或 Gravity.RIGHT 之后，x值就表示到特定边的距离
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;
        // 以屏幕左上角为原点，设置x、y初始值，使按钮定位在右下角
        windowManagerParams.x = 0;
        windowManagerParams.y = 0;
        // 设置悬浮窗口长宽数据
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;
        // 显示myFloatView图像
        wm.addView(this, windowManagerParams);
        isShown = true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.listener = l;
    }

    public void dismiss() {
        wm.removeView(this);
        isShown = false;
    }

    @Override
    public boolean isShown() {
        return isShown;
    }
}
