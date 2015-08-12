package com.demo;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import joe.frame.utils.AsyncHttpUtils;
import joe.frame.utils.LogUtils;
import joe.frame.utils.ToastUtils;

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
        AsyncHttpUtils.doHttpRequestForString(AsyncHttpUtils.GET, "http://qkhf.device.53iq.com/api/about", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, Header[] headers, String s) {
                LogUtils.d("joe=====" + s);
            }
        });
    }

    public void getAboutForJson(final Context context) {
        AsyncHttpUtils.doHttpRequestForJson(AsyncHttpUtils.GET, "http://qkhf.device.53iq.com/api/about", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                LogUtils.d("joe json======" + response.toString());
                ToastUtils.show(context, response.toString());
            }
        });
    }
}
