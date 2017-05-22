package com.joe.oil.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.CheckItemAdapter;
import com.joe.oil.dialog.CheckItemTypeChooseDialog;
import com.joe.oil.dialog.CheckItemTypeInputDialog;
import com.joe.oil.entity.CheckItem;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Well;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.http.XUtilsHttp;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;
import com.joe.oil.util.PreferencesUtils;
import com.umeng.analytics.MobclickAgent;

@SuppressLint({ "SimpleDateFormat", "HandlerLeak" })
public class CheckItemActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private ImageView back;
	private ListView listview;
	private TextView finish;
	private TextView title;
	private TextView tv_notice;
	private Context context;
	private CheckItemAdapter cItemAdapter;
	private SqliteHelper sqliteHelper;
	private String currentDeviceId;
	private List<PlanDetail> planDetails; // 该站点的所有巡检项
	private List<PlanDetail> requiredPlanDetails; // 该站点需要巡检填写的所有巡检项
	private List<PlanDetail> notUploadPlandDetails;
	public static Handler refreshHandler;
	private int currentSelectedPosition;
	private String deviceId;
	private OilApplication application;
	private User user;
	private Device currentDevice;
	private Well currentWell;
	private HttpRequest http;
	private String intentFrom;
	private String currentInspection;
	private String patrolTime;
	public static Handler directFinishHandler;
	public static Handler nextHandler;
	private String code;
	private int tagResultCount = 0;
	private SqliteHelperForItem sqliteHelperForItem;

	private CheckItemTypeChooseDialog dialogChoose;
	private CheckItemTypeInputDialog dialogInput;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_checkitem);

		initView();
		initDataSet();
		// modifyDataStatus();
		jumdgeFinish();
		setHandler();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		if (dialogChoose != null && dialogChoose.isShowing()) {
			dialogChoose.onResume();
			return;
		}
		if (dialogInput != null && dialogInput.isShowing()) {
			dialogInput.onResume();
			return;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.checkitem_btn_back);
		listview = (ListView) this.findViewById(R.id.checkitem_listview);
		finish = (TextView) this.findViewById(R.id.checkitem_btn_finish);
		title = (TextView) this.findViewById(R.id.checkitem_title_tv);
		tv_notice = (TextView) this.findViewById(R.id.tv_notice);

		back.setOnClickListener(this);
		listview.setOnItemClickListener(this);
		finish.setOnClickListener(this);
		tv_notice.setVisibility(View.GONE);
	}

	private void initDataSet() {
		context = CheckItemActivity.this;
		http = HttpRequest.getInstance(context);
		sqliteHelper = new SqliteHelper(context);
		application = (OilApplication) getApplication();
		sqliteHelperForItem = new SqliteHelperForItem(context);
		user = application.getUser();
		currentInspection = PreferencesUtils.getString(context, "currentInspection", "station");
		code = getIntent().getStringExtra("code");
		intentFrom = getIntent().getStringExtra("intentFrom");
		if (intentFrom.equals("InspectionActivity")) {
			deviceId = getIntent().getStringExtra("deviceId");
			currentDeviceId = deviceId;
		} else {
			if (currentInspection.equals("station")) {
				currentDeviceId = getIntent().getStringExtra("deviceIds");
				Log.d("CheckItemActivity", "currentDeviceId:    " + currentDeviceId);
			} else {
				currentDeviceId = PreferencesUtils.getString(context, Constants.CURRENT_WELL, "1");
			}
		}
		patrolTime = getIntent().getStringExtra("patrolTime");
		requiredPlanDetails = new ArrayList<PlanDetail>();

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (currentInspection.equals("station")) {
			planDetails = sqliteHelper.getPlanDetailsByDeviceId(currentDeviceId, patrolTime);
			Log.d("tag", planDetails.size() + "");
			currentDevice = sqliteHelper.getDeviceById(currentDeviceId);
			if (planDetails.size() > 0 && planDetails.get(0).getResult() != null && planDetails.get(0).getResult().contains("部分投用")) {
				Log.d("tag", "1111111111111111");
				for (int i = 0; i < planDetails.size(); i++) {
					PlanDetail data = planDetails.get(i);
					requiredPlanDetails.add(data);
				}
			} else {
				for (int i = 0; i < planDetails.size(); i++) {
					Log.d("tag", "2222222222222222");
					PlanDetail data = planDetails.get(i);
					if (data.getIsRequiredToWrite() == null || !data.getIsRequiredToWrite().equals("unRequired")) {
						requiredPlanDetails.add(data);
					}
				}
			}
		} else {
			planDetails = new ArrayList<PlanDetail>();
			List<PlanDetail> pList = new ArrayList<PlanDetail>();
			pList = sqliteHelper.getWellPlanDetailsByDeviceId(currentDeviceId);
			Date now = new Date();
			for (int i = 0; i < pList.size(); i++) {
				PlanDetail data = pList.get(i);
				try {
					Date upTimeDate = format.parse(data.getUpTime());
					Date downTimeDate = format.parse(data.getDownTime());
					if (downTimeDate.before(now) && upTimeDate.after(now)) {
						planDetails.add(data);
						if (data.getIsRequiredToWrite() == null || !data.getIsRequiredToWrite().equals("unRequired")) {
							requiredPlanDetails.add(data);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			currentWell = sqliteHelper.getWellById(currentDeviceId);
		}
		Collections.sort(planDetails);
		if (planDetails.size() > 0) {
			title.setText(planDetails.get(0).getPointName());
		}

		cItemAdapter = new CheckItemAdapter(context, requiredPlanDetails);
		listview.setAdapter(cItemAdapter);
		listview.setDividerHeight(0);
	}

	private void modifyDataStatus() {
		tv_notice.setVisibility(View.VISIBLE);
		requiredPlanDetails.clear();
		Log.d("tag", planDetails.size() + "");
		List<PlanDetail> updateList = new ArrayList<PlanDetail>();
		if (planDetails.size() > 0 && planDetails.get(0).getResult() != null && planDetails.get(0).getResult().contains("部分投用")) {
			for (int i = 0; i < planDetails.size(); i++) {
				Log.d("tag", "++++++++++++++++++");
				PlanDetail data = planDetails.get(i);
				data.setIsRequiredToWrite("unRequired");
				if (data.getStatus().equals("1")) {
					data.setStatus("2");
				}
				updateList.add(data);
				// 添加部分启用数据筛选
				requiredPlanDetails.add(data);
			}
		} else {
			for (int i = 0; i < planDetails.size(); i++) {
				Log.d("tag", "-----------------" + i + planDetails.get(i).getCode());
				PlanDetail data = planDetails.get(i);
				data.setIsRequiredToWrite("required");
				if (data.getResult() == null || data.getResult().equals("")) {
					data.setStatus("1");
				}
				String tag = data.getTag();
				String[] tags = tag.split(",");
				if (tags != null && !tags[0].equals("")) {
					String connectionItemId = tags[0];
					CheckItem checkItem = sqliteHelper.getCheckItemsByItemId(connectionItemId);
					if (checkItem == null) {
						continue;
					}
					String itemCode = checkItem.getCode();
					String planCode = itemCode.substring(0, itemCode.length() - 2);
					/* 一、从数据库中去查寻PlanDetail */
					// PlanDetail planDetail =
					// sqliteHelper.getPlanDetailByCodeAndPatrolTime(planCode,
					// data.getPatrolTime());
					/* 二、从已有数据中去判断得出PlanDetail */
					PlanDetail planDetail = null;
					for (int j = 0; j < planDetails.size(); j++) {
						if (planDetails.get(j).getCode().equals(planCode)) {
							planDetail = planDetails.get(j);
							break;
						}
					}
					if (planDetail == null) {
						requiredPlanDetails.add(data);
						continue;
					}
					if (planDetail.getResult() != null && planDetail.getResult().equals(checkItem.getName())) {
						data.setIsRequiredToWrite("unRequired");
						if (data.getStatus().equals("1")) {
							data.setStatus("2");
						}
						// updateList.add(data);
					} else {
						// data.setIsRequiredToWrite("required");
						requiredPlanDetails.add(data);
						// if (data.getResult() == null ||
						// data.getResult().equals("")) {
						// data.setStatus("1");
						// }
						// updateList.add(data);
					}

					// boolean isPlanDataExist =
					// sqliteHelperForItem.getPlanDetailByXjItemIdAndPatrolTime(connectionItemId,
					// data.getPatrolTime());
					// if (isPlanDataExist) {
					// Log.d("tag", "-----------");
					// data.setIsRequiredToWrite("unRequired");
					// if (data.getStatus().equals("1")) {
					// data.setStatus("2");
					// updateList.add(data);
					// }
					// } else {
					// Log.d("tag", "+++++++++++");
					// data.setIsRequiredToWrite("required");
					// if (data.getResult() == null ||
					// data.getResult().equals(""))
					// {
					// data.setStatus("1");
					// updateList.add(data);
					// }
					// }

				} else {
					requiredPlanDetails.add(data);
				}
				updateList.add(data);
			}
		}
		cItemAdapter.notifyDataSetChanged();
		tv_notice.setVisibility(View.GONE);
		updatePlanDetail(updateList);
	}

	private void updatePlanDetail(final List<PlanDetail> data_list) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < data_list.size(); i++) {
					sqliteHelper.updateDetailPlanByItemId(data_list.get(i));
				}
			}
		}).start();
	}

	private void jumdgeFinish() {
		tagResultCount = 0;
		for (int i = 0; i < planDetails.size(); i++) {
			PlanDetail data = planDetails.get(i);
			if (!data.getStatus().equals("1")) {
				tagResultCount++;
			}
		}
		if (planDetails.size() == tagResultCount && intentFrom.equals("CurrentInspectionDialog")) {
			finish.setVisibility(View.VISIBLE);
		} else {
			finish.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkitem_btn_back:
			this.finish();
			break;

		case R.id.checkitem_btn_finish:
			// currentDevicefinished();
			Intent mIntent = new Intent();
			mIntent.setClass(CheckItemActivity.this, ReadRF.class);
			startActivityForResult(mIntent, 0);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date itemUpTime = sdf.parse(planDetails.get(0).getUpTime());
			long result = new Date().getTime() - itemUpTime.getTime();
			if (result > 0) {
				Constants.showToast(context, "当前巡检已超时，请开始新的巡检");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		switch (resultCode) {
		case RESULT_OK:
			if(data == null) {
				return;
			}
			String rfidData1 = data.getStringExtra("code1");
			// if (sharedPreferences.getString("currentInspection",
			// "station").equals("station")) {
			if (PreferencesUtils.getString(context, "currentInspection", "station").equals("station")) {
				if (currentDevice != null && rfidData1.equals(code)) {
					Constants.showDialog(context);
					doWithFinish();
				} else {
					Constants.showToast(context, "不是当前作业点 ×");
				}
			} else {
				if (currentWell != null && rfidData1.equals(currentWell.getCode())) {
					Constants.showDialog(context);
					doWithFinish();
				} else {
					Constants.showToast(context, "不是当前作业点 ×");
				}
			}
			break;
		default:
			break;
		}
	}

	private void doWithFinish() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (PlanDetail planDetail : planDetails) {
					planDetail.setStatus("3");
				}

				sqliteHelper.updateUploadPlanStatus(planDetails);
				notUploadPlandDetails = sqliteHelper.getNotUploadPlanDetails();
				// http.requestSubmitGasList(uploadHandler,
				// notUploadPlandDetails, "normal", user.getUserId());

				XUtilsHttp.getInstance(context).requestSubmitGasList(uploadHandler, notUploadPlandDetails, "normal", user.getUserId());
				currentDevicefinished();
			}
		}).start();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		currentSelectedPosition = position;
		if (requiredPlanDetails.get(position).getItemType().equals("1")) {
			dialogChoose = new CheckItemTypeChooseDialog(context, requiredPlanDetails.get(position), intentFrom);
			dialogChoose.show();
		} else {
			dialogInput = new CheckItemTypeInputDialog(context, requiredPlanDetails.get(position), intentFrom);
			dialogInput.show();
		}
	}

	@SuppressLint("HandlerLeak")
	private void setHandler() {
		refreshHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					PlanDetail data = requiredPlanDetails.get(currentSelectedPosition);
					List<CheckItem> checkItems = sqliteHelper.getCheckItemChooseValue(data.getCode());
					Log.d("tag", checkItems.size() + " checkItems.size()");
					if (checkItems != null && checkItems.size() > 0) {
						for (int i = 0; i < checkItems.size(); i++) {
							CheckItem checkItem = checkItems.get(i);
							Log.d("tag", checkItem.getItemId() + " checkItem.getItemId()");
							PlanDetail result = sqliteHelper.getDetailPlanByItemId2(checkItem.getItemId());
							if (result != null) {
								modifyDataStatus();
								break;
							}
							if (i == checkItems.size() - 1) {
								cItemAdapter.notifyDataSetChanged();
							}
						}
					} else {
						cItemAdapter.notifyDataSetChanged();
					}
					listview.setSelection(currentSelectedPosition);
					jumdgeFinish();
					break;

				case 1:
					cItemAdapter.notifyDataSetChanged();
					jumdgeFinish();
					break;

				default:
					break;
				}

			}
		};

		nextHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					currentSelectedPosition++;
					if (currentSelectedPosition > requiredPlanDetails.size() - 1) {
						currentSelectedPosition = requiredPlanDetails.size() - 1;
						Constants.showToast(context, "已是最后一项 √");
					} else {
						if (requiredPlanDetails.get(currentSelectedPosition).getItemType().equals("1")) {
							dialogChoose = new CheckItemTypeChooseDialog(context, requiredPlanDetails.get(currentSelectedPosition), intentFrom);
							dialogChoose.show();
						} else if (requiredPlanDetails.get(currentSelectedPosition).getItemType().equals("2")) {
							dialogInput = new CheckItemTypeInputDialog(context, requiredPlanDetails.get(currentSelectedPosition), intentFrom);
							dialogInput.show();
						}
					}
					break;

				default:
					break;
				}
			}
		};
	}

	private Handler mHandler = new Handler();

	private void currentDevicefinished() {
		int stationCount = 0;
		int wellCount = 0;
		if (currentInspection.equals("station")) {
			// stationCount = sharedPreferences.getInt(Constants.STATION_COUNT,
			// 0);
			stationCount = PreferencesUtils.getInt(context, Constants.STATION_COUNT, 0);
			Log.d("CheckItemActivity", "stationCount:    " + stationCount);
			stationCount++;
		} else {
			// wellCount = sharedPreferences.getInt(Constants.WELL_COUNT, 0);
			wellCount = PreferencesUtils.getInt(context, Constants.WELL_COUNT, 0);
			wellCount++;
		}

		// if (currentInspection.equals("station")) {
		// // editor.remove(Constants.CURRENT_STATION);
		// if (stationCount >= deviceIds.length) {
		// // editor.putString(Constants.CURRENT_STATION, "-1");
		// PreferencesUtils.putString(context, Constants.CURRENT_STATION, "-1");
		// stationCount = 0;
		// } else {
		// // editor.putString(Constants.CURRENT_STATION,
		// deviceIds[stationCount] + "");
		// PreferencesUtils.putString(context, Constants.CURRENT_STATION,
		// deviceIds[stationCount] + "");
		// Log.d("CheckItemActivity",
		// "editor.putString(Constants.CURRENT_STATION:    " + stationCount);
		// }
		// // editor.remove(Constants.STATION_COUNT);
		// // editor.putInt(Constants.STATION_COUNT, stationCount);
		// PreferencesUtils.putInt(context, Constants.STATION_COUNT,
		// stationCount);
		//
		// } else {
		// editor.remove(Constants.CURRENT_WELL);
		// if (wellCount >= deviceIds.length) {
		// // editor.putString(Constants.CURRENT_WELL, "-1");
		// PreferencesUtils.putString(context, Constants.CURRENT_WELL, "-1");
		// wellCount = 0;
		// } else {
		// // editor.putString(Constants.CURRENT_WELL, deviceIds[wellCount] +
		// "");
		// PreferencesUtils.putString(context, Constants.CURRENT_WELL,
		// deviceIds[wellCount] + "");
		// }
		// // editor.remove(Constants.WELL_COUNT);
		// // editor.putInt(Constants.WELL_COUNT, wellCount);
		// PreferencesUtils.putInt(context, Constants.CURRENT_WELL, wellCount);
		// }
		Message msg = InspectionActivity.refreshHandler.obtainMessage();
		msg.sendToTarget();
		Constants.dismissDialog();
		CheckItemActivity.this.finish();
	}

	private Handler uploadHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case HttpRequest.REQUEST_FAILER:
				// Constants.dismissDialog();
				// Constants.showToast(context, "巡检数据上传失败，已保存到本地！");
				// currentDevicefinished();
				Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
				msg2.sendToTarget();
				// CheckItemActivity.this.finish();
				break;

			case HttpRequest.REQUEST_SUCCESS:
				if (msg.obj.toString().contains("can't")) {
					Constants.showToast(context, "当前巡检计划已过期，请联系管理员更新！");
				} else {
					for (int i = 0; i < notUploadPlandDetails.size(); i++) {
						notUploadPlandDetails.get(i).setStatus("4");
					}
					sqliteHelper.updateUploadPlanStatus(notUploadPlandDetails);
					Message msg3 = InspectionActivity.refreshHandler.obtainMessage();
					msg3.sendToTarget();
				}
				break;

			default:
				break;
			}
		}
	};
}
