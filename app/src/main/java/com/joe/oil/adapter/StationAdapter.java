package com.joe.oil.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Office;
import com.joe.oil.entity.PlanDetail;

@SuppressLint("InflateParams")
public class StationAdapter extends BaseAdapter {
	private final String TAG = "StationAdapter";
	private List<PlanDetail> plans;
	private LayoutInflater inflater;

	public StationAdapter(Context context, List<PlanDetail> plans) {
		this.plans = plans;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return plans.size();
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_station, null);
			holder.name = (TextView) convertView.findViewById(R.id.item_station_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		PlanDetail plan = plans.get(position);
		holder.name.setText(plan.getOfficeName().trim());

		return convertView;
	}

	class ViewHolder {
		TextView name;
	}

}
