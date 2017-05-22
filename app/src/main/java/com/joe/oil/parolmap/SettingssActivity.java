package com.joe.oil.parolmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class SettingssActivity extends BaseActivity
{
	private final static int Setting_CODE=1;

	private ImageView Online_Map_OpenView;
	private ImageView Online_Map_CloseView;
	private ImageView navbar_backView;
	private ImageView item_ip_setting;
	private ImageView item_gps_setting;

	private TextView value_ip;
	private TextView value_time;
	private Map<String,String> params;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);


		Online_Map_OpenView=(ImageView)findViewById(R.id.switch_open_onlinemap);
		Online_Map_CloseView=(ImageView)findViewById(R.id.switch_close_onlinemap);

		//params.get("online_map").equals("显示")

		navbar_backView=(ImageView)findViewById(R.id.navbar_back);
		navbar_backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();

			}
		});
		item_ip_setting=(ImageView)findViewById(R.id.ipSet);
		item_ip_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(SettingssActivity.this, ItemSettingActivity.class);
				intent.putExtra("itemTitle", "1,服务器IP设置");
				startActivityForResult(intent,Setting_CODE);
				//startActivity(intent);
			}
		});

		item_gps_setting=(ImageView)findViewById(R.id.gpsSet);
		item_gps_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(SettingssActivity.this, ItemSettingActivity.class);
				intent.putExtra("itemTitle", "2,定位间隔时间设置");
				startActivityForResult(intent,Setting_CODE);
				//startActivity(intent);
			}
		});

		value_ip=(TextView)findViewById(R.id.iv_ip);
		value_time=(TextView)findViewById(R.id.iv_gps);

		params= Constantss.getParams();

		value_ip.setText(String.valueOf(params.get("serverIP")));
		value_time.setText(String.valueOf(params.get("gps"))+"秒");

		if(params.get("online_map").equals("显示"))
		{
			Online_Map_CloseView.setVisibility(View.GONE);
			Online_Map_OpenView.setVisibility(View.VISIBLE);
		}
		else
		{
			Online_Map_CloseView.setVisibility(View.VISIBLE);
			Online_Map_OpenView.setVisibility(View.GONE);
		}

		Listener tl=new Listener();
		Online_Map_OpenView.setOnClickListener(tl);
		Online_Map_CloseView.setOnClickListener(tl);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode==Setting_CODE)
		{

			Bundle bundle=data.getExtras();
			String str=bundle.getString("value");

			if (resultCode==ItemSettingActivity.Setting_IP)
			{
				value_ip.setText(str);

				Map<String,String> params_ip = new HashMap<String,String>();
				params_ip.put("serverIP", str);

				Constantss.updateParams(params_ip);
			}
			else if (resultCode==ItemSettingActivity.Setting_TIME)
			{
				value_time.setText(str+"秒");

				Map<String,String> params_gps = new HashMap<String,String>();
				params_gps.put("gps", str);

				Constantss.updateParams(params_gps);
			}
			else
			{

			}
		}
	}
	class Listener implements OnClickListener
	{
		@Override
		public void onClick(View v){

			switch (v.getId()) {
				case R.id.switch_open_onlinemap:
				{
					Map<String,String> params = new HashMap<String,String>();
					params.put("online_map", "不显示");

					Constantss.updateParams(params);

					Online_Map_CloseView.setVisibility(View.VISIBLE);
					Online_Map_OpenView.setVisibility(View.GONE);

					break;

				}
				case R.id.switch_close_onlinemap:
				{
					Map<String,String> params = new HashMap<String,String>();
					params.put("online_map", "显示");

					Constantss.updateParams(params);

					Online_Map_CloseView.setVisibility(View.GONE);
					Online_Map_OpenView.setVisibility(View.VISIBLE);

					break;
				}
			}
		}
	}
}
