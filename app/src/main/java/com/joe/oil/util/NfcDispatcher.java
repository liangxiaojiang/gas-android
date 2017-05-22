package com.joe.oil.util;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcV;

public class NfcDispatcher {

	private static NfcAdapter mAdapter;
	private static PendingIntent mPendingIntent;

	public static IntentFilter[] TAG_DISCOVERED_FILTERS = new IntentFilter[] { new IntentFilter(
			NfcAdapter.ACTION_TAG_DISCOVERED) };
	public static IntentFilter[] TECH_DISCOVERED_FILTERS = new IntentFilter[] { new IntentFilter(
			NfcAdapter.ACTION_NDEF_DISCOVERED) };
	public static IntentFilter[] NDEF_DISCOVERED_FILTERS = new IntentFilter[] { new IntentFilter(
			NfcAdapter.ACTION_NDEF_DISCOVERED) };
	public static IntentFilter[] NFC_ALL_FILTERS = new IntentFilter[] {
			new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
			new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
			new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) };

	public static String[][] NFCA_TECH_LIST = new String[][] { new String[] { NfcA.class
			.getName() } };
	public static String[][] NFCB_TECH_LIST = new String[][] { new String[] { NfcB.class
			.getName() } };
	public static String[][] NFCV_TECH_LIST = new String[][] { new String[] { NfcV.class
			.getName() } };
	public static String[][] MIFARE_TECH_LIST = new String[][] { new String[] { MifareClassic.class
			.getName() } };

	public static String[][] ALL_TECH_LISTS = new String[][] {
			new String[] { MifareClassic.class.getName() },
			new String[] { NfcA.class.getName() },
			new String[] { NfcB.class.getName() },
			new String[] { NfcV.class.getName() } };

	public static void initial(Activity mActivity, IntentFilter[] filters) {
		mAdapter = NfcAdapter.getDefaultAdapter(mActivity);
		mPendingIntent = PendingIntent.getActivity(mActivity, 0, new Intent(
				mActivity, mActivity.getClass())
				.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		try {
			for (int i = 0; i < filters.length; i++)
				filters[i].addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
	}

	public static void enableDispatch(Activity mActivity,
			IntentFilter[] filters, String[][] techLists) {
		mAdapter.enableForegroundDispatch(mActivity, mPendingIntent, filters,
				techLists);
	}

	public static void disableDispatch(Activity mActivity) {
		mAdapter.disableForegroundDispatch(mActivity);
	}
}
