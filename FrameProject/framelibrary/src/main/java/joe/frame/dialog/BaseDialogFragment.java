package joe.frame.dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import joe.framelibrary.R;

/**
 * Description  对话框基础类
 * Created by chenqiao on 2015/10/19.
 */
public class BaseDialogFragment extends DialogFragment {

    private TextView titleView;
    private TextView contentView;
    private TextView otherBtn, okBtn, cancelBtn;
    private FrameLayout contentLayoutView;

    private String title = "", content = "", otherText = "", okText = "OK", cancelText = "CANCEL";

    private View.OnClickListener otherListener = null, okListener = null, cancelListener = null;

    private boolean showOk = true, showOther = false;

    private boolean isShown = false;

    private View defineView;

    private int width, height;

    private int minWidth = 200, minHeight = 150;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.frame_dialog_base, container);
        titleView = (TextView) view.findViewById(R.id.base_dialog_title);
        contentView = (TextView) view.findViewById(R.id.base_dialog_content);
        contentView.setMovementMethod(new ScrollingMovementMethod());
        contentLayoutView = (FrameLayout) view.findViewById(R.id.base_dialog_content_layout);

        otherBtn = (TextView) view.findViewById(R.id.base_dialog_btn_other);
        okBtn = (TextView) view.findViewById(R.id.base_dialog_btn_ok);
        cancelBtn = (TextView) view.findViewById(R.id.base_dialog_btn_cancel);
        otherBtn.setVisibility(View.GONE);
        okBtn.setOnClickListener(default_listener);
        cancelBtn.setOnClickListener(default_listener);
        update();
        isShown = true;
        return view;
    }

    private void update() {
        titleView.setText(title);
        contentView.setText(content);
        otherBtn.setText(otherText);

        if (defineView != null) {
            contentLayoutView.removeAllViews();
            contentLayoutView.addView(defineView);
            contentLayoutView.setVisibility(View.VISIBLE);
            contentView.setVisibility(View.GONE);
        }

        if (showOther) {
            otherBtn.setVisibility(View.VISIBLE);
        } else {
            otherBtn.setVisibility(View.GONE);
        }
        if (otherListener != null) {
            otherBtn.setOnClickListener(otherListener);
        }

        okBtn.setText(okText);
        if (!showOk) {
            okBtn.setVisibility(View.GONE);
        } else {
            okBtn.setVisibility(View.VISIBLE);
        }

        if (okListener != null) {
            okBtn.setOnClickListener(okListener);
        }

        cancelBtn.setText(cancelText);
        if (cancelListener != null) {
            cancelBtn.setOnClickListener(cancelListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (width > minWidth && height > minHeight) {
            getDialog().getWindow().setLayout(width, height);
        }
    }

    private View.OnClickListener default_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss();
        }
    };

    /**
     * 设置Dialog标题
     *
     * @param resId
     */
    public void setTitle(int resId) {
        setTitle(getString(resId));
    }

    public void setTitle(String title) {
        this.title = title;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置Dialog内容text
     *
     * @param resId
     */
    public void setContent(int resId) {
        setContent(getString(resId));
    }

    public void setContent(String content) {
        this.content = content;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置Dialog自定义显示内容
     *
     * @param view
     */
    public void setContentView(View view) {
        this.defineView = view;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置Ok按钮
     *
     * @param resId
     * @param listener
     */
    public void setOkBtn(int resId, View.OnClickListener listener) {
        setOkBtn(getString(resId), listener);
    }

    public void setOkBtn(String text, View.OnClickListener listener) {
        this.okText = text;
        this.okListener = listener;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置是否显示ok按钮
     *
     * @param tf
     */
    public void showOkBtn(boolean tf) {
        showOk = tf;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置cancel按钮
     *
     * @param resId
     * @param listener
     */
    public void setCancelBtn(int resId, View.OnClickListener listener) {
        setCancelBtn(getString(resId), listener);
    }

    public void setCancelBtn(String text, View.OnClickListener listener) {
        this.cancelText = text;
        this.cancelListener = listener;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置是否显示第三个按钮
     *
     * @param tf
     */
    public void showOtherBtn(boolean tf) {
        showOther = tf;
        if (isShown) {
            update();
        }
    }

    public void setOtherBtn(int resId, View.OnClickListener listener) {
        setOtherBtn(getString(resId), listener);
    }

    public void setOtherBtn(String text, View.OnClickListener listener) {
        this.otherText = text;
        this.otherListener = listener;
        if (isShown) {
            update();
        }
    }

    /**
     * 设置显示的Dialog的长宽
     *
     * @param width
     * @param height
     */
    public void setDialogWidthAndHeight(int width, int height) {
        this.width = width;
        this.height = height;
        if (isShown) {
            onResume();
        }
    }
}