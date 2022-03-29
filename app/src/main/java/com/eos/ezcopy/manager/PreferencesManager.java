package com.eos.ezcopy.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "global_config";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;
    private static Context mContext;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        Log.i("1xxxx", "init sp");
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
            mContext = context;
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setTextDataList(Set<String> textDataList) {
        mPref.edit().putStringSet("text_list", textDataList).apply();
    }

    public Set<String> getTextDataList() {
        return mPref.getStringSet("text_list", new HashSet<>());
    }
}

