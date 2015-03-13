package com.lk.remoteassist.app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.CallLog;
import android.text.TextUtils;
import android.util.Log;

import com.lk.remoteassist.app.BuildConfig;
import com.lk.remoteassist.app.IConstants;
import com.lk.remoteassist.app.MyPreferences;
import com.lk.remoteassist.app.R;
import com.lk.remoteassist.app.Utils;

public class RemoteAssistService extends IntentService implements IConstants {

	private static final String TAG = RemoteAssistService.class.getSimpleName();
	private static final boolean DEBUG = false || BuildConfig.DEBUG;
	private String mMessageSender;
	private DevicePolicyManager mDevicePolicyManager;
	
	private static void log(String text) {
		if (DEBUG) Log.d(TAG, text);
	}
	
	public RemoteAssistService(String name) {
		super(name);
	}
	
	public RemoteAssistService() {
		super("RemoteAssistService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mDevicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		final String messageBody = intent.getStringExtra(KEY_MESSAGE);
		mMessageSender = intent.getStringExtra(KEY_SENDER);
		log(mMessageSender + " -- " + messageBody);
		final String secretCode = MyPreferences.getPrefSecretCode();
		if (messageBody.startsWith(secretCode) || messageBody.startsWith(SECRET_CODE)) {
			MyPreferences.removeSender(mMessageSender);
			final int commandCode = parseMessageForCommandCode(messageBody);
			switch (commandCode) {
			case 0:
				processSecretCodeRecoveryQuestionCommand();
				break;
			case 1:
				processPhoneLockCommand();
				break;
			case 2:
				processClearLogsCommand();
				break;
			case 3:
				processPhoneRingerCommand();
				break;
			case 4:
				processPhoneSilentCommand();
				break;
			case 5:
				processPhoneVibrateCommand();
				break;
			case 6:
				processLockAndClearLogsCommand();
				break;
			case 7:
				processForgotSecretCodeCommand();
				break;
			case 8:
				processResetPhoneCommand();
				break;
			case 9:
				processDeleteMessagesCommand();
				break;
			case 10:
				processRemoveAllGoogleAccounts();
				break;
			case 11:
				processMasterCommand();
				break;
			case 12:
				processGetLocationCommand();
				break;
			default:
				Utils.sendMessage(mMessageSender, getString(R.string.cmd_failure));
				break;
			}
		} else {
			Utils.blockSenderIfPossible(mMessageSender);
		}
	}
	
	/**
	 * Parses the message for command code.
	 *
	 * @param message the message
	 * @return the int
	 */
	private int parseMessageForCommandCode(String message) {
		int commandCode = -1;
		final String[] stringArray = message.split(CMD_SEPARATOR);
		if (stringArray.length > 1) {
			final String command = stringArray[1];
			if (CMD_FORGOT.equalsIgnoreCase(command)) {
				commandCode = 0;
			} else if (CMD_LOCK.equalsIgnoreCase(command)) {
				commandCode = 1;
			} else if (CMD_CLR_LOGS.equalsIgnoreCase(command)) {
				commandCode = 2;
			} else if (CMD_RING.equalsIgnoreCase(command)) {
				commandCode = 3;
			} else if (CMD_SILENT.equalsIgnoreCase(command)) {
				commandCode = 4;
			} else if (CMD_VIBRATE.equalsIgnoreCase(command)) {
				commandCode = 5;
			} else if (CMD_LOCK_CLR_LOGS.equalsIgnoreCase(command)) {
				commandCode = 6;
			} else if (command.equalsIgnoreCase(MyPreferences.getPrefSecurityAnswer())){
				commandCode = 7;
			} else if (CMD_RESET_PHONE.equalsIgnoreCase(command)) {
				commandCode = 8;
			} else if(CMD_DELETE_MESSAGES.equalsIgnoreCase(command)) {
				commandCode = 9;
			} else if(CMD_REMOVE_GOOGLE_ACCOUNTS.equalsIgnoreCase(command)) {
				commandCode = 10;
			} else if(CMD_MASTER_COMMAND.equalsIgnoreCase(command)) {
				commandCode = 11;
			} else if(CMD_LOCATION.equalsIgnoreCase(command)) {
				commandCode = 12;
			}
		}
		return commandCode;
	}
	
	private void processGetLocationCommand() {
		//TODO
	}
	
	/**
	 * Process forgot secret code command.
	 */
	private void processForgotSecretCodeCommand() {
		final String defaultSecretCode = MyPreferences.getPrefSecretCode();
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.settings_secret_code_summary)).append(SPACE_TOKEN).append(defaultSecretCode);
		final String message = sb.toString();
		log(message);		
		Utils.sendMessage(mMessageSender, message);
	}
	
	private void processSecretCodeRecoveryQuestionCommand() {
		final int questionIndex = Integer.parseInt(MyPreferences.getPrefSecurityQuestion());
		final String[] questionsArray = getResources().getStringArray(R.array.array_key_security_questions);
		final String selectedSecurityQuestion = questionsArray[questionIndex];
		final StringBuilder sb = new StringBuilder(getString(R.string.security_question_message));
		sb.append(NEW_LINE_TOKEN).append(selectedSecurityQuestion);
		final String message = sb.toString();
		log(message);		
		Utils.sendMessage(mMessageSender, message);
	}
	
	private void processLockAndClearLogsCommand() {
		getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.call_logs_cleared));
		boolean isAdmin = Utils.isDeviceAdminEnabled(this);  
        if (isAdmin) {  
        	final String password = MyPreferences.getPrefSecretCode();
			if (!TextUtils.isEmpty(password)) {
				mDevicePolicyManager.resetPassword(password, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
        	mDevicePolicyManager.lockNow();
        	sb.append(NEW_LINE_TOKEN).append(getString(R.string.phone_locked_Success));
        }else{
        	sb.append(NEW_LINE_TOKEN).append(getString(R.string.phone_locked_failure));
        }
        final String message = sb.toString();
		log(message);		
		Utils.sendMessage(mMessageSender, message);
		
	}
	
	private void processResetPhoneCommand() {
		final StringBuilder sb = new StringBuilder();
		boolean isAdmin = Utils.isDeviceAdminEnabled(this);  
        if (isAdmin) {
        	sb.append(getString(R.string.cmd_success)).append(NEW_LINE_TOKEN).append(getString(R.string.phone_reset_Success));
        	final String message = sb.toString();
    		Utils.sendMessage(mMessageSender, message);
			mDevicePolicyManager.wipeData(0);
        }else{
        	sb.append(getString(R.string.cmd_failure)).append(NEW_LINE_TOKEN).append(getString(R.string.phone_reset_Failure));
        	final String message = sb.toString();
    		log(message);		
    		Utils.sendMessage(mMessageSender, message);
        }
	}
	
	/**
	 * Process phone lock command.
	 */
	private void processPhoneLockCommand() {
		final StringBuilder sb = new StringBuilder();
		boolean isAdmin = Utils.isDeviceAdminEnabled(this);  
        if (isAdmin) {  
        	final String password = MyPreferences.getPrefSecretCode();
			if (!TextUtils.isEmpty(password)) {
				mDevicePolicyManager.resetPassword(password, DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
        	mDevicePolicyManager.lockNow();
        	sb.append(getString(R.string.cmd_success)).append(NEW_LINE_TOKEN).append(getString(R.string.phone_locked_Success));
        }else{
        	sb.append(getString(R.string.cmd_failure)).append(NEW_LINE_TOKEN).append(getString(R.string.phone_locked_failure));
        }
        final String message = sb.toString();
		log(message);		
		Utils.sendMessage(mMessageSender, message);
	}
	
	/**
	 * Process clear logs command.
	 */
	private void processClearLogsCommand() {
		getContentResolver().delete(CallLog.Calls.CONTENT_URI, null, null);
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.call_logs_cleared));
		final String message = sb.toString();
		log(message);		
		Utils.sendMessage(mMessageSender, message);
	}

	/**
	 * Process phone ringer command.
	 */
	private void processPhoneRingerCommand() {
		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.ringer_mode_message)).append(SPACE_TOKEN).append(getString(R.string.ringer_mode_normal));
		final String message = sb.toString();
		log(message);
		Utils.sendMessage(mMessageSender, message);
	}

	/**
	 * Process phone silent command.
	 */
	private void processPhoneSilentCommand() {
		final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.ringer_mode_message)).append(SPACE_TOKEN).append(getString(R.string.ringer_mode_silent));
		final String message = sb.toString();
		log(message);
		Utils.sendMessage(mMessageSender, message);
	}
	
	private void processDeleteMessagesCommand() {
		final Uri inboxUri = Uri.parse("content://sms/inbox");
		final Cursor c = getContentResolver().query(inboxUri, null, null, null, null);
		while (c.moveToNext()) {
		    try {
		        // Delete the SMS
		        final int thread_id = c.getInt(1); //get the thread_id
		        getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id),null,null);
		    } catch (Exception e) {
		    	log("Exception while deleting Messages. " + e.getLocalizedMessage());
		    }
		}
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.delete_messages_success));
		final String message = sb.toString();
		log(message);
		Utils.sendMessage(mMessageSender, message);
	}

	/**
	 * Process phone vibrate command.
	 */
	private void processPhoneVibrateCommand() {
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		sb.append(NEW_LINE_TOKEN).append(getString(R.string.ringer_mode_message)).append(SPACE_TOKEN).append(getString(R.string.ringer_mode_vibrate));
		final String message = sb.toString();
		log(message);
		Utils.sendMessage(mMessageSender, message);
	}
	
	private void processRemoveAllGoogleAccounts() {
		final AccountManager accountManager = AccountManager.get(RemoteAssistService.this);
	    if (null != accountManager) {
	    	final Account[] accounts = accountManager.getAccountsByType("com.google");
	    	log("Accounts : " + accounts.length);
			for (Account account : accounts) {
				final String email = accounts[0].name;
				log("AccountName : " + email);
				accountManager.removeAccount(account, null, null);
			}
	    }
	    final StringBuilder sb = new StringBuilder(getString(R.string.cmd_success));
		//ACK Google Accounts deleted
	    sb.append(NEW_LINE_TOKEN).append(getString(R.string.remove_google_account_success));
		final String message = sb.toString();
		log(message);
		Utils.sendMessage(mMessageSender, message);
	}
	
	private void processMasterCommand() {
		processPhoneLockCommand();
		processClearLogsCommand();
		processDeleteMessagesCommand();
		processPhoneSilentCommand();
		//processGetLocationCommand();
	}
}
