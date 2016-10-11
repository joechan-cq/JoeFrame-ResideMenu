package joe.frame.utils.http;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import joe.frame.utils.LogUtils;


//Http请求的工具类
public class HttpUtils {

    private static final int TIMEOUT_IN_MILLIONS = 8000;
    private static ExecutorService coreExecutorService = Executors.newCachedThreadPool();

    public interface CallBack {
        void onRequestComplete(HttpResponse result);

        void onError(Exception e);
    }

    public static void doGetAsync(String urlString, Map<String, String> params, CallBack callBack) {
        StringBuilder urlBuilder = new StringBuilder(urlString.trim() + "?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    urlBuilder.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String url = urlBuilder.substring(0, urlBuilder.length() - 1);
        doGetAsync(url, callBack);
    }

    /**
     * 异步的Get请求
     *
     * @param urlStr   请求地址
     * @param callBack 结果回调
     */
    public static void doGetAsync(final String urlStr, final CallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse result = doGet(urlStr);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (IOException e) {
                    LogUtils.e("doGetAsync Exception:" + e.toString());
                    if (callBack != null) {
                        callBack.onError(e);
                    }
                }
            }
        };
        coreExecutorService.execute(runnable);
    }

    public static void doPostAsync(String urlString, Map<String, String> params, CallBack callBack) {
        StringBuilder paramsBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    paramsBuilder.append(URLEncoder.encode(key, "UTF-8")).append("=").append(URLEncoder.encode(value, "UTF-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String paramsStr = paramsBuilder.substring(0, paramsBuilder.length() - 1);
        LogUtils.d("doPost params:" + paramsStr);
        doPostAsync(urlString, paramsStr, callBack);
    }

    /**
     * 异步的Post请求
     *
     * @param urlStr   请求地址
     * @param params   请求参数
     * @param callBack 结果回调
     */
    public static void doPostAsync(final String urlStr, final String params, final CallBack callBack) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    HttpResponse result = doPost(urlStr, params);
                    if (callBack != null) {
                        callBack.onRequestComplete(result);
                    }
                } catch (IOException e) {
                    LogUtils.e("doPostAsync Exception:" + e.toString());
                    if (callBack != null) {
                        callBack.onError(e);
                    }
                }
            }
        };
        coreExecutorService.execute(runnable);
    }

    /**
     * Get请求，获得返回数据
     *
     * @param urlStr 请求地址
     * @return 请求结果
     * @throws IOException
     */
    public static HttpResponse doGet(String urlStr) throws IOException {
        LogUtils.d("doGet url:" + urlStr);
        URL url;
        HttpURLConnection conn;
        InputStream is;
        ByteArrayOutputStream baos;
        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
        conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            is = conn.getInputStream();
            baos = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[128];
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            is.close();
            byte[] result = baos.toByteArray();
            baos.close();
            conn.disconnect();
            LogUtils.d("response:" + new String(result));
            return new HttpResponse(code, result, "");
        } else {
            return new HttpResponse(code, null, "ResponseCode is not 2xx but " + code);
        }
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url   发送请求的 URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return 所代表远程资源的响应结果
     * @throws IOException
     */
    public static HttpResponse doPost(String url, String param) throws IOException {
        LogUtils.d("doPost url:" + url + "  params:" + param);
        PrintWriter out = null;
        URL realUrl = new URL(url);
        // 打开和URL之间的连接
        HttpURLConnection conn = (HttpURLConnection) realUrl
                .openConnection();
        // 设置通用的请求属性
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setUseCaches(false);
        // 发送POST请求必须设置如下两行
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
        conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

        if (param != null && !param.trim().equals("")) {
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
        }
        if (out != null) {
            out.close();
        }
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            InputStream is = conn.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int len;
            byte[] buf = new byte[128];
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            is.close();
            byte[] result = baos.toByteArray();
            baos.close();
            conn.disconnect();
            LogUtils.d("response:" + new String(result));
            return new HttpResponse(code, result, "");
        } else {
            return new HttpResponse(code, null, "ResponseCode is not 2xx but " + code);
        }
    }

    public static InputStream doGetForInputStream(String urlStr) throws IOException {
//        urlStr = URLEncoder.encode(urlStr, "UTF-8");
        LogUtils.d("doGet url:" + urlStr);
        URL url;
        HttpURLConnection conn;
        InputStream is;
        url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
        conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("connection", "Keep-Alive");
        int code = conn.getResponseCode();
        if (code >= 200 && code < 300) {
            is = conn.getInputStream();
            return is;
        } else {
            throw new RuntimeException(" responseCode is " + conn.getResponseCode());
        }
    }
}
