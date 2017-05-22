package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.TaskAdapter;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.view.CommentListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistoryActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private ImageView back;
	private CommentListView listview;
	private TextView noTask,tongbu;
	private Context context;
	private SqliteHelper sqliteHelper;
	private List<Task> tasks;
	private TaskAdapter taskAdapter;
	private User user;
	private OilApplication application;
	private HttpRequest http;
	private getTaskHandler TaskHandler;
	private List<Task> taskall;
	private List<Task> taskGx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_history);

		initView();
		initMembers();
		initData();
//		http.requestGetTask1(TaskHandler,user.getUserId());
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.history_btn_back);
		listview = (CommentListView) this.findViewById(R.id.history_task);
		noTask = (TextView) this.findViewById(R.id.history_no_task);

		back.setOnClickListener(this);
		listview.setOnItemClickListener(this);

		tongbu= (TextView) this.findViewById(R.id.task_synchronization);
		tongbu.setOnClickListener(this);
	}

	private void initMembers() {
		context = HistoryActivity.this;
		application = (OilApplication) getApplication();
		user = application.getUser();
		sqliteHelper = new SqliteHelper(context);
		tasks = new ArrayList<Task>();
		taskall = new ArrayList<Task>();
		taskGx=new ArrayList<Task>();
		http = HttpRequest.getInstance(context);
		TaskHandler = new getTaskHandler();
	}

	private void initData() {
		if ((getIntent().getStringExtra("title")!=null?getIntent().getStringExtra("title"):"").equals("作业纪录")){
			tasks = sqliteHelper.getFinishedTaskByCode(user.getUserId(), getIntent().getStringExtra("code"));
		}else {
//			tasks = sqliteHelper.getTaskIsFinished(user.getUserId());
			tasks=sqliteHelper.getTaskNotFinish2(user.getUserId());
		}

		if (tasks.size() == 0) {
			noTask.setVisibility(View.VISIBLE);
		} else {
			noTask.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
		}
		Collections.reverse(tasks);//集合倒序
		taskAdapter = new TaskAdapter(context, tasks);
		listview.setAdapter(taskAdapter);
		listview.setPullLoadEnable(false);
		listview.setPullRefreshEnable(false);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.history_btn_back:
			this.finish();
			break;

			case R.id.task_synchronization:
				http.requestGetTask1(TaskHandler,user.getUserId());
				break;

		default:
			break;
		}
	}



	@SuppressLint("HandlerLeak")
	private class getTaskHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HttpRequest.REQUEST_SUCCESS:
					Constants.dismissDialog();
					taskall = (List<Task>) msg.obj;
//					tasks.clear();
					if (taskall.size() == 10) {
						listview.setPullLoadEnable(true);
					} else {
						listview.setPullLoadEnable(false);
					}
					tasks.addAll(taskall);
					if (taskall.size() == 0) {
						noTask.setVisibility(View.VISIBLE);
						Constants.showToast(context, "没有任务数据");
					} else {
						noTask.setVisibility(View.GONE);
						Constants.showToast(context, "任务数据同步成功");
					}

					taskGx.addAll(tasks);
					sqliteHelper.insert(taskGx);
					if ((getIntent().getStringExtra("title")!=null?getIntent().getStringExtra("title"):"").equals("作业纪录")){
						tasks = sqliteHelper.getFinishedTaskByCode(user.getUserId(), getIntent().getStringExtra("code"));
					}else {
						tasks = sqliteHelper.getFinishedTaskIs(user.getUserId());
					}

					Collections.sort(taskGx);
					for (int i=0;i<taskGx.size();i++){
						if (taskGx.get(i).getWorkDetails().size()!=0){
							sqliteHelper.insertTemplate2(taskGx.get(i).getWorkDetails());
						}

					}
					Collections.sort(tasks);
					taskAdapter = new TaskAdapter(context, tasks);
					taskAdapter.notifyDataSetChanged();
					listview.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
					Intent intent = new Intent(Constants.STATION_PLAN);
					sendBroadcast(intent);

					break;
				case HttpRequest.REQUEST_FAILER:
					Constants.dismissDialog();
					HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
					break;
				default:
					break;
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
//		Intent intent = new Intent(HistoryActivity.this, TaskDetailActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("task", tasks.get(position-1));
//		intent.putExtras(bundle);
//		intent.putExtra("intentFrom", "history");
//		startActivity(intent);

		Intent intent=new Intent(HistoryActivity.this,TaskFillDetailActivity.class);
		intent.putExtra("task", tasks.get(position-1));
		intent.putExtra("intentFrom", "history");
		startActivity(intent);
	}
}
