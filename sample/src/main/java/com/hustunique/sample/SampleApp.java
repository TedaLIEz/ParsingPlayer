package com.hustunique.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * Created by CoXier on 17-2-28.
 */

public class SampleApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
