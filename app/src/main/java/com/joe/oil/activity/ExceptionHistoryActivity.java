package com.joe.oil.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.ExceptionHistoryAdapter;
import com.joe.oil.entity.UploadException;
import com.joe.oil.entity.User;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.view.CommentListView;
import com.joe.oil.view.CommentListView.IXListViewListener;
import com.umeng.analytics.MobclickAgent;

public class ExceptionHistoryActivity extends BaseActivity implements OnClickListener, IXListViewListener, OnItemClickListener {

	private final String TAG = "ExceptionHistoryActivity";
	private ImageView back;
	private Context context;
	private TextView notice_none_data;
	private CommentListView mListView;
	private List<UploadException> uploadExceptions;
	private SqliteHelper sqliteHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_exception_history);

		initView();
		initMembers();
		setData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.exception_history_back);
		notice_none_data = (TextView) this.findViewById(R.id.notice_none_data);
		mListView = (CommentListView) this.findViewById(R.id.exception_history_listview);

		back.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}

	private void initMembers() {
		context = ExceptionHistoryActivity.this;
		sqliteHelper = new SqliteHelper(context);
		OilApplication oilApplication = (OilApplication) getApplication();
		User user = oilApplication.getUser();
		uploadExceptions = sqliteHelper.geteExceptionByUserId(user.getUserId());
		if (uploadExceptions != null && uploadExceptions.size() > 0) {
			notice_none_data.setVisibility(View.GONE);
		} else {
			notice_none_data.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}

	private void setData() {
		if (uploadExceptions != null && uploadExceptions.size() > 0) {
			ExceptionHistoryAdapter exceptionHistoryAdapter = new ExceptionHistoryAdapter(context, uploadExceptions);
			mListView.setAdapter(exceptionHistoryAdapter);
			mListView.setPullRefreshEnable(false);
			mListView.setPullLoadEnable(false);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exception_history_back:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		List<ImageBean> imgData = sqliteHelper.getLocalPics(uploadExceptions.get(position - 1).getTime());
		ImageGroup imageGroup = null;
		if (imgData != null && imgData.size() > 0) {
			imageGroup = new ImageGroup("ALL", imgData);
		}else {
			imageGroup = new ImageGroup();
		}
		Intent intent = new Intent(context, ExceptionHistoryDetailActivity.class);
		intent.putExtra("uploadException", uploadExceptions.get(position - 1));
		intent.putExtra("imageGroup", imageGroup);
		startActivity(intent);
	}

}
