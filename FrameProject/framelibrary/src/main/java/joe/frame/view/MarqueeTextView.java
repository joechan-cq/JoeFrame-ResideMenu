package joe.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import joe.framelibrary.R;

/**
 * Description  走马灯TextView
 * Created by chenqiao on 2016/3/7.
 */
public class MarqueeTextView extends TextView {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;

    // 滚动速度
    private float speed;
    // 滚动的方向 0为垂直，1为水平
    private int orientation;

    private int newLine = 100;

    public MarqueeTextView(Context context) {
        this(context, null);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.MarqueeTextView);
        orientation = a.getInt(R.styleable.MarqueeTextView_tv_orientation, 1);
        speed = a.getFloat(R.styleable.MarqueeTextView_speed, 1.0f);
        newLine = a.getInt(R.styleable.MarqueeTextView_newLine, 100);
        if (speed < 0 || speed > 1.0f) {
            speed = 1.0f;
        }
        a.recycle();
        initView();
    }

    private int textLength;

    private float pointX = 0;
    private float pointY = 0;
    private float step = 2;
    private float scrollY = 0;

    private Rect textRect;
    private Paint.FontMetrics metrics;

    /**
     * 初始化控件
     */
    private void initView() {
        String text = getText().toString();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(getCurrentTextColor());
        textRect = new Rect();
        if (text.length() > 0) {
            textRect = measureText(text);
            textLength = textRect.width();
        } else {
            textLength = 0;
        }
        metrics = paint.getFontMetrics();
    }

    private Rect measureText(String text) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(getTextSize());
        paint.setColor(getCurrentTextColor());
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length() - 1, rect);
        return rect;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        switch (orientation) {
            case 0:
                String text = getText().toString();
                String[] s = text.split("\n");
                int maxWidth = 0;
                int minHeight = 999;
                for (String item : s) {
                    Rect rect = measureText(item);
                    if (rect.width() > maxWidth) {
                        maxWidth = rect.width();
                    }
                    if (rect.height() < minHeight) {
                        minHeight = rect.height();
                    }
                }
                maxWidth += getTotalPaddingLeft();
                maxWidth += getTotalPaddingRight();
                setMeasuredDimension(maxWidth, minHeight + 10);
                break;
            case 1:
                break;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        switch (orientation) {
            case 0:
                String[] content = getText().toString().split("\n");
                pointX = 0;
                float y = 0;
                float realY = 0;
                float lastHeight = 0;
                for (int p = 0; p < content.length; p++) {
                    TextPaint textPaint = new TextPaint();
                    textPaint.setColor(getCurrentTextColor());
                    textPaint.setTextSize(getTextSize());
                    canvas.save();
                    realY = y + scrollY;
                    canvas.translate(0, realY);
                    StaticLayout layout = new StaticLayout(content[p], textPaint, newLine, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, false);
                    layout.draw(canvas);
                    canvas.restore();
                    y += layout.getHeight();
                    lastHeight = layout.getHeight();
                }
                if (realY + lastHeight < 0) {
                    scrollY = getMeasuredHeight();
                }
                scrollY -= step;
                break;
            case 1:
                if (pointX >= width + 5) {
                    pointX = 0;
                }
                pointY = getHeight() / 2 - (metrics.ascent + metrics.descent) / 2;
                float size = getTextSize();
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(size);
                paint.setColor(getCurrentTextColor());
                canvas.drawText(getText().toString(), pointX, pointY, paint);
                pointX = pointX + step;
                break;
        }
        long invalidateTime = (long) (100 - 50 * speed);
        if (invalidateTime != 100) {
            postInvalidateDelayed(invalidateTime);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start,
                                 int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        initView();
    }

    public void setOrientation(int direction) {
        if (direction < 0 || direction > 1) {

        } else {
            this.orientation = direction;
        }
    }

    public void setSpeed(float spd) {
        if (spd < 0 || spd > 1) {

        } else {
            this.speed = spd;
        }
    }

    public void setNewLine(int length) {
        if (length > 0) {
            newLine = length;
        }
    }
}
