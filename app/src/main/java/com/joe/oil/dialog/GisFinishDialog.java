package com.joe.oil.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.activity.GisActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;

public class GisFinishDialog extends Dialog implements OnClickListener {

	private EditText lineName;
	private TextView confirm;
	private TextView startTime;
	private TextView endTime;
	private HttpRequest http;
	private Context context;
	private OilApplication oilApplication;
	private User user;
	private SqliteHelper sqliteHelper;
	private GisFinish gisFinish;

	public GisFinishDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_gis_finish);
		this.context = context;
		initView();
		initMembers();
		setData();
	}

	private void initView() {
		lineName = (EditText) this.findViewById(R.id.dialog_gis_finish_name);
		confirm = (TextView) this.findViewById(R.id.dialog_gis_finish_confirm);
		startTime = (TextView) this.findViewById(R.id.dialog_gis_finish_start_time);
		endTime = (TextView) this.findViewById(R.id.dialog_gis_finish_end_time);

		confirm.setOnClickListener(this);
	}

	private void initMembers() {
		http = HttpRequest.getInstance(context);
		oilApplication = (OilApplication) ((Activity) context).getApplication();
		user = oilApplication.getUser();
		sqliteHelper = new SqliteHelper(context);
		// gisFinish =
		// sqliteHelper.getGisFinishByTaskNo(Constants.GIS_START_NUM);
		gisFinish = sqliteHelper.getGisFinishNotFinish();
	}

	private void setData() {
		gisFinish.setEndTime(DateUtils.getDateTime());
		startTime.setText("开始时间：   " + gisFinish.getCreatTime());
		endTime.setText("结束时间：   " + gisFinish.getEndTime());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_gis_finish_confirm:
			confrim();
			break;

		default:
			break;
		}
	}

	private void confrim() {
		String lineText = lineName.getText().toString();
		if (lineText.trim().equals("")) {
			Constants.showToast(context, "请输入巡护名称！");
			return;
		}

		if (lineText.length() > 50) {
			Constants.showToast(context, "巡护名称太长！");
			return;
		}
		String name = lineName.getText().toString();
		gisFinish.setLineName(name);
		sqliteHelper.updateGisFinish(gisFinish);
		Constants.GIS_START_NUM = "";
		Constants.IS_LINE_START = false;

		gisFinish = sqliteHelper.getGisFinishNotSubmitByCreateTime(gisFinish.getCreatTime());
		if (gisFinish.getGisNum() == 0) {
			gisFinish.setStatus("4");
			sqliteHelper.updateGisFinish(gisFinish);
			Constants.showToast(context, "当前巡护作业无Gis数据");
			dismiss();
			if (GisActivity.gisHandler != null) {
				Message msg_gis = GisActivity.gisHandler.obtainMessage();
				msg_gis.what = 0;
				msg_gis.sendToTarget();
			}
			return;
		} else {
			sqliteHelper.updateGisFinish(gisFinish);
			http.requestGisFinish(new FinishHandler(gisFinish), gisFinish);
		}

		Constants.showDialog(context);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				doWithFinish();
			}
		}, 2000);
	}

	private Handler mHandler = new Handler();

	private void doWithFinish() {
		Constants.dismissDialog();
		if (!Constants.checkNetWork(context)) {
			Constants.showToast(context, "网络不给力，数据已转至后台上传");
		}
		dismiss();
		if (GisActivity.gisHandler != null) {
			Message msg_gis = GisActivity.gisHandler.obtainMessage();
			msg_gis.what = 0;
			msg_gis.sendToTarget();
		}
	}

	@SuppressLint("HandlerLeak")
	private class FinishHandler extends Handler {
		private GisFinish gisFinish;

		public FinishHandler(GisFinish gisFinish) {
			this.gisFinish = gisFinish;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_SUCCESS:
				gisFinish.setStatus("2");
				sqliteHelper.updateGisFinish(gisFinish);
				break;

			case HttpRequest.REQUEST_FAILER:
				gisFinish.setStatus("1");
				sqliteHelper.updateGisFinish(gisFinish);
				break;

			default:
				break;
			}
			if (GisActivity.gisHandler != null) {
				Message msg_gis = GisActivity.gisHandler.obtainMessage();
				msg_gis.what = 0;
				msg_gis.sendToTarget();
			}
		}
	}

}
