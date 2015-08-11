package joe.framelibrary.frame.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

/**
 * Description  竖型Viewpager
 * Created by chenqiao on 2015/8/5.
 */
public class VerticalViewPager extends ViewGroup {

    private int mScreenWidth, mScreenHeight;

    private boolean isScrolling;

    private Scroller mScroller;

    private VelocityTracker mVelocityTracker;
    private int mScrollStart;
    private int mLastY;
    private int mScrollEnd;

    private OnPageChangeListener mPageChnangeListener;
    private int currentPage = 0;

    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        mScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            int childCount = getChildCount();
            MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            lp.height = mScreenHeight * childCount;
            setLayoutParams(lp);

            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != View.GONE) {
                    child.layout(l, i * mScreenHeight, r, (i + 1) * mScreenHeight);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isScrolling) {
            return super.onTouchEvent(event);
        }
        int action = event.getAction();
        int downY = (int) event.getY();

        initVelocity(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mScrollStart = getScrollY();
                mLastY = downY;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
                int dy = downY - mLastY;
                int scrollY = getScrollY();
                //边界判定
                if (dy > 0 && scrollY - dy < 0) {
                    dy = -scrollY;
                }
                if (dy < 0 && (scrollY - dy) > getHeight() - mScreenHeight) {
                    dy = getHeight() - mScreenHeight - scrollY;
                }
                // 界面上滑,参数为负
                scrollBy(0, -dy);
                mLastY = downY;
                break;
            case MotionEvent.ACTION_UP:
                mScrollEnd = getScrollY();
                int dScrollY = mScrollEnd - mScrollStart;
                if (dScrollY > 0) {
                    // 界面往上滚动
                    if (dScrollY > mScreenHeight / 2 || Math.abs(getYVelocity()) > 500) {
                        mScroller.startScroll(0, getScrollY(), 0, mScreenHeight - dScrollY);
                    } else {
                        //没有滚动到一半或速度不够，弹回
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }
                if (dScrollY < 0) {
                    if (-dScrollY > mScreenHeight / 2 || Math.abs(getYVelocity()) > 500) {
                        mScroller.startScroll(0, getScrollY(), 0, -mScreenHeight - dScrollY);
                    } else {
                        //没有滚动到一半或速度不够，弹回
                        mScroller.startScroll(0, getScrollY(), 0, -dScrollY);
                    }
                }
                isScrolling = true;
                postInvalidate();
                recycleVelocity();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            postInvalidate();
        } else {
            int position = getScrollY() / mScreenHeight;
            if (position != currentPage) {
                currentPage = position;
                if (mPageChnangeListener != null) {
                    mPageChnangeListener.onPageChange(currentPage);
                }
            }
            isScrolling = false;
        }
    }

    private void initVelocity(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private int getYVelocity() {
        mVelocityTracker.computeCurrentVelocity(1000);
        return (int) mVelocityTracker.getYVelocity();
    }

    private void recycleVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.mPageChnangeListener = listener;
    }

    public interface OnPageChangeListener {
        void onPageChange(int currentPage);
    }
}