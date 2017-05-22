package com.joe.oil.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.PlanDetail;

public class CheckItemAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<PlanDetail> planDetails;

	public CheckItemAdapter(Context context, List<PlanDetail> planDetails) {
		this.context = context;
		this.planDetails = planDetails;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return planDetails.size();
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
			convertView = inflater.inflate(R.layout.item_checkitem, null);
			holder.name = (TextView) convertView.findViewById(R.id.item_checkitem_name);
			holder.rl_bg = (RelativeLayout) convertView.findViewById(R.id.item_checkitem_container);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PlanDetail planDetail = planDetails.get(position);
		String result = planDetail.getResult();
		String unit = planDetail.getItemUnit();

		if (result == null || result != null && unit == null || result != null && unit != null && unit.equals("null")) {
			unit = "";
		}
		if (result == null) {
			result = "";
			holder.name.setBackgroundColor(context.getResources().getColor(R.color.white));
		} else {
			if (planDetail.getExceptionStatus() != null && !planDetail.getExceptionStatus().equals("")) {
				if (planDetail.getExceptionStatus().equals("1")) {
					holder.name.setBackgroundColor(context.getResources().getColor(R.color.green));
				} else {
					holder.name.setBackgroundColor(context.getResources().getColor(R.color.qianhong));
				}
			}
		}
		if (planDetail.getIsRequiredToWrite() != null && planDetail.getIsRequiredToWrite().equals("unRequired")) {
			// holder.name.setTextColor(context.getResources().getColor(R.color.gray));
			holder.rl_bg.setBackgroundResource(R.drawable.ems_ic_preference_single_pressed);
		} else {
			holder.name.setTextColor(context.getResources().getColor(R.color.black));
			holder.rl_bg.setBackgroundResource(R.drawable.ems_ic_preference_single_normal);
		}

		holder.name.setText(planDetail.getItemName() + " = " + result + unit);
		return convertView;
	}

	class ViewHolder {
		TextView name;
		RelativeLayout rl_bg;
	}

}
