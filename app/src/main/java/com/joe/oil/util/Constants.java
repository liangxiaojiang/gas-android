package com.joe.oil.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.joe.oil.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({ "ShowToast", "InlinedApi", "SdCardPath" })
public class Constants {

	public static final int CURRENT_AREA = 1; // *当前作业区编号，在各个作业区打包时录入，1、2、3、4分别代表作业一二三四区
	/**
	 * 网络请求ip，基地址系统默认为2G网IP
	 */
//	public static String BASE_URL = "http://211.149.209.186:8180/gas";
	public static String BASE_URL = "http://113.200.64.39:8091/gas";
//	public static String BASE_URL = "http://192.168.3.12:8090/gas";
//	 public static String BASE_URL = "http://117.34.66.240:8081/gas";
	// wifi 状态下IP
	 public static String WIFI_IP = "http://10.59.211.208:8091/gas";
//	public static String WIFI_IP = "http://192.168.3.12:8090/gas";
//	 public static String WIFI_IP = "http://117.34.66.240:8081/gas";
//	 public static String WIFI_IP = "http://113.200.64.39:8091/gas";

	public static final String CURRENT_PATROL_TIME = "currentPatrolTime"; // 巡站当前patrolTime
	public static final String CURRENT_OFFICEID = "currentOfficeId"; // 巡站当前officeId
	public static final String CURRENT_STATION = "currentDeviceId"; // 巡站当前的巡检点ID
	public static final String CURRENT_WELL = "currentWellId"; // 巡井当前的巡检点ID
	public static final String STATION_COUNT = "stationCount"; // 巡站完成的巡检点个数
	public static final String WELL_COUNT = "wellCount"; // 巡井完成的巡检点个数
	public static final String IS_DOWNLOAD_PLAN = "downPlan"; // 当天计划是否下载
	public static final String STATION_PLAN = "stationPlan"; // 场站巡护计划
	public static final String PHOTO_PATH = "/sdcard-ext/DCIM/Camera/oil/";
	public static final String XJ_ITEM = "xj_items";
	public static final String DATA_LOADING_FINISH = "data_loading_finish";
	public static final String DATA_LOADING_START = "data_loading_start";
	public static final String DEVICE_MODEL_OF_BEIJIN_STRING = "SafeTone EW4Q"; // 北京设备型号
    public static final String DAWNLOAD_FINISH = "download_finish";

	public static boolean IS_WORKING = false; // 派工状态是否处于工作状态
	public static boolean IS_LINE_START = false; // 是否开始管线巡检
	public static int CUR_GIS_INSPECTION_TYPE = 0; // 0 管线  1 道路
	public static String GIS_START_NUM; // gis开始时间
	public static String GIS_FINISH_NUM; // gis结束时间
	public static String YSJ_STATUS; // 大门口压缩机状态 0：启用压缩机， 1：停用压缩机
	public static String GPS_TIME = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"); // GPS定位成功后的时间，定位成功后系统所使用的所有时间为ci时间
	public static String DEVICE_ID = ""; // 采集器设备号
	public static String DEVICE_NAME = ""; // 采集器名称
	public static String CURRENT_LINE_ID = ""; // 当前管线ID
	public static double CURRENT_LAT = 0; // 当前时刻的纬度
	public static double CURRENT_LNG = 0; // 当前时刻的经度
	public static String CURRENT_WELL_CODE = "";
	public static String TASK_TYPE = null;
	public static int CURRENT_PAGE = 1;
	public static int NEXT_PAGE = 1;
	public static int TOTAL_COUNT = 0;
	public static int GPS_INTERVAL = 0; // gps定位时间间隔
	public static String GPS_LAST_TIME =  "1991-4-5 12:00:00"; // gps上一次定位的時間
	public static String GIS_GET_PHOTO_TIME = ""; // gis界面获取照片的时间
	public static String UPLOADE_EXCEPTION_GET_PHOTO_TIME = ""; // 上报异常界面获取照片时间
	/**
	 * @description 巡检卡本地图片前缀，用于和数据返回的图片id组合成本地图片名称，如：drawable_117
	 */
	public static String NATIVE_PIC_PREFIX = "drawable_";
	public static String GIS = "gis";
	private static View view;
	private static LayoutInflater inflater;
	private static ImageView iv_loading;
	private static Dialog waitDialog;
	private static AnimationDrawable anim = null;

	public static final int REQUEST_JUMP_TO_OTHER_STATION_CHOOSE = 101;
	public static final int REQUEST_JUMP_TO_MESSAGE_LIST = 102;

	public static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static Dialog createLoadingDialog(Context context, View view, boolean cancelable) {
		Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);
		loadingDialog.setCanceledOnTouchOutside(cancelable);
		loadingDialog.setContentView(view, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		return loadingDialog;
	}

	/**
	 * 
	 * @Description 显示等待加载的dialog
	 * @param context
	 * @date 2014年7月4日 下午3:49:21
	 */
	@SuppressLint("InflateParams")
	public static void showDialog(Context context) {
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_upload, null);
		iv_loading = (ImageView) view.findViewById(R.id.iv_loading);

		waitDialog = createLoadingDialog(context, view, true);
		waitDialog.show();
		Object obj = iv_loading.getBackground();
		anim = (AnimationDrawable) obj;
		anim.stop();
		anim.start();
	}

	public static void showDialogCancelable(Context context) {
		inflater = LayoutInflater.from(context);
		view = inflater.inflate(R.layout.dialog_upload, null);
		iv_loading = (ImageView) view.findViewById(R.id.iv_loading);

		waitDialog = createLoadingDialog(context, view, false);
		waitDialog.show();
		Object obj = iv_loading.getBackground();
		anim = (AnimationDrawable) obj;
		anim.stop();
		anim.start();
	}

	public static void dismissDialog() {
		if (waitDialog != null) {
			waitDialog.dismiss();
		}
	}

	/**
	 * 
	 * @Description 对用户输入的密码进行MD5加密
	 * @param str
	 * @return
	 * @date 2014年7月14日 下午2:43:11
	 */
	public final static String get32MD5Str(String str) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] byteArray = messageDigest.digest();
		StringBuffer md5StrBuff = new StringBuffer();
		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}
		return md5StrBuff.toString();
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @return
	 */
	public static Bitmap readBitMap(Context context, String filePath) {
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inPreferredConfig = Bitmap.Config.RGB_565;
		option.inPurgeable = true;
		option.inInputShareable = true;
		File xmlFile = new File(filePath);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(xmlFile);
			return BitmapFactory.decodeStream(inputStream, null, option);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param context
	 * @description 获取屏幕宽度
	 * @return
	 */
	public static int getDeviceWidth(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	/**
	 * @param context
	 * @description 获取屏幕高度
	 * @return
	 */
	public static int getDeviceHeight(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	/**
	 * @param context
	 * @description 获取状态栏高度
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		java.lang.reflect.Field field = null;
		int x = 0;
		int statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	/**
	 * @description 判断当前是否有可用网络
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context) {
		NetworkInfo networkInfo = null;
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			networkInfo = connectivityManager.getActiveNetworkInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (networkInfo == null || !networkInfo.isAvailable()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的
	 * 
	 * @param context
	 * @return true 表示开启
	 */
	public static final boolean isOPen(final Context context) {
		LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
		@SuppressWarnings("unused")
		boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		// if (gps || network) {
		// return true;
		// }
		if (gps) {
			return true;
		}

		return false;
	}

	/**
	 * 强制帮用户打开GPS
	 * 
	 * @param context
	 */
	public static final void openGPS(Context context) {
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		// GPSIntent.setData(Uri.parse("3"));
		try {
			PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
	}
}
