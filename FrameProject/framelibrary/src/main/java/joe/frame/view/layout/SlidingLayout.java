package joe.frame.view.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Description
 * Created by chenqiao on 2016/8/30.
 */
public class SlidingLayout extends FrameLayout implements View.OnTouchListener {

    private Path mPath;
    private Paint mPaint;
    private RectF rectF;
    private int backgroundColor = Color.parseColor("#303030");
    private GestureDetector gestureDetector;
    private OnFlipToEndListener listener;

    public SlidingLayout(Context context) {
        this(context, null);
    }

    public SlidingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setPadding(20, 20, 20, 20);
        mPaint = new Paint();
        mPath = new Path();
        rectF = new RectF();
        setWillNotDraw(false);
        gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                View child = getChildAt(0);
                int left = child.getLeft();
                int top = child.getTop();
                int newLeft = (int) (left - distanceX);
                if (newLeft < getPaddingLeft()) {
                    newLeft = getPaddingLeft();
                }
                int newRight = newLeft + child.getWidth();
                child.layout(newLeft, top, newRight, top + child.getHeight());
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (getChildCount() > 0) {
                    View child = getChildAt(0);
                    int top = child.getTop();
                    if (child.getLeft() >= getMeasuredWidth() - 10) {
                        if (listener != null) {
                            listener.onFlipToEnd();
                        }
                    }
                    child.layout(getPaddingLeft(), top, getPaddingLeft() + child.getWidth(), top + child.getHeight());
                    return true;
                } else {
                    return false;
                }

            }
        });
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        mPaint.reset();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setColor(backgroundColor);

        rectF.setEmpty();
        rectF.set(getScrollX(), 0, getScrollX() + 40, height);
        mPath.reset();
        mPath.moveTo(20, 0);
        mPath.lineTo(width - 20, 0);

        rectF.offsetTo(getScrollX() + width - 40, 0);
        mPath.arcTo(rectF, -90, 180);
        mPath.lineTo(20, height);

        rectF.offsetTo(getScrollX(), 0);
        mPath.arcTo(rectF, 90, 180);

        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        if (childCount > 1) {
            throw new IllegalStateException("SlidingLayout has more than one child, it only supports one child");
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean tf = gestureDetector.onTouchEvent(event);
        if (!tf && event.getAction() == MotionEvent.ACTION_UP) {
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                int top = child.getTop();
                if (child.getLeft() >= getMeasuredWidth()) {
                    if (listener != null) {
                        listener.onFlipToEnd();
                    }
                }
                child.layout(getPaddingLeft(), top, getPaddingLeft() + child.getWidth(), top + child.getHeight());
                return true;
            } else {
                return false;
            }
        } else {
            return tf;
        }
    }

    public void setFlipToEndListener(OnFlipToEndListener listener) {
        this.listener = listener;
    }

    public interface OnFlipToEndListener {
        void onFlipToEnd();
    }
}