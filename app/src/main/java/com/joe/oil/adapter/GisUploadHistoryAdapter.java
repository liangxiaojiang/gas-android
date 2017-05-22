package com.joe.oil.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Gis;

public class GisUploadHistoryAdapter extends BaseAdapter {

	private Context context;
	private List<Gis> gisUploads;
	private LayoutInflater inflater;

	public GisUploadHistoryAdapter(Context context, List<Gis> gisUploads) {
		this.context = context;
		this.gisUploads = gisUploads;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return gisUploads.size();
	}

	@Override
	public Object getItem(int position) {
		return gisUploads.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_gis_upload_history, null);

			viewHolder.tvCurLat = (TextView) convertView.findViewById(R.id.tv_cur_lat);
			viewHolder.tvCurLng = (TextView) convertView.findViewById(R.id.tv_cur_lng);
			viewHolder.tvMessageDescription = (TextView) convertView.findViewById(R.id.tv_message_description);
			viewHolder.tvFillTime = (TextView) convertView.findViewById(R.id.tv_fill_time);
			viewHolder.tvUploadStatus = (TextView) convertView.findViewById(R.id.tv_upload_status);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		Gis data = gisUploads.get(position);

		viewHolder.tvCurLat.setText(data.getLatitude());
		viewHolder.tvCurLng.setText(data.getLongitude());
		viewHolder.tvFillTime.setText(data.getTime());
		viewHolder.tvMessageDescription.setText(data.getMemo());

		if (data.getStatus().equals("2")) {
			if (data.getPics().equals("")) {
				viewHolder.tvUploadStatus.setText("图片等待上传");
				viewHolder.tvUploadStatus.setTextColor(context.getResources().getColor(R.color.qianhong));
			} else {
				viewHolder.tvUploadStatus.setText("上传成功");
				viewHolder.tvUploadStatus.setTextColor(context.getResources().getColor(R.color.green));
			}
		} else {
			viewHolder.tvUploadStatus.setText("等待上传");
			viewHolder.tvUploadStatus.setTextColor(context.getResources().getColor(R.color.qianhong));
		}

		return convertView;
	}

	private class ViewHolder {
		private TextView tvCurLat;
		private TextView tvCurLng;
		private TextView tvMessageDescription;
		private TextView tvFillTime;
		private TextView tvUploadStatus;
	}

}
