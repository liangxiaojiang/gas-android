package com.joe.oil.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.PlanDetail;

public class NotInspectionAdapter extends BaseAdapter {
	
	private List<PlanDetail> nots;
	private LayoutInflater inflater;
	
	public NotInspectionAdapter(Context context, List<PlanDetail> nots){
		this.nots = nots;
		inflater = LayoutInflater.from(context);
		
	}

	@Override
	public int getCount() {
		return nots.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_inspection, null);
			holder.name = (TextView) convertView.findViewById(R.id.item_inspection_name);
			holder.planTime = (TextView) convertView.findViewById(R.id.item_inspection_plan_time);
			holder.inspectionTime = (TextView) convertView.findViewById(R.id.item_inspection_inspection_time);
			holder.ll = (LinearLayout) convertView.findViewById(R.id.item_inspection_ll);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlanDetail not = nots.get(position);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date upTime = sdf.parse(not.getUpTime());
			Date now = sdf.parse(sdf.format(new Date()));
			if(now.after(upTime) && not.getStatus().equals("1")){
				holder.ll.setBackgroundResource(R.color.qianhong);
			}
			else if(not.getStatus().equals("4")){
				holder.ll.setBackgroundResource(R.color.green);
			}
			else {
				holder.ll.setBackgroundResource(R.color.white);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		holder.name.setText("巡检点名： " + not.getPointName());
		holder.planTime.setText("计划时间： " + not.getPatrolTime());
		if(not.getPlanType().equals("1")){
			holder.inspectionTime.setText(not.getDownTime().substring(11, 16) + " ~ " + not.getUpTime().substring(11, 16));
		}
		else {
			holder.inspectionTime.setText(not.getDownTime().substring(5, 16) + " ~ " + not.getUpTime().substring(5, 16));
			holder.inspectionTime.setTextSize(15);
		}
		return convertView;
	}
	
	class ViewHolder {
		TextView name;
		TextView planTime;
		TextView inspectionTime;
		LinearLayout ll;
	}
}
