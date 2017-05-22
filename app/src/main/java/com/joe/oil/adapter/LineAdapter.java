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
import com.joe.oil.entity.Line;

public class LineAdapter extends BaseAdapter {
	
	private List<Line> lines;
	private LayoutInflater inflater;
	
	public LineAdapter(Context context, List<Line> lines) {
		this.lines = lines;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return lines.size();
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
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_line, null);
			holder.lineName = (TextView) convertView.findViewById(R.id.item_line_line_name);
			holder.officeName = (TextView) convertView.findViewById(R.id.item_line_office_name);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		Line line = lines.get(position);
		holder.lineName.setText("管线名称：" + line.getName());
		holder.officeName.setText("站点名称：" + line.getOfficeName());
		return convertView;
	}
	
	class ViewHolder{
		TextView officeName;
		TextView lineName;
	}
}
