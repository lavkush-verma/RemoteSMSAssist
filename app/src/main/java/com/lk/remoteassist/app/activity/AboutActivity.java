package com.lk.remoteassist.app.activity;


import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.lk.remoteassist.app.R;

public class AboutActivity extends ActionBarActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
//		getSupportActionBar().setTitle(R.string.about);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            break;

        default:
            return super.onOptionsItemSelected(item);
        }
        return true;
	}
}
