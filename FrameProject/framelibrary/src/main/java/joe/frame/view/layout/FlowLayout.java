package joe.frame.view.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Description  线性流式布局
 * Created by chenqiao on 2015/8/4.
 */
public class FlowLayout extends ViewGroup {
    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int size = getChildCount();
        int width = sizeWidth;
        int height = getPaddingBottom() + getPaddingTop();

        int sumwidth = getPaddingLeft() + getPaddingRight();
        int tempheight = 0;
        MarginLayoutParams lp = null;
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            lp = (MarginLayoutParams) child.getLayoutParams();
            if (sumwidth + child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin <= width) {
                sumwidth = sumwidth + child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
                if (tempheight < child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin) {
                    tempheight = child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
                }
            } else {
                height = height + tempheight;
                tempheight = child.getMeasuredHeight() + lp.bottomMargin + lp.topMargin;
                sumwidth = getPaddingLeft() + getPaddingRight();
            }
        }
        height = height + tempheight;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int size = getChildCount();

        int anchorX = getPaddingLeft();
        int anchorY = getPaddingTop();
        int lineheight = 0;
        for (int i = 0; i < size; i++) {
            View child = getChildAt(i);
            int cWidth = child.getMeasuredWidth();
            int cHeight = child.getMeasuredHeight();
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int cleft = anchorX;
            int cright, ctop, cbottom;
            if (cleft + lp.leftMargin + lp.rightMargin + cWidth > getMeasuredWidth() - getPaddingRight()) {
                anchorX = getPaddingLeft();
                anchorY = getPaddingTop() + lineheight;
                cleft = anchorX + lp.leftMargin;
                cright = cleft + cWidth;
                ctop = anchorY + lp.topMargin;
                cbottom = ctop + cHeight;
                lineheight = 0;
                lineheight = (cbottom + lp.bottomMargin) > lineheight ? (cbottom + lp.bottomMargin) : lineheight;
                anchorX = cright + lp.rightMargin;
            } else {
                cleft = anchorX + lp.leftMargin;
                cright = cleft + cWidth;
                ctop = anchorY + lp.topMargin;
                cbottom = ctop + cHeight;
                lineheight = (cbottom + lp.bottomMargin) > lineheight ? (cbottom + lp.bottomMargin) : lineheight;
                anchorX = cright + lp.rightMargin;
            }
            child.layout(cleft, ctop, cright, cbottom);
        }
    }
}