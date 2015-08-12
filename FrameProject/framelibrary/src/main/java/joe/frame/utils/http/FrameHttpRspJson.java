package joe.frame.utils.http;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Description  http请求Json数据回调
 * Created by chenqiao on 2015/8/12.
 */
public abstract class FrameHttpRspJson extends JsonHttpResponseHandler {

    public abstract void onSuccess(int statusCode, String codeMsg, JSONObject rspJSONObject);

    public abstract void onSuccess(int statusCode, String codeMsg, JSONArray rspJSONArray);

    public abstract void onFailed(int statusCode, String codeMsg, Object rspResult, Throwable throwable);

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        this.onSuccess(statusCode, HttpStatusCodeMean.getMean(statusCode), response);
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
        this.onSuccess(statusCode, HttpStatusCodeMean.getMean(statusCode), response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        this.onFailed(statusCode, HttpStatusCodeMean.getMean(statusCode), responseString, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        this.onFailed(statusCode, HttpStatusCodeMean.getMean(statusCode), errorResponse, throwable);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        this.onFailed(statusCode, HttpStatusCodeMean.getMean(statusCode), errorResponse, throwable);
    }
}