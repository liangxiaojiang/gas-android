package com.joe.oil.adapter;

import java.util.List;

import com.joe.oil.R;
import com.joe.oil.entity.UploadException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ExceptionHistoryAdapter extends BaseAdapter {

	private List<UploadException> uploadExceptions;
	private LayoutInflater inflater;
	private Context context;

	public ExceptionHistoryAdapter(Context context, List<UploadException> uploadExceptions) {
		this.uploadExceptions = uploadExceptions;
		inflater = LayoutInflater.from(context);
		this.context = context;
	}

	@Override
	public int getCount() {
		return uploadExceptions.size();
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_exception_history, null);
			holder.point = (TextView) convertView.findViewById(R.id.item_exception_history_point);
			holder.type = (TextView) convertView.findViewById(R.id.item_exception_history_type);
			holder.description = (TextView) convertView.findViewById(R.id.item_exception_history_description);
			holder.time = (TextView) convertView.findViewById(R.id.item_exception_history_time);
			holder.isUploadSuccess = (TextView) convertView.findViewById(R.id.item_exception_history_isuploadsuccess);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UploadException uploadException = uploadExceptions.get(position);
		holder.point.setText("    " + uploadException.getPointName() + "_" + uploadException.getDeviceName());
		holder.type.setText(uploadException.getWorkTypeName());
		holder.description.setText(uploadException.getDescription());
		holder.time.setText(uploadException.getTime());
		if (uploadException.getIsUploadSuccess().equals("1")) {
			holder.isUploadSuccess.setText("数据上传成功");
			holder.isUploadSuccess.setTextColor(context.getResources().getColor(R.color.green));
		} else {
			holder.isUploadSuccess.setText("等待上传");
			holder.isUploadSuccess.setTextColor(context.getResources().getColor(R.color.qianhong));
		}
		return convertView;
	}

	class ViewHolder {
		TextView point;
		TextView type;
		TextView description;
		TextView time;
		TextView isUploadSuccess;
	}

}
