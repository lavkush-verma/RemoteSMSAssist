package com.lk.remoteassist.app.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyDeviceAdminReceiver extends DeviceAdminReceiver {

	private static final String TAG = MyDeviceAdminReceiver.class.getSimpleName();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		final String action = intent.getAction();
		Log.d(TAG, "Action name  : " + action);
	}
}
