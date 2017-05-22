package com.joe.oil.activity;

import java.util.List;

import u.aly.ap;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.GisHistoryAdapter;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.view.CommentListView;

public class GisHistroyActivity extends Activity implements OnClickListener {

	private Context context;
	private ImageView back;
	private TextView notice_none_data;
	private CommentListView mListView;
	private SqliteHelper sqliteHelper;
	private List<GisFinish> gisFinishs;
	private OilApplication application;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gis_history);

		initView();
		initMembers();
		setListView();
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.gis_history_back);
		notice_none_data = (TextView) this.findViewById(R.id.notice_none_data);
		mListView = (CommentListView) this.findViewById(R.id.gis_history_listview);

		back.setOnClickListener(this);
	}

	private void initMembers() {
		context = GisHistroyActivity.this;
		application = (OilApplication) getApplication();
		user = application.getUser();
		sqliteHelper = new SqliteHelper(context);
		gisFinishs = sqliteHelper.getAllGisFinishs(user.getUserId());
		if (gisFinishs != null && gisFinishs.size() > 0) {
			notice_none_data.setVisibility(View.GONE);
		} else {
			notice_none_data.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	private void setListView() {
		if (gisFinishs != null && gisFinishs.size() > 0) {
			GisHistoryAdapter gisHistoryAdapter = new GisHistoryAdapter(context, gisFinishs);
			mListView.setAdapter(gisHistoryAdapter);
			mListView.setPullLoadEnable(false);
			mListView.setPullRefreshEnable(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.gis_history_back:
			this.finish();
			break;

		default:
			break;
		}
	}

}
