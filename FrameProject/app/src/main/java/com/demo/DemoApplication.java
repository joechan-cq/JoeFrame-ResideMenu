package com.demo;

import joe.frame.application.BaseApplication;

/**
 * Description
 * Created by chenqiao on 2015/9/21.
 */
public class DemoApplication extends BaseApplication {
    private static DemoApplication instance;

    @Override
    protected void onBaseCreate() {
        instance = this;
    }

    @Override
    public void crashFileSaveTo(String filePath) {

    }

    public synchronized static DemoApplication getInstance() {
        return instance;
    }
}
