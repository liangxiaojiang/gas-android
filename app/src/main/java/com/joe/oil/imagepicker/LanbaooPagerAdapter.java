/**
 *LanbaooPagerAdapter
 *2/18/14 11:50 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import com.joe.oil.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * User: demo
 * wuchangqi@meet-future.com
 * Date: 2/18/14
 * Time: 11:50 AM
 */
public class LanbaooPagerAdapter extends PagerAdapter {

	protected OnItemChangeListener mOnItemChangeListener;
	private int mCurrentPosition;
	private ImageGroup list = null;
	private ImageLoader imageLoader = null;
	private Context context = null;

	private LanbaooPagerAdapter () {
	}

	public LanbaooPagerAdapter (Context context, ImageGroup list) {
		this.list = list;
		this.context = context;
		imageLoader = ImageLoader.getInstance ();
	}

	@Override
	public int getCount () {
		if (list != null && list.getImageSets () != null) {
			return list.getImageSets ().size ();
		}
		return 0;
	}

	@Override
	public boolean isViewFromObject (View view, Object object) {
		return view.equals (object);
	}

	@Override
	public void setPrimaryItem (ViewGroup container, int position, Object object) {
		super.setPrimaryItem (container, position, object);
		if (mCurrentPosition == position) return;
		mCurrentPosition = position;
		if (mOnItemChangeListener != null) mOnItemChangeListener.onItemChange (mCurrentPosition);
	}

	@Override
	public void destroyItem (ViewGroup container, int position, Object object) {
		container.removeView ((View) object);
	}

	@Override
	public Object instantiateItem (ViewGroup container, int position) {
		String imageUri = "file://" + list.getImageSets ().get (position).path;
		PhotoView imageView = new PhotoView (context);
		imageLoader.displayImage (imageUri, imageView, Config.mImageOptionsDefault);
		container.addView (imageView);
		return imageView;
	}

	public void setOnItemChangeListener (OnItemChangeListener listener) {
		mOnItemChangeListener = listener;
	}

	public static interface OnItemChangeListener {
		public void onItemChange (int currentPosition);
	}
}
