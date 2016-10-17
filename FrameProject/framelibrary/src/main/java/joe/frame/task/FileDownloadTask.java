package joe.frame.task;

import android.util.Log;
import android.webkit.URLUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description
 * Created by chenqiao on 2016/10/10.
 */
public class FileDownloadTask {
    private static final String TAG = "FileDownloadTask";
    private String downloadUrl;
    private File file;
    private File dataFile;
    private RandomAccessFile saveFile;

    private long fileAvailable = 0;
    private long hasDownload = 0;

    private DownloadListener listener;

    private Thread downloadThread;
    public boolean isPaused = false;
    public boolean isFinished = false;
    public boolean isDownloading = false;

    private int httpCode = 0;

    private ScheduledExecutorService updateTask;

    private final Object lock = new Object();

    public FileDownloadTask(String url, File file) {
        this.downloadUrl = url;
        this.file = file;
        this.dataFile = new File(file.getAbsolutePath() + ".tmp");
    }

    public void setDownloadListener(DownloadListener listener) {
        this.listener = listener;
    }

    /**
     * 开始下载
     *
     * @param isResume 是否从断点下载
     */
    public void startDownload(final boolean isResume) {
        if (isDownloading) {
            Log.d(TAG, "download has began");
            return;
        }
        if (!URLUtil.isValidUrl(downloadUrl)) {
            Log.w(TAG, "downloadUrl is not a valid url:" + downloadUrl);
            return;
        }
        downloadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                long nowSize;
                if (file.exists()) {
                    if (listener != null) {
                        listener.onDownloadFinished(file);
                    }
                    return;
                }
                if (!isResume) {
                    dataFile.delete();
                }
                try {
                    if (!dataFile.exists()) {
                        if (dataFile.createNewFile()) {
                            Log.d(TAG, "File create success:" + file.getAbsolutePath());
                        } else {
                            Log.e(TAG, "File create failed:" + file.getAbsolutePath());
                        }
                    }
                    saveFile = new RandomAccessFile(dataFile, "rw");
                    InputStream inputStream;
                    nowSize = dataFile.length();
                    if (isResume && nowSize > 0) {
                        inputStream = getNetInputStream(downloadUrl, nowSize - 1);
                        if (httpCode == 206) {
                            //206表示支持断点下载
                            saveFile.seek(nowSize - 1);
                            fileAvailable += (nowSize - 1);
                            hasDownload = nowSize - 1;
                            Log.d(TAG, "support resume");
                        } else if (httpCode == 416) {
                            Log.w(TAG, "website doesn't support resume or range is wrong, then download anew");
                            inputStream = getNetInputStream(downloadUrl, 0);
                        } else {
                            Log.e(TAG, "download response is error, http code is " + httpCode);
                        }
                    } else {
                        inputStream = getNetInputStream(downloadUrl, 0);
                    }

                    if (inputStream == null) {
                        return;
                    }

                    if (listener != null) {
                        listener.onStart();
                    }
                    updateTask.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (listener != null) {
                                listener.onDownloading(hasDownload, fileAvailable);
                            }
                        }
                    }, 0, 500, TimeUnit.MILLISECONDS);

                    int len;
                    byte[] temp = new byte[10240];
                    while ((len = inputStream.read(temp)) > 0 && !downloadThread.isInterrupted()) {
                        synchronized (lock) {
                            if (isPaused) {
                                lock.wait();
                            }
                            byte[] data = new byte[len];
                            System.arraycopy(temp, 0, data, 0, len);
                            saveFile.write(data);
                            hasDownload += len;
                        }
                    }

                    inputStream.close();
                    saveFile.close();
                    saveFile = null;
                    if (hasDownload == fileAvailable) {
                        isFinished = true;
                        updateTask.shutdownNow();
                        if (listener != null) {
                            listener.onDownloading(hasDownload, fileAvailable);
                        }
                    }
                    if (listener != null && isFinished) {
                        dataFile.renameTo(file);
                        listener.onDownloadFinished(file);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "startDownload: ", e);
                    if (listener != null) {
                        listener.onError(e);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                }
            }
        });
        downloadThread.start();
        isDownloading = true;
        updateTask = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 暂停下载，不能暂停过长时间，不然resume时会有异常发生。
     */
    public void pause() {
        if (!isDownloading) {
            Log.d(TAG, "download has not began");
        } else {
            isPaused = true;
            isDownloading = false;
        }
    }

    /**
     * pause之后，继续下载。
     */
    public void resume() {
        if (isPaused) {
            synchronized (lock) {
                lock.notify();
            }
        }
        isPaused = false;
        isDownloading = true;
    }

    /**
     * 取消当前下载
     */
    public void cancel() {
        synchronized (lock) {
            lock.notifyAll();
        }
        if (downloadThread != null) {
            downloadThread.interrupt();
        }
        if (updateTask != null) {
            updateTask.shutdownNow();
        }
        isDownloading = false;
        isPaused = false;
        isFinished = false;
    }

    private InputStream getNetInputStream(String urlStr, long start) throws IOException {
        URL url;
        HttpURLConnection conn;
        InputStream is;
        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(5000);
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("Range", "bytes=" + start + "-");
        fileAvailable = conn.getContentLength();
        if (fileAvailable == -1) {
            Log.w(TAG, "the content length is not known, or if the content length is greater than Integer.MAX_VALUE");
        } else {
            Log.d(TAG, "available file length:" + fileAvailable);
        }
        httpCode = conn.getResponseCode();
        if (httpCode >= 200 && httpCode < 300) {
            is = conn.getInputStream();
            return is;
        } else {
            Log.e(TAG, " responseCode is " + conn.getResponseCode());
            return null;
        }
    }

    public interface DownloadListener {
        void onStart();

        void onDownloading(long now, long all);

        void onDownloadFinished(File file);

        void onError(Throwable throwable);
    }
}