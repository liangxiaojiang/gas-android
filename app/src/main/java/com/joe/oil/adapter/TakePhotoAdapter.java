package com.joe.oil.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Photo;

public class TakePhotoAdapter extends BaseAdapter {
	
	private List<Photo> photos;
	private LayoutInflater inflater;
	
	public TakePhotoAdapter(Context context, List<Photo> photos){
		this.photos = photos;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return 10;
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
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_take_photo, null);
			holder.img = (ImageView) convertView.findViewById(R.id.item_take_photo_img);
			holder.name = (TextView) convertView.findViewById(R.id.item_task_time);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
//		Photo photo = photos.get(position);
//		holder.name.setText("张三@2014-7-24 16:24:20");
//		holder.img.setImageResource(R.drawable.default_img);
		return convertView;
	}
	
	class ViewHolder{
		TextView name;
		ImageView img;
	}

}
