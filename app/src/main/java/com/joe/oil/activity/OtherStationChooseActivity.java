package com.joe.oil.activity;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.joe.oil.R;
import com.joe.oil.adapter.StationOtherAdapter;
import com.joe.oil.entity.Office;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;

public class OtherStationChooseActivity extends BaseActivity implements OnItemClickListener, OnClickListener {

	private final String TAG = "StationChooseActivity";
	private ImageView back;
	private ListView listView;
	private Context context;
	private List<Office> offices;
	private String officeIds;
	private SharedPreferences sPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_choose_plan);

		initView();
		initMembers();
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.choose_plan_btn_back);
		listView = (ListView) this.findViewById(R.id.choose_plan_listview);

		back.setOnClickListener(this);
		listView.setOnItemClickListener(this);
	}

	private void initMembers() {
		context = OtherStationChooseActivity.this;
		sPreferences = context.getSharedPreferences("oil", 0);
		SqliteHelperForItem sqliteHelperForItem = new SqliteHelperForItem(context);
		officeIds = getIntent().getStringExtra("officeIds");
		String[] ids = officeIds.split(",");
		offices = sqliteHelperForItem.getDeviceOfOffice();
		Iterator<Office> iterator = offices.iterator();
		while (iterator.hasNext()) {
			String officeId = iterator.next().getOfficeId();
			for (int i = 0; i < ids.length; i++) {
				String id = ids[i];
				if (id.equals(officeId)) {
					iterator.remove();
					break;
				}
			}
		}
		StationOtherAdapter sAdapter = new StationOtherAdapter(context, offices);
		listView.setAdapter(sAdapter);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.choose_plan_btn_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SqliteHelper sqliteHelper = new SqliteHelper(context);
		String type = null;
		if (sPreferences.getString("currentInspection", "station").equals("station")) {
			type = "1";
		}else {
			type = "2";
		}
		if (sqliteHelper.isCurrentStationPlanExist(offices.get(position).getName(), type)) {
			Constants.showToast(context, "该站点计划存在，不需要生成");
		} else {
			setResult(RESULT_OK);

			Intent intent = new Intent("creat_plan");
			intent.putExtra("from", "other");
			intent.putExtra("officeId", offices.get(position).getOfficeId());
			sendBroadcast(intent);
		}
	}
}
