package com.joe.oil.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.joe.oil.R;
import com.joe.oil.adapter.LineAdapter;
import com.joe.oil.entity.Line;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

public class LineActivity extends BaseActivity implements OnClickListener, OnItemClickListener{
	private ImageView back;
	private ListView listview;
	private SqliteHelper sqliteHelper;
	private Context context;
	private List<Line> lines;
	private LineAdapter lineAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_line);
		
		initView();
		initMembers();
		setListView();
	}
	
	private void initView(){
		back = (ImageView) this.findViewById(R.id.line_btn_back);
		listview = (ListView) this.findViewById(R.id.line_listview);
		
		back.setOnClickListener(this);
		listview.setOnItemClickListener(this);
	}
	
	private void initMembers(){
		context = LineActivity.this;
		sqliteHelper = new SqliteHelper(context);
		lines = sqliteHelper.getLineData();
	}
	
	private void setListView(){
		lineAdapter = new LineAdapter(context, lines);
		listview.setAdapter(lineAdapter);
		listview.setDividerHeight(0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.line_btn_back:
			this.finish();
			Constants.TASK_TYPE = null;
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(LineActivity.this, GisActivity.class);
		Constants.CURRENT_LINE_ID = lines.get(position).getLineId();
		intent.putExtra("lineName", lines.get(position).getName());
		intent.putExtra("officeId", lines.get(position).getOfficeId());
		startActivity(intent);
	}
}
