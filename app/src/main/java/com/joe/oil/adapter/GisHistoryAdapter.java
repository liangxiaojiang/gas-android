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
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.sqlite.SqliteHelper;

@SuppressLint("InflateParams")
public class GisHistoryAdapter extends BaseAdapter {

	private Context context;
	private List<GisFinish> gisFinishs;
	private LayoutInflater inflater;
	private SqliteHelper sqliteHelper;

	public GisHistoryAdapter(Context context, List<GisFinish> gisFinishs) {
		this.context = context;
		this.gisFinishs = gisFinishs;
		this.inflater = LayoutInflater.from(context);
		this.sqliteHelper = new SqliteHelper(context);
	}

	@Override
	public int getCount() {
		return gisFinishs.size();
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
	public boolean isEnabled(int position) {
		return false;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_gis_histroy, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.item_gis_history_name);
			holder.startTime = (TextView) convertView.findViewById(R.id.item_history_start_time);
			holder.endTime = (TextView) convertView.findViewById(R.id.item_history_end_time);
			holder.status = (TextView) convertView.findViewById(R.id.item_history_upload_status);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		GisFinish gisFinish = gisFinishs.get(position);
		holder.name.setText(gisFinish.getLineName());
		holder.startTime.setText(gisFinish.getCreatTime());
		holder.endTime.setText(gisFinish.getEndTime());
		if (gisFinish.getStatus().equals("4")) {
			holder.status.setText("未上传,共0条数据");
			holder.status.setTextColor(context.getResources().getColor(R.color.green));
		} else if (gisFinish.getStatus().equals("2")) {
			List<Gis> data = sqliteHelper.getGisByNum(gisFinish.getTaskNo());
			if (data != null && data.size() > 0) {
				holder.status.setText("GIS数据等待上传,共" + data.size() + "条");
				holder.status.setTextColor(context.getResources().getColor(R.color.qianhong));
			} else {
				holder.status.setText("上传成功,共" + gisFinish.getGisNum() + "条数据");
				holder.status.setTextColor(context.getResources().getColor(R.color.green));
			}
		} else if (gisFinish.getStatus().equals("0") && gisFinish.getLineName().equals("")) {
			holder.status.setText("正在进行巡护作业");
			holder.status.setTextColor(context.getResources().getColor(R.color.blue1));
		} else {
			holder.status.setText("巡护记录未上传");
			holder.status.setTextColor(context.getResources().getColor(R.color.qianhong));
		}

		return convertView;
	}

	class ViewHolder {
		TextView name;
		TextView startTime;
		TextView endTime;
		TextView status;
	}

}
