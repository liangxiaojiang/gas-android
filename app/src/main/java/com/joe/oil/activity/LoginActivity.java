package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.service.OilService;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.view.ResizeLayout;
import com.umeng.analytics.MobclickAgent;
/*
登陆
 */
@SuppressLint({ "ShowToast", "HandlerLeak" })
public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG = "LoginActivity";
	private Context context;
	private EditText password;
	private EditText name;
	private TextView login;
	private ScrollView sv_login;
	private ResizeLayout resizeLayout;
	private OilApplication application;
	private SqliteHelper sqliteHelper;
	private User user;
	private Intent intent;
//	private static final String ADMIN_NAME = "admin"; // 超级管理员用户名
//	private static final String ADMIN_PASSWORD = "123456"; // 超级管理员密码
	private HttpRequest http;

	private final int TYPE_RESIZE = 100;

	private String sName;
	private String sPassword;
	//记住密码按钮
	private CheckBox mRememberCb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_login2);

		initView();
		initMembers();
//		initData();

	}


//	/**
//	 * 初始化数�?
//	 */
//	private void initData() {
//		SharedPreferences preferences=getSharedPreferences("remember_pwd", 0);
//		boolean isRemember=preferences.getBoolean("isRemember", false);
//		mRememberCb.setChecked(isRemember);//给复选框赋�??
//		if(isRemember){//记住密码
//			name.setText(preferences.getString("username", ""));
//			password.setText(preferences.getString("password", ""));
//		}
//
//	}

	@Override
	protected void onResume() {
		super.onResume();
		login.setEnabled(true);
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		password = (EditText) this.findViewById(R.id.login_et_password);
		name = (EditText) this.findViewById(R.id.login_et_name);
		login = (TextView) this.findViewById(R.id.login_btn_login);
		sv_login = (ScrollView) findViewById(R.id.sv_login);
		resizeLayout = (ResizeLayout) findViewById(R.id.resizeLayout);
		login.setOnClickListener(this);

		mRememberCb=(CheckBox) findViewById(R.id.checkBox);

		resizeLayout.setOnResizeListener(new ResizeLayout.OnResizeListener() {
			@Override
			public void OnResize(int w, int h, int oldWidth, int oldHeight) {
				if (h < oldHeight) {
					Message msg = mHandler.obtainMessage();
					msg.what = TYPE_RESIZE;
					msg.sendToTarget();
				}
			}
		});

		sv_login.setOnTouchListener(new View.OnTouchListener() {
			@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
//					v.performClick();
					break;
				default:
					break;
				}
				return true;
			}
		});
//		getWindow().setBackgroundDrawableResource(R.drawable.login_bg);
	}

	private void initMembers() {
		context = LoginActivity.this;
		http = HttpRequest.getInstance(context);
		application = (OilApplication) getApplication();
		sqliteHelper = new SqliteHelper(context);
		sqliteHelper.initUserLoginStatus();
		SharedPreferences sPreferences = context.getSharedPreferences("oil", 0);
		Editor editor = sPreferences.edit();
		// 初始化IP存储
		String wifi_ip = sPreferences.getString("wifiIp", "");
		String tog_ip = sPreferences.getString("2GIp", "");
		if (wifi_ip.equals(""))
			editor.putString("wifiIp", Constants.WIFI_IP);
		if (tog_ip.equals(""))
			editor.putString("2GIp", Constants.BASE_URL);
		editor.commit();

		String deviceId = sPreferences.getString("device_id", "");
		if (deviceId.equals("")) {
			Constants.showToast(context, "设备号为空，请设置！");
		} else {
			Constants.DEVICE_ID = deviceId;
		}
//		intent = new Intent(context, OilService.class);
//		startService(intent);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case TYPE_RESIZE:
				sv_login.scrollTo(0, Constants.getDeviceWidth(LoginActivity.this));
				break;

			case HttpRequest.REQUEST_SUCCESS:
				Constants.dismissDialog();
				user = (User) msg.obj;
				application.setUser(user);

				User user_local = sqliteHelper.getUserByLoginName(user.getLoginName());
				if (user_local == null) {
					Log.d("tag", "11111#" + user.getLoginStatus());
					sqliteHelper.insertUser(user);
				} else {
					Log.d("tag", "22222#" + user.getLoginStatus());
					sqliteHelper.updateUser(user);
				}

				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				break;

			case HttpRequest.REQUEST_FAILER:
				// login.setEnabled(true);
				// Constants.dismissDialog();
				// Toast.makeText(context, msg.obj.toString(),
				// Toast.LENGTH_LONG).show();
				// 处理登录--native
				login_native(sName, sPassword);
				break;

			default:
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn_login:
			login.setEnabled(false);
			login();

			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @Description 登录
	 * @date 2014年7月14日 下午2:45:40
	 */
	private void login() {
		sName = name.getText().toString().trim();
		sPassword = password.getText().toString().trim();
		if (sName.equals("")) {
			Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show();
			login.setEnabled(true);
		} else if (!sName.equals("") && sPassword.equals("")) {
			Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
			login.setEnabled(true);
//		} else if (sName.equals(ADMIN_NAME) && sPassword.equals(ADMIN_PASSWORD)) {
//			// 管理员登陆
//			setApplication(sName, sPassword);
//			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//			startActivity(intent);
		} else {
			// 处理登录--net
//			login_net(sName, sPassword);
			login_native(sName, sPassword);
		}
	}

	/**
	 * net cheek
	 * 
	 * @param sName
	 * @param sPassword
	 */
	private void login_net(String sName, String sPassword) {
		if (Constants.checkNetWork(context)) {
			Constants.showDialog(context);
			http.requestCheckLogin(mHandler, 1, sName, Constants.get32MD5Str(sPassword));
		} else {
			mHandler.sendEmptyMessage(HttpRequest.REQUEST_FAILER);
		}
	}

	/**
	 * native cheek
	 * 
	 * @param sName
	 * @param sPassword
	 */
	private void login_native(String sName, String sPassword) {
		Constants.dismissDialog();
		user = sqliteHelper.getUserByLoginName(sName);
		if (user == null) {
			Constants.showToast(context, "非本区作业人员");
			login.setEnabled(true);
		} else {
			if (Constants.get32MD5Str(sPassword).equals(user.getPassword())) {
				application.setUser(user);
				user.setLoginStatus("1");
				sqliteHelper.updateUser(user);
				Intent intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				http.requestCheckLogin(1, user.getLoginName());

				intent = new Intent(context, OilService.class);
				application.setCeshhi(3);
				startService(intent);

//				rememberPassWord(sName,sPassword);

			} else {
				login.setEnabled(true);
				Constants.showToast(context, "密码错误");
			}
		}
	}

//	/**
//	 *
//	 * @Description 将用户输入的用户名和密码保存到Application
//	 * @param loginName
//	 * @param password
//	 * @date 2014年7月14日 下午2:45:55
//	 */
//	private void setApplication(String loginName, String password) {
//		User user = new User();
//		user.setLoginName(loginName);
//		user.setPassword(password);
//		application.setUser(user);
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_HOME:
				ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
				am.restartPackage(getPackageName());
//			android.os.Process.killProcess(android.os.Process.myPid());  //获取PID
//			System.exit(0);   //常规java、c#的标准退出法，返回值为0代表正常退出
//
		}
		return super.onKeyDown(keyCode, event);
//		}
	}


}
