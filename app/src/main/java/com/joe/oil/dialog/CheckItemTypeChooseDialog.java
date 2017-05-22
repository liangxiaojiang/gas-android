package com.joe.oil.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.joe.oil.R;
import com.joe.oil.activity.CheckItemActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.activity.PicSelectedEnsureActivity;
import com.joe.oil.entity.*;
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
public class CheckItemTypeChooseDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView takePhoto; // 拍照
	private TextView photograph; // 录像
	private TextView confirm;//确定
	private TextView edit;
	private TextView next;
	private TextView deviceName; // 巡检点名称
	private TextView itemName; // 巡检内容
	private Spinner spinner; // 巡检标准
	private EditText exception;
	private EditText solve;
	private EditText etJilu;//比原代码多了这行
	private TextView photoNum;
	private RelativeLayout takePhoto_rl;

	private TextView tvTips;

	private Context context;
	private PlanDetail planDetail;
	private String[] stringSpinner;
	private SqliteHelper sqliteHelper;
	private List<CheckItem> checkChooseValue;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String result;
	private String memo;
	private String handleAdvice;
	private String intentFrom;
	private HttpRequest http;
	private OilApplication application;
	private User user;
	private String curTotalPicId = "";
	private UploadException exceptionPlanDetail;
	private String originResult = ""; // dialog生成时，planDetail的初始result
	private String originDescription = "";
	private String originAdvice = "";
	private Handler mHandler = new Handler();

	private final int TAG_EDIT = 0; // 将右上角编辑按钮标记为编辑状态
	private final int TAG_CANCEL = 1; // 将右上角编辑按钮标记为取消编辑状态
	
	private final String NO_DATA = "无选择项";

	public CheckItemTypeChooseDialog(Context context, PlanDetail planDetail, String intentFrom) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_check_type_choose);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.context = context;
		this.planDetail = planDetail;
		this.intentFrom = intentFrom;

		initView();
		initMembers();
		initData();
	}

	private void initView() {
		takePhoto = (TextView) this.findViewById(R.id.dialog_check_type_choose_photo);
		tvTips = (TextView) this.findViewById(R.id.dialog_check_type_input_tips);
		photograph = (TextView) this.findViewById(R.id.dialog_check_type_choose_movie);
		confirm = (TextView) this.findViewById(R.id.dialog_check_type_choose_confirm);
		next = (TextView) this.findViewById(R.id.dialog_check_type_choose_next);
		edit = (TextView) this.findViewById(R.id.dialog_check_type_choose_edit);
		deviceName = (TextView) this.findViewById(R.id.dialog_check_type_choose_station_name);
		itemName = (TextView) this.findViewById(R.id.dialog_check_type_choose_check_name);
		spinner = (Spinner) this.findViewById(R.id.dialog_check_type_choose_spinner);
		exception = (EditText) this.findViewById(R.id.dialog_check_type_choose_exception);
		solve = (EditText) this.findViewById(R.id.dialog_check_type_choose_solve);
		etJilu = (EditText) this.findViewById(R.id.dialog_check_type_choose_jilu);
		photoNum = (TextView) this.findViewById(R.id.dialog_check_type_choose_photo_num);
		takePhoto_rl = (RelativeLayout) this.findViewById(R.id.dialog_check_type_choose_photo_rl);

		takePhoto_rl.setOnClickListener(this);
		photograph.setOnClickListener(this);
		confirm.setOnClickListener(this);
		next.setOnClickListener(this);
		edit.setOnClickListener(this);
		edit.setVisibility(View.GONE);
		edit.setTag(TAG_EDIT);
		photoNum.setText("");
		photoNum.setVisibility(View.GONE);
	}

	private void initMembers() {
		sqliteHelper = new SqliteHelper(context);
		http = HttpRequest.getInstance(context);
		application = (OilApplication) ((Activity) context).getApplication();
		user = application.getUser();
		sharedPreferences = context.getSharedPreferences("oil", 0);
		editor = sharedPreferences.edit();
		checkChooseValue = sqliteHelper.getCheckItemChooseValue(planDetail.getCode());
		if(checkChooseValue.size() == 0) {
			stringSpinner = new String[1];
			stringSpinner[0] = NO_DATA;
		} else {
			stringSpinner = new String[checkChooseValue.size()];
			for (int i = 0; i < checkChooseValue.size(); i++) {
				stringSpinner[i] = checkChooseValue.get(i).getName();
			}
		}
	}

	private void initData() {
		exceptionPlanDetail = sqliteHelper.getExceptionOfPlanDetail(planDetail.getItemId());
		deviceName.setText("巡检点名： " + planDetail.getPointName());
		itemName.setText("巡检内容： " + planDetail.getItemName());
		ArrayAdapter<String> adapterChoose = new ArrayAdapter<String>(context, R.layout.item_spinner, stringSpinner);
		adapterChoose.setDropDownViewResource(R.layout.item_spinner_dropdown);
		spinner.setAdapter(adapterChoose);
		exception.setText(planDetail.getMemo());
		solve.setText(planDetail.getHandleAdvice());
		exception.setText(planDetail.getMemo());
		solve.setText(planDetail.getHandleAdvice());

		Log.d("tips", planDetail.getTips() + "");
		if (planDetail.getType().equals("2") && !planDetail.getTips().equals("null")) {
			tvTips.setVisibility(View.VISIBLE);
			tvTips.setText(planDetail.getTips());
		}else {
			tvTips.setVisibility(View.GONE);
		}
		if (planDetail.getResult() != null) {
			originResult = planDetail.getResult();
			originAdvice = planDetail.getHandleAdvice();
			originDescription = planDetail.getMemo();
			for (int i = 0; i < stringSpinner.length; i++) {
				if (planDetail.getResult().equals(stringSpinner[i])) {
					spinner.setSelection(i);
				}
			}
		}
		// 以下代码用于分条件控制界面的可编辑性
		if (intentFrom.equals("InspectionActivity")) { // 表示只允许浏览
			edit.setVisibility(View.GONE);
			exception.setEnabled(false);
			spinner.setEnabled(false);
			solve.setEnabled(false);
			exception.setTextColor(context.getResources().getColor(R.color.white));
			solve.setTextColor(context.getResources().getColor(R.color.white));
			exception.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			spinner.setBackgroundResource(R.drawable.arrow);
			solve.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			checkPicSelectStatus(planDetail.getItemId());
		} else {
			if (exceptionPlanDetail != null && planDetail.getResult() != null && planDetail.getExceptionStatus().equals("2")) {
				exception.setEnabled(false);
				spinner.setEnabled(false);
				solve.setEnabled(false);
				edit.setTag(TAG_EDIT);
				edit.setText("编辑");
				edit.setVisibility(View.VISIBLE);
				edit.setTextColor(context.getResources().getColor(R.color.green));
				exception.setTextColor(context.getResources().getColor(R.color.white));
				solve.setTextColor(context.getResources().getColor(R.color.white));
				exception.setBackgroundResource(R.drawable.bg_edittext_uninputable);
				spinner.setBackgroundResource(R.drawable.arrow);
				solve.setBackgroundResource(R.drawable.bg_edittext_uninputable);
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
		case R.id.dialog_check_type_choose_confirm:
			lockClick();
			Log.d("lx","==============进入================");
			doWithComplate(false);//这行报错7.26
			Log.v("lxj","==================");
			break;

		case R.id.dialog_check_type_choose_next:
			lockClick();
			doWithComplate(true);
			break;

		case R.id.dialog_check_type_choose_photo_rl:
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

		case R.id.dialog_check_type_choose_edit:
			if ((Integer) edit.getTag() == TAG_EDIT) {
				exception.setEnabled(true);
				spinner.setEnabled(true);
				solve.setEnabled(true);
				takePhoto_rl.setEnabled(true);
				edit.setText("恢复");
				edit.setTag(TAG_CANCEL);
				edit.setTextColor(context.getResources().getColor(R.color.white));
				exception.setTextColor(context.getResources().getColor(R.color.black));
				solve.setTextColor(context.getResources().getColor(R.color.black));
				exception.setBackgroundResource(R.drawable.bg_edittext);
				spinner.setBackgroundResource(R.drawable.arrow);
				solve.setBackgroundResource(R.drawable.bg_edittext);
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
		// 判断处理新的内容编辑
		if (planDetail.getPointName().equals("大门口") && spinner.getSelectedItemId() == 0) {
			Constants.YSJ_STATUS = "0";
			editor.putString("YSJ_STATUS", Constants.YSJ_STATUS);
			editor.commit();
		} else if (planDetail.getPointName().equals("大门口") && spinner.getSelectedItemId() == 1) {
			Constants.YSJ_STATUS = "1";
			editor.putString("YSJ_STATUS", Constants.YSJ_STATUS);
			editor.commit();
		}
		result = spinner.getSelectedItem().toString();
		if(result.equals(NO_DATA)) {
			result = "";
		}
		memo = exception.getText().toString();
		handleAdvice = solve.getText().toString();

		if (memo == null || memo.length() <= 0 && handleAdvice.length() > 0) {
			Constants.showToast(context, "请输入异常现象");
			unLockClick();
		} else {
			planDetail.setResult(result);

			if (memo.length() <= 0 || memo.equals("")) {
				planDetail.setExceptionStatus("1");
				sqliteHelper.deleteExceptionByItemId(planDetail.getItemId());
			} else {
				planDetail.setExceptionStatus("2");
				curTotalPicId = "";
				List<ImageBean> imgData = sqliteHelper.getLocalPics("xj_" + planDetail.getItemId(), planDetail.getPatrolTime());
				if (imgData != null && imgData.size() > 0) {
					for (int i = 0; i < imgData.size(); i++) {
						curTotalPicId += ",";
					}
				}
				Log.d("CheckItemTypeChooseDialog", curTotalPicId);
				UploadException uploadException = new UploadException();
				uploadException.setPointName(planDetail.getPointName());
				uploadException.setTime(DateUtils.getDateTime());
				uploadException.setDeviceName(planDetail.getOfficeName());
				uploadException.setDeviceCode(planDetail.getCode().substring(0, 8));
				uploadException.setWorkTypeId("22");
				uploadException.setWorkTypeName("");
				uploadException.setResult(result);
				uploadException.setTreatmentAdvice(handleAdvice);
				uploadException.setItemId(planDetail.getItemId());
				uploadException.setUserId(user.getUserId());
				uploadException.setDescription(memo);
				uploadException.setIsUploadSuccess("0");
				uploadException.setPicId(curTotalPicId);
				uploadException.setWorkId("");
				uploadException.setFromWhere("1");
				uploadException.setHistoryId("");
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
					if (!local_data.getFromWhere().equals("1")) { // 如果本地记录数据已和网络数据衔接，继续标记为已衔接
						uploadException.setFromWhere("3");
						Log.d("KKKKKKKKKKKKKKKK", " -----------------++++++++++++++++++++");
					}
					// 先保留之前异常数据的picId字段值
					uploadException.setPicId(local_data.getPicId());
					sqliteHelper.updetaExceptionOfPlanDetail(uploadException);
				}
				String lat=application.getLat();
				String lng=application.getLng();

				String uploadType="";

				// 向服务器提交该条异常数据
				http.requestUploadWork(new UploadWorkHandler(uploadException), uploadException.getDeviceName() + uploadException.getPointName(), planDetail.getCode().substring(0, 8), "22",
						user.getUserId(), "4", curTotalPicId, memo,lat,lng,uploadType);
			}
			planDetail.setMemo(memo);
			planDetail.setHandleAdvice(handleAdvice);
			planDetail.setStatus("2");
			//这是巡检记录，得到用户输入的数据
			planDetail.setLogging(etJilu.getText().toString());
			String date = "";
			if (Constants.GPS_TIME.length() > 0) {
				date = Constants.GPS_TIME;
			} else {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format.format(new Date());
			}
			planDetail.setUpdateTime(date);
			sqliteHelper.updateDetailPlanByItemId(planDetail);//7.26测试这行报错
			dismiss();
			boolean isNotice = false;
			if (!originResult.equals(result)) {
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

			if (!originDescription.equals(memo) || !originAdvice.equals(handleAdvice)) {
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
//		dismiss();
		if (isGoNext) {
			Message msg2 = CheckItemActivity.nextHandler.obtainMessage();
			msg2.what = 0;
			msg2.sendToTarget();
		}
	}

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
				Log.d("CheckItemTypeChooseDialog", "数据上传失败啦");
				break;

			case HttpRequest.REQUEST_SUCCESS:
				String result = msg.obj.toString();
				try {
					JSONObject object = new JSONObject(result);
					String workId = object.getString("workId");
					String historyId = object.getString("historyId");

					planDetail.setWorkId(workId);
					sqliteHelper.updateDetailPlanByItemId(planDetail);
					// 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
					uploadException.setWorkId(workId);
					uploadException.setHistoryId(historyId);
					sqliteHelper.updetaException(uploadException);
					Log.d("CheckItemTypeChooseDialog", "数据上传成功啦");
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
				Log.d("CheckItemTypeChooseDialog", "数据操作成功啦");
				String status = msg.obj.toString();
				if (status.equals("success")) {
					uploadException.setIsUploadSuccess("1");
					sqliteHelper.updetaException(uploadException);
				}
				break;

			case HttpRequest.REQUEST_FAILER:
				Log.d("CheckItemTypeChooseDialog", "数据操作失败啦");
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
/*
package com.joe.oil.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import com.joe.oil.R;
import com.joe.oil.activity.CheckItemActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.activity.PicSelectedEnsureActivity;
import com.joe.oil.entity.*;
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
public class CheckItemTypeChooseDialog extends Dialog implements android.view.View.OnClickListener {

	private TextView takePhoto; // 拍照
	private TextView photograph; // 录像
	private TextView confirm;//确认
	private TextView edit;
	private TextView next;//下一项
	private TextView deviceName; // 巡检点名称
	private TextView itemName; // 巡检内容
	private Spinner spinner; // 巡检标准
	//巡检标准和原代码不同，原代码是private TextView standard;
	private EditText exception;
	private EditText solve;
	private TextView photoNum;
	private RelativeLayout takePhoto_rl;

	private TextView tvTips;

	private Context context;
	private PlanDetail planDetail;
	private String[] stringSpinner;
	private SqliteHelper sqliteHelper;
	private List<CheckItem> checkChooseValue;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String result;
	private String memo;//异常描述
	private String handleAdvice;//处理意见
	private String intentFrom;
	private HttpRequest http;
	private OilApplication application;
	private User user;
	private String curTotalPicId = "";
	private UploadException exceptionPlanDetail;
	private String originResult = ""; // dialog生成时，planDetail的初始result
	private String originDescription = "";
	private String originAdvice = "";
	private Handler mHandler = new Handler();

	private final int TAG_EDIT = 0; // 将右上角编辑按钮标记为编辑状态
	private final int TAG_CANCEL = 1; // 将右上角编辑按钮标记为取消编辑状态

	private final String NO_DATA = "无选择项";

	public CheckItemTypeChooseDialog(Context context, PlanDetail planDetail, String intentFrom) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_check_type_choose);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		this.context = context;
		this.planDetail = planDetail;
		this.intentFrom = intentFrom;

		initView();
		initMembers();
		initData();
	}

	private void initView() {
		takePhoto = (TextView) this.findViewById(R.id.dialog_check_type_choose_photo);
		tvTips = (TextView) this.findViewById(R.id.dialog_check_type_input_tips);
		photograph = (TextView) this.findViewById(R.id.dialog_check_type_choose_movie);
		confirm = (TextView) this.findViewById(R.id.dialog_check_type_choose_confirm);
		next = (TextView) this.findViewById(R.id.dialog_check_type_choose_next);
		edit = (TextView) this.findViewById(R.id.dialog_check_type_choose_edit);
		deviceName = (TextView) this.findViewById(R.id.dialog_check_type_choose_station_name);
		itemName = (TextView) this.findViewById(R.id.dialog_check_type_choose_check_name);
		spinner = (Spinner) this.findViewById(R.id.dialog_check_type_choose_spinner);
		exception = (EditText) this.findViewById(R.id.dialog_check_type_choose_exception);
		solve = (EditText) this.findViewById(R.id.dialog_check_type_choose_solve);
		photoNum = (TextView) this.findViewById(R.id.dialog_check_type_choose_photo_num);
		takePhoto_rl = (RelativeLayout) this.findViewById(R.id.dialog_check_type_choose_photo_rl);

		takePhoto_rl.setOnClickListener(this);
		photograph.setOnClickListener(this);
		confirm.setOnClickListener(this);
		next.setOnClickListener(this);
		edit.setOnClickListener(this);
		edit.setVisibility(View.GONE);
		edit.setTag(TAG_EDIT);
		photoNum.setText("");
		photoNum.setVisibility(View.GONE);
	}

	private void initMembers() {
		sqliteHelper = new SqliteHelper(context);
		http = HttpRequest.getInstance(context);
		application = (OilApplication) ((Activity) context).getApplication();
		user = application.getUser();
		sharedPreferences = context.getSharedPreferences("oil", 0);
		editor = sharedPreferences.edit();
		checkChooseValue = sqliteHelper.getCheckItemChooseValue(planDetail.getCode());
		if(checkChooseValue.size() == 0) {
			stringSpinner = new String[1];
			stringSpinner[0] = NO_DATA;
		} else {
			stringSpinner = new String[checkChooseValue.size()];
			for (int i = 0; i < checkChooseValue.size(); i++) {
				stringSpinner[i] = checkChooseValue.get(i).getName();
			}
		}
	}

	private void initData() {
		exceptionPlanDetail = sqliteHelper.getExceptionOfPlanDetail(planDetail.getItemId());
		deviceName.setText("巡检点名： " + planDetail.getPointName());
		itemName.setText("巡检内容： " + planDetail.getItemName());
		ArrayAdapter<String> adapterChoose = new ArrayAdapter<String>(context, R.layout.item_spinner, stringSpinner);
		adapterChoose.setDropDownViewResource(R.layout.item_spinner_dropdown);
		spinner.setAdapter(adapterChoose);
		exception.setText(planDetail.getMemo());
		solve.setText(planDetail.getHandleAdvice());
		exception.setText(planDetail.getMemo());
		solve.setText(planDetail.getHandleAdvice());

		Log.d("tips", planDetail.getTips() + "");
		if (planDetail.getType().equals("2") && !planDetail.getTips().equals("null")) {
			tvTips.setVisibility(View.VISIBLE);
			tvTips.setText(planDetail.getTips());
		}else {
			tvTips.setVisibility(View.GONE);
		}
		if (planDetail.getResult() != null) {
			originResult = planDetail.getResult();
			originAdvice = planDetail.getHandleAdvice();
			originDescription = planDetail.getMemo();
			for (int i = 0; i < stringSpinner.length; i++) {
				if (planDetail.getResult().equals(stringSpinner[i])) {
					spinner.setSelection(i);
				}
			}
		}
		// 以下代码用于分条件控制界面的可编辑性
		if (intentFrom.equals("InspectionActivity")) { // 表示只允许浏览
			edit.setVisibility(View.GONE);
			exception.setEnabled(false);
			spinner.setEnabled(false);
			solve.setEnabled(false);
			exception.setTextColor(context.getResources().getColor(R.color.white));
			solve.setTextColor(context.getResources().getColor(R.color.white));
			exception.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			spinner.setBackgroundResource(R.drawable.arrow);
			solve.setBackgroundResource(R.drawable.bg_edittext_uninputable);
			checkPicSelectStatus(planDetail.getItemId());
		} else {
			if (exceptionPlanDetail != null && planDetail.getResult() != null && planDetail.getExceptionStatus().equals("2")) {
				exception.setEnabled(false);
				spinner.setEnabled(false);
				solve.setEnabled(false);
				edit.setTag(TAG_EDIT);
				edit.setText("编辑");
				edit.setVisibility(View.VISIBLE);
				edit.setTextColor(context.getResources().getColor(R.color.green));
				exception.setTextColor(context.getResources().getColor(R.color.white));
				solve.setTextColor(context.getResources().getColor(R.color.white));
				exception.setBackgroundResource(R.drawable.bg_edittext_uninputable);
				spinner.setBackgroundResource(R.drawable.arrow);
				solve.setBackgroundResource(R.drawable.bg_edittext_uninputable);
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
			case R.id.dialog_check_type_choose_confirm:
				lockClick();
				doWithComplate(false);
				break;

			case R.id.dialog_check_type_choose_next:
				lockClick();
				doWithComplate(true);
				break;

			case R.id.dialog_check_type_choose_photo_rl:
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

			case R.id.dialog_check_type_choose_edit:
				if ((Integer) edit.getTag() == TAG_EDIT) {
					exception.setEnabled(true);
					spinner.setEnabled(true);
					solve.setEnabled(true);
					takePhoto_rl.setEnabled(true);
					edit.setText("恢复");
					edit.setTag(TAG_CANCEL);
					edit.setTextColor(context.getResources().getColor(R.color.white));
					exception.setTextColor(context.getResources().getColor(R.color.black));
					solve.setTextColor(context.getResources().getColor(R.color.black));
					exception.setBackgroundResource(R.drawable.bg_edittext);
					spinner.setBackgroundResource(R.drawable.arrow);
					solve.setBackgroundResource(R.drawable.bg_edittext);
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
		// 判断处理新的内容编辑
		if (planDetail.getPointName().equals("大门口") && spinner.getSelectedItemId() == 0) {
			Constants.YSJ_STATUS = "0";
			editor.putString("YSJ_STATUS", Constants.YSJ_STATUS);
			editor.commit();
		} else if (planDetail.getPointName().equals("大门口") && spinner.getSelectedItemId() == 1) {
			Constants.YSJ_STATUS = "1";
			editor.putString("YSJ_STATUS", Constants.YSJ_STATUS);
			editor.commit();
		}
		result = spinner.getSelectedItem().toString();
		if(result.equals(NO_DATA)) {
			result = "";
		}
		memo = exception.getText().toString();
		handleAdvice = solve.getText().toString();

		if (memo == null || memo.length() <= 0 && handleAdvice.length() > 0) {
			Constants.showToast(context, "请输入异常现象");
			unLockClick();
		} else {
			planDetail.setResult(result);

			if (memo.length() <= 0 || memo.equals("")) {
				planDetail.setExceptionStatus("1");
				sqliteHelper.deleteExceptionByItemId(planDetail.getItemId());
			} else {
				planDetail.setExceptionStatus("2");
				curTotalPicId = "";
				List<ImageBean> imgData = sqliteHelper.getLocalPics("xj_" + planDetail.getItemId(), planDetail.getPatrolTime());
				if (imgData != null && imgData.size() > 0) {
					for (int i = 0; i < imgData.size(); i++) {
						curTotalPicId += ",";
					}
				}
			Log.d("CheckItemTypeChooseDialog", curTotalPicId);
				UploadException uploadException = new UploadException();
				uploadException.setPointName(planDetail.getPointName());
				uploadException.setTime(DateUtils.getDateTime());
				uploadException.setDeviceName(planDetail.getOfficeName());
				uploadException.setDeviceCode(planDetail.getCode().substring(0, 8));
				uploadException.setWorkTypeId("22");
				uploadException.setWorkTypeName("");
				uploadException.setResult(result);
				uploadException.setTreatmentAdvice(handleAdvice);
				uploadException.setItemId(planDetail.getItemId());
				uploadException.setUserId(user.getUserId());
				uploadException.setDescription(memo);
				uploadException.setIsUploadSuccess("0");
				uploadException.setPicId(curTotalPicId);
				uploadException.setWorkId("");
				uploadException.setFromWhere("1");
				uploadException.setHistoryId("");
				uploadException.setCategory("4");
				uploadException.setPatrolTime(planDetail.getPatrolTime());

				UploadException local_data = sqliteHelper.geteExceptionByItemId(planDetail.getItemId());
				if (local_data == null) {
					// 向数据库中插入保存该条异常数据
					sqliteHelper.insertException(uploadException);
				} else {
					// 向数据库中更新保存该条异常数据
					if (!local_data.getFromWhere().equals("1")) { // 如果本地记录数据已和网络数据衔接，继续标记为已衔接
						uploadException.setFromWhere("3");
					}
					// 先保留之前异常数据的picId字段值
					uploadException.setPicId(local_data.getPicId());
					sqliteHelper.updetaExceptionOfPlanDetail(uploadException);
				}
				// 向服务器提交该条异常数据
				http.requestUploadWork(new UploadWorkHandler(uploadException), uploadException.getDeviceName() + uploadException.getPointName(), planDetail.getCode().substring(0, 8), "22",
						user.getUserId(), "4", curTotalPicId, memo);
			}
			planDetail.setMemo(memo);
			planDetail.setHandleAdvice(handleAdvice);
			planDetail.setStatus("2");
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
			if (!originResult.equals(result)) {
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

			if (!originDescription.equals(memo) || !originAdvice.equals(handleAdvice)) {
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
//		dismiss();
		if (isGoNext) {
			Message msg2 = CheckItemActivity.nextHandler.obtainMessage();
			msg2.what = 0;
			msg2.sendToTarget();
		}
	}

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
					Log.d("CheckItemTypeChooseDialog", "数据上传失败啦");
					break;

				case HttpRequest.REQUEST_SUCCESS:
					String result = msg.obj.toString();
					try {
						JSONObject object = new JSONObject(result);
						String workId = object.getString("workId");
						String historyId = object.getString("historyId");

						planDetail.setWorkId(workId);
						sqliteHelper.updateDetailPlanByItemId(planDetail);
						// 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
						uploadException.setWorkId(workId);
						uploadException.setHistoryId(historyId);
						sqliteHelper.updetaException(uploadException);
						Log.d("CheckItemTypeChooseDialog", "数据上传成功啦");
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
					Log.d("CheckItemTypeChooseDialog", "数据操作成功啦");
					String status = msg.obj.toString();
					if (status.equals("success")) {
						uploadException.setIsUploadSuccess("1");
						sqliteHelper.updetaException(uploadException);
					}
					break;

				case HttpRequest.REQUEST_FAILER:
					Log.d("CheckItemTypeChooseDialog", "数据操作失败啦");
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
*/