package com.android.dvr;

import android.app.Application;
import android.content.Context;

/**
 * Created by duanpeifeng on 2017/3/11 0011.
 */

public class ProApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // 初始化程序崩溃抓取日志
        // CrashUtils.getInstance().init(this);
    }

    public static Context getContext() {
        return context;
    }
}
