package com.joe.oil.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.joe.oil.R;
import com.joe.oil.activity.CheckItemActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.activity.PicSelectedEnsureActivity;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.UploadException;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class CheckItemTypeInputDialog extends Dialog implements android.view.View.OnClickListener {
	private TextView takePhoto; // 拍照
	private TextView photograph; // 录像
	private TextView edit;
	private TextView confirm;//确认
	private TextView next;//下一项
	private TextView deviceName; // 巡检点名称
	private TextView itemName; // 巡检内容
	private TextView standard; // 巡检标准
	private EditText result;
	private EditText memo; // 异常描述
	private EditText etJilu;
	private EditText handleAdvice; // 处理意见
	private Spinner status;
	private LinearLayout ll;
	private TextView photoNum;
	private RelativeLayout takePhoto_rl;

	private TextView tvTips;

	private Context context;
	private PlanDetail planDetail;
	private SqliteHelper sqliteHelper;
	private Double downValue;
	private Double upValue;
	private boolean isResultException = false;
	private String[] spinner = new String[] { "启用", "停用" };
	private boolean isSpinnerVisible = false;
	private String intentFrom;
	private OilApplication application;
	private User user;
	private HttpRequest http;
	private String curTotalPicId = "";
	private UploadException exceptionPlanDetail;
	private Handler mHandler = new Handler();
	private String originResult = ""; // dialog生成时，planDetail的初始result
	private String originDescription = "";
	private String originAdvice = "";

	private final int TAG_EDIT = 0; // 将右上角编辑按钮标记为编辑状态
	private final int TAG_CANCEL = 1; // 将右上角编辑按钮标记为取消编辑状态

	public CheckItemTypeInputDialog(Context context, PlanDetail planDetail, String intentFrom) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_check_type_input);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		this.context = context;
		this.planDetail = planDetail;
		this.intentFrom = intentFrom;
		sqliteHelper = new SqliteHelper(context);

		initView();
		setUpAndDownValue();
		initData();
	}

	private void initView() {
		takePhoto = (TextView) this.findViewById(R.id.dialog_check_type_input_photo);
		tvTips = (TextView) this.findViewById(R.id.dialog_check_type_input_tips);
		photograph = (TextView) this.findViewById(R.id.dialog_check_type_input_movie);
		edit = (TextView) this.findViewById(R.id.dialog_check_type_input_edit);
		confirm = (TextView) this.findViewById(R.id.dialog_check_type_input_confirm);
		next = (TextView) this.findViewById(R.id.dialog_check_type_input_next);
		deviceName = (TextView) this.findViewById(R.id.dialog_check_type_input_station_name);
		itemName = (TextView) this.findViewById(R.id.dialog_check_type_input_check_name);
		standard = (TextView) this.findViewById(R.id.dialog_check_type_input_check_standard);
		result = (EditText) this.findViewById(R.id.dialog_check_type_input_value);
		memo = (EditText) this.findViewById(R.id.dialog_check_type_input_exception);
		etJilu = (EditText) this.findViewById(R.id.dialog_check_type_input_jilu);
		handleAdvice = (EditText) this.findViewById(R.id.dialog_check_type_input_solve);
		status = (Spinner) this.findViewById(R.id.dialog_check_type_input_spinner);
		ll = (LinearLayout) this.findViewById(R.id.dialog_check_type_input_ll);
		photoNum = (TextView) this.findViewById(R.id.dialog_check_type_choose_photo_num);
		takePhoto_rl = (RelativeLayout) this.findViewById(R.id.dialog_check_type_input_photo_rl);

		result.addTextChangedListener(textWatcher);
		takePhoto_rl.setOnClickListener(this);
		photograph.setOnClickListener(this);
		edit.setOnClickListener(this);
		confirm.setOnClickListener(this);
		next.setOnClickListener(this);
		edit.setVisibility(View.GONE);
		edit.setTag(TAG_EDIT);
		photoNum.setText("");
		photoNum.setVisibility(View.GONE);
	}

	private void initData() {
		application = (OilApplication) ((Activity) context).getApplication();
		user = application.getUser();
		http = HttpRequest.getInstance(context);

		exceptionPlanDetail = sqliteHelper.getExceptionOfPlanDetail(planDetail.getItemId());
		deviceName.setText("巡检点名： " + planDetail.getPointName());
		itemName.setText("巡检内容： " + planDetail.getItemName());
		standard.setText("巡检标准： " + downValue + planDetail.getItemUnit() + " ~ " + upValue + planDetail.getItemUnit());

		Log.d("tips", planDetail.getTips() + "");
		if (planDetail.getType().equals("2") && !planDetail.getTips().equals("null")) {
			tvTips.setVisibility(View.VISIBLE);
			tvTips.setText(planDetail.getTips());
		}else {
			tvTips.setVisibility(View.GONE);
		}
		if (planDetail.getResult() != null) {
			result.setText(planDetail.getResult());
			originResult = planDetail.getResult();
			originAdvice = planDetail.getHandleAdvice();
			originDescription = planDetail.getMemo();
		}
		if (planDetail.getItemName().contains("进站干管压力")) {
			ll.setVisibility(View.VISIBLE);
			isSpinnerVisible = true;
		} else {
			ll.setVisibility(View.GONE);
			isSpinnerVisible = false;
		}
		ArrayAdapter<String> adapterChoose = new ArrayAdapter<String>(context, R.layout.item_spinner, spinner);
		adapterChoose.setDropDownViewResource(R.layout.item_spinner_dropdown);
		status.setAdapter(adapterChoose);
		memo.setText(planDetail.getMemo());
		handleAdvice.setText(planDetail.getHandleAdvice());

		if (planDetail.getResult() != null) {
			for (int i = 0; i < spinner.length; i++) {
				if (planDetail.getResult().equals(spinner[i])) {
					status.setSelection(i);
				}
			}
		}
		// 以下代码用于分条件控制界面的可编辑性
		if (intentFrom.equals("InspectionActivity")) { // 表示只允许浏览
			edit.setVisibility(View.GONE);
			result.setEnabled(false);
			memo.setEnabled(false);
			status.setEnabled(false);
			handleAdvice.setEnabled(false);
			result.setTextColor(context.getResources().getColor(R.color.white));
			memo.setTextColor(context.getResources().getColor(R.color.white));
			handleAdvice.setTextColor(context.getResources().getColor(R.color.white));
			memo.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			result.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			status.setBackgroundResource(R.drawable.arrow);
			handleAdvice.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			checkPicSelectStatus(planDetail.getItemId());
		} else {
			if (exceptionPlanDetail != null && planDetail.getResult() != null && planDetail.getExceptionStatus().equals("2")) {
				memo.setEnabled(false);
				status.setEnabled(false);
				handleAdvice.setEnabled(false);
				result.setEnabled(false);
				edit.setTag(TAG_EDIT);
				edit.setText("编辑");
				edit.setVisibility(View.VISIBLE);
				edit.setTextColor(context.getResources().getColor(R.color.green));
				result.setTextColor(context.getResources().getColor(R.color.white));
				memo.setTextColor(context.getResources().getColor(R.color.white));
				handleAdvice.setTextColor(context.getResources().getColor(R.color.white));
				memo.setBackgroundResource(R.drawable.bg_edittext_uninputable);
				result.setBackgroundResource(R.drawable.bg_edittext_uninputable);
				status.setBackgroundResource(R.drawable.arrow);
				handleAdvice.setBackgroundResource(R.drawable.bg_edittext_uninputable);
				checkPicSelectStatus(exceptionPlanDetail.getItemId());
				takePhoto_rl.setEnabled(false);
				takePhoto_rl.setBackgroundResource(R.color.gray);
			} else {
				onResume();
			}
		}
	}

	private void checkPicSelectStatus(String itemId) {
		List<ImageBean> imgData = sqliteHelper.getLocalPics("xj_" + itemId, planDetail.getPatrolTime());
		if (imgData != null && imgData.size() > 0) {
			takePhoto.setText("查看图片");
			photoNum.setText(imgData.size() + "");
			photoNum.setVisibility(View.VISIBLE);
			takePhoto_rl.setEnabled(true);
			takePhoto_rl.setBackgroundResource(R.drawable.selector_btn_blue);
		} else {
			takePhoto.setText("添图");
			photoNum.setText("");
			photoNum.setVisibility(View.GONE);
			takePhoto_rl.setEnabled(false);
			takePhoto_rl.setBackgroundResource(R.color.gray);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_check_type_input_confirm:
			//当点击确定时报错，
			lockClick();
			doWithComplate(false);
			break;

		case R.id.dialog_check_type_input_next:
			//当点击下一项时报错
			lockClick();
			doWithComplate(true);
			break;

		case R.id.dialog_check_type_input_photo_rl:
			lockClick();
			Log.d("Image Select Flag", "start!");
			int myIntentFromNum = 1;
			String myTypeOfId = "xj_" + planDetail.getItemId();
			List<ImageBean> imgData = null;

			if (intentFrom.equals("InspectionActivity")) { // 处于只可查看情况
				myIntentFromNum = 5;
				myTypeOfId = "xj_" + planDetail.getItemId();
			} else {
				if (exceptionPlanDetail != null && edit.getVisibility() == View.VISIBLE && (Integer) edit.getTag() == TAG_EDIT) {
					myIntentFromNum = 5;
					myTypeOfId = "xj_" + exceptionPlanDetail.getItemId();
				} else {
					myIntentFromNum = 1;
					myTypeOfId = "xj_" + planDetail.getItemId();
				}
			}

			imgData = sqliteHelper.getLocalPics(myTypeOfId, planDetail.getPatrolTime());
			if (imgData != null && imgData.size() > 0) {
				ImageGroup imageGroup = new ImageGroup("ALL", imgData);
				Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
				intent.putExtra("intentFrom", myIntentFromNum);
				intent.putExtra("typeOfId", myTypeOfId);
				intent.putExtra("planDetail", planDetail);
				intent.putExtra("imageSelected", imageGroup);
				context.startActivity(intent);
			} else {
				Log.d("Image Select Flag", "imgData null");
				Intent intent = new Intent(context, ImagePickerActivity.class);
				intent.putExtra("intentFrom", myIntentFromNum);
				intent.putExtra("typeOfId", myTypeOfId);
				intent.putExtra("planDetail", planDetail);
				context.startActivity(intent);
			}
			break;

		case R.id.dialog_check_type_input_edit:
			if ((Integer) edit.getTag() == TAG_EDIT) {
				memo.setEnabled(true);
				status.setEnabled(true);
				handleAdvice.setEnabled(true);
				result.setEnabled(true);
				takePhoto_rl.setEnabled(true);
				edit.setText("恢复");
				edit.setTag(TAG_CANCEL);
				edit.setTextColor(context.getResources().getColor(R.color.white));
				result.setTextColor(context.getResources().getColor(R.color.black));
				memo.setTextColor(context.getResources().getColor(R.color.black));
				handleAdvice.setTextColor(context.getResources().getColor(R.color.black));
				memo.setBackgroundResource(R.drawable.bg_edittext);
				status.setBackgroundResource(R.drawable.arrow);
				result.setBackgroundResource(R.drawable.bg_edittext);
				handleAdvice.setBackgroundResource(R.drawable.bg_edittext);
				takePhoto_rl.setBackgroundResource(R.drawable.selector_btn_blue);
			} else {
				initData(); // 恢复初始状态
			}
			break;

		default:
			break;
		}
	}

	private void lockClick() {
		confirm.setEnabled(false);
		next.setEnabled(false);
		takePhoto_rl.setEnabled(false);
	}

	private void unLockClick() {
		confirm.setEnabled(true);
		next.setEnabled(true);
		takePhoto_rl.setEnabled(true);
	}

	private void setUpAndDownValue() {
		if (planDetail.getUpValue() == null || planDetail.getUpValue().equals("") || planDetail.getUpValue().equals("null")) {
			if (Constants.YSJ_STATUS.equals("0")) {
				downValue = 0.6;
				upValue = 3.5;
			} else {
				downValue = 2.2;
				upValue = 3.5;
			}
		} else {
			downValue = Double.parseDouble(planDetail.getDownValue());
			upValue = Double.parseDouble(planDetail.getUpValue());
		}
	}

	private void doWithComplate(final boolean isGoNext) {
		// 编辑按钮显示并且显示编辑字样 或者处于浏览状态
		if ((edit.getVisibility() == View.VISIBLE && (Integer) edit.getTag() == TAG_EDIT) || intentFrom.equals("InspectionActivity")) {
			if (isGoNext) {
				Message msg2 = CheckItemActivity.nextHandler.obtainMessage();
				msg2.what = 0;
				msg2.sendToTarget();
			}
			dismiss();
			return;
		}

		if(result.getText().toString().equals("")) {
			isResultException = false;
		}

		// 判断处理新的内容编辑
		if (result.getText().toString().equals("") && !(status.getSelectedItemId() == 1)) {
			Constants.showToast(context, "请输入巡检结果");
			unLockClick();
		} else {
			String exceptionStr = memo.getText().toString();
			boolean isException = false;
			if (exceptionStr.length() <= 0) {
				if (isResultException && !(status.getSelectedItemId() == 1)) {
					Constants.showToast(context, "请输入异常现象");
					unLockClick();
					return;
				}
				if (status.getSelectedItemId() == 1) {
					isException = isResultException;
				}
			} else {
				isException = true;
			}
			if (isException) {
				planDetail.setExceptionStatus("2");
				curTotalPicId = "";
				List<ImageBean> imgData = sqliteHelper.getLocalPics("xj_" + planDetail.getItemId(), planDetail.getPatrolTime());
				if (imgData != null && imgData.size() > 0) {
					for (int i = 0; i < imgData.size(); i++) {
						curTotalPicId += ",";
					}
				}
				Log.d("CheckItemTypeInputDialog", curTotalPicId);
				UploadException uploadException = new UploadException();
				uploadException.setPointName(planDetail.getPointName());
				uploadException.setTime(DateUtils.getDateTime());
				uploadException.setDeviceName(planDetail.getOfficeName());
				uploadException.setDeviceCode(planDetail.getCode().substring(0, 8));
				uploadException.setWorkTypeId("22");
				uploadException.setWorkTypeName("");
				uploadException.setResult(result.getText().toString());
				uploadException.setTreatmentAdvice(handleAdvice.getText().toString());
				uploadException.setUserId(user.getUserId());
				uploadException.setDescription(memo.getText().toString());
				uploadException.setItemId(planDetail.getItemId());
				uploadException.setIsUploadSuccess("0");
				uploadException.setPicId(curTotalPicId);
				uploadException.setWorkId("");
				uploadException.setHistoryId("");
				uploadException.setFromWhere("1");
				uploadException.setCategory("4");
				uploadException.setPatrolTime(planDetail.getPatrolTime());

				UploadException local_data = sqliteHelper.geteExceptionByItemId(planDetail.getItemId());
				if (local_data == null) {
					Log.d("KKKKKKKKKKKKKKKK", " -------------------------------------");
					// 向数据库中插入保存该条异常数据
					sqliteHelper.insertException(uploadException);
				} else {
					Log.d("KKKKKKKKKKKKKKKK", " +++++++++++++++++++++++++++++++++++++");
					// 向数据库中更新保存该条异常数据
					if (!local_data.getFromWhere().equals("1")) {
						// 如果本地记录数据已和网络数据衔接，继续标记为已衔接
						uploadException.setFromWhere("3");
						Log.d("KKKKKKKKKKKKKKKK", " -----------------++++++++++++++++++++");
					}
					sqliteHelper.updetaExceptionOfPlanDetail(uploadException);
				}

				String lat=application.getLat();
				String lng=application.getLng();

				String uploadType = "";


				// 向服务器提交该条异常数据
				http.requestUploadWork(new UploadWorkHandler(uploadException), uploadException.getDeviceName() + uploadException.getPointName(), planDetail.getCode().substring(0, 8), "22",
						user.getUserId(), "4", curTotalPicId, memo.getText().toString(),lat,lng,uploadType);
			} else {
				planDetail.setExceptionStatus("1");
				sqliteHelper.deleteExceptionByItemId(planDetail.getItemId());
			}
			planDetail.setResult(result.getText().toString());
			planDetail.setMemo(memo.getText().toString());
			planDetail.setHandleAdvice(handleAdvice.getText().toString());
			planDetail.setStatus("2");
			planDetail.setLogging(etJilu.getText().toString());
			String date = "";
			if (Constants.GPS_TIME.length() > 0) {
				date = Constants.GPS_TIME;
			} else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format.format(new Date());
			}
			planDetail.setUpdateTime(date);
			sqliteHelper.updateDetailPlanByItemId(planDetail);
			dismiss();

			boolean isNotice = false;
			if (!originResult.equals(planDetail.getResult())) {
				isNotice = true;
				Constants.showDialogCancelable(context);
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						sendRefreshMsg(0);
					}
				}, 100);
			} else {
				sendRefreshMsg(1);
			}
			if (!originDescription.equals(planDetail.getMemo()) || !originAdvice.equals(planDetail.getHandleAdvice())) {
				isNotice = true;
			}
			final boolean canNotice = isNotice;
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					doWithFinish(isGoNext, canNotice);
				}
			}, 200);
		}
	}

	private void sendRefreshMsg(int what) {
		Message msg = CheckItemActivity.refreshHandler.obtainMessage();
		msg.what = what;
		msg.sendToTarget();
	}

	private void doWithFinish(boolean isGoNext, boolean canNotice) {
		Constants.dismissDialog();
		if (!Constants.checkNetWork(context) && canNotice) {
			Constants.showToast(context, "网络不给力，数据已转至后台上传");
		}
		if (isGoNext) {
			Message msg2 = CheckItemActivity.nextHandler.obtainMessage();
			msg2.what = 0;
			msg2.sendToTarget();
		}
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			try {
				double x = Double.parseDouble(result.getText().toString());
				// if (x <= upValue && x >= downValue && !isSpinnerVisible ||
				// isSpinnerVisible && status.getSelectedItemId() == 1 ||
				// isSpinnerVisible && status.getSelectedItemId() == 0 && x <=
				// upValue
				// && x >= downValue) {
				if(status.getSelectedItem().toString().equals("启用")){

				if (x <= upValue && x >= downValue) {
					isResultException = false;
					result.setBackgroundResource(R.drawable.bg_edittext);
				} else {
					result.setBackgroundColor(context.getResources().getColor(R.color.red));
					isResultException = true;
				}
				}else{
					isResultException = false;
					result.setBackgroundResource(R.drawable.bg_edittext);
				}

			} catch (Exception e) {
				e.getStackTrace();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	private class UploadWorkHandler extends Handler {
		private UploadException uploadException;

		public UploadWorkHandler(UploadException uploadException) {
			this.uploadException = uploadException;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_FAILER:
				Log.d("CheckItemTypeInputDialog", "数据上传失败啦");
				break;

			case HttpRequest.REQUEST_SUCCESS:
				String result = msg.obj.toString();
				try {
					JSONObject object = new JSONObject(result);
					String workId = object.getString("workId");
					String historyId = object.getString("historyId");

					planDetail.setWorkId(workId);
					// sqliteHelper.insertCheckResult(planDetail);
					sqliteHelper.updateDetailPlanByItemId(planDetail);
					// 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
					uploadException.setWorkId(workId);
					uploadException.setHistoryId(historyId);
					sqliteHelper.updetaException(uploadException);
					Log.d("CheckItemTypeInputDialog", "数据上传成功啦");
					// 请求服务器处理此条上报异常数据
					http.requestHandleWork(new HandleWorkHandler(uploadException), workId, user.getUserId());

					// 更新此条上报异常数据对应的图片信息
					Picture picture = sqliteHelper.getPicByTypeOfId(uploadException.getTime());
					if (picture != null) {
						picture.setTypeOfId(historyId);
						sqliteHelper.updatePic(picture);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
		}
	}

	private class HandleWorkHandler extends Handler {
		private UploadException uploadException;

		public HandleWorkHandler(UploadException uploadException) {
			this.uploadException = uploadException;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_SUCCESS:
				Log.d("CheckItemTypeInputDialog", "数据操作成功啦");
				String status = msg.obj.toString();
				if (status.equals("success")) {
					uploadException.setIsUploadSuccess("1");
					sqliteHelper.updetaException(uploadException);
				}
				break;

			case HttpRequest.REQUEST_FAILER:
				Log.d("CheckItemTypeInputDialog", "数据操作失败啦");
				break;

			default:
				break;
			}
		}
	}

	public void onResume() {
		unLockClick();
		if (intentFrom.equals("InspectionActivity")) {
			return;
		}
		List<ImageBean> imgData = sqliteHelper.getLocalPics("xj_" + planDetail.getItemId(), planDetail.getPatrolTime());
		if (imgData != null && imgData.size() > 0) {
			takePhoto.setText("查看图片");
			photoNum.setText(imgData.size() + "");
			photoNum.setVisibility(View.VISIBLE);
		} else {
			takePhoto.setText("添图");
			photoNum.setText("");
			photoNum.setVisibility(View.GONE);
		}
	}

}
