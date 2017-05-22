package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.LocationSource;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.joe.oil.R;
import com.joe.oil.dialog.ChooseDialogCreator;
import com.joe.oil.dialog.LineDialog;
import com.joe.oil.entity.Device;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Version;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.parolmap.Constantss;
import com.joe.oil.parolmap.NavigationActivity;
import com.joe.oil.receiver.DownloadCompleteReceiver;
import com.joe.oil.service.UpdateService;
import com.joe.oil.sqlite.DBManager;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.sqlit;
import com.joe.oil.util.Constants;
import com.joe.oil.util.GPSUtil;
import com.joe.oil.util.SlideMenu;
import com.joe.oil.util.StringUtils;
import com.umeng.analytics.MobclickAgent;

import net.safetone.rfid.lib.RfidReader;
import net.safetone.rfid.lib.exception.InvalidDeviceException;
import net.safetone.rfid.lib.exception.NoDeviceException;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/**
 * 主界面
 */
@SuppressLint({"SimpleDateFormat", "HandlerLeak"})
public class MainActivity extends BaseActivity implements OnClickListener, AMapLocationListener {

    private RelativeLayout gps, scanner, single;
    private RelativeLayout work;
    private RelativeLayout station;
    private RelativeLayout well;
    private RelativeLayout exit;
    private RelativeLayout setting;
    private RelativeLayout rlHse;
    private RelativeLayout rlUpload;
    private RelativeLayout rlMessage;
    private RelativeLayout rlRepair;

    private RelativeLayout rlguanxian;

    private RelativeLayout rl_autonomous;//自主作业

    private ImageView imgGps;
    private ImageView imgWork;
    private ImageView imgStation;
    private ImageView imgWell;
    private ImageView imgSetting;
    private ImageView ivMessage;
    private ImageView ivUpload;
    private ImageView ivHse;
    private TextView tvSetting;
    private TextView tvStation;
    private TextView tvWell;
    private TextView tvLine;
    private TextView tvWork;
    private TextView tvVersion;
    private TextView tvName;
    private TextView tvMessage;
    private TextView tvUpload;
    private TextView tvHse;
    private ProgressBar pb_loading;
    private Intent intent;
    private OilApplication application;
    private User user;
    private LocationClient mLocationClient;
    private LocationMode tempMode = LocationMode.Hight_Accuracy;
    private String tempcoor = "bd09ll";
    private LocationClientOption option;
    public static Handler gisHandler;
    public static final int LOCATION_STOP = 0;
    private SharedPreferences sPreferences;
    private Editor edit;
    private Context context;
    private HttpRequest http;
    private List<PlanDetail> planDetails;
    private SqliteHelper sqliteHelper;
    private Handler mHandler;
    private MyHandler handler = new MyHandler();
    private String date;
    private ProgressDialog mProgressDialog;
    public static RfidReader mRfidReader = null;
    /* RFID Reader Bluetooth */
    private BluetoothDevice mBtDevice = null;
    //显示下载框
    private PopupWindow mPopupWindow;
    //PopWindow的view
    private View popupView;

    private TextView tvMessageCount;//消息上面的显示数量框
    private TextView tvTesknumber;

    private Dialog gisDialog;
    private String[] levels;

    private SlideMenu slideMenu;//侧边栏
    private LinearLayout headsidebar;//侧边栏头
    private LinearLayout personal;//个人
    private LinearLayout signin;//签到
    private LinearLayout messagelist;//消息中心
    private LinearLayout systemupdate;//系统更新
    private LinearLayout help;//帮助
    private Button upthe;//设置
    private LinearLayout feedback;//意见反馈
    private Button slexit;//退出
    private LinearLayout about;//关于
    private TextView tname;

    private List<Task> taskall;
    private String deadTime = "";

    private int isFinishing = 0;

    DBManager dbManager;
    private String date1 = null;//单天日期
    private String name1 = null;//当前用户
    private TextView btSigin;//签到


    //定位需要的声明
    private AMapLocationClient mLocationClient1 = null;//定位发起端
    private AMapLocationClientOption mLocationOption = null;//定位参数
    private LocationSource.OnLocationChangedListener mListener = null;//定位监听器
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private RelativeLayout rl_single;
    private RelativeLayout rl_gis;
    private RelativeLayout rl_dataaccess;

    public static RfidReader getRfidReader() {
        return mRfidReader;
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            final String action = intent.getAction();
            if (action.equals(Constants.DAWNLOAD_FINISH)) {
                mPopupWindow.dismiss();
            }
            if (action.equals(Constants.DATA_LOADING_FINISH)) {
//                pb_loading.setVisibility(View.GONE);
            } else if (action.equals(Constants.DATA_LOADING_START)) {
//                pb_loading.setVisibility(View.VISIBLE);
            } else if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled() && Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
                    new ConnectTask().execute(mBtDevice);
                }
            }
        }
    };
    private final static int TYPE_SETTINGGPS = 1;
    private final static int TYPE_SETTING_BLUETOOCH = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.REQUEST_JUMP_TO_MESSAGE_LIST && resultCode == RESULT_OK) {
            requestUnreadMessageCount();
        }
        switch (resultCode) {
            case RESULT_OK:
                String rfidData1 = data.getStringExtra("code1");//读卡得到点位CODE
                intent = new Intent(this, ScannerActivity.class);
                intent.putExtra("code", rfidData1);
                intent.putExtra("userId", user.getUserId());
                startActivity(intent);
                break;
            default:
                break;
        }
        switch (requestCode) {
            case TYPE_SETTINGGPS:
                Message msg = gisHandler.obtainMessage();
                msg.obj = 20;
                msg.what = 0;
                msg.sendToTarget();
                break;
            case TYPE_SETTING_BLUETOOCH:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter.isEnabled()) {
                    Constants.showToast(context, "蓝牙已开启");
                    if (Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
                        new ConnectTask().execute(mBtDevice);
                    }
                } else {
                    Constants.showToast(context, "蓝牙未开启");
                }
                checkGPS();
                break;

            default:
                break;
        }
    }

    private class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    String name = msg.obj.toString();
//                    positionFill.setText(name);
                    break;

                case HttpRequest.REQUEST_FAILER:
                    Constants.dismissDialog();
                    break;

                default:
                    break;
            }
        }
    }

    private Version info;
    private ProgressDialog pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();
        initMembers();
        InitLocation();
        setHandler();
        initData();

        initviewSlide();//侧边栏
        judgmentSign();//这是签到

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable() && info.getType() == ConnectivityManager.TYPE_WIFI) {
            checkUpdate();//更新系统的
        }
        //开始定位
        initLoc();
        //刷新ui
        new Thread(mRunnable).start();
        //得到当前的sha1值，是为做地图时使用的
        sHA1(MainActivity.this);
    }



    void downFile(final String url) {
        pBar = new ProgressDialog(MainActivity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    pBar.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "gas-android.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。
                        byte[] buf = new byte[10];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process);       //这里就是关键的实时更新进度了！
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
//                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * Function : 实现run()方法，每1秒发送一条Message给Handler
     */
    private Runnable mRunnable = new Runnable() {
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    gengxinHandler.sendMessage(gengxinHandler.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Function   :   实现handleMessage()方法，用于接收Message，刷新UI
     */
    private Handler gengxinHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();

        }
    };

    /**
     * Function   :   刷新UI
     * 下面是修改主页面后，把任务按钮放在最上面了，这是当有未完成任务的时候让显示
     * 有多少条未完成的任务
     */
    private void refreshUI() {
        taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
        if (taskall.size() == 0) {
            tvTesknumber.setVisibility(View.GONE);
        } else {
            tvTesknumber.setVisibility(View.VISIBLE);
            tvTesknumber.setText("" + taskall.size() + "");
        }
    }

    /**
     * 侧边栏
     */
    private void initviewSlide() {
        slideMenu = (SlideMenu) this.findViewById(R.id.slide_menu);//侧边栏绑定布局
        ImageView sidebartitle = (ImageView) this.findViewById(R.id.sidebar);//侧边栏按钮（因在标题上，直接绑定整个标题）
        sidebartitle.setOnClickListener(this);//侧边栏的监听

        tname = (TextView) slideMenu.findViewById(R.id.tv_uname);//用户名id文本框
        btSigin = (TextView) slideMenu.findViewById(R.id.tv_siginmeirr);
        name1 = user.getName();

        headsidebar = (LinearLayout) slideMenu.findViewById(R.id.headsidebar);
//        personal= (LinearLayout) slideMenu.findViewById(R.id.ll_personal);//个人
        signin = (LinearLayout) slideMenu.findViewById(R.id.ll_signin);//签到布局
        messagelist = (LinearLayout) slideMenu.findViewById(R.id.ll_messagelist);//消息中心
        systemupdate = (LinearLayout) slideMenu.findViewById(R.id.ll_systemupdate);//系统更新
        help = (LinearLayout) slideMenu.findViewById(R.id.ll_help);//帮助
        upthe = (Button) slideMenu.findViewById(R.id.but_settings);//设置
        feedback = (LinearLayout) slideMenu.findViewById(R.id.ll_feedback);//意见反馈
        slexit = (Button) slideMenu.findViewById(R.id.but_exit);//退出
        about = (LinearLayout) slideMenu.findViewById(R.id.ll_about);//关于
//
        //意见反馈
        feedback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,FeedbackActivity.class);
                intent.putExtra("intentFrom", "MainActivity");
                startActivity(intent);
            }
        });

        signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignActivity.class);
                intent.putExtra("name", user.getName());
                startActivity(intent);
            }
        });

        about.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);//先得到构造器
                builder.setTitle("生产运行智能监管系统");
                builder.setMessage("当前版本： v" + getVersionName());
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });


        headsidebar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PersonalActivity.class);
                intent.putExtra("name", user.getName());
                intent.putExtra("OfficeName", user.getOfficeName());
                startActivity(intent);
//                slideMenu.closeMenu();
            }
        });

        systemupdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUpdate();
                //这里来检测版本是否需要更新
                Constants.showToast(context, "已经是最新版本了");
            }
        });


        slexit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GisFinish data = sqliteHelper.getGisFinishNotFinish();
                if (data != null) {
                    Constants.IS_LINE_START = true;
                    Constants.GIS_START_NUM = data.getTaskNo();
                    LineDialog lineDialog = new LineDialog(context, 1);
                    lineDialog.setCanceledOnTouchOutside(false);
                    lineDialog.show();
                    return;
                }
                if (mLocationClient.isStarted()) {
                    mLocationClient.stop();
                    Log.d("MainActivity", "stop");
                }
                http.requestCheckLogin(new Handler(), 2, user.getLoginName(), user.getPassword());
                user = sqliteHelper.getUserByLoginName(user.getLoginName());
                if (user != null) {
                    user.setLoginStatus("0");
                    sqliteHelper.updateUser(user);
                    application.setUser(null);
                }
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                application.setCeshhi(2);
                sqliteHelper.deleteAllSingle();
                MainActivity.this.finish();
                System.exit(0);

            }
        });

        upthe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
//                slideMenu.closeMenu();
            }
        });

        messagelist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(MainActivity.this, MessageListActivity.class);
                intent.putExtra("userId", user.getUserId() + "");
                startActivityForResult(intent, Constants.REQUEST_JUMP_TO_MESSAGE_LIST);
//                slideMenu.closeMenu();
            }
        });


        if (user.getRoleName() != null && user.getRoleName().length() > 0) {

            tname.setText(user.getName());

        } else {

            tname.setText(user.getLoginName());
        }

    }


    /**
     * 这是打开侧边栏
     *
     * @param view
     */

    public void toggleMenu(View view) {
        slideMenu.toggle();
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
        gps = (RelativeLayout) this.findViewById(R.id.start_btn_gps);
        single = (RelativeLayout) this.findViewById(R.id.rl_single);
        scanner = (RelativeLayout) this.findViewById(R.id.scanning);
        work = (RelativeLayout) this.findViewById(R.id.start_btn_work);
        station = (RelativeLayout) this.findViewById(R.id.start_btn_gas_station);
        well = (RelativeLayout) this.findViewById(R.id.start_btn_singlewell);
//        exit = (RelativeLayout) this.findViewById(R.id.start_btn_exit);//注销按钮
        setting = (RelativeLayout) this.findViewById(R.id.start_btn_setting);
        rlHse = (RelativeLayout) this.findViewById(R.id.rl_hse);
        rlMessage = (RelativeLayout) this.findViewById(R.id.rl_message);
        rlRepair = (RelativeLayout) this.findViewById(R.id.rl_repair);
        rlUpload = (RelativeLayout) this.findViewById(R.id.rl_upload);
        imgGps = (ImageView) this.findViewById(R.id.start_img_gps);
        imgWork = (ImageView) this.findViewById(R.id.start_img_task);
        imgStation = (ImageView) this.findViewById(R.id.start_img_station);
        imgWell = (ImageView) this.findViewById(R.id.start_img_single_well);
//        imgSetting = (ImageView) this.findViewById(R.id.start_img_setting);
        ivHse = (ImageView) this.findViewById(R.id.iv_hse);
        ivMessage = (ImageView) this.findViewById(R.id.iv_message);
        ivUpload = (ImageView) this.findViewById(R.id.iv_upload);
//        tvSetting = (TextView) this.findViewById(R.id.start_tv_setting);
        tvStation = (TextView) this.findViewById(R.id.main_tv_station);
        tvVersion = (TextView) this.findViewById(R.id.tv_version);
        tvName = (TextView) this.findViewById(R.id.tv_name);
        tvHse = (TextView) this.findViewById(R.id.tv_hse);
        tvUpload = (TextView) this.findViewById(R.id.tv_upload);
        tvMessage = (TextView) this.findViewById(R.id.tv_message);
        tvWell = (TextView) this.findViewById(R.id.main_tv_well);
        tvWork = (TextView) this.findViewById(R.id.main_tv_work);
        tvLine = (TextView) this.findViewById(R.id.main_tv_line);
//        pb_loading = (ProgressBar) this.findViewById(R.id.pb_loading);
        tvMessageCount = (TextView) findViewById(R.id.tv_messag_count);
        tvTesknumber = (TextView) findViewById(R.id.tv_messag_task);

        rl_single = (RelativeLayout) findViewById(R.id.rl_single);
        rl_single.setOnClickListener(this);
        rl_gis = (RelativeLayout) findViewById(R.id.rl_gis);
        rl_gis.setOnClickListener(this);

        gps.setOnClickListener(this);
        single.setOnClickListener(this);
        scanner.setOnClickListener(this);
        work.setOnClickListener(this);
        station.setOnClickListener(this);
        well.setOnClickListener(this);
//        exit.setOnClickListener(this);
//        setting.setOnClickListener(this);
        rlHse.setOnClickListener(this);
        rlRepair.setOnClickListener(this);//这是运维报修
        rlMessage.setOnClickListener(this);
        rlUpload.setOnClickListener(this);
//        pb_loading.setVisibility(View.GONE);

        rlguanxian = (RelativeLayout) this.findViewById(R.id.rl_guanxin);
        rlguanxian.setOnClickListener(this);

        rl_dataaccess = (RelativeLayout) this.findViewById(R.id.rl_dataaccess);
        rl_dataaccess.setOnClickListener(this);

        rl_autonomous= (RelativeLayout) findViewById(R.id.rl_autonomous);
        rl_autonomous.setOnClickListener(this);
    }

    private void initMembers() {
        context = MainActivity.this;

        application = (OilApplication) getApplication();
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        Constants.DEVICE_NAME = android.os.Build.MODEL;

        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        if (application == null) {
            user = sqliteHelper.getLoginUser();
        } else {
            user = application.getUser();
        }
        mLocationClient = application.mLocationClient;
        sPreferences = context.getSharedPreferences("oil", 0);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        String ip = "";
        if (info != null && info.isAvailable()) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                ip = sPreferences.getString("wifiIp", Constants.WIFI_IP);
            } else {
                ip = sPreferences.getString("2GIp", Constants.BASE_URL);
            }
        }
        if (ip.length() > 12) {
            Constants.BASE_URL = ip;
        }
        Constants.YSJ_STATUS = sPreferences.getString("YSJ_STATUS", "0");
        edit = sPreferences.edit();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DATA_LOADING_START);
        filter.addAction(Constants.DATA_LOADING_FINISH);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(Constants.DAWNLOAD_FINISH);
//        registerReceiver(mReceiver, filter);

        // 如果当前设备为北京设备，则启动蓝牙链接
        if (Constants.DEVICE_NAME.equals(Constants.DEVICE_MODEL_OF_BEIJIN_STRING)) {
            openBlueTooch();
        } else {
            checkGPS();
        }
    }

    private void initData() {
        // 已经经过从数据库中恢复用户后，如果当前用户仍为空，则默认为管理员用户登录
//        if (user == null) {
//            User user = new User();
//            user.setLoginName("admin");
//            user.setPassword("123456");
//            application.setUser(user);
//        }
//        if (user.getLoginName().equals("admin")) {
//            imgGps.setImageResource(R.drawable.gps_disable_clicked);
//            gps.setEnabled(false);
//            imgStation.setImageResource(R.drawable.gas_station_disable_clicked);
//            station.setEnabled(false);
//            imgWork.setImageResource(R.drawable.working_disable_clicked);
//            work.setEnabled(false);
//            imgWell.setImageResource(R.drawable.single_well_disable_clicked);
//            well.setEnabled(false);
//            ivMessage.setImageResource(R.drawable.btn_xiaoxi_grey);
//            rlMessage.setEnabled(false);
//            ivHse.setImageResource(R.drawable.btn_ducha_grey);
//            rlHse.setEnabled(false);
//            tvStation.setTextColor(getResources().getColor(R.color.gray));
//            tvWell.setTextColor(getResources().getColor(R.color.gray));
//            tvWork.setTextColor(getResources().getColor(R.color.gray));
//            tvLine.setTextColor(getResources().getColor(R.color.gray));
//            tvMessage.setTextColor(getResources().getColor(R.color.gray));
//            tvHse.setTextColor(getResources().getColor(R.color.gray));
//        } else {
        mLocationClient.start();
//        tvSetting.setText("上报问题");
//        imgSetting.setImageResource(R.drawable.selector_btn_upload_excpetion);
        String officeId = sPreferences.getString("officeId", "3");
        if (!user.getOfficeId().equals(officeId)) {
            sqliteHelper.deleteAllPlanDetail();
            sqliteHelper.deleteAllTask();
            edit.remove("officeId");
            edit.putString("officeId", user.getOfficeId());
            edit.commit();
//            pb_loading.setVisibility(View.VISIBLE);
        }
        Intent intent = new Intent("creat_plan");
        intent.putExtra("from", "main");
        sendBroadcast(intent);
        http.requestGetDevice(mHandler, user.getOfficeId());
        tvMessageCount.setVisibility(View.GONE);
        tvMessageCount.setText("0");

        tvVersion.setText("当前版本： v" + getVersionName());
        if (user.getRoleName() != null && user.getRoleName().length() > 0) {
            tvName.setText("登录人员： " + user.getRoleName() + "-" + user.getName());

        } else {
            tvName.setText("登录人员： " + user.getLoginName());
        }

        getUnreadCountEvery30Min();

        /**
         * 下面是修改主页面后，把任务按钮放在最上面了，这是当有未完成任务的时候让显示
         *   有多少条未完成的任务
         */
//        taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
//        if (taskall.size() == 0) {
//            tvTesknumber.setVisibility(View.GONE);
//        } else {
//            tvTesknumber.setVisibility(View.VISIBLE);
//            tvTesknumber.setText("" + taskall.size() + "");
//        }

    }

    /**
     * 以固定延迟时间进行执行
     * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
     */
    private void getUnreadCountEvery30Min() {

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleWithFixedDelay(
                new EchoServer(),
                0,
                30,
                TimeUnit.MINUTES);
    }

    public void requestUnreadMessageCount() {
        AjaxParams params = new AjaxParams();
        params.put("toId", user.getUserId() + "");

        http.getFinalHttp().get(Constants.BASE_URL + "/api/message/unread/count", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                try {
                    JSONObject object = new JSONObject(o.toString());
                    if (object.has("unRead") && object.getString("unRead").length() > 0 &&
                            !object.getString("unRead").equals("0")) {

                        tvMessageCount.setVisibility(View.VISIBLE);
                        tvMessageCount.setText(object.getString("unRead"));
                    } else {
                        tvMessageCount.setVisibility(View.GONE);
                        tvMessageCount.setText("0");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvMessageCount.setVisibility(View.GONE);
                    tvMessageCount.setText("0");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);

                tvMessageCount.setVisibility(View.GONE);
                tvMessageCount.setText("0");
            }
        });
    }

    private void checkGPS() {
        // 检查手机GPS状态
        if (!Constants.isOPen(this)) {
            Constants.openGPS(this);
        }
        if (!Constants.isOPen(this)) {
            setGPS();
        }
    }

    // 调用蓝牙连接
    private class ConnectTask extends AsyncTask<BluetoothDevice, String, String> {

        @Override
        public void onPreExecute() {
            showProgressDialog(R.string.connect_to_reader, R.string.connecting);
        }

        @Override
        protected String doInBackground(BluetoothDevice... arg0) {
            try {
                if (mRfidReader == null) {
                    mRfidReader = RfidReader.getInstance(MainActivity.this, arg0[0] == null ? null : arg0[0].getAddress());
                }
            } catch (NoDeviceException e) {
                e.printStackTrace();
                return "NoDeviceException";
            } catch (IOException e) {
                e.printStackTrace();
                return "IOException " + e.getMessage();
            } catch (InvalidDeviceException e) {
                e.printStackTrace();
                return "InvalidDeviceException";
            }

            return null;
        }

        @Override
        public void onPostExecute(String errmsg) {
            dismissProgressDialog();
            Log.d("MainActivity", "-----joe---->>  errmsg:  " + errmsg);
            if (TextUtils.isEmpty(errmsg)) {
                Constants.showToast(context, "读卡设备链接成功！");
            } else {
                Constants.showToast(context, "链接失败，请开启蓝牙");
            }
        }
    }

    private void showProgressDialog(int titleResId, int mesgResId) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(context);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (mRfidReader != null) {
                        mRfidReader.destroy();
                        mRfidReader = null;
                    }
                }
            });
        }
        mProgressDialog.setTitle(titleResId);
        mProgressDialog.setMessage(getString(mesgResId));
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog == null)
            return;
        mProgressDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn_exit:
                GisFinish data = sqliteHelper.getGisFinishNotFinish();
                if (data != null) {
                    Constants.IS_LINE_START = true;
                    Constants.GIS_START_NUM = data.getTaskNo();
                    LineDialog lineDialog = new LineDialog(context, 1);
                    lineDialog.setCanceledOnTouchOutside(false);
                    lineDialog.show();
                    return;
                }
                if (mLocationClient.isStarted()) {
                    mLocationClient.stop();
                    Log.d("MainActivity", "stop");
                }
                http.requestCheckLogin(new Handler(), 2, user.getLoginName(), user.getPassword());
                user = sqliteHelper.getUserByLoginName(user.getLoginName());
                if (user != null) {
                    user.setLoginStatus("0");
                    sqliteHelper.updateUser(user);
                    application.setUser(null);
                }
                this.finish();
                break;

            case R.id.start_btn_work:
                intent = new Intent(MainActivity.this, TaskActivity.class);
                Constants.TASK_TYPE = "task";
                startActivity(intent);
                break;

            case R.id.start_btn_gas_station:
                intent = new Intent(MainActivity.this, StationChooseActivity.class);
                intent.putExtra("intentFrom", "main");
                edit.putString("currentInspection", "station");
                edit.commit();
                Constants.TASK_TYPE = "patrol";
                startActivity(intent);
                break;

            case R.id.start_btn_singlewell:
                intent = new Intent(MainActivity.this, StationChooseActivity.class);
                intent.putExtra("intentFrom", "main");
                edit.putString("currentInspection", "singlewell");
                edit.commit();
                Constants.TASK_TYPE = "well";
                startActivity(intent);
                break;

            case R.id.start_btn_gps:
                showGisLevelWindow();
                break;

            case R.id.rl_single:
                intent = new Intent(MainActivity.this, SingleActivity.class);
                startActivity(intent);
                break;

            case R.id.start_btn_setting:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_hse:
                intent = new Intent(MainActivity.this, HseSupervisionActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_repair:
                Constants.showToast(context, "此功能开发中，敬请期待");
//                intent = new Intent(MainActivity.this, HseSupervisionActivity.class);
//                startActivity(intent);
                break;
            case R.id.rl_upload:
                intent = new Intent(MainActivity.this, UploadExceptionActivity.class);
                intent.putExtra("intentFrom", "MainActivity");
                startActivity(intent);
                break;
            case R.id.rl_message:
                intent = new Intent(MainActivity.this, MessageListActivity.class);
                intent.putExtra("userId", user.getUserId() + "");
                startActivityForResult(intent, Constants.REQUEST_JUMP_TO_MESSAGE_LIST);
                break;

            case R.id.sidebar:
                if (slideMenu.isMainScreenShowing()) {
                    slideMenu.openMenu();
                } else {
                    slideMenu.closeMenu();
                }
                break;
            case R.id.rl_guanxin:
                intent = new Intent(context, GisActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("officeId", 0);
                startActivity(intent);
                break;
            case R.id.rl_gis:
                intent = new Intent(context, NavigationActivity.class);
                startActivity(intent);
                break;
            case R.id.scanning://scanning

                intent = new Intent(context, ReadRF.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.rl_dataaccess:
                intent = new Intent(context, DataAccessActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_autonomous:
//                Constants.showToast(context, "此功能开发中，敬请期待");
                intent =new Intent(context,AutonomousActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void showGisLevelWindow() {
        if (user.getOfficeCode().length() < 6) {
            Toast.makeText(context, "非作业人员", Toast.LENGTH_SHORT).show();
            return;
        }
        levels = new String[]{"道路巡护", "异常巡护", "GIS历史"};
        final String officeId = user.getOfficeCode().substring(4, 6);
        Constants.TASK_TYPE = "gis";

        if (gisDialog == null) {
            ChooseDialogCreator creator = new ChooseDialogCreator();
            gisDialog = creator.createDialog(context, levels[0], levels[1],
                    new ChooseDialogCreator.OnChooseListener() {
                        @Override
                        public void onOneClick() {

//                            jumpLineInspection(officeId);
                            jumpWayInspection(officeId);
                        }

                        @Override
                        public void onTowClick() {

//                            jumpWayInspection(officeId);
                            jumpExceptionInspection(officeId);
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
            creator.setOnChooseThreeListener(new ChooseDialogCreator.OnChooseThreeListener() {
                @Override
                public void onThreeClick() {

//                    jumpExceptionInspection(officeId);
                    Intent intent = new Intent(context, GisHistroyActivity.class);
                    startActivity(intent);
                }
            }, levels[2]);

//            creator.setOnChooseFourListener(new ChooseDialogCreator.OnChooseFourListener() {
//                @Override
//                public void onFourClick() {
//                    Intent intent = new Intent(context, GisHistroyActivity.class);
//                    startActivity(intent);
//                }
//            }, levels[3]);
        }
        gisDialog.show();
    }

    private void jumpLineInspection(String officeId) {

        intent = new Intent(context, GisActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void jumpWayInspection(String officeId) {

        intent = new Intent(context, GisActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void jumpExceptionInspection(String officeId) {

        intent = new Intent(context, GisExceptionInspectionActivity.class);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void InitLocation() {
        option = new LocationClientOption();
        option.setLocationMode(tempMode);// 设置定位模式
        option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
        int span = 10000;
        try {
            // gps定位间隔时间默认为10秒
            span = 10000;
            Constants.GPS_INTERVAL = span;
        } catch (Exception e) {
            e.getStackTrace();
        }
        option.setScanSpan(span);
        mLocationClient.setLocOption(option);
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        gisHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case LOCATION_STOP:
                        mLocationClient.stop();
                        int time = Integer.parseInt(msg.obj.toString());
                        option.setScanSpan(time * 1000);
                        mLocationClient.setLocOption(option);
                        Constants.GPS_INTERVAL = time * 1000;
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                mLocationClient.start();
                            }
                        }, 1000);
                        break;

                    default:
                        break;
                }
            }
        };

        mHandler = new Handler() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpRequest.DOWNLOAD_DEVICE:
                        // sqliteHelper.deleteAllDevice();
                        // SqliteHelperForItem sqliteHelperForItem = new
                        // SqliteHelperForItem(context);
                        // int deviceCount =
                        // sqliteHelperForItem.insertDevice((List<Device>) msg.obj);
                        List<Device> data = (List<Device>) msg.obj;
                        if (data != null) {
                            sqliteHelper.updateDevice(data);
                            // Constants.showToast(context,
                            // getResources().getString(R.string.device_data_download_finish)
                            // + "共" + data.size() + "条");
                        }
                        break;

                    case HttpRequest.REQUEST_FAILER:
                        // Constants.showToast(context, "巡检点数据下载失败");
                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * GPS未开启时，调用设置方法
     */
    public void setGPS() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.location_marker).setTitle("位置服务提示信息").setMessage("温馨提醒:\n您的设备未开启GPS," + "是否开启？").setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
                    /**
                     * 判断手机系统的版本！如果API大于10 就是3.0+
                     * 因为3.0以上的版本的设置和3.0以下的设置不一样，调用的方法不同
                     */
                    if (android.os.Build.VERSION.SDK_INT > 10) {
                        intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        // intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } else {
                        intent = new Intent();
                        ComponentName component = new ComponentName("com.android.settings", "com.android.settings.SecuritySettings");
                        intent.setComponent(component);
                        intent.setAction("android.intent.action.VIEW");
                    }
                    startActivityForResult(intent, TYPE_SETTINGGPS);
                }
            }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).create().show();
        } catch (Exception e) {
            try {
                intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_SETTINGS);
                startActivityForResult(intent, TYPE_SETTINGGPS);
            } catch (Exception e2) {
            }
        }
    }

    /**
     * @Description 开启蓝牙
     * @author joe
     * @date 2014年12月24日 上午11:54:42
     */
    private void openBlueTooch() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!mBluetoothAdapter.isEnabled()) { // 蓝牙未开启，则开启蓝牙
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, TYPE_SETTING_BLUETOOCH);
        } else {
            new ConnectTask().execute(mBtDevice);
            checkGPS();
        }
    }

    private void checkUpdate() {
        FinalHttp finalHttp = http.getFinalHttp();
        String url = String.format(Constants.BASE_URL + "/api/version/check?ver=%s&officeCode=%s",
                getVersionName(), Constants.CURRENT_AREA);

        finalHttp.get(url, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
            }

            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                try {
                    JSONObject object = new JSONObject(o.toString());
                    if (object.getInt("result") == 1) {

                        JSONObject version = object.getJSONObject("appVersion");
                        final Version appVersion = new Version();

                        appVersion.setDownloadUrl(version.getString("downloadUrl"));
                        appVersion.setDescription(version.getString("description"));
                        appVersion.setEnforce(version.getBoolean("enforce"));
                        appVersion.setVer(version.getString("ver"));

                        if (appVersion.getDownloadUrl() != null && appVersion.getDownloadUrl().length() > 0) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(StringUtils.sortString(appVersion.getDescription()));
                            builder.setTitle("最新版本" + appVersion.getVer());
                            builder.setCancelable(false);
                            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

//                                    Intent intent1=new Intent(Intent.ACTION_VIEW);
//                                    intent1.setData(Uri.parse(appVersion.getDownloadUrl()));
//                                    startActivity(intent1);
                                    DownloadCompleteReceiver.getInstance().setContext(context);
                                    Intent intent = new Intent(context, UpdateService.class);
                                    intent.putExtra("appVersion", appVersion);
                                    startService(intent);
//                                    showUpdateDialog();
//                                    handler1.sendEmptyMessage(0);
//                                    showDownLoadPop();
                                    downFile(appVersion.getDownloadUrl());
                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();

                                }
                            });
                            builder.create().show();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getVersionName() {
        // 获取packagemanager的实例
        PackageManager packageManager = getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = null;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
            e.printStackTrace();
        }
        return version;
    }


    public void showDownLoadPop() {

        popupView = getLayoutInflater().inflate(R.layout.pop_view, null);
        mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.showAtLocation(findViewById(R.id.main), Gravity.CENTER, 0, 0);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
    }

    private class EchoServer implements Runnable {
        @Override
        public void run() {
            try {
                requestUnreadMessageCount();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void judgmentSign() {
        dbManager = new DBManager(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        date1 = formatter.format(curDate);//当前时间，转化为string字符串
        query();
    }

    /**
     * 这是查询数据库中，看是否签到，当签到后这边的签到按钮就会变成已签到
     */
    public void query() {
        List<sqlit> persons = dbManager.query(name1);//这边是用实参的方式让值传过去
        for (sqlit person : persons) {
            if (date1.equals(person.getDate()) && name1.equals(person.getName())) {
                btSigin.setText("已签到");
            }
        }
    }


    //定位
    private void initLoc() {

        if (GPSUtil.hasGPSDevice()) {
            //GPSUtil.locator();
            String strInterval = Constantss.getParams().get("gps");
            if (Long.valueOf(strInterval) > 0) {
                GPSUtil.flag = "gps";
                GPSUtil.resetOption(strInterval);
                GPSUtil.startLocation();
//                Toast.makeText(NavigationActivity.this, "开始定位！", Toast.LENGTH_SHORT).show();
//                btn_GPS_open.setVisibility(View.VISIBLE);
//                btn_GPS.setVisibility(View.GONE);

            }
        } else {

            Toast.makeText(MainActivity.this, "请开启GPS！", Toast.LENGTH_LONG).show();
        }



        //初始化定位
        mLocationClient1 = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient1.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient1.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient1.startLocation();


    }

    //定位回调函数
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                application.setLat(String.valueOf(amapLocation.getLatitude()));//这是坐标
                application.setLng(String.valueOf(amapLocation.getLongitude()));
                // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
                if (isFirstLoc) {
                    //获取定位信息
                    StringBuffer buffer = new StringBuffer();
                    buffer.append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getProvince() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
//                    Toast.makeText(getApplicationContext(), buffer.toString(), Toast.LENGTH_LONG).show();
                    isFirstLoc = false;
                }

            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
//                Toast.makeText(getApplicationContext(), "定位失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    //激活定位
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
    }

    //停止定位
    public void deactivate() {
        mListener = null;
    }


    public static String sHA1(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            byte[] cert = info.signatures[0].toByteArray();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(cert);
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < publicKey.length; i++) {
                String appendString = Integer.toHexString(0xFF & publicKey[i])
                        .toUpperCase(Locale.US);
                if (appendString.length() == 1)
                    hexString.append("0");
                hexString.append(appendString);
                hexString.append(":");
            }
            String result = hexString.toString();
            Log.d("lxj",result);
            return result.substring(0, result.length()-1);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
