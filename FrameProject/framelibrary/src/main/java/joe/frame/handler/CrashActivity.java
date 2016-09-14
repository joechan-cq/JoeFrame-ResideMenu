package joe.frame.handler;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import joe.frame.activity.FrameBaseActivity;
import joe.frame.utils.FileUtils;
import joe.framelibrary.R;

/**
 * Description
 * Created by chenqiao on 2016/9/13.
 */
public class CrashActivity extends FrameBaseActivity implements View.OnClickListener {
    private TextView crashTv;

    @Override
    protected boolean setToolbarAsActionbar() {
        return false;
    }

    @Override
    protected void onBaseActivityCreated(Bundle savedInstanceState) {
        setMyContentView(R.layout.activity_crash);
        crashTv = (TextView) findViewById(R.id.tv_crash_log);
        crashTv.setMovementMethod(new ScrollingMovementMethod());
        View btn1 = findViewById(R.id.btn_restart);
        if (btn1 != null) {
            btn1.setOnClickListener(this);
        }
        View btn2 = findViewById(R.id.btn_restart_no_wifi);
        if (btn2 != null) {
            btn2.setOnClickListener(this);
        }
        View btn3 = findViewById(R.id.btn_restart_no_cache);
        if (btn3 != null) {
            btn3.setOnClickListener(this);
        }
        String crashLog = CrashHandler.getInstance(null).getLastCrashFile();
        crashTv.setText(crashLog);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        if (i == R.id.btn_restart) {
        } else if (i == R.id.btn_restart_no_wifi) {
            ((WifiManager) getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(false);
        } else if (i == R.id.btn_restart_no_cache) {
            clearUserDataAndCache();
            /** 以下代码会使应用完全杀死，因此无法通过intent进行唤醒。
             ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
             Class clazz = activityManager.getClass();
             try {
             Method method = clazz.getDeclaredMethod("clearApplicationUserData");
             method.invoke(activityManager);
             Log.d("CrashActivity", "clear User data successfully");
             } catch (NoSuchMethodException e) {
             Log.wtf("CrashActivity", e);
             } catch (InvocationTargetException e) {
             Log.wtf("CrashActivity", e);
             } catch (IllegalAccessException e) {
             Log.wtf("CrashActivity", e);
             }
             **/
        }
        startActivity(intent);
        finish();
    }

    private void clearUserDataAndCache() {
        FileUtils.deleteFile(getCacheDir().getAbsolutePath());
        FileUtils.deleteFile(getFilesDir().getAbsolutePath());
        if (getExternalCacheDir() != null) {
            FileUtils.deleteFile(getExternalCacheDir().getAbsolutePath());
        }
        FileUtils.deleteFile("/data/data/" + getPackageName() + "/shared_prefs");
    }
}
