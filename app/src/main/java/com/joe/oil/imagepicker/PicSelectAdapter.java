package com.joe.oil.imagepicker;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import com.joe.oil.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PicSelectAdapter extends BaseAdapter {

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	Context mContext;
	ImageGroup bean;
	ImagePickerActivity.OnImageSelectedListener onImageSelectedListener;
	ImagePickerActivity.OnImageSelectedCountListener onImageSelectedCountListener;
	private Point mPoint = new Point(0, 0);// 用来封装ImageView的宽和高的对象
	private GridView mGridView;

	private final int TYPE_TAKE_PHOTO = 0;
	private final int TYPE_SELECT = 1;

	public PicSelectAdapter(Context context, GridView mGridView, ImagePickerActivity.OnImageSelectedCountListener onImageSelectedCountListener) {
		this.mContext = context;
		this.mGridView = mGridView;
		this.onImageSelectedCountListener = onImageSelectedCountListener;
	}

	public void taggle(ImageGroup bean) {
		this.bean = bean;
		notifyDataSetChanged();
	}

	public void setOnImageSelectedListener(ImagePickerActivity.OnImageSelectedListener onImageSelectedListener) {
		this.onImageSelectedListener = onImageSelectedListener;
	}

	@Override
	public int getCount() {
		return bean == null || bean.getImageCount() == 0 ? 1 : bean.getImageCount();
	}

	@Override
	public Object getItem(int position) {
		return bean == null ? null : bean.imageSets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		if (position == 0) {
			return TYPE_TAKE_PHOTO;
		} else {
			return TYPE_SELECT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int index = position;
		int type = getItemViewType(position);

		final ViewHolder holder;
		if (convertView == null) {
			convertView = new GalleryPickItem(mContext);
			holder = new ViewHolder();
			holder.mImageView = ((GalleryPickItem) convertView).getmImageView();
			holder.shadeView = ((GalleryPickItem) convertView).getShadeView();
			holder.mCheckBox = ((GalleryPickItem) convertView).getmCheck();
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.mImageView.setImageResource(R.drawable.pictures_no);
		}

		switch (type) {
		case TYPE_TAKE_PHOTO:
			imageLoader.displayImage("drawable://" + R.drawable.take_photo, holder.mImageView, Config.mImageOptions);
			holder.mCheckBox.setVisibility(View.GONE);
			break;

		case TYPE_SELECT:
			final ImageBean ib = (ImageBean) getItem(index);
			holder.mImageView.setTag(ib.path);
			imageLoader.displayImage("file://" + ib.path, holder.mImageView, Config.mImageOptions);
			holder.mCheckBox.setVisibility(View.VISIBLE);
			holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int count = onImageSelectedCountListener.getImageSelectedCount();
					if (count == Config.limit && !holder.mCheckBox.isSelected()) {
						Toast.makeText(mContext, "最多只能选择" + Config.limit + "张图片", Toast.LENGTH_SHORT).show();
						// holder.mCheckBox.setSelected(ib.isChecked);
						holder.mCheckBox.setSelected(false);
					} else {
						ib.isChecked = !ib.isChecked;
						holder.mCheckBox.setSelected(ib.isChecked);
					}
					if (ib.isChecked) {
						holder.shadeView.setVisibility(View.VISIBLE);
					} else {
						holder.shadeView.setVisibility(View.GONE);
					}
					onImageSelectedListener.notifyChecked();
				}
			});
			holder.mCheckBox.setSelected(ib.isChecked);
			if (ib.isChecked) {
				holder.shadeView.setVisibility(View.VISIBLE);
			} else {
				holder.shadeView.setVisibility(View.GONE);
			}
			if (DebugConfig.debug)
				Log.v("QiLog", "PicSelectAdapter.getView" + " ~~~ " + "file://" + ib.path);
			break;

		default:
			break;
		}
		((GalleryPickItem) convertView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		return convertView;
	}

	public List<ImageBean> getAllItem() {
		return bean.imageSets;
	}

	public static class ViewHolder {
		public LanbaooImageView mImageView;
		public LanbaooImageView shadeView;
		public TextView mCheckBox;
	}

}
