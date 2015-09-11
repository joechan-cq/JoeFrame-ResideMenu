package joe.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import joe.framelibrary.R;

/**
 * 自定义属性periodnum(阶段数量，几个圆点)
 * periodColor(已完成的阶段表示颜色)
 * uptextColor,downtextColor(进度上下文字颜色)
 * uptextArray,downArray(进度上下文字)
 * getNowPeriod()获取当前进度
 * setNowPeriod()设置当前进度
 */

/**
 * Description  阶段进展View
 * Created by chenqiao on 2015/9/9.
 */
public class PeriodView extends View {

    private int mPeriodColor, mUptextColor, mDownTextColor;

    private int mPeriodNum;

    private int nowPeriod = 0;

    private CharSequence[] upStrings;
    private CharSequence[] downStrings;

    private Paint mPaint;

    public PeriodView(Context context) {
        this(context, null);
    }

    public PeriodView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeriodView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPeriodColor = Color.parseColor("#4388FC");
        mUptextColor = Color.parseColor("#4388FC");
        mDownTextColor = Color.parseColor("#C7C7C7");
        mPeriodNum = 1;

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PeriodView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.PeriodView_periodColor) {
                mPeriodColor = a.getColor(attr, Color.parseColor("#4388FC"));

            } else if (attr == R.styleable.PeriodView_periodnum) {
                mPeriodNum = a.getInt(attr, 1);

            } else if (attr == R.styleable.PeriodView_uptextColor) {
                mUptextColor = a.getColor(attr, Color.parseColor("#4388FC"));

            } else if (attr == R.styleable.PeriodView_downtextColor) {
                mDownTextColor = a.getColor(attr, Color.parseColor("#C7C7C7"));

            } else if (attr == R.styleable.PeriodView_uptextArray) {
                upStrings = a.getTextArray(attr);

            } else if (attr == R.styleable.PeriodView_downtextArray) {
                downStrings = a.getTextArray(attr);

            }
        }
        a.recycle();
        mPaint = new Paint();
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
            width = 0;
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = 0;
        }

        Rect bound = new Rect();
        mPaint.setTextSize(20);
        int upwidth = 0, downwidth = 0;
        if (upStrings != null) {
            for (int i = 0; i < upStrings.length; i++) {
                String str = upStrings[i].toString();
                mPaint.getTextBounds(str, 0, str.length(), bound);
                upwidth += bound.width();
            }
        }
        if (downStrings != null) {
            for (int i = 0; i < downStrings.length; i++) {
                String str = downStrings[i].toString();
                mPaint.getTextBounds(str, 0, str.length(), bound);
                downwidth += bound.width();
            }
        }
        height = bound.height() * 2 + 80;
        width = upwidth > downwidth ? upwidth : downwidth;
        width += 150;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画进度图
        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        int centerY = getHeight() / 2;
        int x = getWidth() / mPeriodNum / 2;
        int distance = x * 2;
        for (int i = 0; i < mPeriodNum; i++) {
            if (nowPeriod > i) {
                mPaint.setColor(mPeriodColor);
            } else {
                mPaint.setColor(Color.parseColor("#C7C7C7"));
            }
            if (i != mPeriodNum - 1) {
                canvas.drawRect(x, centerY - 2, x + distance, centerY + 2, mPaint);
            }
            if (nowPeriod >= i) {
                mPaint.setColor(mPeriodColor);
            } else {
                mPaint.setColor(Color.parseColor("#C7C7C7"));
            }
            canvas.drawCircle(x, centerY, 10, mPaint);
            x += distance;
        }

        //画上行字
        if (upStrings != null && upStrings.length > 0) {
            x = getWidth() / mPeriodNum / 2;
            mPaint.reset();
            mPaint.setColor(mUptextColor);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(20);
            Rect bound = new Rect();
            for (CharSequence upString : upStrings) {
                String str = upString.toString();
                mPaint.getTextBounds(str, 0, str.length(), bound);
                canvas.drawText(str, x - bound.width() / 2f, centerY - 8 - bound.height(), mPaint);
                x += distance;
            }
        }

        //画下行字
        if (downStrings != null && downStrings.length > 0) {
            x = getWidth() / mPeriodNum / 2;
            mPaint.reset();
            mPaint.setColor(mDownTextColor);
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(18);
            Rect bound = new Rect();
            for (CharSequence downString : downStrings) {
                String str = downString.toString();
                mPaint.getTextBounds(str, 0, str.length(), bound);
                canvas.drawText(str, x - bound.width() / 2f, centerY + 10 + bound.height(), mPaint);
                x += distance;
            }
        }
    }

    public int getNowPeriod() {
        return nowPeriod;
    }

    public void setNowPeriod(int nowPeriod) {
        this.nowPeriod = nowPeriod;
        postInvalidate();
    }

    public int getmPeriodNum() {
        return mPeriodNum;
    }
}
