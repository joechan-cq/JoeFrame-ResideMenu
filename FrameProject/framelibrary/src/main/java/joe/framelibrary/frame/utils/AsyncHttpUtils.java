package joe.framelibrary.frame.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

/**
 * Description  异步网络请求类，再封装android-async-http类
 * Created by chenqiao on 2015/7/20.
 */
public class AsyncHttpUtils {
    public static final int GET = 0x09;
    public static final int POST = 0x10;

    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * 网络请求，服务器结果返回为字符串类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForString(int requestMethod, Context context, String url, RequestParams params, TextHttpResponseHandler response) {
        switch (requestMethod) {
            case GET:
                client.get(context, url, params, response);
                break;
            case POST:
                client.post(context, url, params, response);
                break;
        }
    }


    /**
     * 网络请求，服务器结果返回为字符串类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForString(int requestMethod, Context context, String url, TextHttpResponseHandler response) {
        doHttpRequestForString(requestMethod, context, url, null, response);
    }

    /**
     * 网络请求，服务器结果返回为字符串类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForString(int requestMethod, String url, RequestParams params, TextHttpResponseHandler response) {
        doHttpRequestForString(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为字符串类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForString(int requestMethod, String url, TextHttpResponseHandler response) {
        doHttpRequestForString(requestMethod, url, null, response);
    }


    /**
     * 网络请求，服务器结果返回为Json类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForJson(int requestMethod, Context context, String url, RequestParams params, JsonHttpResponseHandler response) {
        switch (requestMethod) {
            case GET:
                client.get(context, url, params, response);
                break;
            case POST:
                client.post(context, url, params, response);
                break;
        }
    }

    /**
     * 网络请求，服务器结果返回为Json类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForJson(int requestMethod, Context context, String url, JsonHttpResponseHandler response) {
        doHttpRequestForJson(requestMethod, context, url, null, response);
    }

    /**
     * 网络请求，服务器结果返回为Json类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForJson(int requestMethod, String url, RequestParams params, JsonHttpResponseHandler response) {
        doHttpRequestForJson(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为Json类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForJson(int requestMethod, String url, JsonHttpResponseHandler response) {
        doHttpRequestForJson(requestMethod, url, null, response);
    }

    /**
     * 网络请求，服务器结果返回为byte[]字节类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForByte(int requestMethod, Context context, String url, RequestParams params, BinaryHttpResponseHandler response) {
        switch (requestMethod) {
            case GET:
                client.get(context, url, params, response);
                break;
            case POST:
                client.post(context, url, params, response);
                break;
        }
    }

    /**
     * 网络请求，服务器结果返回为byte[]字节类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param context       传入context，当context在stop或destroy时会停止该请求
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForByte(int requestMethod, Context context, String url, BinaryHttpResponseHandler response) {
        doHttpRequestForByte(requestMethod, context, url, null, response);
    }

    /**
     * 网络请求，服务器结果返回为byte[]字节类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param params        访问参数
     * @param response      结果回调
     */
    public static void doHttpRequestForByte(int requestMethod, String url, RequestParams params, BinaryHttpResponseHandler response) {
        doHttpRequestForByte(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为byte[]字节类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForByte(int requestMethod, String url, BinaryHttpResponseHandler response) {
        doHttpRequestForByte(requestMethod, url, null, response);
    }

    public static AsyncHttpClient getClient() {
        return client;
    }

    /**
     * 设置链接超时时长
     *
     * @param time
     */
    public static void SetTimeOut(int time) {
        client.setTimeout(time);
    }
}
