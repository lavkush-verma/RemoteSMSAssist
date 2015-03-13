package com.lk.remoteassist.app.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lk.remoteassist.app.IConstants;
import com.lk.remoteassist.app.MyPreferences;
import com.lk.remoteassist.app.R;
import com.lk.remoteassist.app.Utils;
import com.lk.remoteassist.app.receiver.IncomingSMSReceiver;
import com.lk.remoteassist.app.receiver.MyDeviceAdminReceiver;

public class UserSettingsActivity extends PreferenceActivity implements IConstants {

	private PreferenceChangeListener mPreferenceListener = null;
	private SharedPreferences prefs;
	private Preference mSecretCodePreference;
	private ListPreference mSecurityQuestionPreference;
	private Preference mSecurityAnswerPreference;
	private ComponentName mComponentName;
	private boolean isDeviceAdminConfigShown = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences_user_settings);
		setContentView(R.layout.activity_user_settings);
		final Button unblockSendersButton = (Button) findViewById(R.id.btnUnblockSenders);
		unblockSendersButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Utils.unblockAllSenders();
			}
		});
		mComponentName = new ComponentName(this, MyDeviceAdminReceiver.class);
		prefs = MyPreferences.getSharedPreferences();
		mPreferenceListener = new PreferenceChangeListener();
		mSecretCodePreference = getPreferenceManager().findPreference(PREF_SECRET_CODE);
		mSecurityQuestionPreference = (ListPreference) getPreferenceManager().findPreference(PREF_SECURITY_QUESTION);
		mSecurityAnswerPreference = getPreferenceManager().findPreference(PREF_SECURITY_ANSWER);
		loadPreferences();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		prefs.registerOnSharedPreferenceChangeListener(mPreferenceListener);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		prefs.unregisterOnSharedPreferenceChangeListener(mPreferenceListener);
	}

	private class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			ApplySettings();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_settings, menu);
		return true;
	}

	public void ApplySettings() {
		final boolean isAdminEnabled = MyPreferences.isPrefDeviceAdminEnabled();
		final boolean isDeviceAdminEnabled = Utils.isDeviceAdminEnabled(this);
		
		if (!isAdminEnabled) {
			Utils.removeAsDeviceAdmin(this);
			isDeviceAdminConfigShown = false;
		}
		
		if (!isDeviceAdminEnabled && isAdminEnabled && !isDeviceAdminConfigShown) {
			isDeviceAdminConfigShown = true;
			final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.device_admin_message));
            startActivityForResult(intent, ADMIN_INTENT_REQUEST_CODE);
		}
		
		final boolean isRemoteAssistEnabled = MyPreferences.isPrefRemoteAccessEnabled();
		if (isRemoteAssistEnabled) Utils.enableApplicationComponent(this, IncomingSMSReceiver.class);
		else Utils.disableApplicationComponent(this, IncomingSMSReceiver.class);
		
		final CharSequence securtiyQuestion = mSecurityQuestionPreference.getEntry();
		mSecurityQuestionPreference.setSummary(securtiyQuestion);
		
		final String securityAnswer = MyPreferences.getPrefSecurityAnswer();
		if(!TextUtils.isEmpty(securityAnswer)) {
			mSecurityAnswerPreference.setSummary(getString(R.string.settings_security_answer_summary) + " \"" + securityAnswer + "\".");
		} else {
			mSecurityAnswerPreference.setSummary(getString(R.string.settings_security_answer_summary) + " \"" + DEFAULT_STRING + "\".");
			MyPreferences.savePrefSecurityAnswer(DEFAULT_STRING);
		}
		
		final String secretCode = MyPreferences.getPrefSecretCode();
		if(!TextUtils.isEmpty(secretCode)) {
			mSecretCodePreference.setSummary(getString(R.string.settings_secret_code_summary) + " \"" + secretCode + "\".");
		} else {
			mSecretCodePreference.setSummary(R.string.settings_secret_code_default);
			MyPreferences.savePrefSecretCode(DEFAULT_SECRET_CODE);
		}
	}
	
	private void loadPreferences() {
		ApplySettings();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_about) {
			final Intent intent = new Intent(UserSettingsActivity.this,	AboutActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == ADMIN_INTENT_REQUEST_CODE) {
    		boolean isAdmin = Utils.isDeviceAdminEnabled(this); 
    		if (!isAdmin) {
    			MyPreferences.saveIsPrefDeviceAdminEnabled(false);
    			isDeviceAdminConfigShown = false;
    		}
    	}
    	
    	super.onActivityResult(requestCode, resultCode, data);
    }
}
