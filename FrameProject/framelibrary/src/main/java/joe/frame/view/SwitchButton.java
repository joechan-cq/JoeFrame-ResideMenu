package joe.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import joe.framelibrary.R;

/**
 * Description  switch开关
 * Created by chenqiao on 2015/8/18.
 */
public class SwitchButton extends View {

    /**
     * 四个状态，关闭，准备关闭，开启，准备开启
     */
    private static final int STATE_OFF = 0X00;
    private static final int STATE_OFFING = 0X01;
    private static final int STATE_ON = 0X02;
    private static final int STATE_ONING = 0x03;

    private int state = STATE_OFF;
    private int lastState = state;

    //按钮颜色
    private int btnColor;

    //开关时的颜色
    private int offColor, onColor;

    //画笔
    private Paint mPaint = new Paint();

    private Path sPath = new Path();
    private Path bPath = new Path();

    //视图长宽
    private int mWidth, mHeight;

    //背景长宽，各点坐标
    private float sWidth, sHeight, sLeft, sRight, sTop, sBottom, sCenterX, sCenterY;
    private float sScale;
    private float sScaleCenterX, sScaleCenterY;//缩放中心点

    //按钮长宽，各点坐标，半径
    private float bWidth, bHeight, bLeft, bRight, bTop, bBottom, bCenterX, bCenterY, bRadius;
    private float bStrokeWidth;
    private float bOffset;
    private float bOnLeftX, bOn2LeftX, bOff2LeftX, bOffLeftX;
    private RectF bRectF = new RectF();

    private float sAnim = 0, bAnim = 0;

    private boolean isChecked = false;

    public SwitchButton(Context context) {
        this(context, null);
    }

    public SwitchButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwitchButton, defStyleAttr, 0);
        btnColor = a.getColor(R.styleable.SwitchButton_btnColor, Color.WHITE);
        offColor = a.getColor(R.styleable.SwitchButton_offColor, Color.WHITE);
        onColor = a.getColor(R.styleable.SwitchButton_onColor, Color.argb(255, 0, 201, 87));
        isChecked = a.getBoolean(R.styleable.SwitchButton_isChecked, false);
        if (isChecked) {
            state = STATE_ON;
            lastState = state;
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = (int) (widthSize * 0.65f);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xffcccccc);
        canvas.drawPath(sPath, mPaint);

        boolean isOn = (state == STATE_ON || state == STATE_OFFING);

        sAnim = sAnim - 0.1f > 0 ? sAnim - 0.1f : 0;
        bAnim = bAnim - 0.1f > 0 ? bAnim - 0.1f : 0;

        float scale = sScale * (isOn ? sAnim : 1 - sAnim);   //如果是已经开启的状态，接下来scale逐渐减小
        float scaleOffset = (bOnLeftX + bRadius - sCenterX) * (isOn ? 1 - sAnim : sAnim);    //按钮中心点与视图中心点的x上的距离

        //绘制关状态颜色的背景和动画
        canvas.save();
        canvas.scale(scale, scale, sCenterX + scaleOffset, sScaleCenterY);
        mPaint.setColor(offColor);
        canvas.drawPath(sPath, mPaint);
        canvas.restore();

        //绘制开状态颜色的背景和动画
        canvas.save();
        canvas.scale(1 - scale, 1 - scale, sCenterX - scaleOffset, sScaleCenterY);
        mPaint.setColor(onColor);
        canvas.drawPath(sPath, mPaint);
        canvas.restore();

        canvas.save();
        boolean isReady = (state == STATE_OFFING || state == STATE_ONING);
        float percent = (isReady ? 1 - bAnim : bAnim);

        //计算按钮平移距离（如果按钮在开的状态，那么转为OFFING，拉伸时，需左移一段距离，不然右边会超出背景）
        caculatePath(percent);
        canvas.translate(calcBTranslate(bAnim), 0);

        mPaint.setColor(btnColor);//设置按钮颜色
        canvas.drawPath(bPath, mPaint);//绘制按钮
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xffdddddd);//按钮边缘颜色
        mPaint.setStrokeWidth(bStrokeWidth);
        canvas.drawPath(bPath, mPaint);
        canvas.restore();

        mPaint.reset();
        if (sAnim > 0 || bAnim > 0) {
            invalidate();
        } else {
            if (state == STATE_OFFING) {
                toggleSwitch(STATE_OFF);
            } else if (state == STATE_ONING) {
                toggleSwitch(STATE_ON);
            }
        }

    }

    private float calcBTranslate(float percent) {
        float result = 0;
        switch (state) {
            case STATE_OFFING:
                result = bOn2LeftX + (bOnLeftX - bOn2LeftX) * percent;
                break;
            case STATE_ON:
                result = bOnLeftX - (bOnLeftX - bOff2LeftX) * percent;
                break;
            case STATE_ONING:
                result = bOff2LeftX - (bOff2LeftX - bOffLeftX) * percent;
                break;
            case STATE_OFF:
                result = bOffLeftX + (bOn2LeftX - bOffLeftX) * percent;
                break;
        }

        return result - bOffLeftX;
    }

    private void caculatePath(float percent) {
        bPath.reset();
        bRectF.left = bLeft + bStrokeWidth;
        bRectF.right = bRight - bStrokeWidth;
        bRectF.top = bTop + bStrokeWidth;
        bRectF.bottom = bBottom - bStrokeWidth;
        bPath.arcTo(bRectF, 90, 180);
        bRectF.left = bLeft + percent * bOffset + bStrokeWidth;
        bRectF.right = bRight + percent * bOffset - bStrokeWidth;
        bPath.arcTo(bRectF, 270, 180);
        bPath.close();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        sLeft = sTop = 0;
        sRight = mWidth;
        sBottom = mHeight;//预留0.2来画阴影
        sWidth = sRight - sLeft;
        sHeight = sBottom - sTop;
        sCenterX = (sLeft + sRight) / 2;
        sCenterY = (sTop + sBottom) / 2;

        RectF sRectf = new RectF(sLeft, sTop, sBottom, sBottom);
        sPath.arcTo(sRectf, 90, 180);//设置左边圆弧
        sRectf.left = sRight - sBottom;//重新定位Rect
        sRectf.right = sRight;
        sPath.arcTo(sRectf, 270, 180);//设置右边圆弧
        sPath.close();

        bLeft = bTop = 0;
        bRight = bBottom = sBottom; //设置按钮所在矩形（正方形）
        bWidth = bHeight = bRight - bLeft;

        float halfHeightofS = sHeight / 2;
        bRadius = halfHeightofS;
        bOffset = bRadius * 0.3f;//变宽的度量,按钮中心点偏移1/3半径
        bStrokeWidth = (halfHeightofS - bRadius) / 2;//按钮边框线宽度
        sScale = 1 - bStrokeWidth / sHeight;//缩放比例
        sScaleCenterX = sWidth - halfHeightofS;//缩放坐标点
        sScaleCenterY = sCenterY;

        bOnLeftX = sWidth - bWidth;//已经开启（按钮在右边），按钮距离左边界距离
        bOn2LeftX = bOnLeftX - bOffset;//准备关闭下，按钮距离左边界距离
        bOffLeftX = 0;//已经关闭下。。。。。。
        bOff2LeftX = 0;//准备开启下。。。。。。
        bRectF.left = bLeft;
        bRectF.right = bRight;
        bRectF.top = bTop + (halfHeightofS - bRadius);
        bRectF.bottom = bBottom - (halfHeightofS - bRadius);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_UP:
                lastState = state;
                switch (state) {
                    case STATE_OFF:
                        refreshState(STATE_ONING);
                        break;
                    case STATE_ON:
                        refreshState(STATE_OFFING);
                        break;
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
        if (isChecked) {
            refreshState(STATE_ON);
        } else {
            refreshState(STATE_OFF);
        }
    }

    public void setOnSwitchStateChangedListener(OnSwitchStateChangedListener listener) {
        this.listener = listener;
    }

    private OnSwitchStateChangedListener listener;

    private synchronized void toggleSwitch(int stateOff) {
        if (stateOff == STATE_OFF || stateOff == STATE_ON) {
            if ((stateOff == STATE_ON && (lastState == STATE_OFF || lastState == STATE_ONING))
                    || (stateOff == STATE_OFF && (lastState == STATE_OFFING || lastState == STATE_ON))) {
                sAnim = 1;
            }
            bAnim = 1;
            refreshState(stateOff);
        }
    }

    public void toggleSwitch(boolean tf) {
        if (tf) {
            if (state == STATE_OFF || state == STATE_ONING) {
                refreshState(STATE_ONING);
            }
        } else {
            if (state == STATE_OFFING || state == STATE_ON) {
                refreshState(STATE_OFFING);
            }
        }
    }

    private void refreshState(int stateOff) {
        if (stateOff == STATE_ON) {
            isChecked = true;
            if (listener != null) {
                listener.onStateChanged(true);
            }
        } else if (stateOff == STATE_OFF) {
            isChecked = false;
            if (listener != null) {
                listener.onStateChanged(false);
            }
        }
        bAnim = 1;
        lastState = state;
        state = stateOff;
        postInvalidate();
    }

    public interface OnSwitchStateChangedListener {
        void onStateChanged(boolean state);
    }
}