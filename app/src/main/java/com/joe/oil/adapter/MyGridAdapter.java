package com.joe.oil.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.util.Constants;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MyGridAdapter extends BaseAdapter {
	private String[] files;
	private Context context;
	private String packageName;
	private LayoutInflater mLayoutInflater;

	public MyGridAdapter(String[] files, Context context) {
		this.files = files;
		this.context = context;
		this.packageName = context.getPackageName();
		this.mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return files == null ? 0 : files.length;
	}

	@Override
	public String getItem(int position) {
		return files[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gridview_item, parent, false);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		String url = getItem(position);

		int resId = 0;
		// 从图片下载地址中分离出图片Id，并组合成内置图片名称
		String strs[] = url.split("/"); 
		String picId = Constants.NATIVE_PIC_PREFIX + strs[strs.length - 1];
		resId = context.getResources().getIdentifier(picId, "drawable", packageName);
		if (resId <= 0) {
			ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
		} else {
			ImageLoader.getInstance().displayImage("drawable://" + resId, viewHolder.imageView);
		}

		return convertView;
	}

	private static class MyGridViewHolder {
		ImageView imageView;
	}
}
