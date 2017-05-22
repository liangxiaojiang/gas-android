package com.joe.oil.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;

import java.util.ArrayList;
import java.util.List;


public class ImageListActivity extends Activity  {
	
	private ListView imageList;
	private String imgUrl;
	private List<Task> tasks;
	private Task task;
	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_imagelist);
		init();
	}

	private void init() {
		initView();
		initEvent();
		setData();
	}

	private void initView() {
		tasks = new ArrayList<Task>();
		task=(Task) getIntent().getSerializableExtra("task");
		imgUrl = getIntent().getStringExtra("imgUrl");
		back = (ImageView) findViewById(R.id.image_back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if(imageList == null){
			imageList = (ListView) findViewById(R.id.imageList);
		}
	}
	
	private void initEvent() {
		imageList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startShowImageActivity();
			}
		});
	}
	
	private void startShowImageActivity() {
		Intent intent = new Intent(ImageListActivity.this , ShowImageActivity.class);
		intent.putExtra("url",imgUrl);
		startActivity(intent);
	}
	
	private void setData() {
		ImageAdapter adapter = new ImageAdapter(ImageListActivity.this,task);
		imageList.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ImageAdapter.AnimateFirstDisplayListener.displayedImages.clear();
	}

}
