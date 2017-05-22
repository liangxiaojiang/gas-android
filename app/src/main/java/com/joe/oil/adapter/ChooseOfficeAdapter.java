package com.joe.oil.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Office;

public class ChooseOfficeAdapter extends BaseAdapter {
	
	private List<Office> officeIds;
	private LayoutInflater inflater;
	
	public ChooseOfficeAdapter(List<Office> officeIds, Context context) {
		this.officeIds = officeIds;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return officeIds.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_station, null);
			holder.name = (TextView) convertView.findViewById(R.id.item_station_name);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(officeIds.get(position).getName());
		return convertView;
	}
	
	class ViewHolder{
		TextView name;
	}

}
