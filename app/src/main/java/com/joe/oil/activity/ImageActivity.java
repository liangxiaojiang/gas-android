package com.joe.oil.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.joe.oil.R;
import com.joe.oil.adapter.MyListAdapter;
import com.joe.oil.entity.MyBean;

@SuppressLint({ "FloatMath", "SdCardPath", "HandlerLeak", "ShowToast" })
public class ImageActivity extends ListActivity implements OnClickListener {
	private String imgUrl;
	private ImageView back;
	private MyListAdapter mAdapter;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_image);
		initView();
		initData();
	}

	private void initView() {
		back = (ImageView) this.findViewById(R.id.image_back);
		back.setOnClickListener(this);
	}

	private void initData() {
		imgUrl = getIntent().getStringExtra("imgUrl");
		ArrayList<MyBean> list = new ArrayList<MyBean>();
		
		MyBean myBean = new MyBean();
		myBean.urls = new String[1];
		myBean.urls[0] = imgUrl;
		list.add(myBean);
		mAdapter = new MyListAdapter(ImageActivity.this, list);
		setListAdapter(mAdapter);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.image_back:
			
			this.finish();
			break;
			
		default:
			break;
		}
	}
}
