package joe.frame.utils.http;

import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Description  http请求String数据回调
 * Created by chenqiao on 2015/8/12.
 */
public abstract class FrameHttpRspString extends TextHttpResponseHandler {
    public FrameHttpRspString() {
        super();
    }

    public FrameHttpRspString(String encoding) {
        super(encoding);
    }

    @Override
    public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, String s, Throwable throwable) {
        this.onFailed(i, HttpStatusCodeMean.getMean(i), s, throwable);
    }

    @Override
    public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, String s) {
        this.onSuccess(i, HttpStatusCodeMean.getMean(i), s);
    }

    public abstract void onSuccess(int statusCode, String codeMsg, String rspResult);

    public abstract void onFailed(int statusCode, String codeMsg, String rspResult, Throwable throwable);
}
