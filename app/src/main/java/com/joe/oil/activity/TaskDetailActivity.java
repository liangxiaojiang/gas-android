package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;
import com.joe.oil.util.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author baiqiao
 * @description 派工任务详情界面
 * @data: 2014年7月7日 上午9:46:11
 * @email baiqiao@lanbaoo.com
 */
@SuppressLint("HandlerLeak")
public class TaskDetailActivity extends BaseActivity implements OnClickListener {

    private Context context;
    private ImageView back;
    //	private TextView taskDetail;
    private TextView taskName;
    private TextView createdTime;
    private TextView tvCompleteTime;
    private TextView endTime;
    private TextView operationCard;
    private TextView charger;
    private TextView partner;
    private TextView memo;
    private TextView vehicleNumber;
    private TextView vehicleDriverName;
    private TextView vehicleDriverPhone;
    private TextView riskTips;
    private TextView controlTips;
    private RelativeLayout rlTime;
    //	private ProgressBar progressBar;
    private ImageView readCard;

    private Dialog readSuccessDialog;
    private boolean isFirstRead = true;

    private String intentFrom;


    private List<Task> taskall;


    /**
     * 第一次读卡成功后通知当前界面计时
     */
    public static Handler knowReadSuccessHandler;
    /**
     * 每隔一秒刷新当前界面
     */
    private Handler refreshUIHandler;
    /**
     * 第二次读卡成功后通知当前界面弹出任务完成情况登记框
     */
    public static Handler secondReadSuccessHandler;
    /**
     * 派工任务时长
     */
    private int time;

    public static Handler finishHandler;

    private Task task;
    private String startTime;
    private String deadTime = "";

    private SQLiteDatabase dbHelper = null;

    private OilApplication oilapp;
    private int isFinishing;

    public static Handler taskfinshHandler;

    public TaskFinishActivity.MyReceiver myReceiver;

    private RelativeLayout rl_zhedie;
    private ScrollView scrollview;
    private ImageView taskdetailhmore, taskdetailhmore1;
    private int by;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_detail);

        initMembers();
        initView();
        initData();
        setknowReadSuccessHandler();
        setrefreshUIHandler();
        setsecondReadSuccessHandler();
        setFinishHandler();
        context = this;
        oilapp = (OilApplication) getApplicationContext();

        setTaskFinishHandler();

        //1.创建广播接收者对象
        myReceiver = new TaskFinishActivity.MyReceiver();
        //2.创建intent-filter对象
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //3.注册广播接收者
        registerReceiver(myReceiver, filter);

    }

    /**
     * 这是判断是否是Taskfinish中点击圆形进度条来判断的
     */
    private void setTaskFinishHandler() {
        taskfinshHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (oilapp.getFinsh() == 1) {
                    readCard.setVisibility(View.INVISIBLE);
                    rlTime.setVisibility(View.INVISIBLE);
                    endTime.setVisibility(View.INVISIBLE);
                    oilapp.setFinsh(2);
                }

            }
        };
    }


    private void initView() {
        back = (ImageView) this.findViewById(R.id.task_detail_btn_back);
        taskName = (TextView) this.findViewById(R.id.task_detail_tv_name);
//		taskDetail = (TextView) this.findViewById(R.id.task_detail_tv_detail);
        createdTime = (TextView) this.findViewById(R.id.task_detail_tv_created_time);
        tvCompleteTime = (TextView) this.findViewById(R.id.tv_complete_time);
//		progressBar = (ProgressBar) this.findViewById(R.id.task_detail_progressbar);
        readCard = (ImageView) this.findViewById(R.id.task_detail_iv_read_card);
        endTime = (TextView) this.findViewById(R.id.task_detail_tv_end_time);
        operationCard = (TextView) this.findViewById(R.id.task_detail_operation_card);
        rlTime = (RelativeLayout) this.findViewById(R.id.rr_time);
//		taskDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        charger = (TextView) this.findViewById(R.id.charger);
        partner = (TextView) this.findViewById(R.id.partner);
        memo = (TextView) this.findViewById(R.id.memo);
        vehicleNumber = (TextView) this.findViewById(R.id.vehicleNumber);
        vehicleDriverName = (TextView) this.findViewById(R.id.vehicleDriverName);
        vehicleDriverPhone = (TextView) this.findViewById(R.id.vehicleDriverPhone);
        riskTips = (TextView) this.findViewById(R.id.riskTips);
        controlTips = (TextView) this.findViewById(R.id.controlTips);

        if (intentFrom.equals("history")) {
            readCard.setVisibility(View.GONE);
            rlTime.setVisibility(View.GONE);
//			progressBar.setVisibility(View.INVISIBLE);
            endTime.setVisibility(View.GONE);

        } else {
            if (task.getDeviceIds().length() < 8) {
                readCard.setImageResource(R.drawable.selector_btn_start);
            } else {
                readCard.setImageResource(R.drawable.selector_btn_read_card);
            }
        }


        back.setOnClickListener(this);
        readCard.setOnClickListener(this);
        operationCard.setOnClickListener(this);

        rl_zhedie = (RelativeLayout) findViewById(R.id.rl_zhedie);
        scrollview = (ScrollView) findViewById(R.id.scrollview);
        taskdetailhmore = (ImageView) findViewById(R.id.taskdetailhmore);
        taskdetailhmore1 = (ImageView) findViewById(R.id.taskdetailhmore1);
        rl_zhedie.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (by) {
                    case 0:
                        scrollview.setVisibility(View.GONE);
                        taskdetailhmore.setVisibility(View.VISIBLE);
                        taskdetailhmore1.setVisibility(View.GONE);
                        by = 1;
                        break;
                    case 1:
                        scrollview.setVisibility(View.VISIBLE);
                        taskdetailhmore1.setVisibility(View.VISIBLE);
                        taskdetailhmore.setVisibility(View.GONE);
                        by = 0;
                        break;
                }
            }
        });

    }

    @SuppressLint("NewApi")
    private void initMembers() {
        context = TaskDetailActivity.this;
        task = (Task) getIntent().getSerializableExtra("task");

        time = Integer.parseInt(task.getInterval());
        intentFrom = getIntent().getStringExtra("intentFrom");

        taskall = new ArrayList<Task>();


    }


    /**
     * TODO 第一次读卡成功通知当前界面开启线程计时
     *
     * @date 2014年6月5日 上午11:00:11
     */
    @SuppressLint("HandlerLeak")
    private void setknowReadSuccessHandler() {
        knowReadSuccessHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                readCard.setImageResource(R.drawable.btn_working);
                readCard.setClickable(false);
                pThread.start();
                Constants.IS_WORKING = true;
            }
        };
    }

    Thread pThread = new Thread(new Runnable() {
        @Override
        public void run() {

            for (int i = 1; i <= time * 60; i++) {
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

    });

    /**
     * TODO 每隔一秒刷新一次当前界面
     *
     * @date 2014年6月5日 上午11:23:03
     */
    private void setrefreshUIHandler() {
        refreshUIHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                int currentTime = Integer.parseInt(msg.obj.toString());

//				int progress = (int) 100 * currentTime / (time * 60);
//				progressBar.setProgress(progress);

                int minute = (int) (time * 60 - currentTime) / 60;
                int second = (time * 60 - currentTime) % 60;
                if (minute > 0) {
                    endTime.setTextSize(14);
                    endTime.setText("剩余时间:  " + minute + "分" + second + "秒");
                    readCard.setImageResource(R.drawable.btn_working);
                    readCard.setClickable(false);
                } else if (second == 0) {
                    if (task.getDeviceIds().length() < 8) {
                        endTime.setTextSize(14);
                        endTime.setText("可以完成任务");
                        readCard.setImageResource(R.drawable.selector_btn_complete);
                        readCard.setClickable(true);
                        isFirstRead = false;
                    } else {
                        endTime.setTextSize(14);
                        endTime.setText("可以再次读卡！！");
                        readCard.setImageResource(R.drawable.btn_readcard);
                        readCard.setClickable(true);
                        isFirstRead = false;
                    }


                } else {
                    endTime.setTextSize(14);
                    endTime.setText("剩余时间:  " + second + "秒");
                    readCard.setImageResource(R.drawable.btn_working);
                    readCard.setClickable(false);
                }
            }
        };
    }

    private void setsecondReadSuccessHandler() {
        secondReadSuccessHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(TaskDetailActivity.this, TaskFinishActivity.class);
                intent.putExtra("taskId", task.getTaskId());
                intent.putExtra("actId", task.getActId());
                intent.putExtra("historyId", task.getHistoryId());
                intent.putExtra("taskName", task.getName());
                intent.putExtra("imgUrl", task.getOperateCardUrl());
                intent.putExtra("task", task);
                intent.putExtra("intentFrom", "TaskDetailActivity");
                startActivity(intent);
            }
        };
    }

    private void setFinishHandler() {
        finishHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                TaskDetailActivity.this.finish();
            }
        };
    }

    /**
     * initData这是当读卡完成后传递到下个界面的逻辑
     */
    private void initData() {
        String name = task.getName();
        String chargerName = task.getChargerName();
        String partnerName = task.getPartnerName();
        String vehicleNumbers = task.getVehicleNumber();
        String vehicleDriverNames = task.getVehicleDriverName();
        String vehicleDriverPhones = task.getVehicleDriverPhone();
        String createTime = task.getCreateTime();
        String riskTipss = task.getRiskTips();
        String controlTipss = task.getControlTips();
        String area = task.getDeviceNames();


        taskName.setText(name);
        taskName.getPaint().setFakeBoldText(true);// 加粗
//		taskDetail.setText(area);
        charger.setText(chargerName);
        partner.setText(partnerName);
        vehicleNumber.setText(vehicleNumbers);
        vehicleDriverName.setText(vehicleDriverNames);
        vehicleDriverPhone.setText(vehicleDriverPhones);
        riskTips.setText(riskTipss);
        vehicleDriverPhone.setText(vehicleDriverPhones);
        controlTips.setText(controlTipss);
        memo.setText(task.getMemo());


        endTime.setTextSize(14);
        endTime.setText("时间要求：作业时间不少于" + time + "分钟");
        createdTime.setText("任务创建时间:  " + createTime);
        createdTime.setTextSize(12);
        tvCompleteTime.setText("要求完成时间:  " + task.getDeadTime());
        tvCompleteTime.setTextSize(12);
        isFirstRead = true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 这是做的监听手机返回键，也让判断返回那页
         * 下面要用finish，不然返回那页在点击返回会留在当前页
         */
        if (oilapp.getFinsh() == 2) {
            Intent intent = new Intent(this, TaskFinishActivity.class);
            intent.putExtra("taskId", task.getTaskId());
            intent.putExtra("actId", task.getActId());
            intent.putExtra("historyId", task.getHistoryId());
            intent.putExtra("taskName", task.getName());
            intent.putExtra("intentFrom", "TaskDetailActivity");
            intent.putExtra("task", task);
            intent.putExtra("imgUrl", task.getOperateCardUrl());
            intent.putExtra("happening", task.getCompletion());
            intent.putExtra("recording", task.getWorkRecord());
            intent.putExtra("remark", task.getMemo());
            startActivity(intent);
            oilapp.setFinsh(0);
//            TaskDetailActivity.this.finish();
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_detail_btn_back:
                /**
                 * 这是用全局变量来判断，当在TaskFinishActivity中点击后，
                 * 在点返回让能准确的判断是那页点击的，让可以回到那页
                 */
                if (oilapp.getFinsh() == 2) {
                    Intent intent = new Intent(this, TaskFinishActivity.class);
                    intent.putExtra("taskId", task.getTaskId());
                    intent.putExtra("actId", task.getActId());
                    intent.putExtra("historyId", task.getHistoryId());
                    intent.putExtra("taskName", task.getName());
                    intent.putExtra("intentFrom", "TaskDetailActivity");
                    intent.putExtra("task", task);
                    intent.putExtra("imgUrl", task.getOperateCardUrl());
                    intent.putExtra("happening", task.getCompletion());
                    intent.putExtra("recording", task.getWorkRecord());
                    intent.putExtra("remark", task.getMemo());
                    startActivity(intent);
                    oilapp.setFinsh(0);
//                    this.finish();
                } else {
                    Intent intent1 = new Intent(this, TaskActivity.class);
                    Bundle bd = new Bundle();
                    bd.putInt("isFinished", 3);
                    intent1.putExtras(bd);
                    startActivity(intent1);
                    this.finish();
                    Constants.IS_WORKING = true;
                }
                break;

            case R.id.task_detail_iv_read_card:

                final String Intent_Action = "zjp";//定义广播，方便我们接收这个广播
                Intent intent1 = new Intent(Intent_Action);
                intent1.putExtra("name", "zhaojunpeng");
                TaskDetailActivity.this.sendBroadcast(intent1);
                if ((oilapp.getNumber() == 3 && task.getIsFinished() == 3) || (oilapp.getNumber() < 3 && task.getIsFinished() == 3)) {
                    Intent intent = new Intent(this, TaskFinishActivity.class);
                    intent.putExtra("taskId", task.getTaskId());
                    intent.putExtra("actId", task.getActId());
                    intent.putExtra("historyId", task.getHistoryId());
                    intent.putExtra("taskName", task.getName());
                    intent.putExtra("intentFrom", "TaskDetailActivity");
                    intent.putExtra("task", task);
                    intent.putExtra("imgUrl", task.getOperateCardUrl());
                    intent.putExtra("happening", task.getCompletion());
                    intent.putExtra("recording", task.getWorkRecord());
                    intent.putExtra("remark", task.getMemo());
                    startActivity(intent);
                } else {
                    if (task.getDeviceIds().length() < 8 && isFirstRead && !Constants.IS_WORKING) {
                        Message msg = knowReadSuccessHandler.obtainMessage();
                        msg.sendToTarget();
                    } else if (!isFirstRead && task.getDeviceIds().length() < 8) {
                        Intent intent = new Intent(TaskDetailActivity.this, TaskFinishActivity.class);
                        intent.putExtra("taskId", task.getTaskId());
                        intent.putExtra("actId", task.getActId());
                        intent.putExtra("historyId", task.getHistoryId());
                        intent.putExtra("taskName", task.getName());
                        intent.putExtra("imgUrl", task.getOperateCardUrl());
                        intent.putExtra("intentFrom", "TaskDetailActivity");
                        intent.putExtra("task", task);
                        startActivity(intent);
                        this.finish();
                    } else if (oilapp.getNumber() < 3 && task.getIsFinished() != 3) {
                            Intent intent = new Intent();
                            intent.setClass(TaskDetailActivity.this, ReadRF.class);
                            startActivityForResult(intent, 0);
                    /*
                    下面注释的是当不读卡时，放开下面的跳转，把上面的跳转注释
                     */
//                        Intent intent = new Intent(this, TaskFinishActivity.class);
//                        intent.putExtra("taskId", task.getTaskId());
//                        intent.putExtra("actId", task.getActId());
//                        intent.putExtra("historyId", task.getHistoryId());
//                        intent.putExtra("taskName", task.getName());
//                        intent.putExtra("intentFrom", "TaskDetailActivity");
//                        intent.putExtra("task", task);
//                        intent.putExtra("imgUrl", task.getOperateCardUrl());
//                        intent.putExtra("happening", task.getCompletion());
//                        intent.putExtra("recording", task.getWorkRecord());
//                        intent.putExtra("remark", task.getMemo());
//                        startActivity(intent);

                    }
                    else {
                        Constants.showToast(context, "有未完成的工作");
                    }
                }
                break;
            case R.id.task_detail_operation_card:

//                    Intent intent = new Intent(TaskDetailActivity.this, ImageListActivity.class);
//                    intent.putExtra("task",task);
//                    intent.putExtra("imgUrl", task.getOperateCardUrl());
//                    startActivity(intent);
                Intent intent = new Intent(TaskDetailActivity.this, OperationDisplayActivity.class);
                intent.putExtra("task",task);
                intent.putExtra("imgUrl", task.getOperateCardUrl());
                startActivity(intent);

                break;

            default:
                break;
        }
    }


//    ////   不用读相对卡的步骤
//    @SuppressLint("SimpleDateFormat")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (resultCode) {
//            case RESULT_OK:
//                String rfidData1 = data.getStringExtra("code1");
//                String deviceIds = task.getDeviceIds();
//                if (rfidData1 != null && rfidData1.length() > 0) {
//                    readSuccessDialog = new FiveStepsDialog(context, isFirstRead);
//                    readSuccessDialog.setCanceledOnTouchOutside(false);
//                    readSuccessDialog.show();
//                }
//
//                break;
//            default:
//                break;
//        }
//    }


    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                String rfidData1 = data.getStringExtra("code1");
                String deviceIds = task.getDeviceIds();
                if (rfidData1 != null && rfidData1.length() > 0) {
                    if (isFirstRead) {
                        if (deviceIds.contains(rfidData1) ||
                                (deviceIds.length() < 12 && (deviceIds + "0001").equals(rfidData1))) {

                            Date date = new Date();
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            startTime = format.format(date);
//                            readSuccessDialog = new ReadSuccessDialog(context, isFirstRead);
                            //添加五步法
//                            readSuccessDialog = new FiveStepsDialog(context, isFirstRead);
//                            readSuccessDialog.setCanceledOnTouchOutside(false);
//                            readSuccessDialog.show();


                            Intent intent = new Intent(TaskDetailActivity.this, FiveStepsActivity.class);
                            intent.putExtra("taskId", task.getTaskId());
                            intent.putExtra("actId", task.getActId());
                            intent.putExtra("historyId", task.getHistoryId());
                            intent.putExtra("taskName", task.getName());
                            intent.putExtra("imgUrl", task.getOperateCardUrl());
                            intent.putExtra("intentFrom", "TaskDetailActivity");
                            intent.putExtra("task", task);
                            startActivity(intent);
                            this.finish();

                        } else {
                            Constants.showToast(context, "非本站点巡检卡！");
                        }
                    } else {
                        if (deviceIds.contains(rfidData1) ||
                                (deviceIds.length() < 12 && (deviceIds + "0001").equals(rfidData1))) {

//                            readSuccessDialog = new ReadSuccessDialog(context, isFirstRead);
                            //添加五步法
//                            readSuccessDialog = new FiveStepsDialog(context, isFirstRead);
//                            readSuccessDialog.setCanceledOnTouchOutside(false);
//                            readSuccessDialog.show();

                            Intent intent = new Intent(TaskDetailActivity.this, FiveStepsActivity.class);
                            intent.putExtra("taskId", task.getTaskId());
                            intent.putExtra("actId", task.getActId());
                            intent.putExtra("historyId", task.getHistoryId());
                            intent.putExtra("taskName", task.getName());
                            intent.putExtra("imgUrl", task.getOperateCardUrl());
                            intent.putExtra("intentFrom", "TaskDetailActivity");
                            intent.putExtra("task", task);
                            startActivity(intent);
                            this.finish();

                        } else {
                            Constants.showToast(context, "非本站点巡检卡！");
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除注册
        unregisterReceiver(myReceiver);
    }
}
