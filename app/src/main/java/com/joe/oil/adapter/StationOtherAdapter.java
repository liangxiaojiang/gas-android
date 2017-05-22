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
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

@SuppressLint("InflateParams")
public class StationOtherAdapter extends BaseAdapter {
	private final String TAG = "StationAdapter";
	private List<Office> offices;
	private LayoutInflater inflater;
	private SqliteHelper sqliteHelper;
	private Context context;

	public StationOtherAdapter(Context context, List<Office> offices) {
		this.offices = offices;
		inflater = LayoutInflater.from(context);
		sqliteHelper = new SqliteHelper(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return offices.size();
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
	
		holder.name.setText(offices.get(position).getName().trim());
		return convertView;
	}

	class ViewHolder {
		TextView name;
	}

}
