package com.joe.oil.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.ExceptionAdapter;
import com.joe.oil.adapter.NotInspectionAdapter;
import com.joe.oil.dialog.CurrentInspectionDialog;
import com.joe.oil.dialog.ExcpetionHandleDialog;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.UploadException;
import com.joe.oil.entity.Well;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;

/**
 * 集气站巡护主界面
 * 
 * @author Administrator
 *
 */
@SuppressLint({ "InflateParams", "CutPasteId", "HandlerLeak", "SimpleDateFormat" })
public class InspectionActivity extends BaseActivity implements OnClickListener {

	private final String TAG = "InspectionActivity";
	private Context context;
	private ProgressBar pb_loading;
	private ViewPager viewPager;// 页卡内容
	private ImageView imageView;// 动画图片
	private TextView inspection, not, already, exception, notNum, alreadyNum, exceptionNum, title;
	private RelativeLayout notInspection, alreadyInspection;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2, view3, view4;// 各个页卡
	private ImageView back, upload;
	private SqliteHelper sqliteHelper;
	private List<Integer> deviceIds;
	private Device currentDevice;
	private Well currentWell;
	private ListView notListview, alreadyListview, exceptionListview;
	private List<PlanDetail> notInspectons;
	private List<PlanDetail> alreadyInspections; // 已经完成巡检的巡检点
	private List<PlanDetail> tempDetails;
	private List<PlanDetail> canInspection; // 可巡检的巡检点
	private List<PlanDetail> exceptionTasks;
	private ImageView readCard;
	private SharedPreferences spPreferences;
	private Editor editor;
	private TextView shouldRead;
	private TextView shouldRead2;
	public static Handler refreshHandler;
	public static Handler handleExceptionHandler;
	private Handler mHandler;
	private String currentDeviceId;
	private String currentInspection;
	private int tempDetailsPosition;
	private int exceptionPosition = 0;
	private String officeId;
	private OilApplication application;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_inspection);

		// 先初始化控件
		initImageView();
		initTextView();
		initViewPager();
		initInspection();
		initNotView();
		initAlreadyView();
		initExceptionView();
		// 然后初始化数据
		initMembers();
		initData();
		initDeviceIds();
		setData();
		setInspection();
		setNotListView();
		setAlreadyListView();
		setExceptionListView();
		// 设置事件以及网络请求
		setHandler();
		setItemClickListener();
		getNetHistoryException();
	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		views = new ArrayList<View>();
		LayoutInflater inflater = getLayoutInflater();
		view1 = inflater.inflate(R.layout.view_inspection, null);
		view2 = inflater.inflate(R.layout.view_not, null);
		view3 = inflater.inflate(R.layout.view_already, null);
		view4 = inflater.inflate(R.layout.view_exception, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		views.add(view4);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setCurrentItem(0);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化头标
	 */
	private void initTextView() {
		back = (ImageView) findViewById(R.id.inspection_btn_back);
		upload = (ImageView) findViewById(R.id.inspection_btn_upload);
		inspection = (TextView) findViewById(R.id.inspection_inspection);
		not = (TextView) findViewById(R.id.inspection_tv_not);
		already = (TextView) findViewById(R.id.inspection_tv_already);
		exception = (TextView) findViewById(R.id.inspection_tv_exception);
		notNum = (TextView) findViewById(R.id.inspection_not_num);
		alreadyNum = (TextView) findViewById(R.id.inspection_already_num);
		exceptionNum = (TextView) findViewById(R.id.inspection_exception_num);
		notInspection = (RelativeLayout) findViewById(R.id.inspection_not);
		alreadyInspection = (RelativeLayout) findViewById(R.id.inspection_already);
		title = (TextView) findViewById(R.id.inspection_title_tv);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

		inspection.setOnClickListener(new MyOnClickListener(0));
		notInspection.setOnClickListener(new MyOnClickListener(1));
		alreadyInspection.setOnClickListener(new MyOnClickListener(2));
		exception.setOnClickListener(new MyOnClickListener(3));
		back.setOnClickListener(this);
		upload.setOnClickListener(this);
		pb_loading.setVisibility(View.GONE);
	}

	/**
	 * 2 * 初始化动画 3
	 */
	private void initImageView() {
		imageView = (ImageView) findViewById(R.id.cursor);
		bmpW = 40; // 白条宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
	}

	private void initMembers() {
		context = InspectionActivity.this;
		notInspectons = new ArrayList<PlanDetail>();
		alreadyInspections = new ArrayList<PlanDetail>();
		deviceIds = new ArrayList<Integer>();
		tempDetails = new ArrayList<PlanDetail>();
		canInspection = new ArrayList<PlanDetail>();
		sqliteHelper = new SqliteHelper(context);
		application = (OilApplication) getApplication();

		spPreferences = context.getSharedPreferences("oil", 0);
		editor = spPreferences.edit();
		currentInspection = spPreferences.getString("currentInspection", "station");
		tempDetails.clear();
		officeId = getIntent().getStringExtra("officeId");
		if (currentInspection.equals("station")) {
			title.setText("集气站巡护");

		} else {
			title.setText("单井巡护");
		}
	}

	private void initData() {
		SqliteHelperForItem sqliteHelperForItem = new SqliteHelperForItem(context);
		tempDetails.clear();
		if (currentInspection.equals("station")) {
			tempDetails = sqliteHelperForItem.getDetailsBetweenTwoTimeGroupByPointId("1", officeId);
		} else {
			tempDetails = sqliteHelperForItem.getDetailsBetweenTwoTimeGroupByPointId("2", officeId);
		}
		notInspectons.clear();
		alreadyInspections.clear();
		canInspection.clear();
		// Collections.sort(tempDetails, new Comparator<PlanDetail>() {
		// @Override
		// public int compare(PlanDetail lhs, PlanDetail rhs) {
		// return lhs.getSort().compareTo(rhs.getSort());
		// }
		// });
		for (int i = 0; i < tempDetails.size(); i++) {
			PlanDetail data = tempDetails.get(i);
			if (data.getStatus().equals("3") || data.getStatus().equals("4")) {
				alreadyInspections.add(data);
			} else {
				notInspectons.add(data);
			}
			canInspection.add(data);
		}
		notNum.setText(notInspectons.size() + "");
		alreadyNum.setText(alreadyInspections.size() + "");
	}

	private void initDeviceIds() {
		for (int i = 0; i < canInspection.size(); i++) {
			deviceIds.add(canInspection.get(i).getPointId());
		}
	}

	private void setData() {
		if (currentInspection.equals("station")) {
			if (deviceIds.size() > 0) {
				currentDeviceId = spPreferences.getString(Constants.CURRENT_STATION, deviceIds.get(0) + "");
			} else {
				currentDeviceId = spPreferences.getString(Constants.CURRENT_STATION, "0");
			}
			int stationCount = spPreferences.getInt(Constants.STATION_COUNT, 0);
			// tempDetailsPosition = stationCount;
			tempDetailsPosition = 0;
		} else {
			if (deviceIds.size() > 0) {
				currentDeviceId = spPreferences.getString(Constants.CURRENT_WELL, deviceIds.get(0) + "");
			} else {
				currentDeviceId = spPreferences.getString(Constants.CURRENT_WELL, "0");
			}
			int wellCount = spPreferences.getInt(Constants.WELL_COUNT, 0);
			// tempDetailsPosition = wellCount;
			tempDetailsPosition = 0;
		}
	}

	/**
	 * 
	 * 头标点击监听 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
			currIndex = i;
		}

		@SuppressLint("NewApi")
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
			handleHeader(index);
		}
	}

	private void handleHeader(int index) {
		switch (index) {
			case 0:
				inspection.setTextColor(getResources().getColor(R.color.blue1));
				not.setTextColor(getResources().getColor(R.color.gray));
				already.setTextColor(getResources().getColor(R.color.gray));
				exception.setTextColor(getResources().getColor(R.color.gray));
				upload.setVisibility(View.GONE);
				break;

			case 1:
				inspection.setTextColor(getResources().getColor(R.color.gray));
				not.setTextColor(getResources().getColor(R.color.blue1));
				already.setTextColor(getResources().getColor(R.color.gray));
				exception.setTextColor(getResources().getColor(R.color.gray));
				upload.setVisibility(View.GONE);
				break;

			case 2:
				inspection.setTextColor(getResources().getColor(R.color.gray));
				not.setTextColor(getResources().getColor(R.color.gray));
				already.setTextColor(getResources().getColor(R.color.blue1));
				exception.setTextColor(getResources().getColor(R.color.gray));
				upload.setVisibility(View.GONE);
				break;

			case 3:
				inspection.setTextColor(getResources().getColor(R.color.gray));
				not.setTextColor(getResources().getColor(R.color.gray));
				already.setTextColor(getResources().getColor(R.color.gray));
				exception.setTextColor(getResources().getColor(R.color.blue1));
				upload.setVisibility(View.GONE);
				break;

			default:
				break;
		}
	}
	public class MyViewPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyViewPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@SuppressLint("NewApi")
		public void onPageSelected(int arg0) {
			Animation animation = new TranslateAnimation(one * currIndex, one * arg0, 0, 0);
			currIndex = arg0;
			Log.d("OnPageChangeListener", currIndex + "");
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			imageView.startAnimation(animation);

			handleHeader(currIndex);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.inspection_btn_back:
			this.finish();
			Constants.TASK_TYPE = null;
			break;

		case R.id.inspection_btn_upload:
			initData();
			setInspection();
			setNotListView();
			setAlreadyListView();
			setExceptionListView();
			Constants.showToast(context, "刷新成功！");
			break;

		case R.id.view_inspection_read_card:
			readCard.setEnabled(false);
			Intent mIntent = new Intent();
			mIntent.setClass(InspectionActivity.this, ReadRF.class);
			startActivityForResult(mIntent, 0);

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// readCard.setEnabled(true);
		setData();
		setInspection();
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_OK:
			String rfidData1 = data.getStringExtra("code1");
			Constants.CURRENT_WELL_CODE = rfidData1;
			if (currentInspection.equals("station")) {
				Log.d("InspectionActivity", rfidData1 + " " + canInspection.get(0).getCode().substring(0, 12));
				if (alreadyInspections.size() == 0 && canInspection.size() > 0 && !rfidData1.equals(canInspection.get(0).getCode().substring(0, 12))
						&& canInspection.get(0).getPointName().trim().equals("大门口")) {
					Constants.showToast(context, "请从" + canInspection.get(0).getPointName() + "开始巡检");
					return;
				}
				for (PlanDetail aCanInspection : canInspection) {
					Log.d("InspectionActivity", "canInspection.get(i).getCode():  " + aCanInspection.getCode().substring(0, 12));
					if (currentDevice != null && rfidData1.equals(aCanInspection.getCode().substring(0, 12))) {
						CurrentInspectionDialog dialog = new CurrentInspectionDialog(context, currentDevice, currentWell, deviceIds, aCanInspection.getPointId() + "", aCanInspection
								.getCode().substring(0, 12), canInspection.get(tempDetailsPosition).getPatrolTime(), canInspection.get(tempDetailsPosition).getOfficeId());
						dialog.show();
						Log.d("InspectionActivity", "times show dialog " + aCanInspection.getCode().substring(0, 12));
					}
				}
			} else {
				List<Well> wells = sqliteHelper.getWellList();
				for (Well well : wells) {
					if (rfidData1.equals(well.getCode())) {
						currentWell = well;
						CurrentInspectionDialog dialog = new CurrentInspectionDialog(context, currentDevice, currentWell, deviceIds, "", well.getCode().substring(0, 12), canInspection.get(
								tempDetailsPosition).getPatrolTime(), canInspection.get(tempDetailsPosition).getOfficeId());
						dialog.show();
						break;
					}
				}
				// if (currentWell != null &&
				// rfidData1.equals(currentWell.getCode())) {
				// CurrentInspectionDialog dialog = new
				// CurrentInspectionDialog(context, currentDevice, currentWell,
				// deviceIds,
				// canInspection.get(tempDetailsPosition).getPatrolTime());
				// dialog.show();
				// } else {
				// Constants.showToast(context, "非本巡检点！");
				// }
			}
			break;
		default:
			break;
		}
	}

	private void initInspection() {
		readCard = (ImageView) view1.findViewById(R.id.view_inspection_read_card);
		shouldRead2 = (TextView) view1.findViewById(R.id.view_inspection_tv1);
		shouldRead = (TextView) view1.findViewById(R.id.view_inspection_should_inspection);
	}

	private void setInspection() {
		if (alreadyInspections.size() >= 0 && alreadyInspections.size() < deviceIds.size()) {
			readCard.setEnabled(true);
			readCard.setClickable(true);
			if (currentInspection.equals("station")) {
				String nextDeviceId = "";
				/* 方案一 始终提示当前巡检的下一个,使用方案一时直接注释方案二即可 */
				// String curPatrolTime = "";
				// String curOfficeId = "";
				// if (canInspection != null && canInspection.size() > 0) {
				// curPatrolTime =
				// canInspection.get(tempDetailsPosition).getPatrolTime();
				// curOfficeId =
				// canInspection.get(tempDetailsPosition).getOfficeId();
				// }
				// String recordPatrolTime =
				// spPreferences.getString(Constants.CURRENT_PATROL_TIME, "");
				// String recordOfficeId =
				// spPreferences.getString(Constants.CURRENT_OFFICEID, "");
				// if (curPatrolTime != "" &&
				// curPatrolTime.equals(recordPatrolTime) && curOfficeId != ""
				// && curOfficeId.equals(recordOfficeId)) {
				// PlanDetail planDetail = null;
				// if (alreadyInspections.size() > 0) {
				// for (int i = 0; i < alreadyInspections.size(); i++) {
				// PlanDetail data = alreadyInspections.get(i);
				// if (currentDeviceId.equals(data.getPointId() + "")) {
				// planDetail = data;
				// break;
				// }
				// }
				// }
				// if (planDetail != null) {
				// int size = deviceIds.size();
				// for (int i = 0; i < size; i++) {
				// if (currentDeviceId.equals(deviceIds.get(i) + "")) {
				// if (i == size - 1) {
				// if (notInspectons.size() > 0) {
				// nextDeviceId = notInspectons.get(0).getPointId() + "";
				// break;
				// }
				// } else {
				// String deviceId = deviceIds.get(i + 1) + ""; //
				// 下一个巡检项的deviceId
				// boolean hasInspectons = false; // 下一个巡检项是否为已巡检状态
				// for (int j = 0; j < alreadyInspections.size(); j++) {
				// PlanDetail data = alreadyInspections.get(j);
				// if (deviceId.equals(data.getPointId() + "")) {
				// hasInspectons = true;
				// break;
				// }
				// }
				// if (!hasInspectons) {
				// nextDeviceId = deviceId;
				// break;
				// } else {
				// currentDeviceId = deviceId;
				// }
				// }
				// }
				// }
				// } else {
				// nextDeviceId = currentDeviceId;
				// }
				// } else {
				// if (canInspection != null && canInspection.size() > 0) {
				// if (notInspectons != null || notInspectons.size() > 0) {
				// nextDeviceId = notInspectons.get(0).getPointId() + "";
				// }
				// }
				// }
				// // 做最后的条件判断
				// if (nextDeviceId.equals("")) {
				// if (notInspectons.size() > 0) {
				// nextDeviceId = notInspectons.get(0).getPointId() + "";
				// }
				// }

				/* 方案二 始终提示未巡检第一个,使用方案二时直接注释方案一即可 */
				nextDeviceId = notInspectons.get(0).getPointId() + "";

				// 最终根据deviceId查询Device
				currentDevice = sqliteHelper.getDeviceById(nextDeviceId);
				if (currentDevice != null) {
					shouldRead.setText(currentDevice.getName());
				}
			} else {
				shouldRead2.setVisibility(View.INVISIBLE);
				shouldRead.setText("请读卡");
			}
			readCard.setOnClickListener(this);
		} else if (alreadyInspections.size() == deviceIds.size()) {
			readCard.setEnabled(false);
			readCard.setClickable(false);
			shouldRead.setText("当前站点已巡检完毕！");
			if (currentInspection.equals("station")) {
				editor.remove(Constants.STATION_COUNT);
				editor.putInt(Constants.STATION_COUNT, 0);
			} else {
				editor.remove(Constants.WELL_COUNT);
				editor.putInt(Constants.WELL_COUNT, 0);
			}
			editor.commit();
		} else if (canInspection.size() <= 0 || currentDeviceId.equals("0")) {
			readCard.setEnabled(false);
			readCard.setClickable(false);
			shouldRead.setText("当前时间点没有可巡检任务");
		} else {
			readCard.setEnabled(true);
			readCard.setClickable(true);
			if (currentInspection.equals("station")) {
				// currentDevice = sqliteHelper.getDeviceById(currentDeviceId);
				String currentPointId = sqliteHelper.getCurrentPointId(officeId);
				if (currentPointId == null) {
					currentDevice = sqliteHelper.getDeviceById(currentDeviceId);
					if (currentDevice != null) {
						shouldRead.setText(currentDevice.getName());
					}
				} else {
					currentDevice = sqliteHelper.getDeviceById((Integer.parseInt(currentPointId) + 1) + "");
					if (currentDevice != null) {
						shouldRead.setText(currentDevice.getName());
					} else {
						shouldRead.setText("");
					}
				}

			} else {
				shouldRead2.setVisibility(View.INVISIBLE);
				shouldRead.setText("请读卡");
			}
			readCard.setOnClickListener(this);
		}
	}

	/**
	 * 初始化未巡界面控件
	 */
	private void initNotView() {
		notListview = (ListView) view2.findViewById(R.id.inspection_not_listview);
	}

	private void setNotListView() {
		NotInspectionAdapter notInspectionAdapter = new NotInspectionAdapter(context, notInspectons);
		notListview.setAdapter(notInspectionAdapter);
	}

	private void initAlreadyView() {
		alreadyListview = (ListView) view3.findViewById(R.id.inspection_already_listview);
	}

	private void setAlreadyListView() {
		NotInspectionAdapter notInspectionAdapter = new NotInspectionAdapter(context, alreadyInspections);
		alreadyListview.setAdapter(notInspectionAdapter);
	}

	private void initExceptionView() {
		exceptionListview = (ListView) view4.findViewById(R.id.inspection_exception_listview);
	}

	private void setExceptionListView() {
		String patrolTime = "";
		if (canInspection != null && canInspection.size() > 0) {
			patrolTime = canInspection.get(tempDetailsPosition).getPatrolTime();
		} else {
			patrolTime = "";
		}
		Log.d("tag", "patrolTime" + patrolTime);
		if (currentInspection.equals("station")) {
			exceptionTasks = sqliteHelper.getExceptionPlanDetailsofStation(patrolTime, officeId);
		} else {
			exceptionTasks = sqliteHelper.getExceptionPlanDetailsofWell(patrolTime, officeId);
		}
		if (exceptionTasks.size() > 0) {
			exceptionNum.setText(exceptionTasks.size() + "");
		} else {
			exceptionNum.setText("");
		}
		Log.d("tag", "exceptionTasks.size" + exceptionTasks.size());
		ExceptionAdapter exceptionAdapter = new ExceptionAdapter(context, exceptionTasks);
		exceptionListview.setAdapter(exceptionAdapter);
	}

	private void setHandler() {
		refreshHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				initData();
				setData();
				setInspection();
				setNotListView();
				setAlreadyListView();
				setExceptionListView();
			}
		};

		handleExceptionHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};

		mHandler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				String patrolTime = "";
				if (canInspection != null && canInspection.size() > 0) {
					patrolTime = canInspection.get(tempDetailsPosition).getPatrolTime();
				}
				if (patrolTime.equals("")) {
					Constants.showToast(context, "请重新登录生成今天巡检计划");
					return;
				}
				switch (msg.what) {
				case HttpRequest.REQUEST_SUCCESS:
					List<UploadException> exceptions = (List<UploadException>) msg.obj;
					for (int i = 0; i < exceptions.size(); i++) {
						UploadException data = exceptions.get(i);
						data.setPatrolTime(patrolTime);
						// 更新exception表数据
						UploadException local_data = sqliteHelper.geteExceptionByItemId(data.getItemId());
						if (local_data == null) {
							sqliteHelper.insertException(data);
						} else {
							// 此条本地数据还没有和网络数据衔接时，更新数据并标记为已衔接，更新PatrolTime字段值
							if (local_data.getFromWhere().equals("1") || !local_data.getPatrolTime().equals(data.getPatrolTime())) {
								sqliteHelper.updetaNetExceptionStatus(data);
							}
						}
						// 更新PlanDetail表数据
						PlanDetail plan_local = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(data.getItemId(), data.getPatrolTime());
						if (plan_local != null && plan_local.getResult() == null) {
							// TODO
							if (local_data == null) {
								sqliteHelper.updateDetailPlanFromNetException(data);
							} else {
								sqliteHelper.updateDetailPlanFromNetException(local_data);
							}
						}
					}
					// 清除已经作废的巡检异常项
					List<UploadException> dumpedData = sqliteHelper.getAllUnUsedException(patrolTime);
					if (dumpedData != null && dumpedData.size() > 0) {
						Log.d("tag", "清除已经作废的巡检异常项个数：" + dumpedData.size());
						for (int i = 0; i < dumpedData.size(); i++) {
							sqliteHelper.deleteException(dumpedData.get(i));
						}
					}
					if (exceptions.size() > 0) {
						Message msg_refresh = refreshHandler.obtainMessage();
						msg_refresh.sendToTarget();
						Constants.showToast(context, "异常项同步成功 √");
					} else {
						Constants.showToast(context, "没有历史巡检异常项");
					}
					pb_loading.setVisibility(View.GONE);
					break;

				case HttpRequest.REQUEST_FAILER:
					List<UploadException> result = sqliteHelper.getAllHisNetException();
					if (result != null && result.size() > 0) {
						for (int i = 0; i < result.size(); i++) {
							UploadException data = result.get(i);
							data.setPatrolTime(patrolTime);
							PlanDetail plan_local = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(data.getItemId(), patrolTime);
							if (plan_local != null && plan_local.getResult() == null) {
								sqliteHelper.updateDetailPlanFromNetException(data);
							}
						}
						Message msg_refresh = refreshHandler.obtainMessage();
						msg_refresh.sendToTarget();
						Constants.showToast(context, "异常项同步成功 √");
					} else {
						Constants.showToast(context, "没有历史巡检异常项");
					}
					pb_loading.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			}
		};
	}

	private void setItemClickListener() {
		alreadyListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(InspectionActivity.this, CheckItemActivity.class);
				intent.putExtra("deviceId", alreadyInspections.get(position).getPointId() + "");
				intent.putExtra("patrolTime", alreadyInspections.get(position).getPatrolTime());
				intent.putExtra("intentFrom", "InspectionActivity");
				startActivity(intent);
			}
		});

		exceptionListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// exceptionPosition = position;
				// Intent intent = new Intent(context,
				// UploadExceptionActivity.class);
				// intent.putExtra("intentFrom", "InspectionActivity");
				// startActivity(intent);
				ExcpetionHandleDialog dialog = new ExcpetionHandleDialog(context, exceptionTasks.get(position), exceptionTasks.size(), exceptionPosition);
				dialog.show();
			}
		});
	}

	private void getNetHistoryException() {
		pb_loading.setVisibility(View.VISIBLE);
		HttpRequest http = HttpRequest.getInstance(context);
		Constants.showToast(this, "获取历史巡检异常项");
		http.requestGetGasException(mHandler, officeId);
	}

}
