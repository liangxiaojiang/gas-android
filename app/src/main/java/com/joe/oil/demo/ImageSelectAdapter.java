package com.joe.oil.demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.imagepicker.*;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.List;
/**
 * @description 图片选择测试适配器
 * @deprecated
 * @author Administrator
 */
public class ImageSelectAdapter extends BaseAdapter {

	private final DisplayImageOptions mImageOptions;
	protected ImageLoader imageLoader = ImageLoader.getInstance ();
	Context mContext;
	ImageGroup bean;

	public ImageSelectAdapter (Context context) {
		this.mContext = context;
		mImageOptions = new DisplayImageOptions.Builder ()
				.resetViewBeforeLoading (false)  // default
				.delayBeforeLoading (0)
				.cacheInMemory (false) // default
				.cacheOnDisk (false) // default
				.considerExifParams (false) // default
				.imageScaleType (ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
				.bitmapConfig (Bitmap.Config.RGB_565) // default
				.displayer (new SimpleBitmapDisplayer ()) // default
				.handler (new Handler ()) // default
				.build ();
	}

	public void taggle (ImageGroup bean) {
		this.bean = bean;
		notifyDataSetChanged ();
	}

	@Override
	public int getCount () {
		return bean == null || bean.getImageCount () == 0 ? 0 : bean.getImageCount ();
	}

	@Override
	public Object getItem (int position) {
		return bean == null ? null : bean.imageSets.get (position);
	}

	@Override
	public long getItemId (int position) {
		return position;
	}

	@Override
	public View getView (int position, View convertView, ViewGroup parent) {
		final int index = position;
		final ImageBean ib = (ImageBean) getItem (index);
		final ViewHolder holder;
		if (convertView == null) {

			convertView = new GalleryPickItem (mContext);
			holder = new ViewHolder ();
			holder.mImageView = ((GalleryPickItem) convertView).getmImageView ();
			holder.mCheckBox = ((GalleryPickItem) convertView).getmCheck ();
			convertView.setTag (holder);

		} else {
			holder = (ViewHolder) convertView.getTag ();
			holder.mImageView
					.setImageResource (R.drawable.pictures_no);
		}

		holder.mImageView.setTag (ib.path);

		imageLoader.displayImage ("file://" + ib.path, holder.mImageView, mImageOptions);
		holder.mCheckBox.setVisibility (View.VISIBLE);

		holder.mCheckBox.setSelected (ib.isChecked);

		if (DebugConfig.debug) Log.v ("QiLog", "PicSelectAdapter.getView" + " ~~~ " + "file://" + ib.path);
		((GalleryPickItem) convertView).setDescendantFocusability (ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		return convertView;
	}

	public List<ImageBean> getAllItem () {
		return bean.imageSets;
	}

	public static class ViewHolder {
		public LanbaooImageView mImageView;
		public TextView mCheckBox;
	}

}
