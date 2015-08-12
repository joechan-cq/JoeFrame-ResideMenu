package joe.frame.utils.http;

import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;

/**
 * Description  http请求String数据回调
 * Created by chenqiao on 2015/8/12.
 */
public abstract class FrameHttpRspString extends TextHttpResponseHandler {

    public abstract void onSuccess(int statusCode, String codeMsg, String rspResult);

    public abstract void onFailed(int statusCode, String codeMsg, String rspResult, Throwable throwable);

    @Override
    public void onFailure(int i, Header[] headers, String s, Throwable throwable) {
        this.onFailed(i, HttpStatusCodeMean.getMean(i), s, throwable);
    }

    @Override
    public void onSuccess(int i, Header[] headers, String s) {
        this.onSuccess(i, HttpStatusCodeMean.getMean(i), s);
    }
}
