package joe.frame.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Description
 * Created by chenqiao on 2016/3/7.
 */
public class MarqueeTextView extends TextView {
    public MarqueeTextView(Context context) {
        super(context);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private int textLength;

    private float pointX = -999;
    private float pointY = 0;
    private float step = 2;

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
            paint.getTextBounds(text, 0, text.length() - 1, textRect);
            textLength = textRect.width();
        } else {
            textLength = 0;
        }
        metrics = paint.getFontMetrics();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        if (pointX == -999) {
            pointX = width;
        }
        pointY = getHeight() / 2 - (metrics.ascent + metrics.descent) / 2;
        float size = getTextSize();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(size);
        paint.setColor(getCurrentTextColor());
        canvas.drawText(getText().toString(), pointX, pointY, paint);
        pointX = pointX - step;
        if (pointX <= -textLength - 10) {
            pointX = width;
        }
        postInvalidateDelayed(100);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        initView();
    }
}
