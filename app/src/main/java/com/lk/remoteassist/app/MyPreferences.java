package com.lk.remoteassist.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MyPreferences implements IConstants {

	private static Context sContext;

	public static String getBlockedSenderList() {
		return getSharedPreferences().getString(PREF_BLOCKED_SENDER, null);
	}
	
	public static int getFailureCountForSender(String sender) {
		return getSharedPreferences().getInt(sender, 0);
	}
	
	public static void updateFailureCountForSender(String sender, int count) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putInt(sender, count).commit();
	}

	public static String getPrefSecretCode() {
		return getSharedPreferences().getString(PREF_SECRET_CODE, DEFAULT_SECRET_CODE);
	}
	
	public static String getPrefSecurityAnswer() {
		return getSharedPreferences().getString(PREF_SECURITY_ANSWER, EMPTY_TOKEN);
	}
	
	public static String getPrefSecurityQuestion() {
		return getSharedPreferences().getString(PREF_SECURITY_QUESTION, "0");
	}

	public static SharedPreferences getSharedPreferences() {
		verify();
		return PreferenceManager.getDefaultSharedPreferences(sContext);
	}

	public static void initialize(Context context) {
		sContext = context;
		if (null == sContext)
			throw new IllegalStateException("context must not be null.");
	}

	public static boolean isPrefAutoStartEnabled() {
		return getSharedPreferences().getBoolean(PREF_IS_AUTO_START_ENABLED, false);
	}

	public static boolean isPrefDeviceAdminEnabled() {
		return getSharedPreferences().getBoolean(PREF_IS_DEVICE_ADMIN_ENABLED, false);
	}

	public static boolean isPrefRemoteAccessEnabled() {
		return getSharedPreferences().getBoolean(PREF_IS_REMOTE_ACCESS_ENABLED, false);
	}

	public static void saveBlockedSenderList(String senderNumberList) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString(PREF_BLOCKED_SENDER, senderNumberList).commit();
	}

	public static void unblockAllSenders() {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.remove(PREF_BLOCKED_SENDER).commit();
	}
	
	public static void removeSender(String sender) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.remove(sender).commit();
	}

	public static void saveIsPrefAutoStartEnabled(boolean value) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean(PREF_IS_AUTO_START_ENABLED, value).commit();
	}

	public static void saveIsPrefDeviceAdminEnabled(boolean value) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean(PREF_IS_DEVICE_ADMIN_ENABLED, value).commit();
	}

	public static void saveIsPrefRemoteAccessEnabled(boolean value) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean(PREF_IS_REMOTE_ACCESS_ENABLED, value).commit();
	}

	public static void savePrefSecretCode(String value) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString(PREF_SECRET_CODE, value).commit();
	}
	
	public static void savePrefSecurityAnswer(String value) {
		final SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putString(PREF_SECURITY_ANSWER, value).commit();
	}

	private static void verify() {
		if (null == sContext)
			throw new IllegalStateException("MyPreferences.initialize must be called before calling this method");
	}

}
