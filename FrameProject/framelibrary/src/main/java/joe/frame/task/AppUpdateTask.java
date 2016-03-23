package joe.frame.task;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import joe.frame.dialog.SweetAlertDialog;
import joe.frame.model.AppUpdateInfo;
import joe.frame.security.MD5Helper;
import joe.frame.utils.FileUtils;
import joe.frame.utils.LogUtils;
import joe.frame.utils.NetUtils;
import joe.frame.utils.PackageUtils;
import joe.frame.utils.ToastUtils;

/**
 * Description  应用升级任务类
 * Created by chenqiao on 2015/9/1.
 */
public abstract class AppUpdateTask {

    private Context mContext;

    private String filePath;
    private String fileDir;//保存路径

    private SweetAlertDialog waitDialog;
    private SweetAlertDialog confirmDialog;

    private boolean isMust = false;

    private BroadcastReceiver receiver;

    public synchronized void checkVersion(Context context, boolean isShowUI, String configUrl, String saveDir) {
        this.mContext = context;
        this.fileDir = saveDir;
        LogUtils.d("AppUpdateTask:fileDir:" + fileDir);
        if (isShowUI) {
            waitDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            waitDialog.getProgressHelper().setBarColor(Color.parseColor("#00ccff"));
            waitDialog.setTitleText("请稍后...");
            waitDialog.setCancelable(true);
            waitDialog.show();
        }
        GetAsyncTask task = new GetAsyncTask() {
            @Override
            protected void onPostExecute(String rspResult) {
                if (rspResult == null) {
                    if (waitDialog != null && waitDialog.isShowing()) {
                        waitDialog.cancel();
                    }
                    ToastUtils.show(mContext, "请求失败，请稍后再试");
                } else {
                    AppUpdateInfo info;
                    if ((info = parseUpdateInfo(rspResult)).isNeedToUpdate()) {
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.dismiss();
                        }
                        File apkFile = new File(fileDir, info.getAppName() + info.getVersionName() + info.getSuffixName());
                        if (apkFile.exists()) {
                            String fileMd5 = MD5Helper.getFileMD5String(apkFile);
                            if (!TextUtils.isEmpty(fileMd5) && fileMd5.equals(info.getMd5())) {
                                showInstallDialog(info);
                            } else {
                                apkFile.delete();
                                showConfirmDialog(info);
                            }
                        } else {
                            showConfirmDialog(info);
                        }
                    } else {
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.cancel();
                        }
                        ToastUtils.show(mContext, "已经是最新版本");
                    }
                }
            }
        };
        task.execute(configUrl);
    }

    /**
     * 本地已经有了安装包,显示的Dialog
     */
    private void showInstallDialog(final AppUpdateInfo info) {
        confirmDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText(info.getAppName() + ":" + info.getVersionName() + "\n已找到本地安装包");
        confirmDialog.setContentText(info.getUpdateInfo());
        confirmDialog.setCancelable(true);
        confirmDialog.setCanceledOnTouchOutside(true);
        if (!info.isMust()) {
            confirmDialog.setConfirmText("启动安装").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    PackageUtils.install(mContext, fileDir + info.getAppName() + info.getVersionName() + info.getSuffixName());
                }
            });
            confirmDialog.setCancelText("取消").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                }
            });
            confirmDialog.setOtherText("忽略版本").setOtherClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ignoreThisVersion(info);
                    sweetAlertDialog.cancel();
                }
            });
        } else {
            confirmDialog.hideOtherButton();
            confirmDialog.hideConfirmButton();
            confirmDialog.setCancelable(false);
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.showCancelButton(false);
            this.isMust = info.isMust();
            PackageUtils.install(mContext, fileDir + info.getAppName() + info.getVersionName() + info.getSuffixName());
        }
        confirmDialog.show();
    }

    /**
     * 显示确认升级的Dialog
     */
    private void showConfirmDialog(final AppUpdateInfo info) {
        confirmDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText(info.getAppName() + ":" + info.getVersionName());
        confirmDialog.setContentText(info.getUpdateInfo());
        confirmDialog.setCancelable(true);
        confirmDialog.setCanceledOnTouchOutside(true);
        if (!info.isMust()) {
            confirmDialog.setConfirmText("升级").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    startDownLoadAPK(info);
                }
            });
            confirmDialog.setCancelText("取消").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.cancel();
                }
            });
            confirmDialog.setOtherText("忽略版本").setOtherClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    ignoreThisVersion(info);
                    sweetAlertDialog.cancel();
                }
            });
        } else {
            confirmDialog.hideOtherButton();
            confirmDialog.hideConfirmButton();
            confirmDialog.setCancelable(false);
            confirmDialog.setCanceledOnTouchOutside(false);
            confirmDialog.showCancelButton(false);
            this.isMust = info.isMust();
            startDownLoadAPK(info);
        }
        confirmDialog.show();
    }

    private void startDownLoadAPK(AppUpdateInfo info) {
        final DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        String fileName = info.getAppName() + info.getVersionName();
        if (FileUtils.makeDirs(fileDir)) {
            LogUtils.d("AppUpdateTask:mk dirs");
        }
        this.filePath = this.fileDir + fileName + info.getSuffixName();
        String downloadUrl = info.getDownloadUrl();
        LogUtils.d("AppUpdateTask:APK下载地址：" + downloadUrl);
        if (!NetUtils.isConnected(mContext)) {
            confirmDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
            confirmDialog.setTitleText("下载失败").setContentText("请检查网络连接");
            return;
        }
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setTitle(info.getAppName() + "-" + info.getVersionName());
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.deleteOnExit();
        }
        request.setDestinationUri(Uri.fromFile(file));
        final long requestId = manager.enqueue(request);

        confirmDialog.setCancelable(true);
        confirmDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                manager.remove(requestId);
                sweetAlertDialog.dismiss();
            }
        });
        confirmDialog.hideOtherButton();
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.showCancelButton(true);
        confirmDialog.setTitleText("正在下载").setContentText("请稍后或查看通知栏中进度").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (requestId == reference) {
                    Log.d("AppUpdateTask", "receive broadcast");
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = manager.query(query);
                    if (cursor.moveToFirst()) {
                        int index = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(index);
                        switch (status) {
                            case DownloadManager.STATUS_SUCCESSFUL:
                                Log.d("AppUpdateTask", "下载成功");
                                confirmDialog.setTitleText("下载完成").setContentText("请进行安装").showCancelButton(true).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                confirmDialog.setConfirmText("启动安装").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        PackageUtils.install(mContext, filePath);
                                    }
                                });
                                if (isMust) {
                                    confirmDialog.showCancelButton(false);
                                }
                                PackageUtils.install(mContext, filePath);
                                break;
                            case DownloadManager.STATUS_FAILED:
                                Log.d("AppUpdateTask", "下载失败");
                                String error = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                                confirmDialog.setCancelable(true);
                                confirmDialog.setCanceledOnTouchOutside(true);
                                confirmDialog.hideConfirmButton().setTitleText("下载失败").setContentText(error).showCancelButton(true).changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                break;
                            default:
                                Log.d("AppUpdateTask", "status code:" + status);
                                break;
                        }
                    }
                    if (receiver != null) {
                        mContext.unregisterReceiver(receiver);
                    }
                    cursor.close();
                }
            }
        };
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(receiver, filter);
    }

    public abstract AppUpdateInfo parseUpdateInfo(String info);

    protected abstract void ignoreThisVersion(AppUpdateInfo info);


    private class GetAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String configUrl = params[0];
            try {
                URL url = new URL(configUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.connect();
                int statusCode = conn.getResponseCode();
                if (statusCode >= 200 && statusCode < 300) {
                    InputStream in = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    String readline;
                    while ((readline = reader.readLine()) != null) {
                        result = result.append(readline).append("\n");
                    }
                    reader.close();
                    conn.disconnect();
                    return result.toString();
                } else {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}