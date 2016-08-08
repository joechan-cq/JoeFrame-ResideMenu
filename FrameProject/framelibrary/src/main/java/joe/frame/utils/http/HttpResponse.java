package joe.frame.utils.http;

import java.util.Locale;

/**
 * Description
 * Created by chenqiao on 2016/7/20.
 */
public class HttpResponse {

    private int httpCode = 0;

    private byte[] body;

    private String errorInfo = "";

    public int getHttpCode() {
        return httpCode;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyToString() {
        if (body == null) {
            return "";
        }
        return new String(body);
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public HttpResponse() {
    }

    public boolean isSuccess() {
        return httpCode == 200;
    }

    public HttpResponse(int httpCode, byte[] body, String errorInfo) {
        this.httpCode = httpCode;
        this.body = body;
        this.errorInfo = errorInfo;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "HttpCode:%d\nBody:%s\nErrorInfo:%s", httpCode, new String(body), errorInfo);
    }
}
