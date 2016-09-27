package com.demo;

import joe.frame.application.BaseApplication;
import joe.frame.utils.LogUtils;

/**
 * Description
 * Created by chenqiao on 2015/9/21.
 */
public class DemoApplication extends BaseApplication {
    private static DemoApplication instance;

    String s = "{\"result\"\n\n\n:\n\n\n\n\n{\"subscribers\":[],\"subscribed\":false,\"creator\":null,\"artists\":null,\"tracks\":[],\"tags\":[],\"status\":0,\"subscribedCount\":0,\"cloudTrackCount\":0,\"trackUpdateTime\":1468807719605,\"updateTime\":1468807717575,\"commentThreadId\":\"A_PL_0_424252342\",\"playCount\":0,\"coverImgId\":3397490930543093,\"userId\":305645102,\"createTime\":1468807717575,\"highQuality\":false,\"trackCount\":0,\"description\":null,\"coverImgUrl\":\"http://p4.music.126.net/EWC8bPR8WW9KvhaftdmsXQ==/3397490930543093.jpg\",\"totalDuration\":0,\"adType\":0,\"trackNumberUpdateTime\":0,\"privacy\":0,\"newImported\":false,\"specialType\":5,\"name\":\"我喜欢的音乐\",\"id\":424252342,\"shareCount\":0,\"commentCount\":0},\"code\":200}";

    @Override
    protected void onBaseCreate() {
        instance = this;
        setCrashHandlerEnable(true);
        LogUtils.json(s);
    }

    @Override
    public void crashFileSaveTo(String filePath) {
    }

    public synchronized static DemoApplication getInstance() {
        return instance;
    }
}
