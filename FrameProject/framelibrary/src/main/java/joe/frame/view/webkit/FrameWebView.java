package joe.frame.view.webkit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Description  框架基础封装的Webview
 * Created by chenqiao on 2015/8/10.
 */
public class FrameWebView extends WebView {

    public FrameWebView(Context context) {
        this(context, WebViewType.ORIGINAL_TYPE);
    }

    public FrameWebView(Context context, WebViewType... types) {
        this(context, null, types);
    }

    public FrameWebView(Context context, AttributeSet attrs, WebViewType... types) {
        this(context, attrs, 0, types);
    }

    public FrameWebView(Context context, AttributeSet attrs, int defStyleAttr, WebViewType... types) {
        super(context, attrs, defStyleAttr);
        initAttribute(types);
    }

    public void setAttributes(WebViewType... types) {
        initAttribute(types);
    }

    private void initAttribute(WebViewType... types) {
        setFocusable(true);
        setFocusableInTouchMode(true);
        WebSettings settings = getSettings();
        if (types.length == 0) {
            return;
        }
        for (WebViewType type : types) {
            switch (type) {
                case ORIGINAL_TYPE:
                    break;
                case ALLOWJS_TYPE:
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setJavaScriptEnabled(true);
                    break;
                case NOSCALE_TYPE:
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        settings.setDisplayZoomControls(false);
                    }
                    settings.setSupportZoom(false);
                    settings.setBuiltInZoomControls(false);
                    break;
                case NOCHOOSETEXT_TYPE:
                    setOnLongClickListener(new OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return true;
                        }
                    });
                    break;
                case CACHE_TYPE:
                    settings.setDomStorageEnabled(true);
                    settings.setAppCacheEnabled(true);
                    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                    break;
                case NOCACHE_TYPE:
                    settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                    break;
                case HARDWAREACCELERATION_TYPE:
                    this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    break;
                case AUTOLOADPICTURE_TYPE:
                    settings.setLoadsImagesAutomatically(true);
                    break;
                case DATEBASE_TYPE:
                    settings.setDatabaseEnabled(true);
                    break;
                case FILEACCESS_TYPE:
                    settings.setAllowFileAccess(true);
                    break;
                case NOIMAGE_TYPE:
                    settings.setBlockNetworkImage(true);
                    break;
                case DISPLAY_ADAPTIVE_TYPE:
                    settings.setUseWideViewPort(true);
                    settings.setLoadWithOverviewMode(true);
                    break;
            }
        }
    }
}
