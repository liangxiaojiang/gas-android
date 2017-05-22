package net.safetone.rfid.bt;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.joe.oil.R;

import java.util.ArrayList;
import java.util.List;

public class ReaderDiscoveryActivity extends ListActivity {

	private static final int MENU_ID_SCAN = Menu.FIRST;

	BluetoothAdapter mBtAdapter = null;
	private List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();
	
	private static final String PAIRED_SEPARATOR = "FF:FF:FF:FF:FF:FF";
	private static final String UNPAIRED_SEPARATOR = "00:00:00:00:00:00";

	DeviceViewAdapter deviceViewAdapter;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				catchDevice(device);
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				setProgress(true);
				invalidateOptionsMenu();
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgress(false);
				invalidateOptionsMenu();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.reader_discovery_activity);

		mBtAdapter = BluetoothAdapter.getDefaultAdapter();

		deviceViewAdapter = new DeviceViewAdapter(this, mDevices);  
		setListAdapter(deviceViewAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (mBtAdapter == null)
			return super.onCreateOptionsMenu(menu);

		boolean btIsEnabled = mBtAdapter.isEnabled();
		boolean isDiscovering = mBtAdapter.isDiscovering();
		int textId = isDiscovering ? R.string.rfidlib_bluetooth_searching_for_devices
				: R.string.rfidlib_bluetooth_search_for_devices;

		MenuItem scan = menu.add(Menu.NONE, MENU_ID_SCAN, 0, textId);
		// scan.setIcon(R.drawable.ic_scan);
		scan.setEnabled(btIsEnabled && !isDiscovering);
		scan.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onStart() {
		super.onStart();

		catchDevice(mBtAdapter.getRemoteDevice(UNPAIRED_SEPARATOR));

		registerReceivers();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (mBtAdapter.isEnabled()) {
			startScanning();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_ID_SCAN:
			if (mBtAdapter.isEnabled()) {
				startScanning();
			}
			return true;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopScanning();
	}

	@Override
	protected void onStop() {
		super.onStop();
		
		mDevices.clear();
		unregisterReceiver();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		BluetoothDevice device;
		Intent result = new Intent();

		device = mDevices.get(position);

		result.putExtra(BluetoothDevice.EXTRA_DEVICE, device);
		setResult(RESULT_OK, result);
		finish();
	}

	private void registerReceivers() {
		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		// Don't forget to unregister during onDestroy
		registerReceiver(mReceiver, filter);
	}

	private void unregisterReceiver() {
		unregisterReceiver(mReceiver);
	}

	private void catchDevice(BluetoothDevice newDev) {
		boolean isPaired = false;
		String addr = newDev.getAddress();
		
		if (!addr.equals(PAIRED_SEPARATOR) && !addr.equals(UNPAIRED_SEPARATOR)
				&& !addr.startsWith("00:0E:EA")) {
			// Invalid device
			return;
		}
		
		for (BluetoothDevice pairedDev : mBtAdapter.getBondedDevices()) {
			if (pairedDev.getAddress().equals(newDev.getAddress())) {
				isPaired = true;
				break;
			}
		}
		
		for (int idx = 0; idx < mDevices.size(); ++idx) {
			BluetoothDevice oldDev = mDevices.get(idx);
			if (isPaired && oldDev.getAddress().equals(UNPAIRED_SEPARATOR)) {
				mDevices.add(idx, newDev);
				if (idx == 0)
					mDevices.add(0, mBtAdapter.getRemoteDevice(PAIRED_SEPARATOR));
				deviceViewAdapter.notifyDataSetChanged();
				return;
			}
			if (oldDev.getAddress().equals(newDev.getAddress())) {
				// replace
				mDevices.set(idx, newDev);
				deviceViewAdapter.notifyDataSetChanged();
				return;
			}
		}
		
		mDevices.add(newDev);

		deviceViewAdapter.notifyDataSetChanged();
	}

	private void startScanning() {
		if (!mBtAdapter.isDiscovering()) {
			mBtAdapter.startDiscovery();
		}
	}

	private void stopScanning() {
		if (mBtAdapter.isDiscovering())
			mBtAdapter.cancelDiscovery();
	}
	
	private void setProgress(boolean progressOn) {
		if (deviceViewAdapter != null) {
			deviceViewAdapter.setProgress(progressOn);
		}
	}

	private class DeviceViewAdapter extends BaseAdapter {
		List<BluetoothDevice> mDevices;
		private LayoutInflater mInflater;
		private boolean mProgress = false;

		public DeviceViewAdapter(Context context, List<BluetoothDevice> devices) {
			mDevices = devices;
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		public void setProgress(boolean progressOn) {
	        mProgress = progressOn;
	        notifyDataSetChanged();
	    }

		@Override
		public int getCount() {
			return mDevices.size();
		}

		@Override
		public BluetoothDevice getItem(int position) {
			return mDevices.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			String addr = getItem(position).getAddress();
			if (addr.equals(PAIRED_SEPARATOR) || addr.equals(UNPAIRED_SEPARATOR)) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			BluetoothDevice dev = getItem(position);

			String addr = dev.getAddress();
			if (addr.equals(PAIRED_SEPARATOR)) {
				return bindPairedSeparatorView(convertView, parent);
			} else if (addr.equals(UNPAIRED_SEPARATOR)) {
				return bindUnpairedSeparatorView(position, convertView, parent);
			}

			return bindDeviceView(position, convertView, parent, dev);
		}

		private View bindPairedSeparatorView(View view, ViewGroup parent) {
			view = mInflater.inflate(R.layout.device_discovery_separator, parent, false);
			TextView textViwe = (TextView) view.findViewById(R.id.title);
			textViwe.setText(R.string.rfidlib_bluetooth_preference_paired_devices);

			return view;
		}

		private View bindUnpairedSeparatorView(int position, View view, ViewGroup parent) {
			view = mInflater.inflate(R.layout.device_discovery_separator, parent, false);
			TextView textViwe = (TextView) view.findViewById(R.id.title);
			textViwe.setText(R.string.rfidlib_bluetooth_preference_found_devices);

			View progressBar = view.findViewById(R.id.scanning_progress);
			View emptyView = view.findViewById(R.id.empty_view);
			boolean noDeviceFound = position >= getCount() - 1; // there is not found device
	        progressBar.setVisibility(mProgress ? View.VISIBLE : View.GONE);
	        if (mProgress || !noDeviceFound) {
	            if (emptyView.getVisibility() != View.GONE)
	            	emptyView.setVisibility(View.GONE);
	        } else {
	        	if (emptyView.getVisibility() != View.VISIBLE)
	        		emptyView.setVisibility(View.VISIBLE);
	        }

			return view;
		}

		private View bindDeviceView(int position, View view, ViewGroup parent, BluetoothDevice dev) {
			view = mInflater.inflate(R.layout.device_discovery_item, parent, false);

			TextView nameView = (TextView) view.findViewById(R.id.name);
			TextView addrView = (TextView) view.findViewById(R.id.address);

			nameView.setText(dev.getName());
			addrView.setText(dev.getAddress());
			
			if (position >= getCount() - 1)
				return view;
			BluetoothDevice nextDev = getItem(position+1);
			if (nextDev != null && nextDev.getAddress().equals(UNPAIRED_SEPARATOR))
				view.findViewById(R.id.divider).setVisibility(View.GONE);

			return view;
		}

	}

}