package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.util.Constants;
import com.joe.oil.util.StringUtils;

public class DeviceIdSettingDialog extends Dialog implements android.view.View.OnClickListener{
	
	private Context context;
	private EditText num;
	private TextView confirm;
	private TextView cancel;
	private SharedPreferences sp;
	

	public DeviceIdSettingDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_divice_id_setting);
		
		this.context = context;
		initView();
		initMembers();
	}
	
	private void initView(){
		num = (EditText) this.findViewById(R.id.dialog_device_id_setting_num);
		cancel = (TextView) this.findViewById(R.id.dialog_device_id_setting_cancel);
		confirm = (TextView) this.findViewById(R.id.dialog_device_id_setting_confirm);
		
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}
	
	private void initMembers(){
		sp = context.getSharedPreferences("oil", 0);
		num.setText(sp.getString("device_id", ""));
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_device_id_setting_cancel:
			dismiss();
			break;
			
		case R.id.dialog_device_id_setting_confirm:
			String telNum = num.getText().toString();
			if(StringUtils.isEmpty(telNum)){
				Constants.showToast(context, "请输入手机号码!");
				return;
			}
			else {
				Editor editor = sp.edit();
				editor.putString("device_id", telNum);
				editor.commit();
				Constants.DEVICE_ID = telNum;
				dismiss();
			}
			
			break;

		default:
			break;
		}
	}
}
