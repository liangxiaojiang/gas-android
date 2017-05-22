package com.joe.oil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.joe.oil.R;
import com.joe.oil.imagepicker.Config;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.LanbaooImageView;
import com.joe.oil.util.CustomUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class GvPicAdapter extends BaseAdapter {

	private Context context;
	private List<String> pics;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public GvPicAdapter(Context context, List<String> pics) {
		this.context = context;
		this.pics = pics;
	}

	@Override
	public int getCount() {
		return pics.size();
	}

	@Override
	public Object getItem(int position) {
		return pics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(R.layout.item_message_pic, null);
			holder = new ViewHolder();

			holder.ivPic = (ImageView) convertView.findViewById(R.id.iv_pic);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String pic = pics.get(position);
		imageLoader.displayImage(pic, holder.ivPic, CustomUtil.getDefaultOptions());

		return convertView;
	}

	private static class ViewHolder {

		private ImageView ivPic;
	}

}
