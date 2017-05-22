package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;
import com.joe.oil.view.NoScrollGridView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

	private ArrayList<Task> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	private LayoutInflater inflater;
	private DisplayImageOptions options;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	private String imgUrl;
	private Task tasks;
	public ImageAdapter(Context context,Task tasks) {
		mInflater = LayoutInflater.from(context);
		mContext=context;
		this.tasks = tasks;

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_stub)
				.showImageForEmptyUri(R.drawable.ic_empty)
				.showImageOnFail(R.drawable.ic_error)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.considerExifParams(true)
				.build();
	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageAdapter.ViewHolder holder;
		if (convertView == null) {
			holder = new ImageAdapter.ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null);
			holder.avator=(ImageView)convertView.findViewById(R.id.avator);
			holder.name=(TextView)convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.name.setVisibility(View.GONE);
			holder.content.setVisibility(View.GONE);
//			holder.gridView=(NoScrollGridView)convertView.findViewById(R.id.gridView);
			convertView.setTag(holder);
		} else {
			holder = (ImageAdapter.ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage(tasks.getOperateCardUrl(), holder.avator , options , animateFirstListener );
		return convertView;
	}

	private void imageBrower(int position, String[] urls) {
		Intent intent = new Intent(mContext, ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, urls);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		mContext.startActivity(intent);
	}
	private static class ViewHolder {

		public TextView name;
		public ImageView avator;
		TextView content;
		NoScrollGridView gridView;
	}

	public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {
		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 50);
					displayedImages.add(imageUri);
				}
			}
		}

	}
}