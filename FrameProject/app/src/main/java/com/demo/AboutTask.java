package com.demo;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import joe.frame.utils.AsyncHttpUtils;
import joe.frame.utils.LogUtils;
import joe.frame.utils.http.FrameHttpRspJson;
import joe.frame.utils.http.FrameHttpRspString;
import joe.frame.utils.http.HttpMethod;

/**
 * Description
 * Created by chenqiao on 2015/7/20.
 */
public class AboutTask {

    private AboutTask() {

    }

    private static AboutTask instance;

    public static AboutTask getInstance() {
        if (instance == null) {
            synchronized (AboutTask.class) {
                if (instance == null) {
                    instance = new AboutTask();
                }
            }
        }
        return instance;
    }

    public void getAboutForString() {
        AsyncHttpUtils.doHttpRequestForString(HttpMethod.GET, "http://qkhf.device.53iq.com/api/about", new FrameHttpRspString() {

            @Override
            public void onSuccess(int statusCode, String codeMsg, String rspResult) {
                LogUtils.d("joe====result:" + rspResult + " statuscode=" + statusCode);
            }

            @Override
            public void onFailed(int statusCode, String codeMsg, String rspResult, Throwable throwable) {

            }
        });
    }

    public void getAboutForJson(final Context context) {
        AsyncHttpUtils.doHttpRequestForJson(HttpMethod.GET, "http://qkhf.device.53iq.com/api/about", new FrameHttpRspJson() {

            @Override
            public void onSuccess(int statusCode, String codeMsg, JSONObject rspJSONObject) {
                LogUtils.d("joe====jsonresult:" + rspJSONObject.toString());
            }

            @Override
            public void onSuccess(int statusCode, String codeMsg, JSONArray rspJSONArray) {

            }

            @Override
            public void onFailed(int statusCode, String codeMsg, Object rspResult, Throwable throwable) {

            }
        });
    }
}
