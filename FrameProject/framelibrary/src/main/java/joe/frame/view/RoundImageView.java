package joe.frame.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import joe.framelibrary.R;

/**
 * Description  圆角ImageView
 * Created by chenqiao on 2015/8/13.
 */
public class RoundImageView extends ImageView {

    private static final int TYPE_CIRCLR = 0;
    private static final int TYPE_ROUND = 1;
    private int type;

    //默认圆角大小
    private static final int BORDER_RADIUS_DEFAULT = 10;

    //圆角大小
    private int mBorderRadius;

    //圆角半径
    private int mRadius;

    private Paint mPaint;

    //View宽度
    private int mWidth;

    private Matrix mMatrix;

    private RectF mRoundRect;

    private Bitmap bm;

    public RoundImageView(Context context) {
        this(context, null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMatrix = new Matrix();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//去锯齿

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageView_borderRadius, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BORDER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));
        type = a.getInt(R.styleable.RoundImageView_type, TYPE_CIRCLR);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (type == TYPE_CIRCLR) {
            //是圆形，强行设为宽高一致，以小值为准
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            int mHeight = mWidth;
            setMeasuredDimension(mWidth, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setShader();
        if (type == TYPE_CIRCLR) {
            canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        } else {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (type == TYPE_ROUND) {
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    private void setShader() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        bm = drawableToBitmap(drawable);
        BitmapShader mBitmapShader = new BitmapShader(bm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLR) {
            //计算图片与View的大小关系和缩放比例
            int bmSize = Math.min(bm.getWidth(), bm.getHeight()); //取宽高最小值
            scale = mWidth * 1.0f / bmSize; //将最小的缩放到View的大小（填充）
        } else if (type == TYPE_ROUND) {
            //如果是圆角矩形，计算宽度缩放比和高度缩放比，取最大(填充且保证图片原宽高比例而不拉伸)
            scale = Math.max(getWidth() * 1.0f / bm.getWidth(), getHeight() * 1.0f / bm.getHeight());
        }
        mMatrix.setScale(scale, scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaint.setShader(mBitmapShader);
    }

    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bm;
    }

    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(STATE_INSTANCE));
            this.type = bundle.getInt(STATE_TYPE);
            this.mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setBorderRadius(int borderRadius) {
        int px = dp2px(borderRadius);
        if (this.mBorderRadius != px) {
            this.mBorderRadius = px;
            invalidate();
        }
    }

    public void setType(int type) {
        if (this.type != type) {
            if (type != TYPE_ROUND && type != TYPE_CIRCLR) {
                this.type = TYPE_CIRCLR;
            }
            requestLayout();
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * 销毁View的时候，回收Bitmap
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bm != null) {
            if (!bm.isRecycled()) {
                bm.recycle();
                bm = null;
            }
        }
        System.gc();
    }
}
