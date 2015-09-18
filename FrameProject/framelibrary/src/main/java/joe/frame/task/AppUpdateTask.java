package joe.frame.task;

import android.content.Context;
import android.graphics.Color;

import java.io.ByteArrayInputStream;

import joe.frame.data.AppUpdateInfo;
import joe.frame.dialog.SweetAlertDialog;
import joe.frame.utils.AsyncHttpUtils;
import joe.frame.utils.FileUtils;
import joe.frame.utils.LogUtils;
import joe.frame.utils.PackageUtils;
import joe.frame.utils.ToastUtils;
import joe.frame.utils.http.FrameHttpRspBytes;
import joe.frame.utils.http.FrameHttpRspString;
import joe.frame.utils.http.HttpMethod;

/**
 * Description  应用升级任务类
 * Created by chenqiao on 2015/9/1.
 */
public abstract class AppUpdateTask {

    private Context mContext;

    private String filePath; //保存路径

    private SweetAlertDialog waitDialog;
    private SweetAlertDialog confirmDialog;

    private boolean isMust = false;

    public void checkVersion(Context context, boolean isShowUI, String configUrl, String saveDir, HttpMethod method) {
        this.mContext = context;
        this.filePath = saveDir;
        rsp.setTag(APPUPDATE_TAG);
        LogUtils.d("AppUpdateTask:filepath:" + filePath);
        if (isShowUI) {
            waitDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            waitDialog.getProgressHelper().setBarColor(Color.parseColor("#00ccff"));
            waitDialog.setTitleText("请稍后...");
            waitDialog.setCancelable(true);
            waitDialog.show();
        }
        AsyncHttpUtils.doHttpRequestForString(method, context, configUrl, new FrameHttpRspString() {
                    @Override
                    public void onSuccess(int statusCode, String codeMsg, String rspResult) {
                        AppUpdateInfo info;
                        if ((info = parseUpdateInfo(rspResult)).isNeedToUpdate()) {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.dismiss();
                            }
                            showConfirmDialog(info);
                        } else {
                            if (waitDialog != null && waitDialog.isShowing()) {
                                waitDialog.cancel();
                            }
                            ToastUtils.show(mContext, "已经是最新版本");
                        }
                    }

                    @Override
                    public void onFailed(int statusCode, String codeMsg, String rspResult, Throwable throwable) {
                        if (waitDialog != null && waitDialog.isShowing()) {
                            waitDialog.cancel();
                        }
                        ToastUtils.show(mContext, "网络异常，请稍后再试");
                    }
                }

        );
    }

    private void showConfirmDialog(final AppUpdateInfo info) {
        confirmDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
        confirmDialog.setTitleText(info.getAppName() + ":" + info.getVersionName());
        confirmDialog.setContentText(info.getUpdateInfo());
        if (!info.isMust()) {
            confirmDialog.setConfirmText("确认升级").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
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
        } else {
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
        confirmDialog.setCancelable(true);
        confirmDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                AsyncHttpUtils.cancelReqeustByTag(APPUPDATE_TAG, true);
                sweetAlertDialog.dismiss();
            }
        });
        confirmDialog.setCanceledOnTouchOutside(false);
        confirmDialog.showCancelButton(true);
        confirmDialog.setTitleText("正在下载").setContentText("0%").changeAlertType(SweetAlertDialog.PROGRESS_TYPE);
        String fileName = info.getAppName() + info.getVersionName();
        if (FileUtils.makeDirs(filePath)) {
            LogUtils.d("AppUpdateTask:mk dirs");
        }
        this.filePath = this.filePath + fileName + info.getSuffixName();
        String downloadUrl = info.getDownloadUrl();
        LogUtils.d("AppUpdateTask:APK下载地址：" + downloadUrl);
        AsyncHttpUtils.doHttpRequestForByte(HttpMethod.GET, mContext, downloadUrl, rsp);
    }

    private static final String APPUPDATE_TAG = "APPUPDATE_TAG";
    private static String[] allowTypes = new String[]{"application/vnd.android.package-archive"};
    private FrameHttpRspBytes rsp = new FrameHttpRspBytes(allowTypes) {
        @Override
        public void onSuccess(int statusCode, String codeMsg, byte[] rspBytes) {
            ByteArrayInputStream bais = new ByteArrayInputStream(rspBytes);
            confirmDialog.setContentText("下载马上完成，请稍后");
            FileUtils.writeFile(filePath, bais, false);
            confirmDialog.setContentText("下载已经完成").showCancelButton(true).changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            if (!isMust) {
                confirmDialog.setConfirmText("启动安装").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        PackageUtils.install(mContext, filePath);
                    }
                });
            } else {
                PackageUtils.install(mContext, filePath);
            }
        }

        @Override
        public void onFailed(int statusCode, String codeMsg, byte[] rspBytes, Throwable throwable) {
            LogUtils.d("AppUpdateTask:code:" + statusCode + "  mean:" + codeMsg);
            confirmDialog.setCancelable(true);
            confirmDialog.setCanceledOnTouchOutside(true);
            confirmDialog.hideConfirmButton().setTitleText("下载失败，请检查网络").showCancelButton(true).changeAlertType(SweetAlertDialog.ERROR_TYPE);
        }

        @Override
        public void onProgress(long bytesWritten, long totalSize) {
            super.onProgress(bytesWritten, totalSize);
            int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
            confirmDialog.setContentText(count + "%");
            LogUtils.d("AppUpdateTask:已经下载：" + count + "%");
        }
    };

    public abstract AppUpdateInfo parseUpdateInfo(String info);
}
