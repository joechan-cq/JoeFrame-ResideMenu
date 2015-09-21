package joe.frame.application;

import android.app.Application;

import joe.frame.handler.CrashHandler;

/**
 * Description
 * Created by chenqiao on 2015/9/21.
 */
public abstract class BaseApplication extends Application {

    protected CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 设置默认异常处理Handler
         */
        crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
        onBaseCreate();
    }

    protected abstract void onBaseCreate();
}
