package com.joe.oil.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.joe.oil.R;
import com.joe.oil.imagepicker.Config;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.LanbaooImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PicSelectedEnsureAdapter extends BaseAdapter {

	private Context context;
	private List<ImageBean> imageSets;
	private final int TYPE_ADD = 0;
	private final int TYPE_PIC = 1;

	private ImageLoader imageLoader = ImageLoader.getInstance();

	public PicSelectedEnsureAdapter(Context context, List<ImageBean> imageSets) {
		this.context = context;
		this.imageSets = imageSets;
	}

	public int getTYPE_ADD() {
		return TYPE_ADD;
	}

	public int getTYPE_PIC() {
		return TYPE_PIC;
	}

	@Override
	public int getCount() {
		int size = imageSets.size();
		if (size == 0) {
			return size + 1;
		} else {
			return imageSets.size();
		}
	}

	@Override
	public Object getItem(int position) {
		return imageSets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		int size = imageSets.size();
		if (size == 0) {
			return TYPE_ADD;
		} else {
			return TYPE_PIC;
		}
	}

	@Override
	public int getViewTypeCount() {
		int size = imageSets.size();
		if (size == Config.limit) {
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = new LanbaooImageView(context);
			viewHolder.iv_picture = (LanbaooImageView) convertView;
			viewHolder.iv_picture.setAdjustViewBounds(true);
			viewHolder.iv_picture.setScaleType(ImageView.ScaleType.FIT_XY);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		int type = getItemViewType(position);
		switch (type) {
		case TYPE_ADD:
			viewHolder.iv_picture.setImageResource(R.drawable.selector_btn_add_pic);
			break;

		case TYPE_PIC:
			ImageBean bean = (ImageBean) getItem(position);
			imageLoader.displayImage("file://" + bean.path, viewHolder.iv_picture, Config.mImageOptions);
			break;

		default:
			break;
		}

		return convertView;
	}

	private class ViewHolder {
		LanbaooImageView iv_picture;
	}

}
