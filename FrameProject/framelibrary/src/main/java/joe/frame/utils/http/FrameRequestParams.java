package joe.frame.utils.http;

import com.loopj.android.http.RequestParams;

import java.util.Map;

/**
 * Description
 * Created by chenqiao on 2015/8/12.
 */
public class FrameRequestParams extends RequestParams {
    public FrameRequestParams() {
        super();
    }

    public FrameRequestParams(Map<String, String> source) {
        super(source);
    }

    public FrameRequestParams(String key, String value) {
        super(key, value);
    }

    public FrameRequestParams(Object... keysAndValues) {
        super(keysAndValues);
    }
}
