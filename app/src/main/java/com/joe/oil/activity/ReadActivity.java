package com.joe.oil.activity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import com.joe.oil.R;
import com.joe.oil.util.Convert;
import com.joe.oil.util.NfcDispatcher;

public class ReadActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read_rf);

		NfcDispatcher.initial(this, NfcDispatcher.TECH_DISCOVERED_FILTERS);

	}

	@Override
	public void onResume() {
		super.onResume();
		NfcDispatcher.enableDispatch(this, NfcDispatcher.TECH_DISCOVERED_FILTERS, NfcDispatcher.ALL_TECH_LISTS);
	}

	@Override
	public void onPause() {
		super.onPause();
		NfcDispatcher.disableDispatch(this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			Log.d("ReadRF", "<<<<<<<<<<<<<<   joe    <<<<<<<<<<<<<<<<<<<<   uid:  " + Convert.bytesToHexString(tagFromIntent.getId()));
		}
	}
}