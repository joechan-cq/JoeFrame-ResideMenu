package joe.frame.utils;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;

import joe.frame.utils.http.FrameHttpRspBytes;
import joe.frame.utils.http.FrameHttpRspJson;
import joe.frame.utils.http.FrameHttpRspString;
import joe.frame.utils.http.FrameRequestParams;
import joe.frame.utils.http.HttpMethod;

/**
 * Description  异步网络请求类，再封装android-async-http类
 * Created by chenqiao on 2015/7/20.
 */
public class AsyncHttpUtils {

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
    public static void doHttpRequestForString(HttpMethod requestMethod, Context context, String url, FrameRequestParams params, FrameHttpRspString response) {
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
    public static void doHttpRequestForString(HttpMethod requestMethod, Context context, String url, FrameHttpRspString response) {
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
    public static void doHttpRequestForString(HttpMethod requestMethod, String url, FrameRequestParams params, FrameHttpRspString response) {
        doHttpRequestForString(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为字符串类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForString(HttpMethod requestMethod, String url, FrameHttpRspString response) {
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
    public static void doHttpRequestForJson(HttpMethod requestMethod, Context context, String url, FrameRequestParams params, FrameHttpRspJson response) {
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
    public static void doHttpRequestForJson(HttpMethod requestMethod, Context context, String url, FrameHttpRspJson response) {
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
    public static void doHttpRequestForJson(HttpMethod requestMethod, String url, FrameRequestParams params, FrameHttpRspJson response) {
        doHttpRequestForJson(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为Json类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForJson(HttpMethod requestMethod, String url, FrameHttpRspJson response) {
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
    public static void doHttpRequestForByte(HttpMethod requestMethod, Context context, String url, FrameRequestParams params, FrameHttpRspBytes response) {
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
    public static void doHttpRequestForByte(HttpMethod requestMethod, Context context, String url, FrameHttpRspBytes response) {
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
    public static void doHttpRequestForByte(HttpMethod requestMethod, String url, FrameRequestParams params, FrameHttpRspBytes response) {
        doHttpRequestForByte(requestMethod, null, url, params, response);
    }

    /**
     * 网络请求，服务器结果返回为byte[]字节类型
     *
     * @param requestMethod 请求方式#GET,#POST
     * @param url           访问地址
     * @param response      结果回调
     */
    public static void doHttpRequestForByte(HttpMethod requestMethod, String url, FrameHttpRspBytes response) {
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