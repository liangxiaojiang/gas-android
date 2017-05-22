package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.util.Constants;

public class IpSettingDialog extends Dialog implements android.view.View.OnClickListener {

	private EditText etIp;
	private Button confrim;
	private Button cancel;
	private Context context;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private RelativeLayout wifi;
	private RelativeLayout tog;
	private ImageView imgWifi;
	private ImageView img2g;
	private TextView wifiState;
	private TextView togState;
	private String wifiIpString;
	private String togIpString;
	private int IP_KIND = 1; // ip种类 1:2g网 2：wifi

	public IpSettingDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_ip_setting);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		this.context = context;

		initView();
		initMembers();
	}

	private void initView() {
		etIp = (EditText) this.findViewById(R.id.dialog_ip_setting_ip);
		confrim = (Button) this.findViewById(R.id.dialog_ip_setting_confirm);
		cancel = (Button) this.findViewById(R.id.dialog_ip_setting_concel);
		wifi = (RelativeLayout) this.findViewById(R.id.dialog_ip_setting_rl_wifi);
		tog = (RelativeLayout) this.findViewById(R.id.dialog_ip_setting_rl_2g);
		imgWifi = (ImageView) this.findViewById(R.id.dialog_ip_setting_img_wifi);
		img2g = (ImageView) this.findViewById(R.id.dialog_ip_setting_img_2g);
		wifiState = (TextView) this.findViewById(R.id.dialog_ip_setting_wifi_state_ip);
		togState = (TextView) this.findViewById(R.id.dialog_ip_setting_2g_state_ip);

		confrim.setOnClickListener(this);
		cancel.setOnClickListener(this);
		wifi.setOnClickListener(this);
		tog.setOnClickListener(this);

		etIp.addTextChangedListener(textWatcher);
	}

	private void initMembers() {
		sharedPreferences = context.getSharedPreferences("oil", 0);
		editor = sharedPreferences.edit();
		wifiIpString = sharedPreferences.getString("wifiIp", Constants.WIFI_IP);
		togIpString = sharedPreferences.getString("2GIp", Constants.BASE_URL);

		setTextDefault();
		doCheckTog();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_ip_setting_concel:
			dismiss();
			break;

		case R.id.dialog_ip_setting_confirm:
			confirm();
			break;

		case R.id.dialog_ip_setting_rl_wifi:
			doCheckWifi();
			break;

		case R.id.dialog_ip_setting_rl_2g:
			doCheckTog();
			break;

		default:
			break;
		}
	}

	private void setTextDefault() {
		togState.setTextColor(context.getResources().getColor(R.color.blue1));
		wifiState.setTextColor(context.getResources().getColor(R.color.blue1));
		togState.setText("2G网状态IP:   \n" + togIpString);
		wifiState.setText("wifi状态IP:   \n" + wifiIpString);
	}

	private void doCheckTog() {
		img2g.setImageResource(R.drawable.pictures_select_icon_selected);
		imgWifi.setImageResource(R.drawable.pictures_select_icon_unselected);
		IP_KIND = 1;
		etIp.setText(togIpString);
		setTextDefault();
	}

	private void doCheckWifi() {
		imgWifi.setImageResource(R.drawable.pictures_select_icon_selected);
		img2g.setImageResource(R.drawable.pictures_select_icon_unselected);
		IP_KIND = 2;
		etIp.setText(wifiIpString);
		setTextDefault();
	}

	private void confirm() {
		String inputIp = etIp.getText().toString();
		if (inputIp.length() <= 0) {
			Constants.showToast(context, "请输入IP地址！");
		} else if (!inputIp.startsWith("http://")) {
			Constants.showToast(context, "IP地址必须以http://开始");
		} else {
			if (IP_KIND == 1) {
				editor.putString("2GIp", inputIp);
				editor.commit();
			} else {
				editor.putString("wifiIp", inputIp);
				editor.commit();
			}

			Constants.showToast(context, "IP设置成功！");
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo info = connectivityManager.getActiveNetworkInfo();
			if (info != null && info.isAvailable()) {
				if (info.getType() == ConnectivityManager.TYPE_WIFI) {
					Constants.showToast(context, "当前为Wifi状态下IP");
					Constants.BASE_URL = sharedPreferences.getString("wifiIp", Constants.WIFI_IP);
				} else {
					Constants.showToast(context, "当前为2G网状态下IP");
					Constants.BASE_URL = sharedPreferences.getString("2GIp", Constants.BASE_URL);
				}
			} else {
				Constants.showToast(context, "当前没有网络，请检查！");
			}
			Message urlMsg = HttpRequest.urlHandler.obtainMessage();
			urlMsg.sendToTarget();
			dismiss();
		}
	}

	TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			String inputIp = etIp.getText().toString();
			if (IP_KIND == 1) {
				togState.setTextColor(Color.rgb(51, 51, 51));
				togState.setText("2G网状态IP:   \n" + inputIp);
			} else {
				wifiState.setTextColor(Color.rgb(51, 51, 51));
				wifiState.setText("wifi状态IP:   \n" + inputIp);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

}
