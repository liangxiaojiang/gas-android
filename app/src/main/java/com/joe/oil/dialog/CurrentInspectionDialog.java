package com.joe.oil.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.CheckItemActivity;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.Well;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

@SuppressLint("SimpleDateFormat")
public class CurrentInspectionDialog extends Dialog implements OnClickListener {

	private static final String TAG = "CurrentInspectionDialog";

	private TextView confirm;
	private TextView cancel;
	private TextView name;
	private Context context;
	private List<Integer> deviceIs;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private String currentInspection;
	private Device currentDevice;
	private Well currentWell;
	private String patrolTime;
	private String officeId;
	private SqliteHelper sqliteHelper;
	private List<PlanDetail> planDetails;
	private String pointId;
	private String code;

	public CurrentInspectionDialog(Context context, Device currentDevice, Well currentWell, List<Integer> deviceIds, String pointId, String code, String patrolTime, String officeId) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_current_inspection);
		this.context = context;
		this.currentDevice = currentDevice;
		this.currentWell = currentWell;
		this.deviceIs = deviceIds;
		this.patrolTime = patrolTime;
		this.pointId = pointId;
		this.code = code;
		this.officeId = officeId;
		initView();
		initMembers();
		setData();
	}

	private void initView() {
		confirm = (TextView) this.findViewById(R.id.dialog_inspection_confirm);
		cancel = (TextView) this.findViewById(R.id.dialog_inspection_cancel);
		name = (TextView) this.findViewById(R.id.dialog_inspection_name);

		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}

	private void initMembers() {
		sqliteHelper = new SqliteHelper(context);
		sharedPreferences = context.getSharedPreferences("oil", 0);
		editor = sharedPreferences.edit();
		currentInspection = sharedPreferences.getString("currentInspection", "station");
		// if (currentInspection.equals("station")) {
		// editor.remove(Constants.CURRENT_STATION);
		// editor.putString(Constants.CURRENT_STATION,
		// currentDevice.getDeviceId() + "");
		// } else {
		// editor.remove(Constants.CURRENT_WELL);
		// editor.putString(Constants.CURRENT_WELL, currentWell.getWellId() +
		// "");
		// }
		// editor.commit();
	}

	private void setData() {
		// if (currentInspection.equals("station") &&
		// currentDevice.getName().equals("大门口")) {
		// name.setText("集气站名称：" + currentDevice.getOfficeName() + "_" +
		// currentDevice.getName() + "\n" + "投产日期：" + currentDevice.getTchDate()
		// + "\n" + "单井数量："
		// + currentDevice.getDjNum() + "\n" + "配产：" + currentDevice.getPch() +
		// "\n" + "压缩机台数：" + currentDevice.getYsjNum() + "\n" + "分离器台数：" +
		// currentDevice.getFlqNum()
		// + "\n" + "脱水撬台数：" + currentDevice.getTshqNum() + "\n" + "数字化撬台数：" +
		// currentDevice.getShzhqNum() + "\n" + "发电机台数：" +
		// currentDevice.getFdjNum() + "\n" + currentDevice.getMemo()); ;
		// } else if (currentInspection.equals("singlewell")) {
		// name.setText("单井名称：" + currentWell.getName() + "\n" + "投产时间：" +
		// currentWell.getTchDate() + "\n" + "生产层位：" + currentWell.getSchcw() +
		// "\n" + "无阻流量："
		// + currentWell.getWzll() + "\n" + "投产前油压：" + currentWell.getTchqYy() +
		// "\n" + "投产前套压：" + currentWell.getTchqTy() + "\n" + "硫化氢含量：" +
		// currentWell.getLhqhl()
		// + "\n" + "配产：" + currentWell.getPch());
		// planDetails =
		// sqliteHelper.getWellPlanDetailsByDeviceId(currentWell.getWellId() +
		// "");
		// } else {
		// name.setTextSize(18);
		// currentDevice = sqliteHelper.getDeviceById(pointId);
		// name.setText("巡检点名称：" + currentDevice.getName() + "\n" +
		// currentDevice.getMemo());
		// }

		if (currentInspection.equals("station")) {
			currentDevice = sqliteHelper.getDeviceById(pointId);
			if (!currentDevice.getTchDate().equals("")) {
				name.setText("集气站名称：" + currentDevice.getOfficeName() + "_" + currentDevice.getName() + "\n" + "投产日期：" + currentDevice.getTchDate() + "\n" + "单井数量：" + currentDevice.getDjNum() + "\n"
						+ "配产：" + currentDevice.getPch() + "\n" + "压缩机台数：" + currentDevice.getYsjNum() + "\n" + "分离器台数：" + currentDevice.getFlqNum() + "\n" + "脱水撬台数：" + currentDevice.getTshqNum()
						+ "\n" + "数字化撬台数：" + currentDevice.getShzhqNum() + "\n" + "发电机台数：" + currentDevice.getFdjNum() + "\n\n" + currentDevice.getMemo());
			} else {
				name.setTextSize(18);
				name.setText("巡检点名称：" + currentDevice.getName() + "\n\n" + currentDevice.getMemo());
			}
		} else {
			name.setText("单井名称：" + currentWell.getName() + "\n" + "投产时间：" + currentWell.getTchDate() + "\n" + "生产层位：" + currentWell.getSchcw() + "\n" + "无阻流量：" + currentWell.getWzll() + "\n"
					+ "投产前油压：" + currentWell.getTchqYy() + "\n" + "投产前套压：" + currentWell.getTchqTy() + "\n" + "硫化氢含量：" + currentWell.getLhqhl() + "\n" + "配产：" + currentWell.getPch());
			planDetails = sqliteHelper.getWellPlanDetailsByDeviceId(currentWell.getWellId() + "");
			Log.d(TAG, "id = " + currentWell.getWellId() + " size = " + planDetails.size() + " code " + Constants.CURRENT_WELL_CODE);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_inspection_cancel:
			dismiss();
			break;

		case R.id.dialog_inspection_confirm:
			if (currentInspection.equals("singlewell")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				if (planDetails.size() > 0) {
					try {
						Date upTime = sdf.parse(planDetails.get(0).getUpTime());
						Date now = sdf.parse(Constants.GPS_TIME);
						Date downTime = sdf.parse(planDetails.get(0).getDownTime());
						if (!now.after(downTime) && now.before(upTime)) {
							Constants.showToast(context, "当前单井没有到巡检时间");
						} else {
							Intent intent = new Intent(context, CheckItemActivity.class);
							intent.putExtra("intentFrom", "CurrentInspectionDialog");
							int[] deviceId = new int[deviceIs.size()];
							for (int i = 0; i < deviceIs.size(); i++) {
								deviceId[i] = deviceIs.get(i);
							}
							intent.putExtra("deviceIds", deviceId);
							intent.putExtra("patrolTime", patrolTime);
							context.startActivity(intent);
							recordData();
							dismiss();
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else {
					Constants.showToast(context, "不是当前站点的卡");
				}

			} else {
				Intent intent = new Intent(context, CheckItemActivity.class);
				intent.putExtra("intentFrom", "CurrentInspectionDialog");
				// int[] deviceId = new int[deviceIs.size()];
				// for (int i = 0; i < deviceIs.size(); i++) {
				// deviceId[i] = deviceIs.get(i);
				// }
				intent.putExtra("deviceIds", pointId);
				intent.putExtra("patrolTime", patrolTime);
				intent.putExtra("code", code);
				context.startActivity(intent);
				recordData();
				dismiss();
			}

			break;

		default:
			break;
		}
	}

	private void recordData() {
		if (currentInspection.equals("station")) {
			// editor.remove(Constants.CURRENT_STATION);
			editor.putString(Constants.CURRENT_STATION, currentDevice.getDeviceId() + "");
			editor.putString(Constants.CURRENT_PATROL_TIME, patrolTime);
			editor.putString(Constants.CURRENT_OFFICEID, officeId);
		} else {
			// editor.remove(Constants.CURRENT_WELL);
			editor.putString(Constants.CURRENT_WELL, currentWell.getWellId() + "");
			editor.putString(Constants.CURRENT_PATROL_TIME, patrolTime);
			editor.putString(Constants.CURRENT_OFFICEID, officeId);
		}
		editor.commit();
	}

}
