/**
 *ImageGroupAdapter
 *11/20/14 11:40 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.joe.oil.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * User: demo wuchangqi@meet-future.com Date: 11/20/14 Time: 11:40 AM
 */
public class ImageGroupAdapter extends BaseAdapter {
	LayoutInflater inflater;
	List<ImageGroup> mImageGroup;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Context mContext;

	public ImageGroupAdapter(Context context) {
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mContext = context;
	}

	public void setData(List<ImageGroup> imageGroups) {
		this.mImageGroup = imageGroups;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mImageGroup == null || mImageGroup.size() == 0 ? 0 : mImageGroup.size();
	}

	@Override
	public Object getItem(int position) {
		return mImageGroup.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = new ImageGroupItem(mContext);
			holder = new ViewHolder();
			holder.mImageView = ((ImageGroupItem) convertView).getmImageView();
			holder.mDirName = ((ImageGroupItem) convertView).getmDirName();
			holder.mCheck = ((ImageGroupItem) convertView).getmCheck();
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			imageLoader.displayImage("drawable://" + R.drawable.pictures_no, holder.mImageView, Config.mImageOptions);
		}

		final ImageGroup imageGroup = (ImageGroup) getItem(position);
		holder.mCheck.setSelected(imageGroup.isSelected);
		holder.mImageView.setTag(imageGroup.getFirstImgPath());

		SpannableString spannableString = new SpannableString(imageGroup.getFolderName() + "\n" + (imageGroup.getImageCount() == 0 ? 0 : imageGroup.getImageCount() - 1));
		ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#333333"));
		spannableString.setSpan(foregroundColorSpan, 0, imageGroup.getFolderName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		holder.mDirName.setText(spannableString);
		imageLoader.displayImage("file://" + imageGroup.getFirstImgPath(), holder.mImageView);

		return convertView;
	}

	static class ViewHolder {
		public ImageView mImageView;
		public TextView mDirName;
		public TextView mCheck;
	}

}
