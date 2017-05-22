/**
 *ImageLoadTask
 *11/20/14 11:24 AM
 *demo
 **/
package com.joe.oil.imagepicker;

/**
 * User: demo
 * wuchangqi@meet-future.com
 * Date: 11/20/14
 * Time: 11:24 AM
 */

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.*;

/**
 * 使用contentProvider扫描图片异步任务
 */
public class ImageLoadTask extends BaseAsyncTask {

	private Context mContext = null;

	/**
	 * 存放图片<文件夹,该文件夹下的图片列表>
	 */
	private ArrayList<ImageGroup> mGruopList = new ArrayList<ImageGroup> ();

	public ImageLoadTask (Context context) {
		super ();
		mContext = context;
		result = mGruopList;
	}

	public ImageLoadTask (Context context, OnTaskResultListener listener) {
		super ();
		mContext = context;
		result = mGruopList;
		setOnResultListener (listener);
	}

	@Override
	protected Boolean doInBackground (Void... params) {
		Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		ContentResolver mContentResolver = mContext.getContentResolver ();
		// 构建查询条件，且只查询jpeg和png的图片
		StringBuilder selection = new StringBuilder ();
		selection.append (MediaStore.Images.Media.MIME_TYPE).append ("=?");
		selection.append (" or ");
		selection.append (MediaStore.Images.Media.MIME_TYPE).append ("=?");

		final String orderBy = MediaStore.Images.Media.DATE_ADDED + " desc";

		Cursor mCursor = null;
		try {
			// 初始化游标
			mCursor = mContentResolver.query (mImageUri, null, selection.toString (), new String[] {
					"image/jpeg", "image/png"
			}, orderBy);

			{
				HashMap<String, List<ImageBean>> map = capacity (mCursor);
				Set<Map.Entry<String, List<ImageBean>>> set = map.entrySet ();

				for (Map.Entry<String, List<ImageBean>> entry : set) {
					String parentName = entry.getKey ();
					ImageBean mImageBean = entry.getValue ().get (0);
					// 将图片的数量加一作为相机图
					ImageGroup tempGroup = new ImageGroup (parentName, entry.getValue ());
					// 第一个位置设置为相机图
					tempGroup.imageSets.add (0, new ImageBean ());
					mGruopList.add (tempGroup);
				}
				ImageGroup GroupAll = new ImageGroup ();
				for (ImageGroup group : mGruopList) {
					GroupAll.getImageSets ().addAll (group.getImageSets ());
				}
				GroupAll.setFolderName ("所有图片");
				mGruopList.add (0, GroupAll);
			}

		} catch (Exception e) {
			// 输出日志
			return false;
		} finally {
			// 关闭游标
			if (mCursor != null && ! mCursor.isClosed ()) {
				mCursor.close ();
			}
		}

		return true;
	}

	/**
	 * 将获取的图片信息分组
	 * 按文件夹分组
	 */
	private HashMap<String, List<ImageBean>> capacity (Cursor mCursor) {

		HashMap<String, List<ImageBean>> beans = new HashMap<String, List<ImageBean>> ();
		while (mCursor.moveToNext ()) {
			// 得到图片数据路径
			String path = mCursor.getString (mCursor
					.getColumnIndex (MediaStore.Images.Media.DATA));

			long size = mCursor.getLong (mCursor
					.getColumnIndex (MediaStore.Images.Media.SIZE));

			String display_name = mCursor.getString (mCursor
					.getColumnIndex (MediaStore.Images.Media.DISPLAY_NAME));

			String parentName = new File (path).getParentFile ().getName ();
			if (parentName == null) {
				parentName = "%未知%";
			}
			List<ImageBean> sb;
			if (beans.containsKey (parentName)) {
				sb = beans.get (parentName);
				sb.add (new ImageBean (parentName, size, display_name, path, false));
			} else {
				sb = new ArrayList<ImageBean> ();
				sb.add (new ImageBean (parentName, size, display_name, path, false));
			}
			beans.put (parentName, sb);
		}
		return beans;
	}
}
