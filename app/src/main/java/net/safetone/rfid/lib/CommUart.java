package net.safetone.rfid.lib;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.safetone.rfid.lib.exception.NoDeviceException;
import android.content.Context;
import android.util.Log;

/**
 * UART通信
 * 
 * @author SafeTone
 *
 */
public class CommUart extends Comm {
	private static final String TAG = "CommUart";
	
	/* native */
	static { System.loadLibrary("uart"); }
	private native static int uart_open(String path, int baudrate,
			int bits, int parity, int stop);
	private native static void uart_close();
	
	/* file descriptor */
	private int fd = -1;
	private FileDescriptor mFileDescriptor = null;

	/**
	 * 构造器
	 * 
	 * @param context Android Application Context
	 */
	protected CommUart(Context context) {
		super(context);
		baseTimeout = 10;
	}

	/**
	 * 打开UART
	 * 
	 * @throws NoDeviceException 在该设备上无读卡器使用的UART
	 * @throws IOException
	 * 
	 * @see #open(String)
	 * @see #close()
	 */
	@Override
	protected void open() throws NoDeviceException, IOException {
		String dev;
		if (android.os.Build.MODEL.equalsIgnoreCase("T07E")) {
			dev = "/dev/ttyS1";
		} else {
			throw new NoDeviceException();
		}
		
		open(dev);
	}

	/**
	 * 打开UART
	 * 
	 * @param dev 指定的UART地址
	 * 
	 * @throws NoDeviceException 在该设备上无读卡器使用的UART
	 * @throws IOException
	 * 
	 * @see #open()
	 * @see #close()
	 */
	@Override
	protected void open(String dev) throws IOException {
		synchronized(this) {
			if ((fd = uart_open(dev, 115200, 8, 'N', 1)) < 0) {
				Log.e(TAG, "native open returns " + fd);
				throw new IOException();
			}
			
			try {
				Method setInt = FileDescriptor.class.getDeclaredMethod("setInt$", int.class);
				mFileDescriptor = new FileDescriptor();
				setInt.invoke(mFileDescriptor, fd);
				mInputStream = new FileInputStream(mFileDescriptor);
				mOutputStream = new FileOutputStream(mFileDescriptor);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new IOException();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new IOException();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new IOException();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new IOException();
			}
		}
	}

	/**
	 * 查看UART是否成功打开
	 * 
	 * @return 状态
	 * 
	 * @see #open()
	 * @see #close()
	 */
	@Override
	public boolean isOpened() {
		synchronized(this) {
			return mFileDescriptor != null;
		}
	}

	/**
	 * 关闭UART
	 * 
	 * @throws IOException
	 * 
	 * @see #open()
	 * @see #isOpened()
	 */
	@Override
	public void close() throws IOException {
		synchronized(this) {
			if (mInputStream != null) {
				mInputStream.close();
				mInputStream = null;
			}
			if (mOutputStream != null) {
				mOutputStream.close();
				mOutputStream = null;
			}
			if (mFileDescriptor != null) {
				uart_close();
				fd = -1;
				mFileDescriptor = null;
			}
		}
		super.close();
	}
	

}
