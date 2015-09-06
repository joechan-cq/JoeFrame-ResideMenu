package joe.frame.utils.http;

import android.os.Looper;

import com.loopj.android.http.BinaryHttpResponseHandler;

import org.apache.http.Header;

/**
 * Description  http请求byte[]回调
 * Created by chenqiao on 2015/8/12.
 */
public abstract class FrameHttpRspBytes extends BinaryHttpResponseHandler {

    public FrameHttpRspBytes() {
        super();
    }

    public FrameHttpRspBytes(String[] allowedContentTypes) {
        super(allowedContentTypes);
    }

    public FrameHttpRspBytes(String[] allowedContentTypes, Looper looper) {
        super(allowedContentTypes, looper);
    }

    public abstract void onSuccess(int statusCode, String codeMsg, byte[] rspBytes);

    public abstract void onFailed(int statusCode, String codeMsg, byte[] rspBytes, Throwable throwable);

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        this.onSuccess(i, HttpStatusCodeMean.getMean(i), bytes);
    }

    @Override
    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
        this.onFailed(i, HttpStatusCodeMean.getMean(i), bytes, throwable);
    }
}
