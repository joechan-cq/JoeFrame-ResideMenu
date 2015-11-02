package joe.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import joe.framelibrary.R;

/**
 * Description  倒计时View
 * Created by chenqiao on 2015/9/9.
 */
public class CountdownView extends View {

    private static final String RESTTIME = "剩余时间";
    private static final String MIN = "min";
    private static final String FINISHED = "已完成";

    private int mFirstColor, mSecondColor, mTextColor;

    private int mCircleWidth = 15, mTextSize = 40;

    private Paint mPaint;
    private Rect bound;
    private long mProgress = 0;

    private boolean isOver = false;

    private boolean isCounting = false;

    private long allTime = 0, restTime = 0;

    private Rect btnRect = new Rect();

    private CountDownTimer timer;

    private onCountDownListener listener;

    public CountdownView(Context context) {
        this(context, null);
    }

    public CountdownView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountdownView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFirstColor = Color.parseColor("#D0DEF9");
        mSecondColor = Color.parseColor("#4388FC");
        mTextColor = Color.parseColor("#4388FC");

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CountdownView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.CountdownView_firstColor) {
                mFirstColor = a.getColor(attr, Color.parseColor("#D0DEF9"));

            } else if (attr == R.styleable.CountdownView_secondColor) {
                mSecondColor = a.getColor(attr, Color.parseColor("#4388FC"));

            } else if (attr == R.styleable.CountdownView_circleWidth) {
                mCircleWidth = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));

            } else if (attr == R.styleable.CountdownView_textColor) {
                mTextColor = a.getColor(attr, Color.parseColor("#4388FC"));

            } else if (attr == R.styleable.CountdownView_textSize) {
                mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));

            }
        }
        a.recycle();
        mPaint = new Paint();
        bound = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width, height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            mPaint.setTextSize(mTextSize);
            mPaint.getTextBounds("00:00", 0, "00:00".length(), bound);
            width = bound.width() + mCircleWidth * 2 + 120;
        }
        height = width;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = centerX - mCircleWidth / 2;

        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);

        //画底层圆圈
        mPaint.setColor(mFirstColor);
        canvas.drawCircle(centerX, centerY, radius, mPaint);

        String remind = RESTTIME;
        if (isOver) {
            remind = FINISHED;
        }
        mPaint.reset();
        mPaint.setColor(Color.parseColor("#4388FC"));
        mPaint.setTextSize(mTextSize - 20);
        mPaint.setAntiAlias(true);
        Rect remindBound = new Rect();
        mPaint.getTextBounds(remind, 0, remind.length(), remindBound);
        canvas.drawText(remind, (getMeasuredWidth() - remindBound.width()) / 2, (getMeasuredHeight() + remindBound.height()) / 2 - 50, mPaint);

        //画时间
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setAntiAlias(true);
        String time = transTime(this.restTime);
        mPaint.getTextBounds(time, 0, time.length(), bound);
        canvas.drawText(time, (getMeasuredWidth() - bound.width()) / 2, (getMeasuredHeight() + bound.height()) / 2 - 5, mPaint);

        //画min
        mPaint.reset();
        mPaint.setColor(mTextColor);
        mPaint.setTextSize(13);
        mPaint.setAntiAlias(true);
        mPaint.getTextBounds(MIN, 0, MIN.length(), remindBound);
        canvas.drawText(MIN, (getMeasuredWidth() + bound.width()) / 2 + 5, (getMeasuredHeight() + bound.height()) / 2 - 5, mPaint);

        //画进度
        RectF oval = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        mPaint.reset();
        mPaint.setStrokeWidth(mCircleWidth);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        if (!isOver) {
            mPaint.setColor(mSecondColor);
        } else {
            mPaint.setColor(Color.RED);
        }
        if (allTime == 0) {
            mProgress = 0;
        } else {
            //降低精度，防止，最后一秒无法画进度
            mProgress = 360 * (allTime / 1000 - restTime / 1000) * 1000 / allTime;
        }
        canvas.drawArc(oval, -90, mProgress, false, mPaint);

        //画按钮
        Bitmap bm = null;
        BitmapDrawable bmdw = null;
        if (isCounting) {
            bmdw = (BitmapDrawable) getResources().getDrawable(R.mipmap.pause);
        } else {
            bmdw = (BitmapDrawable) getResources().getDrawable(R.mipmap.play);
        }
        if (bmdw != null) {
            bm = bmdw.getBitmap();
        }
        bm = scaleImage(bm, getMeasuredWidth() / 3.0f / bm.getWidth(), getMeasuredHeight() / 3.0f / bm.getHeight());
        btnRect.left = (getMeasuredWidth() - bm.getWidth()) / 2;
        btnRect.top = getMeasuredHeight() - bm.getHeight() - 25;
        btnRect.right = btnRect.left + bm.getWidth();
        btnRect.bottom = btnRect.top + bm.getHeight();
        canvas.drawBitmap(bm, (getMeasuredWidth() - bm.getWidth()) / 2, getMeasuredHeight() - bm.getHeight() - 22, null);
    }

    private Bitmap scaleImage(Bitmap org, float scaleWidth, float scaleHeight) {
        if (org == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(org, 0, 0, org.getWidth(), org.getHeight(), matrix, true);
    }

    private SimpleDateFormat format = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private String transTime(long time) {
        return getTime(time, format);
    }

    private String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    public void startCountDown(long alltime, long resttime) {
        this.allTime = alltime;
        this.restTime = resttime;
        isOver = false;
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(restTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                restTime = millisUntilFinished;
                if (listener != null) {
                    listener.onTicked(allTime, restTime);
                }
                postInvalidate();
            }

            @Override
            public void onFinish() {
                restTime = 0;
                isOver = true;
                isCounting = false;
                postInvalidate();
                if (listener != null) {
                    listener.onFinished();
                }
            }
        };
        timer.start();
        isCounting = true;
    }

    public CountDownTimer getTimer() {
        return timer;
    }

    public void cancelCountDown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void pauseCountDown() {
        if (timer != null) {
            timer.cancel();
        }
    }

    public void resumeCountDown() {
        startCountDown(allTime, restTime);
    }

    public void setOnFinishedListener(onCountDownListener listener) {
        this.listener = listener;
    }

    public interface onCountDownListener {
        void onFinished();

        void onTicked(long alltime, long resttime);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (btnRect.contains(x, y)) {
                    isCounting = !isCounting;
                    if (isCounting) {
                        resumeCountDown();
                    } else {
                        pauseCountDown();
                    }
                    postInvalidate();
                    return true;
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}