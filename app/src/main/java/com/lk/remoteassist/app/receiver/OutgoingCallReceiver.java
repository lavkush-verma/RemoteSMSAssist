package com.lk.remoteassist.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lk.remoteassist.app.BuildConfig;
import com.lk.remoteassist.app.IConstants;
import com.lk.remoteassist.app.MyPreferences;
import com.lk.remoteassist.app.Utils;
import com.lk.remoteassist.app.activity.UserSettingsActivity;

public class OutgoingCallReceiver extends BroadcastReceiver {
	private static final String TAG = OutgoingCallReceiver.class.getSimpleName();
	private static final boolean DEBUG = false | BuildConfig.DEBUG;

	private static void log(String message) {
		if (DEBUG) Log.d(TAG, message);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String actionName = intent.getAction();
		log(actionName);
		if (Intent.ACTION_NEW_OUTGOING_CALL.equals(actionName)) {
			final String dialingCode = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			log("Dialling Code " + dialingCode);
			if (TextUtils.equals(IConstants.USER_SETTINGS_DIALING_CODE, dialingCode)) {
				setResultData(null);
				abortBroadcast();
				if (!Utils.isDeviceAdminEnabled(context)) {
					MyPreferences.saveIsPrefDeviceAdminEnabled(false);
				}
				final Intent userSettingIntent = new Intent(context, UserSettingsActivity.class);
				userSettingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(userSettingIntent);
			}
		}
	}

}
