package joe.frame.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

/**
 * Description  音乐媒体播放工具类,因为是静态，所以每次使用前请release。
 * Created by chenqiao on 2015/10/16.
 */
public class MediaUtils {
    private static MediaPlayer mediaPlayer;

    /**
     * 播放Assets中的媒体文件
     *
     * @param context  上下文
     * @param fileName 媒体文件名
     */
    public static void playFromAssets(Context context, String fileName) {
        try {
            AssetFileDescriptor fileDescript = context.getAssets().openFd(fileName);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileDescript.getFileDescriptor(), fileDescript.getStartOffset(), fileDescript.getLength());
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放资源中的媒体文件
     *
     * @param context 上下文
     * @param resId   资源ID
     */
    public static void playFromRes(Context context, int resId, boolean isLoop) {
        try {
            mediaPlayer = MediaPlayer.create(context, resId);
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放Uri指向的媒体文件
     *
     * @param context 上下文
     * @param uri     资源Uri
     * @param isLoop  是否循环播放
     */
    public static void playFromUri(Context context, Uri uri, boolean isLoop) {
        try {
            mediaPlayer = MediaPlayer.create(context, uri);
            mediaPlayer.setLooping(isLoop);
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放MediaPlayer
     */
    public static void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
