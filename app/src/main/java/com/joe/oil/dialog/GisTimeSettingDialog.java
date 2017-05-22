package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.MainActivity;
import com.joe.oil.util.Constants;

public class GisTimeSettingDialog extends Dialog implements OnClickListener{
	
	private TextView confirm;
	private TextView cancel;
	private EditText etTime;
	private Context context;

	public GisTimeSettingDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_gis_time_setting);
		this.context = context;
		
		initView();
	}
	
	private void initView(){
		confirm = (TextView) this.findViewById(R.id.dialog_gis_time_setting_confirm);
		cancel = (TextView) this.findViewById(R.id.dialog_gis_time_setting_cancel);
		etTime = (EditText) this.findViewById(R.id.dialog_gis_time_setting_time);
		
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);
	}
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_gis_time_setting_cancel:
			dismiss();
			break;
			
		case R.id.dialog_gis_time_setting_confirm:
			int time = Integer.parseInt(etTime.getText().toString());
			Message msg = MainActivity.gisHandler.obtainMessage();
			msg.what = MainActivity.LOCATION_STOP;
			msg.obj = time;
			msg.sendToTarget();
			Constants.showToast(context, "设置成功");
			dismiss();
			break;

		default:
			break;
		}
	}
}
