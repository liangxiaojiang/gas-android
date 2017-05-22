package com.joe.oil.dialog;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.InspectionActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.http.XUtilsHttp;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

@SuppressLint("HandlerLeak")
public class ExcpetionHandleDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView name;
	private TextView project;
	private TextView result;
	private TextView description;
	private TextView suggestion;
	private EditText exceptionResult;
	private TextView cancel;
	private TextView confirm;
	private Context context;
	private PlanDetail planDetail;
	private HttpRequest http;
	private SqliteHelper sqliteHelper;
	private List<PlanDetail> planDetails;
	private OilApplication application;
	private User user;
	private int planDetailsSize;
	private int position;

	public ExcpetionHandleDialog(Context context, PlanDetail planDetail, int planDetailsSize, int position) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_gas_exception_handle);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		this.planDetail = planDetail;
		this.context = context;
		this.planDetailsSize = planDetailsSize;
		this.position = position;
		initView();
		initMembers();
		initData();

	}

	private void initView() {
		name = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_name);
		project = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_project);
		result = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_result);
		description = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_description);
		suggestion = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_suggestion);
		exceptionResult = (EditText) this.findViewById(R.id.dialog_gas_exception_handle_exception_result);
		cancel = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_cancel);
		confirm = (TextView) this.findViewById(R.id.dialog_gas_exception_handle_confirm);

		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	private void initMembers() {
		http = HttpRequest.getInstance(context);
		sqliteHelper = new SqliteHelper(context);
		application = (OilApplication) ((Activity) context).getApplication();
		user = application.getUser();
	}

	private void initData() {
		name.setText("巡检点名：" + planDetail.getPointName());
		project.setText("巡检项目：" + planDetail.getItemName());
		result.setText("巡检结果：" + planDetail.getResult());
		description.setText("异常描述：" + planDetail.getMemo());
		suggestion.setText("处理意见：" + planDetail.getHandleAdvice());
		if (planDetail.getHandleMemo() != null && !planDetail.getHandleMemo().equals("")) {
			exceptionResult.setText(planDetail.getHandleMemo());
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_gas_exception_handle_cancel:
			dismiss();
			break;

		case R.id.dialog_gas_exception_handle_confirm:
			confirm();
			break;

		default:
			break;
		}
	}

	private void confirm() {
		// if (planDetail.getWorkId() == null ||
		// planDetail.getWorkId().equals("")) {
		// Constants.showToast(context, "此条异常数据还处于上报状态，请等待一段时间再操作");
		// return;
		// }
		String exceptionRe = exceptionResult.getText().toString();
		if (exceptionRe.length() <= 0) {
			Constants.showToast(context, "请输入处理结果");
		} else {
			planDetail.setHandleMemo(exceptionRe);
			planDetail.setExceptionStatus("1");
			planDetails = new ArrayList<PlanDetail>();
			planDetails.add(planDetail);
			// http.requestSubmitGasList(new SubmitHandler(planDetails),
			// planDetails, "normal", user.getUserId());
			XUtilsHttp.getInstance(context).requestSubmitGasList(new SubmitHandler(planDetails), planDetails, "normal", user.getUserId());
			Constants.showDialog(context);
		}
	}

	private class SubmitHandler extends Handler {

		private List<PlanDetail> planDetails;

		public SubmitHandler(List<PlanDetail> planDetails) {
			this.planDetails = planDetails;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_SUCCESS:
				// http.requestDeleteWork(new DeleteHandler(planDetails),
				// user.getUserId(), planDetails.get(0).getWorkId());

				Constants.dismissDialog();
				for (int i = 0; i < planDetails.size(); i++) {
					planDetails.get(i).setHandleMemoUpload("1");
					planDetails.get(i).setExceptionStatus("2");
					sqliteHelper.deleteExceptionByItemId(planDetails.get(i).getItemId());
				}
				sqliteHelper.updateUploadPlanStatus(planDetails);
				Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
				msg2.sendToTarget();
				Constants.showToast(context, "处理结果上传成功");
				dismiss();
				break;

			case HttpRequest.REQUEST_FAILER:
				sqliteHelper.updateUploadPlanStatus(planDetails);
				Constants.dismissDialog();
				for (int i = 0; i < planDetails.size(); i++) {
					planDetails.get(i).setHandleMemoUpload("0");
					planDetails.get(i).setExceptionStatus("2");
				}
				sqliteHelper.updateUploadPlanStatus(planDetails);
				Constants.showToast(context, "处理结果上传失败");
				break;
			default:
				break;
			}
		}
	}

	private class DeleteHandler extends Handler {

		private List<PlanDetail> planDetails;

		public DeleteHandler(List<PlanDetail> planDetails) {
			this.planDetails = planDetails;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_FAILER:
				Constants.dismissDialog();
				for (int i = 0; i < planDetails.size(); i++) {
					planDetails.get(i).setHandleMemoUpload("0");
					planDetails.get(i).setExceptionStatus("2");
				}
				sqliteHelper.updateUploadPlanStatus(planDetails);
				Constants.showToast(context, "处理结果上传失败");
				dismiss();
				break;

			case HttpRequest.REQUEST_SUCCESS:
				Constants.dismissDialog();
				for (int i = 0; i < planDetails.size(); i++) {
					planDetails.get(i).setHandleMemoUpload("1");
					planDetails.get(i).setExceptionStatus("2");
					sqliteHelper.deleteExceptionByItemId(planDetails.get(i).getItemId());
				}
				sqliteHelper.updateUploadPlanStatus(planDetails);
				Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
				msg2.sendToTarget();
				Constants.showToast(context, "处理结果上传成功");
				dismiss();
				break;

			default:
				break;
			}
		}
	}
}
