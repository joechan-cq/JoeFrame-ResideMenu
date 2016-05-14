package joe.frame.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.Checkable;
import android.widget.ImageView;

import joe.framelibrary.R;

/**
 * Description  具有check状态的ImageView
 * Created by chenqiao on 2016/5/11.
 */
public class StatusButton extends ImageView implements Checkable {
    private static final int[] CHECKED_STATE_SET = {android.R.attr.state_checked};
    private final StateListDrawable stateListDrawable;
    private boolean mChecked;

    public StatusButton(Context context) {
        this(context, null);
    }

    public StatusButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StatusButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StatusButton);
        stateListDrawable = (StateListDrawable) a.getDrawable(R.styleable.StatusButton_statusSrc);
        mChecked = a.getBoolean(R.styleable.StatusButton_statusChecked, false);
        setChecked(mChecked);
        a.recycle();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (mChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (stateListDrawable != null) {
            int[] myDrawableState = getDrawableState();
            stateListDrawable.setState(myDrawableState);
            Drawable drawable = stateListDrawable.getCurrent();
            setImageDrawable(drawable);
        }
    }
}