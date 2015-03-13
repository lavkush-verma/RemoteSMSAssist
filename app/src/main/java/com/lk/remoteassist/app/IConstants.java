package com.lk.remoteassist.app;

public interface IConstants {
	
	public static final int ADMIN_INTENT_REQUEST_CODE = 100;
	public static final String SMS_FORMAT = ".*##.*";
	public static final String CMD_CLR_LOGS = "CLEAR_LOGS";
	public static final String CMD_LOCK_CLR_LOGS = "LOCK_CLEAR_LOGS";
	public static final String CMD_FORGOT = "FORGOT";
	public static final String CMD_HELP = "HELP";
	public static final String CMD_HIDE ="HIDE";
	public static final String CMD_LOCK = "LOCK";
	public static final String CMD_RING = "RING";
	public static final String CMD_RESET_PHONE = "RESET_PHONE";
	public static final String CMD_DELETE_MESSAGES = "DELETE_MESSAGES";
	public static final String CMD_REMOVE_GOOGLE_ACCOUNTS = "REMOVE_GOOGLE";
	public static final String CMD_MASTER_COMMAND = "MASTER_COMMAND";
	public static final String CMD_LOCATION = "LOC";
	
	public static final String CMD_SEPARATOR = "##";
	public static final String CMD_SILENT = "SILENT";
	public static final String CMD_VIBRATE = "VIBRATE";
	public static final String DEFAULT_SECRET_CODE = "XXXXX";
	public static final String DELIMITER = ";";
	public static final String EMPTY_TOKEN = "";
	public static final String KEY_MESSAGE = "KEY_MESSAGE";
	public static final String KEY_SENDER = "KEY_SENDER";
	public static final int MAX_FAILED_ATTEMPT = 5;
	public static final String NEW_LINE_TOKEN ="\n";
	public static final String PREF_BLOCKED_SENDER = "prefBlockedSender";
	public static final String PREF_IS_AUTO_START_ENABLED = "prefIsAutoStartupEnabled";
	public static final String PREF_IS_DEVICE_ADMIN_ENABLED = "prefIsDeviceAdminEnabled";
	public static final String PREF_IS_REMOTE_ACCESS_ENABLED = "prefIsRemoteAccessEnabled";
	public static final String PREF_SECRET_CODE = "prefSecretCode";
	public static final String PREF_SECURITY_QUESTION = "prefSecurityQuestion";
	public static final String PREF_SECURITY_ANSWER = "prefSecurityAnswer";
	public static final String SECRET_CODE = "SECRET_CODE";
	public static final String SPACE_TOKEN = " ";
	public static final String USER_SETTINGS_DIALING_CODE = "00000";
	public static final String DEFAULT_STRING = "EMPTY";
	
}

