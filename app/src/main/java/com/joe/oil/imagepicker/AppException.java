package com.joe.oil.imagepicker;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

public class AppException extends Exception implements Thread.UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private Context mContext;

	private AppException(Context context) {
		this.mContext = context;
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	/**
	 * 获取APP异常崩溃处理对象
	 *
	 * @param
	 * @return
	 */
	public static AppException getAppExceptionHandler(Context context) {
		return new AppException(context);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {

		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
	}

	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 *
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}

		final Context context = mContext;

		if (context == null) {
			return false;
		}

		final String crashReport = getCrashReport(context, ex);
		// 显示异常信息&发送报告
		new Thread() {
			public void run() {
				Looper.prepare();
				ImagePickerActivity.sendAppCrashReport(context, crashReport);
				Looper.loop();
			}

		}.start();
		return true;
	}

	/**
	 * 获取APP崩溃异常报告
	 *
	 * @param ex
	 * @return
	 */
	private String getCrashReport(Context context, Throwable ex) {
		PackageManager manager = context.getApplicationContext().getPackageManager();
		PackageInfo pinfo = null;
		try {
			pinfo = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
		}
		StringBuffer exceptionStr = new StringBuffer();
		exceptionStr.append("Version: " + pinfo.versionName + "(" + pinfo.versionCode + ")\n");
		exceptionStr.append("Android: " + android.os.Build.VERSION.RELEASE + "(" + android.os.Build.MODEL + ")\n");
		exceptionStr.append("Exception: " + ex.getMessage() + "\n");
		StackTraceElement[] elements = ex.getStackTrace();
		for (int i = 0; i < elements.length; i++) {
			exceptionStr.append(elements[i].toString() + "\n");
		}
		return exceptionStr.toString();
	}

}