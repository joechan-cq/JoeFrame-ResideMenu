package com.demo.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.demo.frameproject.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import joe.frame.adapter.CommonAdapter;
import joe.frame.adapter.ViewHolder;
import joe.frame.annotations.ViewInject;
import joe.frame.dialog.SweetAlertDialog;
import joe.frame.fragment.FrameBaseFragment;
import joe.frame.task.FileDownloadTask;

/**
 * Description
 * Created by chenqiao on 2015/7/20.
 */
public class DemoFragment extends FrameBaseFragment {

    @ViewInject(R.id.jump)
    private Button btn;

    @ViewInject(R.id.listView)
    private ListView listView;

    FileDownloadTask downloadTask;

    SweetAlertDialog dialog;

    @Override
    protected boolean isShowLoading() {
        return false;
    }

    @Override
    protected void onBaseFragmentCreate(Bundle savedInstanceState) {
        setMyContentView(R.layout.demo_weblayout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new SecondFragment(), "Second");
            }
        });
        //要使Fragment重写Toolbar的菜单栏，需设置true
        setHasOptionsMenu(true);
        List<String> data = new ArrayList<>();
        data.add("hello world");
        data.add("it's a test");
        data.add("it's a demo");
        data.add("welcome frame");
        data.add("author Joe");

        CommonAdapter myAdapter = new CommonAdapter<String>(context, data, R.layout.demo_listitem) {
            @Override
            public void convert(ViewHolder holder, String item) {
                holder.setText(R.id.itemtextview, item);
            }
        };
        listView.setAdapter(myAdapter);

        File file = new File(getContext().getExternalCacheDir(), "ireader.apk");
        downloadTask = new FileDownloadTask("http://dl-sh-ctc-2.pchome.net/3r/bz/iReader_android_V5.6.0_.apk?key=434c4cd653149638ba1cd095b49521da&tmp=1476091326019", file);
        downloadTask.setDownloadListener(new FileDownloadTask.DownloadListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onDownloading(final long now, final long all) {
                Log.d("DemoFragment", "onDownloading: " + now + "  " + all);
                btn.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setTitleText("总大小:" + String.valueOf(all));
                        dialog.setContentText("已下载:" + String.valueOf(now));
                    }
                });
            }

            @Override
            public void onDownloadFinished(File file) {
                btn.post(new Runnable() {
                    @Override
                    public void run() {
                        dialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        dialog.hideConfirmButton().hideOtherButton().setCancelText("确定").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(Throwable throwable) {
            }
        });
        dialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitleText("总大小:").setContentText("已下载:").setCancelText("停止下载").setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                downloadTask.cancel();
                sweetAlertDialog.dismiss();
            }
        }).showConfirmButton().setConfirmText("暂停").setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (!downloadTask.isPaused && downloadTask.isDownloading) {
                    downloadTask.pause();
                    dialog.setConfirmText("继续");
                } else if (downloadTask.isPaused) {
                    dialog.setConfirmText("暂停");
                    downloadTask.resume();
                }
            }
        });

        findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask.startDownload(true);
                dialog.show();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}