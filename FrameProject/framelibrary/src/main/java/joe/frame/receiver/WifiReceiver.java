package joe.frame.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

/**
 * Description  Wifi连接广播接收,使用静态注册，添加以下广播
 * <intent-filter>
 * <action android:name="android.net.wifi.RSSI_CHANGED"/>
 * <action android:name="android.net.wifi.STATE_CHANGE"/>
 * <action android:name="android.net.wifi.WIFI_STATE_CHANGED"/>
 * <action android:name="android.net.wifi.SCAN_RESULTS"/>
 * </intent-filter>
 * Created by chenqiao on 2015/11/10.
 */
public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO 完善接收到广播后的操作
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {

        } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {// wifi连接上与否
            System.out.println("网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                switch (info.getDetailedState()) {
                    case DISCONNECTED:
                        System.out.println("wifi网络连接断开");
                        break;
                    case FAILED:
                        System.out.println("wifi连接失败");
                        break;
                }
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                System.out.println("wifi网络连接成功");
            } else if (info.getState().equals(NetworkInfo.State.CONNECTING)) {
                switch (info.getDetailedState()) {
                    case CONNECTING:
                        System.out.println("wifi正在连接");
                        break;
                }
            }

        } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {// wifi打开与否
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                System.out.println("系统关闭wifi");
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                System.out.println("系统开启wifi");
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLING) {
                System.out.println("系统正在开启wifi");
            }
        } else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {// wifi扫描结果可得
            System.out.println("Wifi扫描结果已得");
        }
    }
}