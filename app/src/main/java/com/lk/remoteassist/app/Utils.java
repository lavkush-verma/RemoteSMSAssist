package com.lk.remoteassist.app;

import java.util.ArrayList;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;

import com.lk.remoteassist.app.receiver.MyDeviceAdminReceiver;

public class Utils implements IConstants {

	private static final String TAG = Utils.class.getSimpleName();
	private static final boolean DEBUG = false || BuildConfig.DEBUG;

	public static void addToBlockedSenderList(String sender) {
		final StringBuilder sb = new StringBuilder();
		log("Number to add in Blocked Senders List : " + sender);
		final String blockedNumbers = MyPreferences.getBlockedSenderList();
		if (!TextUtils.isEmpty(blockedNumbers)) {
			sb.append(blockedNumbers).append(sender).append(DELIMITER);
		} else {
			sb.append(sender).append(DELIMITER);
		}
		final String blockedNumberList = sb.toString();
		if (!TextUtils.isEmpty(blockedNumberList)) {
			MyPreferences.saveBlockedSenderList(blockedNumberList);
		}
		log("Blocked Senders List : " + blockedNumberList);
	}

	public static void unblockAllSenders() {
		log("Blocked Senders List Before : " + MyPreferences.getBlockedSenderList());
		MyPreferences.unblockAllSenders();
		log("Blocked Senders List After : " + MyPreferences.getBlockedSenderList());
	}

	public static void disableApplicationComponent(Context context,
			Class<?> className) {
		PackageManager packageManager = context.getPackageManager();
		packageManager.setComponentEnabledSetting(new ComponentName(context,
				className), PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP);
	}

	public static void enableApplicationComponent(Context context,
			Class<?> className) {
		PackageManager packageManager = context.getPackageManager();
		packageManager.setComponentEnabledSetting(new ComponentName(context,
				className), PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);
	}

	public static boolean isDeviceAdminEnabled(Context context) {
		final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		final ComponentName componentName = new ComponentName(context,
				MyDeviceAdminReceiver.class);
		return devicePolicyManager.isAdminActive(componentName);
	}
	
	public static void removeAsDeviceAdmin(Context context) {
		final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		final ComponentName componentName = new ComponentName(context,
				MyDeviceAdminReceiver.class);
		if (devicePolicyManager.isAdminActive(componentName)) {
			devicePolicyManager.removeActiveAdmin(componentName);
		}
	}

	public static boolean isSenderBlocked(String sender) {
		boolean isBlocked = false;
		final String blockedList = MyPreferences.getBlockedSenderList();
		if (!TextUtils.isEmpty(blockedList)) {
			final String[] senderArray = blockedList.split(DELIMITER);
			final ArrayList<String> list = new ArrayList<String>();
			for (String s : senderArray) {
				list.add(s);
			}
			if (list.contains(sender)) {
				isBlocked = true;
			}
		}
		log("Blocked status for " + sender + " is " + isBlocked);
		return isBlocked;
	}
	
	public static void blockSenderIfPossible (String sender) {
		int failureCount = MyPreferences.getFailureCountForSender(sender);
		failureCount++;
		log("Failed attempt count : " + failureCount);
		MyPreferences.updateFailureCountForSender(sender, failureCount);
		if (failureCount >= MAX_FAILED_ATTEMPT) {
			MyPreferences.removeSender(sender);
			Utils.addToBlockedSenderList(sender);
		} else {
			Utils.sendMessage(sender, "Invalid Code. Please Try Again.");
		}
	}
	

	private static void log(String text) {
		if (DEBUG)
			Log.d(TAG, text);
	}

	public static void sendMessage(String sender, String message) {
		if (!TextUtils.isEmpty(sender) && !TextUtils.isEmpty(message)) {
			final SmsManager smsManager = SmsManager.getDefault();
			if (message.length() > SmsMessage.MAX_USER_DATA_BYTES) {
				final ArrayList<String> parts = smsManager
						.divideMessage(message);
				smsManager.sendMultipartTextMessage(sender, null, parts, null,
						null);
			} else {
				smsManager.sendTextMessage(sender, null, message, null, null);
			}
			log("Message has been sent.");
		} else
			log("Message could not be sent. Sender or Message is empty.");

	}
}
