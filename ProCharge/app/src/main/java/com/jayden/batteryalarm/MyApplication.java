package com.jayden.batteryalarm;

import android.app.Application;
import android.content.Context;

import com.balsikandar.crashreporter.CrashReporter;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReporter.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
