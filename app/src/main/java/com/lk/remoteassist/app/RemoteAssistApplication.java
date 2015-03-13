package com.lk.remoteassist.app;

import android.app.Application;
import android.util.Log;

public class RemoteAssistApplication extends Application {
	private static final String TAG = RemoteAssistApplication.class.getSimpleName();
	private static final boolean DEBUG = false || BuildConfig.DEBUG;
	
	private static void log(String message) {
		if (DEBUG) Log.d(TAG, message);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		log("OnCreate");
		MyPreferences.initialize(getApplicationContext());
	}
	
}
