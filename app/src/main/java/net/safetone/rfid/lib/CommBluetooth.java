package net.safetone.rfid.lib;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.safetone.rfid.bt.BluetoothUuid;
import net.safetone.rfid.lib.exception.NoDeviceException;
import net.safetone.rfid.utils.Utils;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * 蓝牙通信
 * 
 * @author SafeTone
 *
 */
public class CommBluetooth extends Comm {
	private static final String SYS_BT_MAC_FILE = "/vendor/SafeTone/Bluetooth_RFID_device_MAC";
	private static final String SHARD_PREF_NAME = "RFID_Reader_Bluetooth_Address";
	private static final String SAVED_MAC_KEY = "Bluetooth_Address";
	
	private BluetoothAdapter mBtAdapter = null;
	private BluetoothSocket mSocket = null;
	
	protected CommBluetooth(Context context) {
		super(context);
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		baseTimeout = 450;
	}

	/**
	 * 将蓝牙地址保存到应用配置文件
	 * 
	 * @param mac
	 */
	private void setBluetoothAddress(String mac) {
		SharedPreferences.Editor editPrefs;
		editPrefs = mContext.getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE).edit();
		editPrefs.putString(SAVED_MAC_KEY, mac);
		editPrefs.commit();
	}
	
	/**
	 * 取得读卡器设备地址
	 * 
	 * @return 读卡器蓝牙MAC
	 */
	private String getBluetoothAddress() {
		String addr = null;
		SharedPreferences pref;
		pref = mContext.getSharedPreferences(SHARD_PREF_NAME, Context.MODE_PRIVATE);
		if (pref != null)
			addr = pref.getString(SAVED_MAC_KEY, null);
		
		if (TextUtils.isEmpty(addr)) {
			byte[] mac = new byte[32];
			int readlen;
			try {
				File macFile = new File(SYS_BT_MAC_FILE);
				InputStream in = new BufferedInputStream(new FileInputStream(macFile));
				readlen = in.read(mac);
				in.close();
				
				if (readlen < 17)
					return null;
				for (int i = 0; i < 17; ++i) {
					switch (i % 3) {
					case 0:
					case 1:
						if (!Utils.isxdigit(mac[i]))
							return null;
						break;
					case 2:
						if (mac[i] != ':')
							return null;
						break;
					}
				}
				addr = new String(mac, 0, 17);
			} catch (IOException e) {
				addr = null;
			}
		}
		
		return addr;
	}
	
	/**
	 * 连接读卡器蓝牙
	 * 
	 * @param socket 蓝牙SPP套节字，
	 * 一般由 BluetoothDevice.createRfcommSocketToServiceRecord 方法获取
	 *
	 * @throws IOException
	 * 
	 * @see #open(BluetoothSocket)
	 * @see #open(BluetoothDevice)
	 * @see #open(String)
	 * @see #isOpened()
	 * @see #close()
	 */
	protected void open(BluetoothSocket socket) throws IOException {
		if (isOpened())
			close();
		
		synchronized (this) {
			mSocket = socket;
			mSocket.connect();
			mInputStream = mSocket.getInputStream();
			mOutputStream = mSocket.getOutputStream();
			try { Thread.sleep(200); } catch (InterruptedException e) {}
			skipDirtyData();
		}
		
		setBluetoothAddress(socket.getRemoteDevice().getAddress());
	}
	
	/**
	 * 连接读卡器蓝牙
	 * 
	 * @param device 蓝牙设备 BluetoothDevice
	 * 
	 * @throws IOException
	 * 
	 * @see #open(BluetoothSocket)
	 * @see #open(String)
	 * @see #open()
	 * @see #isOpened()
	 * @see #close()
	 */
	protected void open(BluetoothDevice device) throws IOException {
		open(device.createRfcommSocketToServiceRecord(BluetoothUuid.SPP.getUuid()));
	}
	
	/**
	 * 连接读卡器蓝牙
	 * 
	 * @param BluetoothAddr 蓝牙设备的硬件地址
	 * 
	 * @throws IOException
	 * 
	 * @see #open(BluetoothSocket)
	 * @see #open(BluetoothDevice)
	 * @see #open()
	 * @see #isOpened()
	 * @see #close()
	 */
	@Override
	protected void open(String BluetoothAddr) throws IOException {
		open(mBtAdapter.getRemoteDevice(BluetoothAddr));
	}

	/**
	 * 连接读卡器蓝牙
	 * 
	 * @throws IOException
	 * @throws NoDeviceException
	 * 
	 * @see #open(BluetoothSocket)
	 * @see #open(BluetoothDevice)
	 * @see #open(String)
	 * @see #isOpened()
	 * @see #close()
	 */
	@Override
	protected void open() throws NoDeviceException, IOException {
		String mac = getBluetoothAddress();
		if (TextUtils.isEmpty(mac))
			throw new NoDeviceException();
		
		open(mBtAdapter.getRemoteDevice(mac));
	}
	
	/**
	 * 查看是否与蓝牙建立连接
	 * 
	 * @return 状态
	 * 
	 * @see #open()
	 * @see #close()
	 */
	@Override
	public boolean isOpened() {
		synchronized (this) {
			return mSocket != null;
		}
	}
	
	/**
	 * 关闭蓝牙连接
	 * 
	 * @throws IOException
	 * 
	 * @see #open()
	 * @see #isOpened()
	 */
	@Override
	public void close() throws IOException {
		synchronized (this) {
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
		}
		super.close();
	}
	
}
