package com.joe.oil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
	private Context context;
	private List<Task> tasks;
	private LayoutInflater inflater;


	public TaskAdapter(Context context, List<Task> tasks) {
		this.context = context;
		this.tasks = tasks;
	}

	@Override
	public int getCount() {
		return tasks.size();
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

		ViewHolder viewHolder ;
		inflater = LayoutInflater.from(context);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.item_task, null);

			viewHolder.taskName = (TextView) convertView
					.findViewById(R.id.taskName);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.item_task_time);
			viewHolder.status=(TextView)convertView.findViewById(R.id.item_status);
			viewHolder.integral=(TextView)convertView.findViewById(R.id.item_task_integral);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Task task = tasks.get(position);
		viewHolder.taskName.setText(task.getName());
		viewHolder.time.setText(task.getManagerTime());
		viewHolder.integral.setText("获得"+task.getIntegral()+"积分");
		if (task.getIsFinished()==2||task.getActId().equals("end")){
			viewHolder.status.setText("上传成功");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.green));
		}else if(task.getIsFinished()==1) {
			viewHolder.status.setText("等待上传");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.blue));
		}else if (task.getIsFinished()==3){
			viewHolder.status.setText("正在工作");
			viewHolder.status.setTextColor(context.getResources().getColor(R.color.red));
//			convertView.setBackgroundColor(context.getResources().getColor(R.color.yellow));
		}
		else {
			viewHolder.status.setText("");
		}

		return convertView;
	}

	private class ViewHolder {
		TextView taskName;
		TextView time;
		TextView status;
		TextView integral;


	}
}
