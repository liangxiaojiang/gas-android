package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.adapter.FinishAdapter;
import com.joe.oil.adapter.MyListAdapter;
import com.joe.oil.dialog.TaskConfirmDialog;
import com.joe.oil.entity.DictDetail;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.Tank;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.parolmap.NavigationActivity;
import com.joe.oil.service.TaskFinishService;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
import com.joe.oil.util.RoundProgressBar;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint({"ShowToast", "HandlerLeak", "SimpleDateFormat"})
public class TaskFinishActivity extends BaseActivity implements View.OnClickListener, Serializable {


    private Context context;
    private Spinner spinner;
    private TextView confirm;
    private ImageView framlay;

    private int lxj;//定义的本页变量，用来判断时间是否完成，当等于1时就是完成，可以进行预览

    private FinishAdapter finishAdapter;//照片显示的GridView
    private List<ImageBean> imageSets;
    private ImageGroup imageGroup;
    private final int REQUEST_CODE = 0x123;
    private GridView mGridView;//布局命名


    private EditText reason;
    private TextView preview;//预览
    private String memo[] = new String[]{"请选择", "已完成", "未完成"};
    private String taskId;
    private String historyId;
    private String actId;
    private String userId;
    private HttpRequest http;
    private SharedPreferences sharedPreferences;
    private boolean isSuccess = false;
    private String takePhotoCompressName;
    private ProgressDialog uploadDialog;
    private OilApplication application;
    private User user;
    private String startTime;
    private Task notFinshTask; // 用于保存完成但提交失败的任务
    private SqliteHelper sqliteHelper;
    private boolean isTakePhoto = false;
    public static Handler finishHandler;
    private String curTotalPicId = "";
    private String samplingAssay = "";

    //-------2016-05-07后添加  joe---------//
    /**
     * 注入前液位输入框
     */
    private EditText etZrqyw;
    /**
     * 注入后液位输入框
     */
    private EditText etZrhyw;
    /**
     * 注入量输入框
     */
    private EditText etZrl;
    /**
     * 添加量输入框
     */
    private EditText etTjl;
    private TextView tvTjl;
    private EditText etBeizhu;
    private EditText etWorkJilu;
    private String taskName = "";
    private TextView createdTime;
    private TextView tvCompleteTime;
    private TextView endTime;
    private Task task;
    private ImageView readCard;

    /**
     * 甲醇入库
     */
    private EditText etGch;//罐车号
    private EditText etJsfs;//接收方数
    private EditText etruqianyewei, etruhouyewei;


    /**
     * 甲醇装车
     */
    private EditText etHjiemaian;
    private EditText etZqianyw;
    private EditText etZhouyw;
    private EditText etZhuangchel;
    /**
     * 甲醇取样
     */
    private EditText etQuyang;
    private TextView tvQuyang;
    /**
     * 派工任务时长
     */
    private int time;
    private Handler refreshUIHandler;
    private String subBeizhu, sWorkJilu, subValue1, subValue2, subValue3;
    private String vehicleid, vehicleNumber, custom;

    private OilApplication oilapp;//定义的全局宏变量
    private RoundProgressBar mRoundProgressBar;//这是圆形进度条

    private static long dayCount;
    private long timeStart;
    private long timeEnd;
    private MyCountDownTimer mc;

    private TextView tvworkTime;

    private String typeOfId;
    private int intentFrom;
    private PlanDetail planDetail;

    private Spinner mSpinnerCar;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private List<String> joinCarMax;
    private List<Tank> tank_list;
    private RelativeLayout rlxialachehao, rlzhaungqianyewei, rlzhuanghouyewei, rlzhuqian, rlzhuhouyewei, rltianjialiang, rlzhuruliang, rlguanchehhao, rljieshoufangshu, rlruqianyewei, rlruhouyewei, rlquyang, rlzhuangcheliang, rlxiangqig;
    private LinearLayout llxiangqing;

    private TextView tv_finishtaskname, tv_riskTips, tv_controlTips, tv_memo, tv_charger, tv_partner, tv_vehicleNumber, tv_vehicleDriverName, tv_vehicleDriverPhone, tvzhuangcheliang;
    private ImageView finishmore, finishmore1, task_finish_btn_back;
    private int by;
    public SqliteHelper mSqliteHelper;
    public String areaTankCar;
    private String imgUrl;
    private MyListAdapter mAdapter;
    private ListView listview1;
    //取样下的浓度、合格与否
    private RelativeLayout rlQualified, rlConcentration;
    private EditText etConcentration;
    private EditText tvQualified;
    private String subQualified, subConcentration;
    //入库（驾驶员、押运员、密度、卸车前流量计读数、卸车后流量计读数
    private RelativeLayout rlDriver, rlEscort, rlPoundweight, rlDensity, rlBeforereadflow, rlAfterreadflow;
    private EditText etDriver, etEscort, etPoundweight, etDensity, etBeforereadflow, etAfterreadflow;
    private String subDriver, subEscort, subPoundweight, subDensity, subBeforereadflow, subAfterreadflow;
    //装车（装车前流量计读数，装车后流量计读数，装车量）
    private RelativeLayout rlBeforeloading, rlAfterloading, rlOutgoingquantity;
    private EditText etBeforeloading, etAfterloading, etOutgoingquantity;
    private String subOutgoingquantity;
    //甲醇注入
    private RelativeLayout rlLicenseplate;
    private EditText etLicenseplate;
    //纱卡车回场检验
    private RelativeLayout rlShifouhege, rlQueren;
    private EditText etShifouhege;
    private String qualified[] = new String[]{"请选择", "合格", "不合格"};
    private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6; //下拉框
    private String subRiskidentification, subPreventivemeasures;

    private String subStartWorkTime, subEndWorkTime;
    private ImageView iv_daohang1;
    private TextView tv_route;
    private String integral;
    private String subIntegal;

    private ListView listView;
    private List<DictDetail> dictDetails;
    private List<DictDetail> dictDetailAll;
    private LinearLayout titles_ll;//任务填报页的布局
    private EditText et1;//这是模板中的输入框（分数字、可以有小数、文本）
    private CheckBox cb1,cb2;//这是模板中的选择框
    private int mYear;// 年
    private int mMonth;// 月
    private int mDay;// 日
    protected static final int DATE_DIALOG_ID = 0;// 创建日期对话框常量
    private Spinner et;
    private List<String> etlist;
    private List<Map<String, Object>> list;
    private int mb;//这是判断是否全部填写的，因为是把所有的值放在上面list中，在遍历的时候会出现多个预览界面，
    private LinearLayout titles_lll;//任务填报页预览的
    private DictDetail dictDetail;
    private String value, value2;
    private String title;
    private Double zrqyw, zrhyw, zqian, zhou, zqianliuliang, zhouliuliang;//一次顺序（注入前液位、注入后液位、装前液位、装后液位、装前流量计读数、装后流量计读数）
    private double hjm;//横截面
    private String chehao;//车号
    private String nongdu;//浓度
    private String hege;//合格与否
    private String vehicleNo, beforeAdd, afterAdd, addValue, beforereadflow,
            afterreadflow, Outgoingquantity, victor, driver, escort, poundweight, density;//甲醇装车（车号、装前液位、装后液位、装车量、装车前流量计读数、装车后流量计读数、出库量）
    private String checkbox,checkbox2;//选择框选中的值
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_finish);
        oilapp = (OilApplication) getApplicationContext();
        initView();
        setrefreshUIHandler();
        initData();
        setSpinnerKind();
        setHandler();
        initCeshi();

        /**
         * 这是圆形进度条的布局文件
         */
        mRoundProgressBar = (RoundProgressBar) this.findViewById(R.id.roundProgressBar3);

        initServ();

        TimerReceive receiver = new TimerReceive();
        // 创建IntentFilter
        IntentFilter filter = new IntentFilter();
        // 添加Action
        filter.addAction("com.example.sendTimerBroadCast");
        // 注册receiver
        registerReceiver(receiver, filter);
//		TimerService timerService = new TimerService(handlerTimer);
        Intent intent = new Intent(this, TaskFinishService.class);
        startService(intent);
    }

    private void initCeshi() {
        if (oilapp.isCeshi() && mSqliteHelper.getNowTask().get(0).getLicensenumber() != null) {
            int spinnerPosition = Integer.parseInt(mSqliteHelper.getNowTask().get(0).getLicensenumber());
            setTaskServ(spinnerPosition);
            oilapp.setCeshi(false);
        }
    }

    private void setTaskServ(int spinnerPosition) {
        etWorkJilu.setText(task.getWorkRecord());
        etBeizhu.setText(task.getMemo());
        etZrqyw.setText(task.getPreinjectionl());//注入前液位
        etZrhyw.setText(task.getPostinjectionl());//注入后液位
        etZrl.setText(task.getLnjectionVolume());//注入量
        etGch.setText(task.getTanknumber());//罐车车号
        etJsfs.setText(task.getRecipientnumber());//接收方数
        etruqianyewei.setText(task.getStoragetanknumber());//甲醇罐装前液位
        etruhouyewei.setText(task.getAfternumber());//甲醇罐装后液位
//        mSpinnerCar.setSelection(Integer.parseInt(task.getLicensenumber()));//车号
        mSpinnerCar.setSelection(spinnerPosition);
        etZqianyw.setText(task.getPreinstalled());//装前液位
        etZhouyw.setText(task.getAfterloading());//装后液位
        etZhuangchel.setText(task.getCrosssection());//装车量
        etQuyang.setText(task.getSamplingrecord());//取样记录
//        etHjiemaian.setText(task.getSamplingrecord());

        etConcentration.setText(task.getConcentration());
        tvQualified.setText(task.getQualified());

    }

    private void initView() {
        task = (Task) getIntent().getSerializableExtra("task");
        historyId = getIntent().getStringExtra("historyId");
        /**
         * 下面的是让选择的图片出现在当前页，出现在当前页的GridView
         */
        mGridView = (GridView) findViewById(R.id.finish_add_picture);//这是绑定布局
        mGridView.setOverScrollMode(View.OVER_SCROLL_NEVER);//这是让布局可以上下滑动
        //这是当点击GridView的监听事件
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(TaskFinishActivity.this, ImagePickerActivity.class);
                intent.putExtra("isModifyPicture", true);
                intent.putExtra("typeOfId", historyId);
                intent.putExtra("intentFrom", 3);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        context = TaskFinishActivity.this;
        mSqliteHelper = new SqliteHelper(context);
        spinner = (Spinner) this.findViewById(R.id.finish_spinner);
        confirm = (TextView) this.findViewById(R.id.finish_confirm);
        preview = (TextView) this.findViewById(R.id.finish_add_preview);//预览
//        finish_add_picture = (ImageView) this.findViewById(R.id.imageView1);//添加图片
        reason = (EditText) this.findViewById(R.id.finish_reason);
        confirm.setOnClickListener(this);
//        finish_add_picture.setOnClickListener(this);
        preview.setOnClickListener(this);
        http = HttpRequest.getInstance(context);
        sharedPreferences = context.getSharedPreferences("oil", Activity.MODE_PRIVATE);
        etZrqyw = (EditText) findViewById(R.id.finish_qianyewei);//注入前液位
        etZrqyw.setOnClickListener(this);
        etZrl = (EditText) findViewById(R.id.finish_zhuruliang);//注入量
        etTjl = (EditText) findViewById(R.id.finish_tianjialiang);//添加量
        etZrhyw = (EditText) findViewById(R.id.finish_zhuhouyewei);//注入后液位
        etZrhyw.setOnClickListener(this);
        etBeizhu = (EditText) findViewById(R.id.finish_beizhu);
        etWorkJilu = (EditText) findViewById(R.id.finish_jilu);

        rlzhuqian = (RelativeLayout) findViewById(R.id.rl_zhuqian);
        rlzhuruliang = (RelativeLayout) findViewById(R.id.rl_zhuruliang);
        rltianjialiang = (RelativeLayout) findViewById(R.id.rl_tianjialiang);
        rlzhuhouyewei = (RelativeLayout) findViewById(R.id.rl_zhuhouyewei);
        rlLicenseplate = (RelativeLayout) findViewById(R.id.rl_licenseplate);
        etLicenseplate = (EditText) findViewById(R.id.finish_licenseplate);

        //甲醇入库
        etGch = (EditText) findViewById(R.id.finish_guanchehhao);//罐车号
        etJsfs = (EditText) findViewById(R.id.finish_jieshoufangshu);//接收方数
        etruqianyewei = (EditText) findViewById(R.id.finish_ruqianye);//甲醇罐入前液位
        etruhouyewei = (EditText) findViewById(R.id.finish_ruhouye);//甲醇罐入后液位
        rlguanchehhao = (RelativeLayout) findViewById(R.id.rl_guanchehhao);
        rljieshoufangshu = (RelativeLayout) findViewById(R.id.rl_jieshoufangshu);
        rlruqianyewei = (RelativeLayout) findViewById(R.id.rl_ruqianyewei);
        rlruhouyewei = (RelativeLayout) findViewById(R.id.rl_ruhouyewei);

        rlDriver = (RelativeLayout) findViewById(R.id.rl_driver);//驾驶员
        rlEscort = (RelativeLayout) findViewById(R.id.rl_escort);//押运员
        rlPoundweight = (RelativeLayout) findViewById(R.id.rl_pound_weight);//磅单量
        rlDensity = (RelativeLayout) findViewById(R.id.rl_density);//密度
        rlBeforereadflow = (RelativeLayout) findViewById(R.id.rl_before_readflow);//卸车前流量计读
        rlAfterreadflow = (RelativeLayout) findViewById(R.id.rl_after_readflow);//卸车后流量计读
        etDriver = (EditText) findViewById(R.id.finish_driver);
        etEscort = (EditText) findViewById(R.id.finish_escort);
        etPoundweight = (EditText) findViewById(R.id.finish_pound_weight);
        etDensity = (EditText) findViewById(R.id.finish_density);
        etBeforereadflow = (EditText) findViewById(R.id.finish_before_readflow);
        etAfterreadflow = (EditText) findViewById(R.id.finish_after_readflow);

        //甲醇装车
        etHjiemaian = (EditText) findViewById(R.id.finish_chehao);
        etZqianyw = (EditText) findViewById(R.id.finish_zhuangqianye);
        etZhouyw = (EditText) findViewById(R.id.finish_zhuanghouye);
        etZhuangchel = (EditText) findViewById(R.id.finish_zhuangcheliang);
        tvzhuangcheliang = (TextView) findViewById(R.id.tv_zhuangcheliang);

        rlxialachehao = (RelativeLayout) findViewById(R.id.rl_xialachehao);
        rlzhaungqianyewei = (RelativeLayout) findViewById(R.id.rl_zhaungqianyewei);
        rlzhuanghouyewei = (RelativeLayout) findViewById(R.id.rl_zhuanghouyewei);
        rlzhuangcheliang = (RelativeLayout) findViewById(R.id.rl_zhuangcheliang);

        rlBeforeloading = (RelativeLayout) findViewById(R.id.rl_before_loading);
        rlAfterloading = (RelativeLayout) findViewById(R.id.rl_after_loading);
        rlOutgoingquantity = (RelativeLayout) findViewById(R.id.rl_outgoing_quantity);
        etBeforeloading = (EditText) findViewById(R.id.finish_before_loading);
        etAfterloading = (EditText) findViewById(R.id.finish_after_loading);
        etOutgoingquantity = (EditText) findViewById(R.id.finish_outgoing_quantity);


        //甲醇取样化验
        etQuyang = (EditText) findViewById(R.id.finish_quyang);
        tvQuyang = (TextView) findViewById(R.id.tv_quyang);
        rlquyang = (RelativeLayout) findViewById(R.id.rl_quyang);
        rlQualified = (RelativeLayout) findViewById(R.id.rl_qualified);
        rlConcentration = (RelativeLayout) findViewById(R.id.rl_concentration);
        etConcentration = (EditText) findViewById(R.id.finish_concentration);//浓度
        tvQualified = (EditText) findViewById(R.id.finish_qualified);//合格与否

        //沙卡车回场检验
        rlShifouhege = (RelativeLayout) findViewById(R.id.rl_shifouhege);
        rlQueren = (RelativeLayout) findViewById(R.id.rl_queren);
        etShifouhege = (EditText) findViewById(R.id.finish_confirm_qualified);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner4 = (Spinner) findViewById(R.id.spinner4);
        spinner5 = (Spinner) findViewById(R.id.spinner5);
        spinner6 = (Spinner) findViewById(R.id.spinner6);

        createdTime = (TextView) this.findViewById(R.id.task_detail_tv_created_time);
        tvCompleteTime = (TextView) this.findViewById(R.id.tv_complete_time);
        endTime = (TextView) this.findViewById(R.id.task_detail_tv_end_time);
        readCard = (ImageView) this.findViewById(R.id.task_detail_iv_read_card);

        framlay = (ImageView) this.findViewById(R.id.framlay);
        framlay.setOnClickListener(this);
        tvworkTime = (TextView) findViewById(R.id.tv_Already_work);

        mSpinnerCar = (Spinner) findViewById(R.id.spinner_car);
        taskName = getIntent().getStringExtra("taskName");
        String name = taskName.substring(0, 4);
        //数据（这是下拉列表数据）
        data_list = new ArrayList<String>();
        joinCarMax = new ArrayList<String>();
        sqliteHelper = new SqliteHelper(context);
        if (sqliteHelper.getTankInfo() != null) {
            tank_list = sqliteHelper.getTankInfo();
            for (Tank tank : tank_list) {
                data_list.add(tank.getNumber());
                //甲醇罐规格计算对应车量的罐横截面积
                if (!tank.getTankarea().equals("null")) {
                    String tankckg = tank.getTankarea();
                    String[] tankSize = tankckg.split("\\*");
                    int ji = Integer.parseInt(tankSize[0]) * Integer.parseInt(tankSize[1]);
                    joinCarMax.add(ji + "");
                }
            }
//            data_list.add(0, "请选择");
            joinCarMax.add(0, "0");
            //适配器
            arr_adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, data_list);
            //设置样式
            arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            //加载适配器
            mSpinnerCar.setAdapter(arr_adapter);
            mSpinnerCar.setSelected(false);
            mSpinnerCar.setOnItemSelectedListener(
                    new OnItemSelectedListenerImpl());
        }

        tv_finishtaskname = (TextView) findViewById(R.id.taskFinishName);
        tv_finishtaskname.setText(task.getName());
        tv_riskTips = (TextView) findViewById(R.id.tv_riskTips);
        tv_controlTips = (TextView) findViewById(R.id.tv_controlTips);
        tv_memo = (TextView) findViewById(R.id.tv_memo);
        tv_charger = (TextView) findViewById(R.id.tv_charger);
        tv_partner = (TextView) findViewById(R.id.tv_partner);
        tv_vehicleNumber = (TextView) findViewById(R.id.tv_vehicleNumber);
        tv_vehicleDriverName = (TextView) findViewById(R.id.tv_vehicleDriverName);
        tv_vehicleDriverPhone = (TextView) findViewById(R.id.tv_vehicleDriverPhone);
        tv_riskTips.setText(task.getRiskTips());
        tv_controlTips.setText(task.getControlTips());
        tv_memo.setText(task.getMemo());
        tv_charger.setText(task.getChargerName());
        tv_partner.setText(task.getPartnerName());
        tv_vehicleNumber.setText(task.getVehicleNumber());
        tv_vehicleDriverName.setText(task.getVehicleDriverName());
        tv_vehicleDriverPhone.setText(task.getVehicleDriverPhone());

        llxiangqing = (LinearLayout) findViewById(R.id.ll_xiangqing);
        rlxiangqig = (RelativeLayout) findViewById(R.id.rl_xiangqig);
        finishmore = (ImageView) findViewById(R.id.finishmore);
        finishmore1 = (ImageView) findViewById(R.id.finishmore1);
        //这是点击显示折叠内容（显示任务详情）
        rlxiangqig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (by) {
                    case 0:
                        llxiangqing.setVisibility(View.GONE);
                        finishmore.setVisibility(View.VISIBLE);
                        finishmore1.setVisibility(View.GONE);
                        by = 1;
                        break;
                    case 1:
                        llxiangqing.setVisibility(View.VISIBLE);
                        finishmore1.setVisibility(View.VISIBLE);
                        finishmore.setVisibility(View.GONE);
                        by = 0;
                        break;
                }

            }
        });
        tv_route = (TextView) findViewById(R.id.tv_route);
        tv_route.setText(task.getTaskRoute());
        //导航按钮
        iv_daohang1 = (ImageView) findViewById(R.id.iv_daohang1);
        iv_daohang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskFinishActivity.this, NavigationActivity.class);
                intent.putExtra("vehicleCode", task.getTaskSingleNumber());
                startActivity(intent);
            }
        });
        task_finish_btn_back = (ImageView) findViewById(R.id.task_finish_btn_back);
        //这是点击返回键执行的逻辑（和点击手机物理返回键功能一样）
        task_finish_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskConfirmDialog dialog = new TaskConfirmDialog(context);
                dialog.show();
            }
        });

        listview1 = (ListView) findViewById(R.id.list1);
        etZrhyw.addTextChangedListener(watcher);
        etZrqyw.addTextChangedListener(watcher);
        etZqianyw.addTextChangedListener(watcher);
        etZhouyw.addTextChangedListener(watcher);
        etHjiemaian.addTextChangedListener(watcher);
        etGch.addTextChangedListener(watcher);

        etConcentration.addTextChangedListener(watcher);
        etDensity.addTextChangedListener(watcher);
        etBeforeloading.addTextChangedListener(watcher);
        etAfterloading.addTextChangedListener(watcher);

        etBeforereadflow.addTextChangedListener(watcher);
        etAfterreadflow.addTextChangedListener(watcher);

        titles_ll = (LinearLayout) findViewById(R.id.titles_ll);
    }

    // 将秒转化成小时分钟秒
    public String FormatMiss(int miss) {
        String hh = miss / 3600 > 9 ? miss / 3600 + "" : "0" + miss / 3600;
        String mm = (miss % 3600) / 60 > 9 ? (miss % 3600) / 60 + "" : "0" + (miss % 3600) / 60;
        String ss = (miss % 3600) % 60 > 9 ? (miss % 3600) % 60 + "" : "0" + (miss % 3600) % 60;
        return hh + "小时" + mm + "分钟" + ss + "秒";
    }


//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (isChecked) {
//            checkbox = cb1.getText().toString();
//            checkbox2 = cb2.getText().toString();
//        }
//    }


    class TimerReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            tvworkTime.setText("已工作时间：" + FormatMiss(intent.getIntExtra("time", 0)));
        }
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                Constants.showToast(context, "屏幕关闭");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void initData() {

        /**
         * 这是对前页传过的值判断，让数据源给adapter
         * 2016.8.27
         **/
        imageGroup = (ImageGroup) getIntent().getSerializableExtra("imageSelected");
        if (imageGroup != null) {
            imageSets = imageGroup.getImageSets();
        } else {
            imageSets = new ArrayList<ImageBean>();
        }
        finishAdapter = new FinishAdapter(this, imageSets);
        mGridView.setAdapter(finishAdapter);

        if (getIntent().getStringExtra("intentFrom").equals("TaskDetailFragment")) {
            startTime = getIntent().getStringExtra("startTime");
        } else {
            startTime = sharedPreferences.getString("startDate", null);
        }
        taskId = getIntent().getStringExtra("taskId");
        historyId = getIntent().getStringExtra("historyId");
        actId = getIntent().getStringExtra("actId");
        taskName = getIntent().getStringExtra("taskName");
        task = (Task) getIntent().getSerializableExtra("task");
//        integral=getIntent().getStringExtra("integral");//积分
        time = Integer.parseInt(task.getInterval());

        application = (OilApplication) getApplication();
        sqliteHelper = new SqliteHelper(context);
        notFinshTask = sqliteHelper.getTaskById(taskId).get(0);
        Log.d("FinishTaskActivity", "notFinshTask.id = " + notFinshTask.getId());
        user = application.getUser();
        userId = user.getUserId();


        dictDetails = sqliteHelper.getTaskDetail(task.getTaskTypeId(), task.getTaskId());
        if (taskName.contains("甲醇注入")) {
            if (!sqliteHelper.getAreaByNum(task.getVehicleNumber()).equals("")) {
                areaTankCar = sqliteHelper.getAreaByNum(task.getVehicleNumber());
            } else {
                Constants.showToast(context, "请联系工作人员分配车辆");
                TaskFinishActivity.this.finish();
            }
        }
        if (dictDetails.size() != 0) {
            template();//模板
        } else {
            if (taskName.contains("甲醇注入") || taskName.contains("缓释剂")) {
                if (!sqliteHelper.getAreaByNum(task.getVehicleNumber()).equals("")) {
                    rlzhuqian.setVisibility(View.VISIBLE);
                    rlzhuruliang.setVisibility(View.VISIBLE);
                    rlzhuhouyewei.setVisibility(View.VISIBLE);
                    //得到注入车号并根据其得到截面积
                    areaTankCar = sqliteHelper.getAreaByNum(task.getVehicleNumber());
                } else {
                    rlLicenseplate.setVisibility(View.VISIBLE);
                    rlzhuruliang.setVisibility(View.VISIBLE);
                }
            }
            if (taskName.contains("泡排剂") || taskName.contains("泡排棒")) {
                rltianjialiang.setVisibility(View.VISIBLE);
            }

            if (taskName.contains("甲醇入库")) {
                rlruhouyewei.setVisibility(View.VISIBLE);
                rlruqianyewei.setVisibility(View.VISIBLE);
                rljieshoufangshu.setVisibility(View.VISIBLE);
                rlguanchehhao.setVisibility(View.VISIBLE);

                rlDriver.setVisibility(View.VISIBLE);
                rlDensity.setVisibility(View.VISIBLE);
                rlEscort.setVisibility(View.VISIBLE);
                rlPoundweight.setVisibility(View.VISIBLE);
                rlBeforereadflow.setVisibility(View.VISIBLE);
                rlAfterreadflow.setVisibility(View.VISIBLE);

            } else if (taskName.contains("甲醇装车")) {
                rlzhaungqianyewei.setVisibility(View.VISIBLE);
                rlzhuanghouyewei.setVisibility(View.VISIBLE);
                rlxialachehao.setVisibility(View.VISIBLE);
                rlzhuangcheliang.setVisibility(View.VISIBLE);
                rlBeforeloading.setVisibility(View.VISIBLE);
                rlAfterloading.setVisibility(View.VISIBLE);
                rlOutgoingquantity.setVisibility(View.VISIBLE);

            } else if (taskName.contains("甲醇取样")) {
                rlguanchehhao.setVisibility(View.VISIBLE);
                rlquyang.setVisibility(View.VISIBLE);
                rlConcentration.setVisibility(View.VISIBLE);
                rlQualified.setVisibility(View.VISIBLE);
            } else if (taskName.contains("回场检验")) {
                rlQueren.setVisibility(View.VISIBLE);
                rlShifouhege.setVisibility(View.VISIBLE);
            }
        }
        /**
         * 8.24日根据项目需求更改的，以前的endTime代表的是时间要求，现在代表的是已作业时间
         */
//        endTime.setTextSize(20);
//        endTime.setText("时间要求：作业时间不少于" + task.getInterval() + "分钟");
//        createdTime.setText("创建时间:  " + task.getCreateTime());
//        createdTime.setTextSize(12);
        tvCompleteTime.setText("要求完成时间:  " + task.getDeadTime());
        tvCompleteTime.setTextSize(12);

        /**
         * 这是读卡的按钮，在这页不显示了，就直接隐藏
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                /**
                 * 原程序是让时间倒计时，for（int i=0；i<=time*60；i++）
                 */
                for (int i = 1; i <= time * 60; i++) {
                    Log.d("FinishTaskActivity", "---------------------i :" + i);
                    try {
                        Thread.sleep(1000);
                        Message msg = refreshUIHandler.obtainMessage();
                        msg.obj = i;
                        msg.sendToTarget();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    /**
     * 动态添加模板
     */
    private void template() {
        titles_ll.removeAllViews();
        list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < dictDetails.size(); i++) {
            final RelativeLayout ll = (RelativeLayout) LayoutInflater.from(TaskFinishActivity.this).inflate(R.layout.combin_item_button, null);
            final TextView btn = (TextView) ll.findViewById(R.id.top_one);
            Map<String, Object> map = new HashMap<String, Object>();
            btn.setText(dictDetails.get(i).getTitle());
            et = (Spinner) ll.findViewById(R.id.spinner);
            et1 = (EditText) ll.findViewById(R.id.top_et1);
            cb1= (CheckBox) ll.findViewById(R.id.checkbox1);
            cb2= (CheckBox) ll.findViewById(R.id.checkbox2);
            map.put("title", btn);
            map.put("value", et1);
            map.put("value2", et);
            map.put("value3",cb1);
            map.put("value4",cb2);
            map.put("workName", dictDetails.get(i).getTaskName());
            list.add(map);

            switch (dictDetails.get(i).getType()) {
                case 0:
                    if (dictDetails.get(i).getTitle().contains("密度") || dictDetails.get(i).getTitle().contains("入库量") || dictDetails.get(i).getTitle().contains("注入量")
                            || dictDetails.get(i).getTitle().contains("装车量") || dictDetails.get(i).getTitle().contains("出库量") || dictDetails.get(i).getTitle().contains("浓度")) {
                        et1.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//能输入小数
                        et1.setHint("请输入" + dictDetails.get(i).getTitle());
                    } else {
                        et1.setInputType(InputType.TYPE_CLASS_NUMBER);
                        et1.setHint("请输入" + dictDetails.get(i).getTitle());
                    }
                    break;
                case 1:
                    et1.setHint("请输入" + dictDetails.get(i).getTitle());
                    break;
                case 2:
                    et1.setVisibility(View.GONE);
                    et.setVisibility(View.VISIBLE);
                    data_list = new ArrayList<String>();
                    joinCarMax = new ArrayList<String>();
                    if (dictDetails.get(i).getTaskName().contains("甲醇装车")) {
                        if (sqliteHelper.getTankInfo() != null) {
                            tank_list = sqliteHelper.getTankInfo();
                            for (Tank tank : tank_list) {
                                data_list.add(tank.getNumber());
                                //甲醇罐规格计算对应车量的罐横截面积
                                if (!tank.getTankarea().equals("null")) {
                                    String tankckg = tank.getTankarea();
                                    String[] tankSize = tankckg.split("\\*");
                                    int ji = Integer.parseInt(tankSize[0]) * Integer.parseInt(tankSize[1]);
                                    joinCarMax.add(ji + "");
                                }
                            }

                            if (oilapp.getJiac() != 1) {
//                            data_list.add(0, "请选择");
                                joinCarMax.add(0, "0");
                                //适配器
                                arr_adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, data_list);
                                //设置样式
                                arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                                //加载适配器
                                et.setAdapter(arr_adapter);
                                et.setSelected(false);
                                et.setOnItemSelectedListener(
                                        new OnItemSelectedListenerImpl());
                            } else {
                                joinCarMax.add(0, "0");
                                //适配器
                                arr_adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, data_list);
                                //设置样式
                                arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                                //加载适配器
                                et.setAdapter(arr_adapter);
                                et.setSelected(false);
                                et.setSelection(Integer.parseInt(dictDetails.get(i).getContent()));
                                et.setOnItemSelectedListener(
                                        new OnItemSelectedListenerImpl());
                            }
                        }
                    }else {
                        String hint = dictDetails.get(i).getHint();
                        String [] hints=hint.split("，");
                        data_list=java.util.Arrays.asList(hints);
                        arr_adapter = new ArrayAdapter<String>(context, R.layout.item_spinner, data_list);
                        //设置样式
                        arr_adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
                        //加载适配器
                        et.setAdapter(arr_adapter);
                        et.setSelected(false);
                        et.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    break;
                case 3:
                    et1.setHint("2016-1-1");
                    et1.setInputType(InputType.TYPE_CLASS_DATETIME);
                    // 时间查选
                    et1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDialog(DATE_DIALOG_ID);
                        }
                    });

                    final Calendar c = Calendar.getInstance();// 获取当前系统日期
                    mYear = c.get(Calendar.YEAR);// 获取年份
                    mMonth = c.get(Calendar.MONTH);// 获取月份
                    mDay = c.get(Calendar.DAY_OF_MONTH);// 获取天数
                    updateDisplay();// 显示设置的日期
                    break;
                case 4:
                    et1.setVisibility(View.GONE);
                    cb1.setVisibility(View.VISIBLE);
                    cb2.setVisibility(View.VISIBLE);
                    String hint = dictDetails.get(i).getHint();
                    String [] hints=hint.split("，");
                    cb1.setText(hints[0]);
                    cb2.setText(hints[1]);
                    cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            checkbox=cb1.getText().toString();
                        }
                    });
                    cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            checkbox=cb2.getText().toString();
                        }
                    });
                    break;
                default:
                    break;
            }

            titles_ll.addView(ll);
            et1.setText(dictDetails.get(i).getContent());

            if (dictDetails.get(i).getTaskName().contains("甲醇注入") || dictDetails.get(i).getTaskName().contains("甲醇装车")
                    || dictDetails.get(i).getTaskName().equals("甲醇取样") || dictDetails.get(i).getTaskName().equals("甲醇入库")) {
                et1.addTextChangedListener(watcher);
            } else {
                oilapp.setCeshi1(true);
            }
        }
//        if (oilapp.isCeshi1()) {
//            et1.addTextChangedListener(watcher);//这个监听放在上面for循环中，输入的值是每输入一个值，会遍历一遍list，放在外面，只会在最后输入完后一次遍历
//        }
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:// 弹出日期选择对话框
                return new DatePickerDialog(TaskFinishActivity.this, mDateSetListener, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;// 为年份赋值
            mMonth = monthOfYear;// 为月份赋值
            mDay = dayOfMonth;// 为天赋值
            updateDisplay();// 显示设置的日期
        }
    };

    private void updateDisplay() {
        // 显示设置的时间
        et1.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1)
                .append("-").append(mDay));
    }

    private void setHandler() {
        finishHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                /**
                 * 必要操作，手动退出时，表示并未提交和保存，此时将用户选择的图片的数据库记录删除
                 */
//                sqliteHelper.deletePics(historyId);
//                sqliteHelper.deleteLocalPics(historyId);

                if (msg.arg1 == 3) {
                    uploadData();
                }
                oilapp.setCeshi(true);
                Intent intent = new Intent(TaskFinishActivity.this, TaskActivity.class);
                startActivity(intent);
                TaskFinishActivity.this.finish();
            }
        };
    }

    private void setSpinnerKind() {
        ArrayAdapter<String> adapterKind = new ArrayAdapter<String>(context, R.layout.item_spinner, memo);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner.setAdapter(adapterKind);
        spinner.setSelected(false);
        ArrayAdapter<String> adapterKind1 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner1.setAdapter(adapterKind1);
        spinner1.setSelected(false);
        ArrayAdapter<String> adapterKind2 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner2.setAdapter(adapterKind2);
        spinner2.setSelected(false);
        ArrayAdapter<String> adapterKind3 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner3.setAdapter(adapterKind3);
        spinner3.setSelected(false);
        ArrayAdapter<String> adapterKind4 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner4.setAdapter(adapterKind4);
        spinner4.setSelected(false);
        ArrayAdapter<String> adapterKind5 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner5.setAdapter(adapterKind5);
        spinner5.setSelected(false);
        ArrayAdapter<String> adapterKind6 = new ArrayAdapter<String>(context, R.layout.item_spinner, qualified);
        adapterKind.setDropDownViewResource(R.layout.item_spinner_dropdown);
        spinner6.setAdapter(adapterKind6);
        spinner6.setSelected(false);
    }

    /**
     * 这是监听手机返回键的
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                TaskConfirmDialog dialog = new TaskConfirmDialog(context);
                dialog.show();
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 这是上传到sqlite中的
     */
    private void uploadData() {

        String names = "";
        String urls = "";
        String urlsName = "";
        for (int i = 0; i < imageSets.size(); i++) {
            urlsName += imageSets.get(i).getPath() + ";";
            ImageBean bean = imageSets.get(i);
            names += bean.getDisplayName() + ";";
            urls += bean.getPath() + ";";
            sqliteHelper.insertLocalPic(bean);
        }

        /**
         *imageSets：图像集合
         */

        if (imageSets.size() > 0) {

            Picture pic = new Picture();
            pic.setChargerId(user.getUserId());
            pic.setCreateTime(DateUtils.getDateTime());
            pic.setName(names);
            pic.setUrl(urls);
            pic.setIsWrokUpdate(0);
            pic.setIsUploadSuccess(0);
            sqliteHelper.insertPic(pic);
        }

        notFinshTask.setMemo(etBeizhu.getText().toString());
        notFinshTask.setWorkRecord(etWorkJilu.getText().toString());

        if (dictDetails.size() != 0) {
//            notFinshTask.setPreinjectionl(addValue);//注入前
//            notFinshTask.setPostinjectionl(afterAdd);//注入后
//            notFinshTask.setLnjectionVolume(beforeAdd);//注入量
//            notFinshTask.setTanknumber(victor);//车号
//            notFinshTask.setRecipientnumber(addValue);
//            notFinshTask.setStoragetanknumber(beforeAdd);
//            notFinshTask.setLicensenumber(vehicleNo);
//            notFinshTask.setPreinstalled(beforeAdd);
//            notFinshTask.setAfterloading(afterAdd);
//            notFinshTask.setCrosssection(etZhuangchel.getText().toString());
//            notFinshTask.setSamplingrecord(etQuyang.getText().toString());
//            notFinshTask.setSamplingrecord(etHjiemaian.getText().toString());
//
//            notFinshTask.setQualified(tvQualified.getText().toString());
//            notFinshTask.setConcentration(etConcentration.getText().toString());
        } else {
            notFinshTask.setPreinjectionl(etZrqyw.getText().toString());
            notFinshTask.setPostinjectionl(etZrhyw.getText().toString());
            notFinshTask.setLnjectionVolume(etZrl.getText().toString());
            notFinshTask.setTanknumber(etGch.getText().toString());
            notFinshTask.setRecipientnumber(etJsfs.getText().toString());
            notFinshTask.setStoragetanknumber(etruqianyewei.getText().toString());
//        notFinshTask.setAfternumber(etruhouyewei.getText().toString());
            notFinshTask.setLicensenumber(mSpinnerCar.getSelectedItemId() + "");
            notFinshTask.setPreinstalled(etZqianyw.getText().toString());
            notFinshTask.setAfterloading(etZhouyw.getText().toString());
            notFinshTask.setCrosssection(etZhuangchel.getText().toString());
            notFinshTask.setSamplingrecord(etQuyang.getText().toString());
            notFinshTask.setSamplingrecord(etHjiemaian.getText().toString());

            notFinshTask.setQualified(tvQualified.getText().toString());
            notFinshTask.setConcentration(etConcentration.getText().toString());
        }
        notFinshTask.setMemo(etBeizhu.getText().toString());
        notFinshTask.setWorkRecord(etWorkJilu.getText().toString());
//        notFinshTask.setPreinjectionl(etZrqyw.getText().toString());
//        notFinshTask.setPostinjectionl(etZrhyw.getText().toString());
//        notFinshTask.setLnjectionVolume(etZrl.getText().toString());
//        notFinshTask.setTanknumber(etGch.getText().toString());
//        notFinshTask.setRecipientnumber(etJsfs.getText().toString());
//        notFinshTask.setStoragetanknumber(etruqianyewei.getText().toString());
//        notFinshTask.setLicensenumber(et.getSelectedItemId() + "");
//        notFinshTask.setPreinstalled(etZqianyw.getText().toString()tx);
//        notFinshTask.setAfterloading(etZhouyw.getText().toString());
//        notFinshTask.setCrosssection(etZhuangchel.getText().toString());
//        notFinshTask.setSamplingrecord(etQuyang.getText().toString());
//        notFinshTask.setSamplingrecord(etHjiemaian.getText().toString());
//
//        notFinshTask.setQualified(tvQualified.getText().toString());
//        notFinshTask.setConcentration(etConcentration.getText().toString());
        notFinshTask.setPreventivemeasures(task.getPreventivemeasures());
        notFinshTask.setRiskidentification(task.getRiskidentification());
        notFinshTask.setIsFinished(3);

        if (notFinshTask.getIsFinished() == 3) {
            oilapp.setNumber(3);
        } else {
            oilapp.setNumber(1);
        }

        sqliteHelper.updateTaskFinishState(notFinshTask);
    }

    private void setrefreshUIHandler() {
        refreshUIHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int currentTime = Integer.parseInt(msg.obj.toString());

                int progress = 100 * currentTime / (time * 60);
                mRoundProgressBar.setProgress(progress);
                /**
                 * minute：分
                 * second：秒
                 */
                int minute = (int) (time * 60 - currentTime) / 60;
                int second = (time * 60 - currentTime) % 60;

                /**
                 * 下面的if{}else{}是因为时间由原来的倒计时变成现在的计数，
                 */

//                if (currentTime == 0) {
////                    endTime.setTextSize(20);
////                    endTime.setText("已作业时间：" + minute + "分" + second + "秒");
////                    framlay.setImageResource(R.drawable.btn_finish_normal);
//                    Constants.showToast(context, "可以读卡提交数据");
//                    lxj = 1;
//                } else {
////                    endTime.setTextSize(20);
////                    endTime.setText("已作业时间：" + minute + "分" + second + "秒");
//                }


                /**
                 * 这是原程序的代码，这是时间是倒计时的
                 */
                if (minute < 0) {
                }
                if (second == 0) {
                    if (task.getDeviceIds().length() < 8) {
                        Constants.showToast(context, "可以完成工作提交数据了");
                    } else {
                        Constants.showToast(context, "可以读卡提交数据");
                        lxj = 1;
                    }
                }
            }
        };
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 这是原选择照片的按钮
             */

//            case R.id.finish_add_picture:
//                Log.d("Image Select Flag", "start!");
//                List<ImageBean> imgData = sqliteHelper.getLocalPics(historyId);
//                if (imgData != null && imgData.size() > 0) {
//                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
//                    Intent intent = new Intent(TaskFinishActivity.this, PicSelectedEnsureActivity.class);
//                    intent.putExtra("intentFrom", 3);
//                    intent.putExtra("typeOfId", historyId);
//                    intent.putExtra("imageSelected", imageGroup);
//                    startActivity(intent);
//                } else {
//                    Log.d("Image Select Flag", "imgData null");
//                    Intent intent = new Intent(TaskFinishActivity.this, ImagePickerActivity.class);
//                    intent.putExtra("intentFrom", 3);
//                    intent.putExtra("typeOfId", historyId);
//                    startActivity(intent);
//                }
//                break;
            case R.id.finish_add_preview://这是预览按钮

                if (lxj == 1) {
                    String sWorkjilu = etWorkJilu.getText().toString();//控件得到填写的值
                    if (dictDetails.size() != 0) {

                        for (int i = 0; i < list.size(); i++) {
                            String title = ((TextView) list.get(i).get("title")).getText().toString();
                            String value = ((EditText) list.get(i).get("value")).getText().toString();

                            if (value.isEmpty()) {
                                Constants.showToast(context, "请填写" + title);
                            } else {
                                mb = i;
                            }
                        }
                    } else {
                        //预览功能

                        String sZrqyw = etZrqyw.getText().toString().trim();
                        String sZrl = etZrl.getText().toString().trim();
                        String sTjl = etTjl.getText().toString().trim();
                        String sChepaihao = etLicenseplate.getText().toString().trim();

                        String sGuanchao = etGch.getText().toString().trim();
                        String sJieshou = etJsfs.getText().toString().trim();
                        String sRuqianyewei = etruqianyewei.getText().toString().trim();
                        String sRuhouyewei = etruhouyewei.getText().toString().trim();

                        String sChehao = mSpinnerCar.getSelectedItem().toString().trim();
                        String sZqianye = etZqianyw.getText().toString().trim();
                        String sZhouye = etZhouyw.getText().toString().trim();
                        String sZhuangchel = etZhuangchel.getText().toString().trim();
                        String sConcentration = etConcentration.getText().toString().trim();
                        String sDriver = etDriver.getText().toString().trim();//驾驶员
                        String sEscort = etEscort.getText().toString().trim();//押运员
                        String sPoundweight = etPoundweight.getText().toString().trim();//磅单量
                        String sDensity = etDensity.getText().toString().trim();//密度
                        String sBeforereadflow = etBeforereadflow.getText().toString().trim();
                        String sAfterreadflow = etAfterreadflow.getText().toString().trim();
                        String sQualified = etShifouhege.getText().toString().trim();
                        String sBeforeloading = etBeforeloading.getText().toString().trim();
                        String sAfterloading = etAfterloading.getText().toString().trim();
                        //contains()是判断是否包含什么的意思
                        //isEmpty()是判断是否为空的意思
                        if (taskName.contains("甲醇入库")) {
                            if (sGuanchao.isEmpty()) {
                                Constants.showToast(context, "请输入车号");
                                return;
                            } else if (sJieshou.isEmpty()) {
                                Constants.showToast(context, "请输入接收方数");
                                return;
                            } else if (sRuqianyewei.isEmpty()) {
                                Constants.showToast(context, "请输入甲醇罐装前液位");
                                return;
                            } else if (sRuhouyewei.isEmpty()) {
                                Constants.showToast(context, "请输入甲醇罐装后液位");
                                return;
                            } else if (sDriver.isEmpty()) {
                                Constants.showToast(context, "请输入驾驶员");
                                return;
                            } else if (sEscort.isEmpty()) {
                                Constants.showToast(context, "请输入押运员");
                                return;
                            } else if (sPoundweight.isEmpty()) {
                                Constants.showToast(context, "请输入磅单量");
                                return;
                            } else if (sDensity.isEmpty()) {
                                Constants.showToast(context, "请输入密度");
                                return;
                            } else if (sBeforereadflow.isEmpty()) {
                                Constants.showToast(context, "请输入卸车前流量计读数");
                                return;
                            } else if (sAfterreadflow.isEmpty()) {
                                Constants.showToast(context, "请输入卸车后流量计读数");
                                return;
                            }

                        } else if (taskName.contains("甲醇装车")) {
                            if (sChehao.contains("请选择")) {
                                Constants.showToast(context, "请选择车号");
                                return;
                            } else if (sZqianye.isEmpty()) {
                                Constants.showToast(context, "请输入装前液位");
                                return;
                            } else if (sZhouye.isEmpty()) {
                                Constants.showToast(context, "请输入装后液位");
                                return;
                            } else if (sBeforeloading.isEmpty()) {
                                Constants.showToast(context, "请输入装车前流量计读数");
                                return;
                            } else if (sAfterloading.isEmpty()) {
                                Constants.showToast(context, "请输入装车后流量计读数");
                                return;
                            }
                        }

                        if (taskName.contains("甲醇取样")) {
                            if (sConcentration.isEmpty()) {
                                Constants.showToast(context, "请输入浓度");
                                return;
                            }
                            if (sGuanchao.isEmpty()) {
                                Constants.showToast(context, "请输入罐车号");
                                return;
                            }
                        }
                        if (taskName.contains("回场检验")) {
                            if (sQualified.isEmpty()) {
                                Constants.showToast(context, "请输入是否合格");
                                return;
                            }
                        }

                        if ((taskName.contains("甲醇注入")) || taskName.contains("缓释剂")) {
                            if (!sqliteHelper.getAreaByNum(task.getVehicleNumber()).equals("")) {
                                if (sZrqyw.isEmpty()) {
                                    Constants.showToast(context, "请输入注入前液位");
                                    return;
                                } else if (sZrl.isEmpty()) {
                                    Constants.showToast(context, "请输入注入量");
                                    return;
                                }
                            } else {
                                if (sZrl.isEmpty()) {
                                    Constants.showToast(context, "请输入注入量");
                                    return;
                                }
                                if (sChepaihao.isEmpty()) {
                                    Constants.showToast(context, "请输入设备类型和牌号");
                                    return;
                                }
                            }
                        }
                        if (taskName.contains("泡排剂") || taskName.contains("泡排棒")) {
                            if (sTjl.isEmpty()) {
                                Constants.showToast(context, "请输入添加量");
                                return;
                            }
                        }
                    }
                    if (sWorkjilu.length() <= 0) {
                        Constants.showToast(context, "请填写工作记录");
                        return;
                    } else if (sWorkjilu.length() >= 255) {
                        Constants.showToast(context, "您输入的内容太长，请删减字数");
                    } else if (spinner.getSelectedItemId() == 0) {
                        Constants.showToast(context, "请选择工作完成情况");
                        return;
                    } else if (dictDetails.size() != 0) {
                        if (mb == list.size() - 1) {//这块的判断是从上面的数，在下面做一个标记
                            dialog();
                        }
                    } else {
                        dialog();
                    }

                } else {
                    Constants.showToast(context, "还没有完成工作");
                }
                break;

            default:
                break;
        }
    }


    /**
     * 这是读卡逻辑，是在预览页中点击读卡跳转到这页完成读卡逻辑
     */
    private void Readers() {

        String sReason = etWorkJilu.getText().toString();
        String sZrqyw = etZrqyw.getText().toString().trim();
        String sZrhyw = etZrhyw.getText().toString().trim();
        String sChepaihao = etLicenseplate.getText().toString();
        String sZrl = etZrl.getText().toString().trim();
        String sTjl = etTjl.getText().toString().trim();
//        String beforeAdd = "";
//        String addValue = "";
//        String afterAdd = "";

        String sGuanchao = etGch.getText().toString().trim();
        String sJieshou = etJsfs.getText().toString().trim();
        String sRuqianyewei = etruqianyewei.getText().toString().trim();
        String sRuhouyewei = etruhouyewei.getText().toString().trim();

        String sDriver = etDriver.getText().toString().trim();//驾驶员
        String sEscort = etEscort.getText().toString().trim();//押运员
        String sPoundweight = etPoundweight.getText().toString().trim();//磅单量
        String sDensity = etDensity.getText().toString().trim();//密度
        String sBeforereadflow = etBeforereadflow.getText().toString().trim();
        String sAfterreadflow = etAfterreadflow.getText().toString().trim();
//        String victor = "";
//        String driver = "";
//        String escort = "";
//        String poundweight = "";
//        String density = "";
//        String beforereadflow = "";
//        String afterreadflow = "";

        String integale = getIntent().getStringExtra("integral");

        String sChehao = mSpinnerCar.getSelectedItem().toString().trim();
        String sZqianye = etZqianyw.getText().toString().trim();
        String sZhouye = etZhouyw.getText().toString().trim();
        String sZuancheliang = etZhuangchel.getText().toString().trim();
//        String vehicleNo = "";
        String sBeforeloading = etBeforeloading.getText().toString().trim();
        String sAfterloading = etAfterloading.getText().toString().trim();
        String sOutgoingquantity = etOutgoingquantity.getText().toString().trim();
//        String Outgoingquantity = "";

        String sQualified = etShifouhege.getText().toString().trim();
        String qualified = "";
        //contains()是判断是否包含什么的意思
        //isEmpty()是判断是否为空的意思
            if (taskName.contains("甲醇入库")) {

                victor = sGuanchao;
                addValue = sJieshou;
                beforeAdd = sRuqianyewei;
                afterAdd = sRuhouyewei;

                driver = sDriver;
                escort = sEscort;
                poundweight = sPoundweight;
                density = sDensity;
                beforereadflow = sBeforereadflow;
                afterreadflow = sAfterreadflow;
            } else if (taskName.contains("甲醇装车")) {
                if (sqliteHelper.getIdByNum(mSpinnerCar.getSelectedItem().toString()).getTankid() != 0) {
                    vehicleNo = sqliteHelper.getIdByNum(mSpinnerCar.getSelectedItem().toString()).getTankid() + "";
                } else {
                    Constants.showToast(context, "请联系添加车辆");
                }
                beforeAdd = sZqianye;
                afterAdd = sZhouye;
                addValue = sZuancheliang;

                beforereadflow = sBeforeloading;//装车前流量计读数
                afterreadflow = sAfterloading;//装车后流量计读数
                Outgoingquantity = sOutgoingquantity;//出库量
            } else if (taskName.contains("甲醇注入") || taskName.contains("缓释剂")) {
                if (!sqliteHelper.getAreaByNum(task.getVehicleNumber()).equals("")) {
                    vehicleNo = sqliteHelper.getIdByNum(task.getVehicleNumber()).getTankid() + "";
                    beforeAdd = sZrqyw;
                    addValue = sZrl;
                    afterAdd = sZrhyw;
                } else {
                    addValue = sZrl;
                    victor = sChepaihao;
                }

            } else if (taskName.contains("泡排剂") || taskName.contains("泡排棒")) {
                beforeAdd = sTjl;
            } else if (taskName.contains("回场检验")) {
                qualified = sQualified;
            }

            Intent intent = new Intent(TaskFinishActivity.this, ReadRF.class);//这是跳转到读卡页
            startActivityForResult(intent, 0);
            custom = sReason;
            subBeizhu = samplingAssay;

            subValue1 = beforeAdd;//注入量
            subValue2 = addValue;//注入前
            subValue3 = afterAdd;//注入后
            vehicleid = vehicleNo;//车辆Id
            vehicleNumber = victor;//输入的车辆号

            subDensity = density;//密度
            subEscort = escort;//押运员
            subDriver = driver;//驾驶员
            subBeforereadflow = beforereadflow;//卸车前流量计读数
            subAfterreadflow = afterreadflow;//卸车后流量计读数
            subPoundweight = poundweight;//磅单量
            subOutgoingquantity = Outgoingquantity;//出库量
            subQualified = qualified;//是否合格
//        }
        subRiskidentification = oilapp.getRiskidentification();//风险识别
        subPreventivemeasures = oilapp.getPreventivemeasures();//预防措施

        subStartWorkTime = oilapp.getStartWorkingTime();//開始做時間
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        subEndWorkTime = str;//結束作業時間
        mc.cancel();

        subIntegal = task.getIntegral();

        Intent intent1 = new Intent(this, TaskFinishService.class);
        stopService(intent1);

    }

    //回调方法
    public void qyUpload() {
        if (dictDetails.size() != 0) {
            vehicleNumber = chehao;//输入的车辆号
            subQualified = hege;
            subConcentration = nongdu;
            subBeizhu = samplingAssay;
        } else {
            String sQuyang = etQuyang.getText().toString().trim();
            String sQualified = tvQualified.getText().toString().trim();
            String sConcentration = etConcentration.getText().toString().trim();
            String sGuanchao = etGch.getText().toString().trim();
            String victor = "";
            String qualified = "";
            String concentration = "";
            if (taskName.contains("甲醇取样")) {
                victor = sGuanchao;
                qualified = sQualified;//合格与否
                concentration = sConcentration;//浓度
            }
            vehicleNumber = victor;//输入的车辆号
            subQualified = qualified;
            subConcentration = concentration;
            subBeizhu = samplingAssay;
        }
        String integale = task.getIntegral();
        Log.d("liangxiaojiang", "-----" + integale);
        subIntegal = integale;

        SimpleDateFormat format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        String endDate = format.format(date);
        AfterSubmitHandler afterSubmitHandler = new AfterSubmitHandler();
        String isFinish = "";
        if (spinner.getSelectedItemId() == 1) {
            isFinish = "1";
        } else if (spinner.getSelectedItemId() == 2) {
            isFinish = "0";
        }
        curTotalPicId = "";
        List<ImageBean> imgData = sqliteHelper.getLocalPics(historyId);
//        String curTotalPicId = sqliteHelper.getPicByTypeOfId(historyId).getPicId();
        if (imgData != null && imgData.size() > 0) {
            for (int i = 0; i < imgData.size(); i++) {
                curTotalPicId += ",";
            }
        }
        notFinshTask.setIsAgree(Integer.parseInt(isFinish));
        notFinshTask.setFinishedMemo(subBeizhu);
        if (curTotalPicId == null || curTotalPicId.equals("")) {
            notFinshTask.setIsHavePic(0);
        } else {
            notFinshTask.setIsHavePic(1);
        }
        if (isTakePhoto) {
            notFinshTask.setPicUrl(takePhotoCompressName);
        }
        notFinshTask.setStartDate(startTime);
        notFinshTask.setEndDate(endDate);

        uploadDialog = new ProgressDialog(context);
        uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        uploadDialog.setTitle("数据上传");
        uploadDialog.setMessage("数据上传中，请稍后...");
        oilapp.setNumber(1);
        uploadDialog.show();

        Log.d("lxj", subQualified + "////" + subConcentration);
        Log.d("lxj", subIntegal);
        http.requestFinishTask(afterSubmitHandler, actId, taskId, startTime, endDate, userId, isFinish, subBeizhu, curTotalPicId, subValue1, subValue2, sWorkJilu, vehicleid, subValue3, vehicleNumber, custom, subQualified, subConcentration, subDriver, subEscort, subPoundweight, subDensity, subBeforereadflow, subAfterreadflow, subOutgoingquantity, subRiskidentification, subPreventivemeasures, subStartWorkTime, subEndWorkTime, subIntegal);
        Constants.IS_WORKING = false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
//                intentFrom = getIntent().getIntExtra("intentFrom", 1);
//                typeOfId = getIntent().getStringExtra("h");

                //这是判断图片的逻辑
                if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
                    ImageGroup newImageGroup = (ImageGroup) data.getSerializableExtra("imageSelected");
                    if (newImageGroup != null && newImageGroup.getImageSets().size() > 0) {
                        imageSets.clear();
                        imageGroup = newImageGroup;
                        List<ImageBean> newData = imageGroup.getImageSets();
                        for (int i = 0; i < newData.size(); i++) {
                            imageSets.add(newData.get(i));
                        }
                        finishAdapter.notifyDataSetChanged();
                        application.setImages(imageSets);
                    }

                    String names = "";
                    String urls = "";
                    for (int i = 0; i < imageSets.size(); i++) {
                        ImageBean bean = imageSets.get(i);
                        bean.setUploadTaskId(historyId);
                        names += bean.getDisplayName() + ";";
                        urls += bean.getPath() + ";";
                        sqliteHelper.insertLocalPic(bean);
                    }
                    if (imageSets.size() > 0) {
                        Picture pic = new Picture();
                        pic.setChargerId(user.getUserId());
                        pic.setCreateTime(DateUtils.getDateTime());
                        pic.setName(names);
                        pic.setType(intentFrom);
                        pic.setUrl(urls);
                        pic.setIsWrokUpdate(0);
                        pic.setTypeOfId(historyId);
                        pic.setIsUploadSuccess(0);
                        sqliteHelper.insertPic(pic);
                        takePhotoCompressName = pic.getUrl();
                    }
                }


                String rfidData1 = data.getStringExtra("code1");
                String deviceIds = task.getDeviceIds();
                if (rfidData1 != null && rfidData1.length() > 0) {
                    if (deviceIds.contains(rfidData1) ||
                            (deviceIds.length() < 12 && (deviceIds + "0001").equals(rfidData1))) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        String endDate = format.format(date);
                        AfterSubmitHandler afterSubmitHandler = new AfterSubmitHandler();
                        String isFinish = "";
                        if (spinner.getSelectedItemId() == 1) {
                            isFinish = "1";
                        } else if (spinner.getSelectedItemId() == 2) {
                            isFinish = "0";
                        }
                        curTotalPicId = "";
                        List<ImageBean> imgData = sqliteHelper.getLocalPics(historyId);
                        if (imgData != null && imgData.size() > 0) {
                            for (int i = 0; i < imgData.size(); i++) {
                                curTotalPicId += ",";
                            }
                        }
                        notFinshTask.setIsAgree(Integer.parseInt(isFinish));
                        notFinshTask.setFinishedMemo(subBeizhu);
                        if (curTotalPicId == null || curTotalPicId.equals("")) {
                            notFinshTask.setIsHavePic(0);
                        } else {
                            notFinshTask.setIsHavePic(1);
                        }
                        if (isTakePhoto) {
                            notFinshTask.setPicUrl(takePhotoCompressName);
                        }
                        notFinshTask.setStartDate(startTime);
                        notFinshTask.setEndDate(endDate);

                        uploadDialog = new ProgressDialog(context);
                        uploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        uploadDialog.setTitle("数据上传");
                        uploadDialog.setMessage("数据上传中，请稍后...");
                        oilapp.setNumber(1);
                        uploadDialog.show();

                        Log.d("TaskFinishActivity", curTotalPicId);
                        http.taskTemplate(afterSubmitHandler,dictDetails);
                        http.requestFinishTask(afterSubmitHandler, actId, taskId, startTime, endDate, userId, isFinish, subBeizhu, curTotalPicId, subValue1, subValue2, sWorkJilu, vehicleid, subValue3, vehicleNumber, custom, subQualified, subConcentration, subDriver, subEscort, subPoundweight, subDensity, subBeforereadflow, subAfterreadflow, subOutgoingquantity, subRiskidentification, subPreventivemeasures, subStartWorkTime, subEndWorkTime, subIntegal);
                        Constants.IS_WORKING = false;
                    } else {
                        Constants.showToast(context, "非本站点巡检卡！");
                    }
                }
        }
    }

    private class AfterSubmitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    uploadDialog.dismiss();
                    notFinshTask.setIsFinished(1);
                    if (sqliteHelper.updateTaskFinishState(notFinshTask)) {
//                        Toast.makeText(context, "网络异常，完成的任务已保存到本地，请有网络的时候提交", Toast.LENGTH_LONG).show();
                        if (taskName.contains("甲醇取样")) {
                            Message msg4 = TaskActivity.refreshHandler.obtainMessage();
                            msg4.sendToTarget();
                            TaskFinishActivity.this.finish();
                        } else {
                            Message msg3 = TaskDetailActivity.finishHandler.obtainMessage();
                            msg3.what = 200;
                            msg3.sendToTarget();
                            Message msg4 = TaskActivity.refreshHandler.obtainMessage();
                            msg4.sendToTarget();
                            TaskFinishActivity.this.finish();
                        }
                    }
                    break;

                case HttpRequest.REQUEST_SUCCESS:
                    uploadDialog.dismiss();
                    if (msg.obj.toString().contains("success")) {
                        isSuccess = true;
                    } else {
                        isSuccess = false;
                    }
                    if (isSuccess) {
                        if (taskName.contains("甲醇取样")) {
                            Message msg2 = TaskActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                            Toast.makeText(context, "数据上传成功", Toast.LENGTH_SHORT).show();
                            TaskFinishActivity.this.finish();

                            notFinshTask.setIsFinished(2);
                            sqliteHelper.updateTaskFinishState(notFinshTask);
                        } else {
                            Message msg3 = TaskDetailActivity.finishHandler.obtainMessage();
                            msg3.what = 200;
                            msg3.sendToTarget();
                            Message msg2 = TaskActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                            Toast.makeText(context, "数据上传成功", Toast.LENGTH_SHORT).show();
                            TaskFinishActivity.this.finish();

                            notFinshTask.setIsFinished(2);
                            sqliteHelper.updateTaskFinishState(notFinshTask);

                            // 更新此条上报异常数据对应的图片信息
                            Picture picture = sqliteHelper.getPicByTypeOfId(historyId);
                            if (picture != null) {
                                picture.setTypeOfId(historyId);
                                sqliteHelper.updatePic(picture);
                            }
                        }
                    } else {
                        Toast.makeText(context, "服务器响应异常，请点击“确定”重新提交数据！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void dialog() {

        LayoutInflater layoutView = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutView.inflate(R.layout.dialog_job_preview, null);
        TextView happening = (TextView) view.findViewById(R.id.dialog_job_preview_happening);
        happening.setText("" + spinner.getSelectedItem().toString());
        TextView recording = (TextView) view.findViewById(R.id.dialog_job_preview_recording);
        recording.setText("" + etWorkJilu.getText().toString());
        TextView remark = (TextView) view.findViewById(R.id.dialog_job_preview_remark);
        remark.setText("" + etBeizhu.getText().toString());
        GridView mgridview = (GridView) view.findViewById(R.id.finish_add_picture);
        mgridview.setAdapter(new FinishAdapter(this, imageSets));
        ImageView iImageview = (ImageView) view.findViewById(R.id.task_detail_iv_read_card);
        iImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (taskName.contains("甲醇取样")) {
                    //回调方法
                    qyUpload();
                } else {
                    Readers();
                }
            }
        });

        if (dictDetails.size() != 0) {
            titles_lll = (LinearLayout) view.findViewById(R.id.titles_lll);
            for (int i = 0; i < dictDetails.size(); i++) {
                final RelativeLayout ll = (RelativeLayout) LayoutInflater.from(TaskFinishActivity.this).inflate(R.layout.item_task_finish, null);
                final TextView btn = (TextView) ll.findViewById(R.id.tv_work_name);
                btn.setText(dictDetails.get(i).getTitle());
                btn.setTextColor(context.getResources().getColor(R.color.blue1));
                TextView editText = (TextView) ll.findViewById(R.id.et_shuzi);

//                if (dictDetails.get(i).getTaskName().contains("甲醇装车")) {
                if (dictDetails.get(i).getType()==2) {
                    editText.setText(((Spinner) list.get(i).get("value2")).getSelectedItem().toString());
                }else if (dictDetails.get(i).getType()==4){
                        editText.setText(checkbox);
                }
                else {
                    editText.setText(((TextView) list.get(i).get("value")).getText().toString());
                }
                titles_lll.addView(ll);
                dictDetails.get(i).setContent(editText.getText().toString());
                sqliteHelper.insertTemplate1(dictDetails);//将填写的数据保存到dict_details表中
            }
        } else {
//            titles_lll.setVisibility(View.GONE);
            RelativeLayout r1 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl5);
            RelativeLayout r2 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl6);
            RelativeLayout r3 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl7);
            RelativeLayout r4 = (RelativeLayout) view.findViewById(R.id.rl_ceshi);
            RelativeLayout r5 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl18);
            RelativeLayout r6 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl19);
            RelativeLayout r7 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl20);
            RelativeLayout r8 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl21);
            RelativeLayout r9 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl22);
            RelativeLayout r10 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl23);
            RelativeLayout r11 = (RelativeLayout) view.findViewById(R.id.item_exception_job_rl15);

            TextView tv1 = (TextView) view.findViewById(R.id.dialog_job_preview_remark5);
            TextView tv1_1 = (TextView) view.findViewById(R.id.item_exception_job_tv5);
            TextView tv2 = (TextView) view.findViewById(R.id.dialog_job_preview_remark6);
            TextView tv2_1 = (TextView) view.findViewById(R.id.item_exception_job_tv6);
            TextView tv3 = (TextView) view.findViewById(R.id.dialog_job_preview_remark7);
            TextView tv3_1 = (TextView) view.findViewById(R.id.item_exception_job_tv7);
            TextView tv4 = (TextView) view.findViewById(R.id.tv_ruhouces);
            TextView tv4_1 = (TextView) view.findViewById(R.id.tv_ruhou);
            TextView tv5 = (TextView) view.findViewById(R.id.dialog_job_preview_remark18);
            TextView tv5_1 = (TextView) findViewById(R.id.item_exception_job_tv18);
            TextView tv6 = (TextView) view.findViewById(R.id.dialog_job_preview_remark19);
            TextView tv6_1 = (TextView) view.findViewById(R.id.item_exception_job_tv19);
            TextView tv7 = (TextView) view.findViewById(R.id.dialog_job_preview_remark20);
            TextView tv7_1 = (TextView) view.findViewById(R.id.item_exception_job_tv20);
            TextView tv8 = (TextView) view.findViewById(R.id.dialog_job_preview_remark21);
            TextView tv8_1 = (TextView) view.findViewById(R.id.item_exception_job_tv21);
            TextView tv9 = (TextView) view.findViewById(R.id.dialog_job_preview_remark22);
            TextView tv9_1 = (TextView) view.findViewById(R.id.item_exception_job_tv22);
            TextView tv10 = (TextView) view.findViewById(R.id.dialog_job_preview_remark23);
            TextView tv10_1 = (TextView) view.findViewById(R.id.item_exception_job_tv23);
            TextView tv11 = (TextView) view.findViewById(R.id.dialog_job_preview_remark15);
            TextView tv11_1 = (TextView) findViewById(R.id.item_exception_job_tv15);


            if (taskName.contains("甲醇入库")) {
                r1.setVisibility(View.VISIBLE);
                r2.setVisibility(View.VISIBLE);
                r3.setVisibility(View.VISIBLE);
                r4.setVisibility(View.VISIBLE);
                r6.setVisibility(View.VISIBLE);
                r7.setVisibility(View.VISIBLE);
                r8.setVisibility(View.VISIBLE);
                r9.setVisibility(View.VISIBLE);
                r10.setVisibility(View.VISIBLE);
                tv1.setText(etJsfs.getText().toString());
                tv1_1.setText("入库量：");
                tv2.setText(etruqianyewei.getText().toString());
                tv2_1.setText("卸车前储罐液位：");
                tv3.setText(etGch.getText().toString());
                tv3_1.setText("卸车车号：");
                tv4.setText(etruhouyewei.getText().toString());
                tv4_1.setText("卸车后储罐液位：");
                tv6.setText(etEscort.getText().toString());
                tv6_1.setText("押运员：");
                tv7.setText(etPoundweight.getText().toString());
                tv7_1.setText("磅单量：");
                tv8.setText(etDensity.getText().toString());
                tv8_1.setText("密度：");
                tv9.setText(etBeforereadflow.getText().toString());
                tv9_1.setText("卸车前流量计读数：");
                tv10.setText(etAfterreadflow.getText().toString());
                tv10_1.setText("卸车后流量计读数：");
            }
            if (taskName.contains("甲醇装车")) {
                r1.setVisibility(View.VISIBLE);
                r2.setVisibility(View.VISIBLE);
                r3.setVisibility(View.VISIBLE);
                r4.setVisibility(View.VISIBLE);
                tv1.setText(mSpinnerCar.getSelectedItem().toString());
                tv1_1.setText("车号：");
                tv2.setText(etZqianyw.getText().toString());
                tv2_1.setText("装前液位（mm)：");
                tv3.setText(etZhouyw.getText().toString());
                tv3_1.setText("装后液位（mm)：");
                tv4.setText(etZhuangchel.getText().toString());
                tv4_1.setText("装车量（方）：");
                r8.setVisibility(View.VISIBLE);
                r6.setVisibility(View.VISIBLE);
                r7.setVisibility(View.VISIBLE);
                tv8.setText(etBeforeloading.getText().toString());
                tv8_1.setText("装车前流量计读数：");
                tv6.setText(etAfterloading.getText().toString());
                tv6_1.setText("装车后流量计读数：");
                tv7.setText(etOutgoingquantity.getText().toString());
                tv7_1.setText("出库量：");
            }
            if (taskName.contains("甲醇取样")) {
                r3.setVisibility(View.VISIBLE);
                r8.setVisibility(View.VISIBLE);
                tv3.setText(etGch.getText().toString());
                tv3_1.setText("卸车车号：");
                tv8.setText(etQuyang.getText().toString());
                tv8_1.setText("甲醇取样化验：");

                r1.setVisibility(View.VISIBLE);
                r2.setVisibility(View.VISIBLE);
                tv1.setText(etConcentration.getText().toString());
                tv1_1.setText("浓度：");
                tv2.setText(tvQualified.getText().toString());
                tv2_1.setText("是否合格：");
            }
            if (taskName.contains("甲醇注入") || taskName.contains("缓释剂")) {
                if (!sqliteHelper.getAreaByNum(task.getVehicleNumber()).equals("")) {
                    r1.setVisibility(View.VISIBLE);
                    r2.setVisibility(View.VISIBLE);
                    r3.setVisibility(View.VISIBLE);
                    tv1.setText(etZrqyw.getText().toString());
                    tv1_1.setText("注入前液位(mm)：");
                    tv2.setText(etZrhyw.getText().toString());
                    tv2_1.setText("注入后液位(mm)：");
                    tv3.setText(etZrl.getText().toString());
                    tv3_1.setText("注入量(方)：");
                } else {
                    r1.setVisibility(View.VISIBLE);
                    r2.setVisibility(View.VISIBLE);
                    tv1.setText(etLicenseplate.getText().toString());
                    tv1_1.setText("设备类型和牌号：");
                    tv2.setText(etZrl.getText().toString());
                    tv2_1.setText("注入量(方)：");
                }
            }

            if (taskName.contains("回场检验")) {
                r1.setVisibility(View.VISIBLE);
                tv1.setText(etShifouhege.getText().toString());
                tv1_1.setText("确认注入量是否合格：");
            }

        }
        final AlertDialog builder = new AlertDialog.Builder(this).create();//先得到构造器
        builder.setView(view);

        ImageView imageViewBack = (ImageView) view.findViewById(R.id.task_ddialog_btn_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });

        builder.show();
    }


    /**
     * 这是倒计时
     */
    private void initServ() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sdf.format(curDate);
        String start = task.getDeadTime();

        try {
            timeStart = sdf.parse(start).getTime();
            timeEnd = sdf.parse(str).getTime();
            dayCount = ((timeStart - timeEnd));
            mc = new MyCountDownTimer(dayCount, 1000);
            mc.start();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mc.cancel();
    }

    /**
     * 继承 CountDownTimer 防范
     * <p>
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    class MyCountDownTimer extends CountDownTimer {
        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少微秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            endTime.setText("已超时");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Integer ss = 1000;
            Integer mi = ss * 60;
            Integer hh = mi * 60;
            Integer dd = hh * 24;

            Log.d("lxj", "-----millisUntilFinished1-----" + millisUntilFinished);

            Long day = millisUntilFinished / dd;
            Long hour = (millisUntilFinished - day * dd) / hh;
            Long minute = (millisUntilFinished - day * dd - hour * hh) / mi;
            Long second = (millisUntilFinished - day * dd - hour * hh - minute * mi) / ss;
            // 正在倒计时
            endTime.setText("剩余作业时间 ：" + day + "天" + hour + "时" + minute + "分" + second + "秒");
        }
    }

    /**
     * 下拉选择车辆得到横截面
     */
    private class OnItemSelectedListenerImpl implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view,
                                   int position, long id) {
            if (position != 0) {
                vehicleid = sqliteHelper.getIdByNum(TaskFinishActivity.this.data_list.get(position)).getTankid() + "";
            }
            if (joinCarMax.get(position) != null) {
                String sH = TaskFinishActivity.this.joinCarMax.get(position) + "";
                hjm = Double.parseDouble(sH) / 10000;
            }

            TaskFinishActivity.this.etHjiemaian.setText(hjm + "");
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {   // 没有选项时触发
        }

    }

    /**
     * EditText监听参数watcher
     */
    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            DecimalFormat df = new DecimalFormat("#0.000");
            if (dictDetails.size() != 0) {
                for (int m = 0; m < list.size(); m++) {
                    title = ((TextView) list.get(m).get("title")).getText().toString();
                    value = ((EditText) list.get(m).get("value")).getText().toString();
                    if (list.get(m).get("workName").toString().contains("甲醇入库")) {
                        if (title.contains("卸车车号") && !value.equals("")) {
//                            victor = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("驾驶员") && !value.equals("")) {
//                            driver = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("押运员") && !value.equals("")) {
//                            escort = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.equals("磅单量") && !value.equals("")) {
//                            poundweight = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.equals("卸车前储罐液位") && !value.equals("")) {
//                            beforeAdd = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.equals("卸车后储罐液位") && !value.equals("")) {
//                            afterAdd = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.equals("卸车前流量计读数") && !value.equals("")) {
//                            beforereadflow = value;
                            zrqyw = Double.parseDouble(value);
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);

                        }
                        if (title.equals("卸车后流量计读数") && !value.equals("")) {
//                            afterreadflow = value;
                            zrhyw = Double.parseDouble(value);
                            dictDetails.get(m).setContent(value);
                            if (zrqyw != null && zrhyw != null) {
                                addValue = df.format(zrhyw - zrqyw);
                                dictDetails.get(m + 1).setContent(addValue);
                            }
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("密度") && !value.equals("")) {
//                            density = value;
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                            double density = Double.parseDouble(value);
                            if (density < 0.80 && density > 0.78) {
                                et1.setTextColor(context.getResources().getColor(R.color.black));
                            } else {
                                et1.setTextColor(context.getResources().getColor(R.color.red));
                            }
                        }
                        if (zrqyw != null && zrhyw != null) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
//                                        oilapp.setJiac(1);
                                        gengxinHandler.sendMessage(gengxinHandler.obtainMessage());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }
                    } else if (list.get(m).get("workName").toString().contains("甲醇注入")) {

                        if (title.contains("注入前液位") && !value.equals("")) {
//                            addValue = value;
                            zrqyw = Double.parseDouble(value);
                        }
                        if (title.contains("注入后液位") && !value.equals("")) {
//                            afterAdd = value;
                            zrhyw = Double.parseDouble(value);
                        }
                        String[] carArea = areaTankCar.split("\\*");
                        double jiemj = Double.parseDouble(carArea[0]) * Double.parseDouble(carArea[1]) / 10000;
                        if (zrqyw != null && zrhyw != null) {
                                        if (title.contains("注入量") && value.equals("")) {
                                            beforeAdd = df.format((zrqyw - zrhyw) / 1000 * jiemj);
                                            et1.setText(df.format((zrqyw - zrhyw) / 1000 * jiemj) + "");
                                        }
                        }
                        dictDetails.get(m).setContent(value);
                        sqliteHelper.insertTemplate1(dictDetails);
                    } else if (list.get(m).get("workName").toString().contains("甲醇装车")) {
                        value2 = ((Spinner) list.get(m).get("value2")).getSelectedItemId() + "";
                        if (title.contains("车号") && !value2.equals("")) {
                            if (sqliteHelper.getIdByNum(((Spinner) list.get(m).get("value2")).getSelectedItem().toString()).getTankid() != 0) {
                                vehicleNo = sqliteHelper.getIdByNum(((Spinner) list.get(m).get("value2")).getSelectedItem().toString()).getTankid() + "";
                            } else {
                                Constants.showToast(context, "请联系添加车辆");
                            }
//                            vehicleNo = ((Spinner) list.get(m).get("value2")).getSelectedItem().toString();
                            dictDetails.get(m).setContent(value2);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("装前液位") && !value.equals("")) {
//                            beforeAdd = value;
                            zqian = Double.parseDouble(value);
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("装后液位") && !value.equals("")) {
//                            afterAdd = value;
                            zhou = Double.parseDouble(value);
                            addValue = df.format((zhou - zqian) / 1000 * hjm);

                            dictDetails.get(m).setContent(value);
                            dictDetails.get(m + 3).setContent(df.format((zhou - zqian) / 1000 * hjm));
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("装车前流量计读数") && !value.equals("")) {
//                            beforereadflow = value;
                            zqianliuliang = Double.parseDouble(value);
                            dictDetails.get(m).setContent(value);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (title.contains("装车后流量计读数") && !value.equals("")) {
//                            afterreadflow = value;
                            zhouliuliang = Double.parseDouble(value);
                            Outgoingquantity = df.format(zqianliuliang - zhouliuliang);
                            dictDetails.get(m).setContent(value);
                            dictDetails.get(m + 2).setContent(df.format(zqianliuliang - zhouliuliang));
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (zqian != null && zhou != null && zqianliuliang != null && zhouliuliang != null && oilapp.getJiac() != 1) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(3000);
                                        oilapp.setJiac(1);
                                        gengxinHandler.sendMessage(gengxinHandler.obtainMessage());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();
                        }

                    } else if (list.get(m).get("workName").toString().contains("甲醇取样")) {
                        if (title.contains("卸车车号") && !value.equals("")) {
//                            chehao = value;
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String str = sdf.format(curDate);
                            dictDetails.get(m).setContent(chehao);
                            dictDetails.get(m + 2).setContent(chehao + "/" + str);
                            samplingAssay = chehao + "/" + str;
                            sqliteHelper.insertTemplate1(dictDetails);

                        }
                        if (title.contains("浓度") && !value.equals("")) {
//                            nongdu = value;
                            double concentration = Double.parseDouble(nongdu);
                            if (concentration >= 98.8) {
                                if (concentration > 100) {
                                    nongdu = "";
                                    Toast.makeText(context, "输入的值是百分比，不能大于100", Toast.LENGTH_SHORT).show();
                                } else {
                                    hege = "合格";
                                }
                            } else {
                                hege = "不合格";
                            }
                            dictDetails.get(m).setContent(nongdu);
                            dictDetails.get(m + 2).setContent(hege);
                            sqliteHelper.insertTemplate1(dictDetails);
                        }
                        if (chehao != null && nongdu != null) {
                            new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                        gengxinHandler.sendMessage(gengxinHandler.obtainMessage());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }).start();

                        }

                    }
                }
            } else {

//            DecimalFormat df = new DecimalFormat("#0.000");
                // TODO Auto-generated method stub
                if (!etZrqyw.getText().toString().equals("") && !etZrhyw.getText().toString().equals("")) {
                    //甲醇注入
                    double zrqyw = Double.parseDouble(etZrqyw.getText().toString());
                    double zrhyw = Double.parseDouble(etZrhyw.getText().toString());
                    String[] carArea = areaTankCar.split("\\*");
                    double jiemj = Double.parseDouble(carArea[0]) * Double.parseDouble(carArea[1]) / 10000;
                    df.format((zrqyw - zrhyw) / 1000 * jiemj);
                    //根据初始化时得到对应车辆截面积来计算注入量
                    etZrl.setText(df.format((zrqyw - zrhyw) / 1000 * jiemj) + "");
                } else if (!etHjiemaian.getText().toString().equals("") && !etZqianyw.getText().toString().equals("") && !etZhouyw.getText().toString().equals("")) {
                    //甲醇装车
                    double zq = Double.parseDouble(etZqianyw.getText().toString());
                    double zh = Double.parseDouble(etZhouyw.getText().toString());
                    double hj = Double.parseDouble(etHjiemaian.getText().toString());
                    df.format((zh - zq) / 1000 * hj);
                    etZhuangchel.setText(df.format((zh - zq) / 1000 * hj) + "");
                    if (!etBeforeloading.getText().toString().equals("") && !etAfterloading.getText().toString().equals("")) {
                        double Beforeloading = Double.parseDouble(etBeforeloading.getText().toString());
                        double Afterloading = Double.parseDouble(etAfterloading.getText().toString());
                        etOutgoingquantity.setText(df.format((Beforeloading - Afterloading)));
                    }
                } else if (!etGch.getText().toString().equals("")) {//甲醇取样
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = sdf.format(curDate);
                    etQuyang.setText(etGch.getText().toString() + "/" + str);
                    samplingAssay = etGch.getText().toString() + "/" + str;

                    if (!etConcentration.getText().toString().equals("")) {
                        double concentration = Double.parseDouble(etConcentration.getText().toString());
                        if (concentration >= 98.8) {
                            if (concentration > 100) {
                                etConcentration.setText("");
                                Toast.makeText(context, "输入的值是百分比，不能大于100", Toast.LENGTH_SHORT).show();
                            } else {
                                tvQualified.setText("合格");
                                tvQualified.setTextColor(context.getResources().getColor(R.color.black));
                            }
                        } else {
                            tvQualified.setText("不合格");
                            tvQualified.setTextColor(context.getResources().getColor(R.color.red));
                        }
                    }
                } else if (!etDensity.getText().toString().equals("")) {//甲醇入库（判断密度是否合格、卸车前计读数-卸车后计读数得到入库量）
                    double density = Double.parseDouble(etDensity.getText().toString());
                    if (density < 0.80 && density > 0.78) {
                        etDensity.setTextColor(context.getResources().getColor(R.color.black));
                    } else {
                        etDensity.setTextColor(context.getResources().getColor(R.color.red));
                    }
                    if ((!etBeforereadflow.getText().toString().equals("") && !etAfterreadflow.getText().toString().equals(""))) {
                        double Beforereadflow = Double.parseDouble(etBeforereadflow.getText().toString());
                        double Afterreadflow = Double.parseDouble(etAfterreadflow.getText().toString());
                        etJsfs.setText(df.format(Afterreadflow - Beforereadflow));

                    }
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
     */
    private void refreshUI() {
        template();
    }
}
