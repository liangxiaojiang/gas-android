package com.joe.oil.imagepicker;

import android.graphics.Bitmap;
import android.os.Handler;
import com.joe.oil.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * 公共配置文件
 *
 * @author join
 */
public class Config {

	static final DisplayImageOptions mImageOptionsDefault;
	public static final DisplayImageOptions mImageOptions;
	static {
		limit = 3;

		savePathString = "/temp";

		mImageOptions = new DisplayImageOptions.Builder().resetViewBeforeLoading(true) // default
				.delayBeforeLoading(0).showImageOnLoading(R.drawable.pictures_no).cacheInMemory(false) // default
				.cacheOnDisk(false) // default
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.handler(new Handler()) // default
				.build();

		mImageOptionsDefault = new DisplayImageOptions.Builder().resetViewBeforeLoading(false) // default
				.delayBeforeLoading(0).cacheInMemory(false) // default
				.cacheOnDisk(false) // default
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig(Bitmap.Config.RGB_565) // default
				.displayer(new SimpleBitmapDisplayer()) // default
				.handler(new Handler()) // default
				.build();
	}
	// static String PHOTO_PATH = "/sdcard-ext/DCIM";
	static String PHOTO_PATH = "/storage/sdcard0/DCIM";
	// static String PHOTO_PATH = "/storage/internalsd/DCIM";
	public static String PHOTO_PATH_OLD_DEVICE = "/sdcard-ext/DCIM";
	public static String PHOTO_PATH_BJ_DEVICE = "/storage/sdcard0/DCIM";
	public static String PHOTO_PATH_SH_DEVICE = "/storage/internalsd/DCIM";
	public static int limit;
	static String savePathString;

	public static String getPhotoPath() {
		return PHOTO_PATH;
	}

	/**
	 * (特殊情况下)设置本机照片存储父目录
	 *
	 * @param photoPath
	 */
	public static void setPhotoPath(String photoPath) {
		PHOTO_PATH = photoPath;
	}

	/**
	 * 获取最多可选择的图片数量
	 *
	 * @return
	 */
	public static int getLimit() {
		return limit;
	}

	/**
	 * 设置最多可以选择的图片数量
	 *
	 * @param limit
	 */
	public static void setLimit(int limit) {
		Config.limit = limit;
	}

	/**
	 * 获取图片保存路径
	 *
	 * @return
	 */
	public static String getSavePath() {
		return savePathString;
	}

	/**
	 * 文件保存路径<br />
	 * 主要处理拍照文件
	 *
	 * @return
	 */
	public static void setSavePath(String path) {
		Config.savePathString = path;
	}
}
