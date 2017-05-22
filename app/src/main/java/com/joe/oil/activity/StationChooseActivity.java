package com.joe.oil.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.StationAdapter;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

@SuppressLint("SimpleDateFormat")
public class StationChooseActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

	private final String TAG = "StationChooseActivity";
	private ImageView back;
	private ListView listView;
	private Context context;
	private SqliteHelper sqliteHepler;
	private List<PlanDetail> allPlan;
	private SharedPreferences sPreferences;
	private View headerView;
	private LayoutInflater inflater;
	private StationAdapter sAdapter;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_JUMP_TO_OTHER_STATION_CHOOSE && resultCode == RESULT_OK) {
			refresh();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_plan);

		initView();
		initMembers();
		setData();
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.choose_plan_btn_back);
		listView = (ListView) this.findViewById(R.id.choose_plan_listview);

		back.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}

	@SuppressLint("InflateParams")
	private void initMembers() {
		context = StationChooseActivity.this;
		inflater = LayoutInflater.from(context);
		headerView = inflater.inflate(R.layout.item_station, null);
		TextView other = (TextView) headerView.findViewById(R.id.item_station_name);
		other.setText("其他");
		sqliteHepler = new SqliteHelper(context);
		allPlan = new ArrayList<PlanDetail>();
		sPreferences = context.getSharedPreferences("oil", 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_plan_btn_back:
			this.finish();
			Constants.TASK_TYPE = null;
			break;

		default:
			break;
		}
	}

	private void setData() {
		Constants.showDialog(context);
		new Thread(new Runnable() {

			@Override
			public void run() {

				if (sPreferences.getString("currentInspection", "station").equals("station")) {
					allPlan = sqliteHepler.getOnePlanEachStation();
				}else {
					allPlan = sqliteHepler.getOnePlanEachSingleWell();
				}


//				Collections.sort(allPlan, new Comparator<PlanDetail>() {
//					@Override
//					public int compare(PlanDetail lhs, PlanDetail rhs) {
//						return lhs.getOfficeId().compareTo(rhs.getOfficeId());
//					}
//				});
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	private void refresh() {
		List<PlanDetail> planDetails = null;
		if (sPreferences.getString("currentInspection", "station").equals("station")) {
			planDetails = sqliteHepler.getOnePlanEachStation();
		}else {
			planDetails = sqliteHepler.getOnePlanEachSingleWell();
		}

		allPlan.clear();
		allPlan.addAll(planDetails);

		sAdapter.notifyDataSetChanged();
	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				sAdapter = new StationAdapter(context, allPlan);
				listView.addFooterView(headerView);
				listView.setAdapter(sAdapter);
				break;

			default:
				break;
			}
			Constants.dismissDialog();
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = null;
		if (position < allPlan.size()) {
			intent = new Intent(StationChooseActivity.this, InspectionActivity.class);
			intent.putExtra("officeId", allPlan.get(position).getOfficeId());
		} else {
			intent = new Intent(StationChooseActivity.this, OtherStationChooseActivity.class);
			String officeIds = "";
			for (int i = 0; i < allPlan.size(); i++) {
				officeIds = officeIds + allPlan.get(i).getOfficeId() + ",";
			}
			intent.putExtra("officeIds", officeIds);
		}
		startActivityForResult(intent, Constants.REQUEST_JUMP_TO_OTHER_STATION_CHOOSE);
	}
}
