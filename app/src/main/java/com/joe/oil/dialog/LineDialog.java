package com.joe.oil.dialog;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.GisActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;
import com.joe.oil.util.CustomUtil;
import com.joe.oil.util.DateUtils;

@SuppressLint("SimpleDateFormat")
public class LineDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView title;
	private TextView tips;
	private TextView cancel;
	private TextView confirm;
	private Context context;
	private OilApplication oilApplication;
	private User user;
	private int fromWhere;

	public LineDialog(Context context, int fromWhere) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_line);
		this.context = context;
		this.fromWhere = fromWhere;

		initView();
		initMembers();
	}

	private void initView() {
		title = (TextView) this.findViewById(R.id.tips);
		tips = (TextView) this.findViewById(R.id.dialog_line_tips);
		cancel = (TextView) this.findViewById(R.id.dialog_line_cancel);
		confirm = (TextView) this.findViewById(R.id.dialog_line_confirm);

		title.setText("巡护作业提示");
		if (Constants.IS_LINE_START) {
			if (fromWhere == 0) {
				tips.setText("确定结束巡护作业？");
			} else if (fromWhere == 1) {
				tips.setText("请先结束当前GIS巡护作业");
			}
		} else {
			tips.setText("确定开始巡护作业？");
		}

		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	private void initMembers() {
		oilApplication = (OilApplication) ((Activity) context).getApplication();
		user = oilApplication.getUser();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_line_cancel:
			dismiss();
			break;

		case R.id.dialog_line_confirm:
			dismiss();
			if (Constants.IS_LINE_START) {
				GisFinishDialog gisFinishDialog = new GisFinishDialog(context);
				gisFinishDialog.setCanceledOnTouchOutside(false);
				gisFinishDialog.show();
			} else {
				if (GisActivity.gisHandler != null) {
					Message msg = GisActivity.gisHandler.obtainMessage();
					msg.what = 0;
					msg.sendToTarget();
				}
				CustomUtil.createNewTaskNo(user);
				GisFinish gisFinish = new GisFinish();
				gisFinish.setCreatTime(DateUtils.getDateTime());
				gisFinish.setTaskNo(Constants.GIS_START_NUM);
				gisFinish.setCategory(Constants.CUR_GIS_INSPECTION_TYPE);
				gisFinish.setStatus("0");
				gisFinish.setGisNum(0);
				gisFinish.setUserId(user.getUserId());
				gisFinish.setLineName("");
				gisFinish.setEndTime("");
				SqliteHelper sqliteHelper = new SqliteHelper(context);
				sqliteHelper.insertGisFinish(gisFinish);
			}
			break;

		default:
			break;
		}
	}
}
