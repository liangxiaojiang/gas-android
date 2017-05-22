package net.safetone.rfid.lib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

import net.safetone.rfid.lib.exception.NoDeviceException;
import android.content.Context;

/**
 * 通信类
 * 
 * @author SafeTone
 *
 */
public abstract class Comm {
	
	private static Comm mInstance = null;
	protected Context mContext = null;
	protected InputStream mInputStream = null;
	protected OutputStream mOutputStream = null;
	
	/** 固有延迟。单位：ms */
	public int baseTimeout = 0;
	
	protected Comm(Context context) {
		mContext = context;
	}
	
	/**
	 * 取得通信连接实例。
	 * 
	 * @param context Android Application Context
	 * @param dev 设备地址
	 * 
	 * @return 已建立好通信的连接。
	 * 
	 * @throws NoDeviceException 在该设备上无读卡器
	 * @throws IOException
	 * 
	 * @see #getInstance(Context)
	 */
	public static final Comm getInstance(Context context, String dev)
			throws NoDeviceException, IOException {
		if (mInstance == null) {
			Comm instance = null;
			if (android.os.Build.MODEL.equalsIgnoreCase("T07E")) {
				// this device use UART
				instance = new CommUart(context);
			} else {
				// this device use Bluetooth
				instance = new CommBluetooth(context);
			}
			
			if (dev != null)
				instance.open(dev);
			else
				instance.open();
			
			mInstance = instance;
		}
		
		return mInstance;
	}
	
	/**
	 * 取得通信连接实例。
	 * <p>
	 * 等价于 getInstance(context, null)
	 * 
	 * @param context Android Application Context
	 * 
	 * @return 已建立好通信的连接。
	 * 
	 * @throws NoDeviceException 在该设备上无读卡器
	 * @throws IOException
	 * 
	 * @see #getInstance(Context, String)
	 */
	public static final Comm getInstance(Context context)
			throws NoDeviceException, IOException {
		return getInstance(context, null);
	}
	
	/**
	 * 打开读卡器通信连接
	 * 
	 * @throws NoDeviceException 在该设备上无读卡器
	 * @throws IOException
	 * 
	 * @see #open(String)
	 * @see #close()
	 */
	protected abstract void open() throws NoDeviceException, IOException;

	/**
	 * 打开读卡器通信连接
	 * 
	 * @param dev 读卡器设备
	 * @throws IOException
	 * 
	 * @see #open()
	 * @see #close()
	 */
	protected abstract void open(String dev) throws IOException;

	/**
	 * 判断连接是否成功打开
	 * 
	 * @return true or false
	 * 
	 * @see #open()
	 * @see #open(String)
	 * @see #close()
	 */
	public abstract boolean isOpened();

	/**
	 * 取得连接的数据流
	 * 
	 * @return InputStream
	 * 
	 * @see #getOutputStream()
	 */
	public InputStream getInputStream() {
		return mInputStream;
	}
	
	/**
	 * 取得连接的输出流
	 * 
	 * @return OutputStream
	 * 
	 * @see #getInputStream()
	 */
	public OutputStream getOutputStream() {
		return mOutputStream;
	}
	
	/**
	 * 在连接上读取一次
	 * 
	 * @param timeout 无数据时，最大等待时间。单位ms。
	 * 
	 * @return 读取到的数据
	 * 
	 * @throws IOException
	 * @throws TimeoutException 在指定的时间内，没有读取到数据
	 * 
	 * @see #getInputStream()
	 * @see #getOutputStream()
	 */
	protected byte[] readOnce(long timeout) throws IOException, TimeoutException {
		byte[] buf = new byte[1024];
		int rn;
		long outtime = System.currentTimeMillis() + timeout + baseTimeout;

		while (mInputStream.available() == 0) {
			if (System.currentTimeMillis() >= outtime)
				throw new TimeoutException();
			try { Thread.sleep(1); } catch (InterruptedException e) {}
			continue;
		}

		rn = mInputStream.read(buf);
		
		byte[] ret = new byte[rn];
		System.arraycopy(buf, 0, ret, 0, rn);
		
		return ret;
	}
	
	/**
	 * 关闭连接
	 * 
	 * @throws IOException
	 * 
	 * @see #open()
	 * @see #open(String)
	 * @see #isOpened()
	 */
	public void close() throws IOException {
		mInstance = null;
	}
	
	/**
	 * 跳过已经在系统缓存中的数据
	 * <p>
	 * 一般在请求一个新的命令时调用该方法，以清除输入流中的无效数据。
	 */
	protected void skipDirtyData() {
		try {
			int len;
			while ((len = mInputStream.available()) != 0)
				mInputStream.skip(len);
		} catch (IOException e) {
		}
	}
}
