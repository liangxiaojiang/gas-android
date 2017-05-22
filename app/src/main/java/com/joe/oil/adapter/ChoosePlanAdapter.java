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

public class ChoosePlanAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<String> planCount;

	public ChoosePlanAdapter(Context context, List<String> planCount){
		this.planCount = planCount;
		inflater = LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		return planCount.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_choose_plan, null);
			holder = new ViewHolder();
			holder.tvPatrolTime = (TextView) convertView.findViewById(R.id.item_choose_plan_patrol_time);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tvPatrolTime.setText("巡检时间：" + planCount.get(position));
		return convertView;
	}
	
	class ViewHolder{
		TextView tvPatrolTime;
	}
}
