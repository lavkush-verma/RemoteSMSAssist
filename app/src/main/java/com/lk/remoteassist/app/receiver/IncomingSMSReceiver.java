package com.lk.remoteassist.app.receiver;

import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.lk.remoteassist.app.BuildConfig;
import com.lk.remoteassist.app.IConstants;
import com.lk.remoteassist.app.MyPreferences;
import com.lk.remoteassist.app.Utils;
import com.lk.remoteassist.app.service.RemoteAssistService;

public class IncomingSMSReceiver extends BroadcastReceiver implements IConstants{

	private static final String TAG = IncomingSMSReceiver.class.getSimpleName();
	private static final boolean DEBUG = false || BuildConfig.DEBUG;
	private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
	
	private static void log(String text) {
		if (DEBUG) Log.d(TAG, text);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final String actionName = intent.getAction();
		log(actionName);
		if (SMS_RECEIVED_ACTION.equals(actionName)) {
			final Bundle bundle = intent.getExtras();
			if (bundle != null) {
				final StringBuilder msgBody = new StringBuilder();
				String msgSender = null;
				final Object[] pdus = (Object[]) bundle.get("pdus");
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				final int messageLength = messages.length;
				log("No. of Messages: " + messageLength);
				for (int index = 0; index < messageLength; index++) {
					// Creates a SMS Message from raw PDU
					messages[index] = SmsMessage.createFromPdu((byte[]) pdus[index]);
					msgSender = messages[index].getOriginatingAddress();
					msgBody.append(messages[index].getMessageBody());
				}
				// Read SMS and start Service
				final String messageBody = msgBody.toString();
				log("SMS Received from : " + msgSender);
				log("Received SMS is : " + messageBody);
				parseCommandForAction(context, msgSender, messageBody);
			}
		}
	}
	
	private void parseCommandForAction(Context context, String sender, String message) {
		final String secretCode = MyPreferences.getPrefSecretCode();
		log(secretCode + " -- " + message);
		final boolean isSenderBlocked = Utils.isSenderBlocked(sender);
		final boolean isSupportedFormat = Pattern.matches(SMS_FORMAT, message);
		log("is SMS Format supported ? " + isSupportedFormat);
		if (!isSenderBlocked && isSupportedFormat) {
			final Intent serviceIntent = new Intent(context, RemoteAssistService.class);
			serviceIntent.putExtra(KEY_MESSAGE, message);
			serviceIntent.putExtra(KEY_SENDER, sender);
			context.startService(serviceIntent);
//			abortBroadcast();
		}
	}
}


