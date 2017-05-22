package com.joe.oil.parolmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.activity.BaseActivity;
import com.joe.oil.util.ClearEditText;


public class ItemSettingActivity extends BaseActivity
{
	public final static int Setting_IP=1;
	public final static int Setting_TIME=2;

	private ImageView navbar_backView;

	private TextView item_Title;
	private ClearEditText item_Value;

	private Button set_ok;

	private String [] temp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_setting);

		item_Title =(TextView)findViewById(R.id.itemTitle);
		item_Value=(ClearEditText)findViewById(R.id.itemValue);
		set_ok=(Button)findViewById(R.id.btn_ok);

		String str = getIntent().getStringExtra("itemTitle");
		temp = str.split(",");

		set_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if(item_Value.getText().toString().equals(""))
				{
					Toast.makeText(ItemSettingActivity.this, "请输入有效信息！", Toast.LENGTH_SHORT).show();
				}
				else {
					Intent intent=new Intent();
					intent.putExtra("value", item_Value.getText().toString());
					if(temp[0].equals("1"))
					{
						setResult(Setting_IP, intent);
					}
					else if(temp[0].equals("2"))
					{
						setResult(Setting_TIME, intent);
					}
					finish();
				}

			}
		});

		item_Title.setText(temp[1]);

		if(temp[0].equals("1"))
		{
			item_Value.setHint("请输入服务器IP地址");
		}
		else if(temp[0].equals("2"))
		{
			item_Value.setHint("请输入定位时间间隔(单位:秒)");
		}

		navbar_backView=(ImageView)findViewById(R.id.navbar_back);
		navbar_backView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent=new Intent();
				intent.putExtra("value", "00");
				setResult(0, intent);
				finish();

			}
		});

	}

}
