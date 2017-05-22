package com.joe.oil.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.PlanDetail;

public class ExceptionAdapter extends BaseAdapter {

	private List<PlanDetail> exceptions;
	private LayoutInflater inflater;

	public ExceptionAdapter(Context context, List<PlanDetail> exceptions) {
		this.exceptions = exceptions;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return exceptions.size();
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
			convertView = inflater.inflate(R.layout.item_exception, null);
			holder.exceptionDescription = (TextView) convertView.findViewById(R.id.item_exception_description);
			holder.handleSuggestion = (TextView) convertView.findViewById(R.id.item_exception_inspection_suggestion);
			holder.inspectionName = (TextView) convertView.findViewById(R.id.item_exception_inspection_name);
			holder.inspectionProject = (TextView) convertView.findViewById(R.id.item_exception_inspection_project);
			holder.result = (TextView) convertView.findViewById(R.id.item_exception_inspection_result);
			holder.tiem = (TextView) convertView.findViewById(R.id.item_exception_inspection_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PlanDetail exceptionTask = exceptions.get(position);
		holder.exceptionDescription.setText(exceptionTask.getMemo());
		holder.handleSuggestion.setText(exceptionTask.getHandleAdvice());
		holder.inspectionName.setText("巡检点名：" + exceptionTask.getPointName());
		holder.inspectionProject.setText("巡检项目：" + exceptionTask.getItemName());
		holder.result.setText("巡检结果： " + exceptionTask.getResult());
		try {
			holder.tiem.setText("异常时间 " + exceptionTask.getCreateTime().substring(0, "yyyy-MM-dd HH:mm".length()));
		} catch (Exception e) {
			holder.tiem.setText("");
		}

		return convertView;
	}

	class ViewHolder {
		TextView inspectionName;
		TextView inspectionProject;
		TextView exceptionDescription;
		TextView handleSuggestion;
		TextView result;
		TextView tiem;
	}

}
