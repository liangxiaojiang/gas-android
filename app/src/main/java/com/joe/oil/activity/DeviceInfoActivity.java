package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Device;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.util.Constants;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"HandlerLeak", "ShowToast", "SimpleDateFormat"})
public class DeviceInfoActivity extends BaseActivity {

	private TextView checkitemTitleTv,mDeviceMemoTv,mDeviceNameTv,mDeviceOfficeTv,mDeviceCodeTv;
	private HttpRequest http;
	private GetTaskHandler getTaskHandler;
	private String title;
	private List<Device> tasks = new ArrayList<Device>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_deviceinfo);

		title = getIntent().getStringExtra("title");
		getTaskHandler = new GetTaskHandler();
		http = HttpRequest.getInstance(DeviceInfoActivity.this);
		initView();
		try {
			initData();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	private void initView() {
		checkitemTitleTv = (TextView) findViewById(R.id.checkitem_title_tv);
		mDeviceMemoTv = (TextView) findViewById(R.id.tv_device_memo);
		mDeviceNameTv = (TextView) findViewById(R.id.tv_device_name);
		mDeviceOfficeTv = (TextView) findViewById(R.id.tv_device_office);
		mDeviceCodeTv = (TextView) findViewById(R.id.tv_device_code);
		checkitemTitleTv.setText(title);
	}

	private void initData() throws FileNotFoundException {

		if (title.equals("概况")){
			Log.d("zjp", "发起请求");
			http.requestDeviceByCode(getTaskHandler, getIntent().getStringExtra("code"), getIntent().getStringExtra("userId"), title);
		}
	}
	@SuppressLint("HandlerLeak")
	private class GetTaskHandler extends Handler {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HttpRequest.REQUEST_SUCCESS:
					Constants.dismissDialog();
					tasks = (List<Device>) msg.obj;
					Log.d("zjp", "result="+tasks);
					if (tasks.size()>0){
					mDeviceCodeTv.setText(tasks.get(0).getCode());
					mDeviceMemoTv.setText(tasks.get(0).getMemo());
					mDeviceNameTv.setText(tasks.get(0).getName());
					mDeviceOfficeTv.setText(tasks.get(0).getOfficeName());}
					break;

				case HttpRequest.REQUEST_FAILER:
					Constants.dismissDialog();
					Log.d("zjp", "false");
					break;


				default:
					break;
			}
		}
	}
}
