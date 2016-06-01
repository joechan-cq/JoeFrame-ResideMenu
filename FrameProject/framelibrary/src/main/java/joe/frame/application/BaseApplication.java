package joe.frame.application;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;

import joe.frame.handler.CrashFileSaveListener;
import joe.frame.handler.CrashHandler;

/**
 * Description  实现自定义异常处理的Application
 * Created by chenqiao on 2015/9/21.
 */
public abstract class BaseApplication extends Application implements CrashFileSaveListener {

    protected CrashHandler crashHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 设置默认异常处理Handler
         */
        crashHandler = CrashHandler.getInstance(this);
        crashHandler.init(getApplicationContext());
        onBaseCreate();
        ApplicationInfo appInfo = this.getApplicationInfo();
        //调试模式下是使用严苛模式
        int appFlags = appInfo.flags;
        if ((appFlags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            // Do StrictMode setup here
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
    }

    protected abstract void onBaseCreate();

    @Override
    public abstract void crashFileSaveTo(String filePath);
}
