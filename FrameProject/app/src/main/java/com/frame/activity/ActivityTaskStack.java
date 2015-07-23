package com.frame.activity;

import java.util.ArrayList;

/**
 * Description
 * Created by chenqiao on 2015/7/16.
 */
public class ActivityTaskStack {

    private static ArrayList<FrameBaseActivity> mActivityList = new ArrayList<>();

    public static void add(FrameBaseActivity activity) {
        mActivityList.add(activity);
    }

    public static void remove(FrameBaseActivity activity) {
        mActivityList.remove(activity);
    }

    public static void clean() {
        for (FrameBaseActivity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        mActivityList.clear();
    }
}
