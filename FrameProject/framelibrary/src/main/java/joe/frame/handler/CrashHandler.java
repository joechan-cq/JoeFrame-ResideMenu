package joe.frame.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TreeSet;

import joe.frame.activity.ActivityTaskStack;
import joe.frame.utils.FileUtils;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类 来接管程序,并记录 发送错误报告.. 註冊方式
 * CrashHandler crashHandler = CrashHandler.getInstance(); //注册crashHandler
 * crashHandler.init(getApplicationContext()); //发送以前没发送的报告(可选)
 * crashHandler.sendPreviousReportsToServer();
 */

/**
 * Description   程序异常崩溃记录类
 * Created by chenqiao on 2015/9/6.
 */
public class CrashHandler implements UncaughtExceptionHandler {
    /**
     * Debug Log tag
     */
    public static final String TAG = "CrashHandler";
    /**
     * 是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提示程序性能
     */
    public static final boolean DEBUG = true;
    /**
     * 系统默认的UncaughtException处理类
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /**
     * CrashHandler实例
     */
    private static CrashHandler INSTANCE;
    /**
     * 程序的Context对象
     */
    private Context mContext;

    private CrashFileSaveListener listener;

    private static boolean needCrashAct = true;

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private static final String VERSION_NAME = "versionName";
    private static final String VERSION_CODE = "versionCode";
    private static final String STACK_TRACE = "STACK_TRACE";
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";

    private SharedPreferences sp;

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler(CrashFileSaveListener listener) {
        this.listener = listener;
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance(CrashFileSaveListener listener) {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler(listener);
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        sp = mContext.getSharedPreferences("crashTime", Context.MODE_PRIVATE);
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 是否使用CrashActivity方案，如果使用，需在主module的清单文件中对CrashActivity进行注册
     */
    public static void setEnableCrashActivity(boolean isEnable) {
        needCrashAct = isEnable;
    }

    private static final String KEY_LAST_CRASH_TIME = "KEY_LAST_CRASH_TIME";

    /**
     * 保存上次崩溃的时间
     */
    private void saveLastCrashTime() {
        sp.edit().putLong(KEY_LAST_CRASH_TIME, System.currentTimeMillis()).apply();
    }

    /**
     * 获取上次崩溃的时间
     */
    private long getLastCrashTime() {
        return sp.getLong(KEY_LAST_CRASH_TIME, -1);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }

            if (needCrashAct) {
                Intent intent;
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                if (System.currentTimeMillis() - getLastCrashTime() > 60 * 1000) {
                    intent = mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName());
                } else {
                    //如果1分钟内频繁崩溃，则启动crash修复界面
                    intent = new Intent(mContext, CrashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0,
                        intent, 0);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
                        restartIntent); // 1秒钟后重启应用
            }
            saveLastCrashTime();

            ActivityTaskStack.exit();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }
        final String msg = ex.getLocalizedMessage();
        // 使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序出错啦:" + msg, Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }

        }.start();
        // 收集设备信息
        collectCrashDeviceInfo(mContext);
        // 保存错误报告文件
        String crashFileName = saveCrashInfoToFile(ex);
        sp.edit().putString("crashFile", crashFileName).apply();
        listener.crashFileSaveTo(crashFileName);
        // 发送错误报告到服务器
        //sendPreviousReportsToServer();
        return true;
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer(mContext);
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    private void sendCrashReportsToServer(Context ctx) {
        String[] crFiles = getCrashReportFiles(ctx);
        String filePath;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
        } else {
            filePath = ctx.getFilesDir().getAbsolutePath();
        }
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                File cr = new File(filePath, fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }

    private void postReport(File file) {
        // TODO 使用HTTP Post 发送错误报告到服务器
    }

    public String getLastCrashFile() {
        String crashFile = sp.getString("crashFile", "");
        if (TextUtils.isEmpty(crashFile)) {
            return "";
        } else {
            File file = new File(crashFile);
            if (!file.exists()) {
                return "";
            } else {
                StringBuilder sb = FileUtils.readFile(crashFile, "UTF-8");
                return sb == null ? "" : sb.toString();
            }
        }
    }

    /**
     * 获取错误报告文件名
     */
    private String[] getCrashReportFiles(Context ctx) {
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
            File dir = new File(filePath);
            return dir.list(filter);
        } else {
            File filesDir = ctx.getFilesDir();
            return filesDir.list(filter);
        }
    }

    /**
     * 保存错误信息到文件中
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put(STACK_TRACE, result);
        String fileName = "";
        try {
            long timestamp = System.currentTimeMillis();
            FileOutputStream trace;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HHmmss", Locale.getDefault());
            String time = format.format(new Date(timestamp));
            fileName = "crash-" + time + CRASH_REPORTER_EXTENSION;
            String filePath;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = mContext.getExternalFilesDir("Crash").getAbsolutePath();
            } else {
                filePath = mContext.getFilesDir().getAbsolutePath() + File.separator + "Crash";
            }
            File file = new File(filePath, fileName);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
            }
            trace = new FileOutputStream(file);
            mDeviceCrashInfo.storeToXML(trace, "crashLog");
            trace.flush();
            trace.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing report file..."
                    + fileName, e);
        }
        return null;
    }

    /**
     * 收集程序崩溃的设备信息
     */
    public void collectCrashDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
                    PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                mDeviceCrashInfo.put(VERSION_NAME,
                        pi.versionName == null ? "not set" : pi.versionName);
                mDeviceCrashInfo.put(VERSION_CODE, String.valueOf(pi.versionCode));
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "Error while collect package info", e);
        }
        // 使用反射来收集设备信息.在Build类中包含各种设备信息,
        // 例如: 系统版本号,设备生产商 等帮助调试程序的有用信息
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                mDeviceCrashInfo.put(field.getName(), field.get(null).toString());
                if (DEBUG) {
                    Log.d(TAG, field.getName() + " : " + field.get(null).toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error while collect crash info", e);
            }
        }
    }

}