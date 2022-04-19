package com.eos.ezcopy;

import android.app.Application;
import android.content.Context;

import com.eos.ezcopy.manager.PreferencesManager;

public class EZCopy extends Application {

    private static EZCopy sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        PreferencesManager.initializeInstance(getApplicationContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static EZCopy getInstance() {
        return sInstance;
    }
}
