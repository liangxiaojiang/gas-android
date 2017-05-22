package com.joe.oil.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangxiaojiang on 2016/10/14.
 */
public class DetailsTaskActivity extends Activity implements View.OnClickListener {
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
//    private ImageView readCard;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_details_task);

        initMembers();
        initView();
        initData();
        setsecondReadSuccessHandler();
        setFinishHandler();
        context = this;
        oilapp= (OilApplication) getApplicationContext();

    }


    private void initView() {
        back = (ImageView) this.findViewById(R.id.task_detail_btn_back1);
        taskName = (TextView) this.findViewById(R.id.task_detail_tv_name1);

        operationCard = (TextView) this.findViewById(R.id.task_detail_operation_card1);

        charger = (TextView) this.findViewById(R.id.charger1);
        partner = (TextView) this.findViewById(R.id.partner1);
        memo = (TextView) this.findViewById(R.id.memo1);
        vehicleNumber = (TextView) this.findViewById(R.id.vehicleNumber1);
        vehicleDriverName = (TextView) this.findViewById(R.id.vehicleDriverName1);
        vehicleDriverPhone = (TextView) this.findViewById(R.id.vehicleDriverPhone1);

        controlTips = (TextView) this.findViewById(R.id.controlTips1);



        back.setOnClickListener(this);
        operationCard.setOnClickListener(this);
    }

//   / @SuppressLint("NewApi")
    private void initMembers() {
        context = DetailsTaskActivity.this;
        task = (Task) getIntent().getSerializableExtra("task");

//        time = Integer.parseInt(task.getInterval());
        intentFrom = getIntent().getStringExtra("intentFrom");

        taskall = new ArrayList<Task>();

    }

    private void setsecondReadSuccessHandler() {
        secondReadSuccessHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Intent intent = new Intent(DetailsTaskActivity.this, TaskFinishActivity.class);
                intent.putExtra("taskId", task.getTaskId());
                intent.putExtra("actId", task.getActId());
                intent.putExtra("historyId", task.getHistoryId());
                intent.putExtra("taskName", task.getName());
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

                DetailsTaskActivity.this.finish();


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
//        String riskTipss = task.getRiskTips();
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
//        riskTips.setText(riskTipss);
        vehicleDriverPhone.setText(vehicleDriverPhones);
        controlTips.setText(controlTipss);
        memo.setText(task.getMemo());

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**
         * 这是做的监听手机返回键，也让判断返回那页
         * 下面要用finish，不然返回那页在点击返回会留在当前页
         */

            Intent intent=new Intent(this,TaskFinishActivity.class);
            intent.putExtra("taskId", task.getTaskId());
            intent.putExtra("actId", task.getActId());
            intent.putExtra("historyId", task.getHistoryId());
            intent.putExtra("taskName", task.getName());
            intent.putExtra("intentFrom", "TaskDetailActivity");
            intent.putExtra("task", task);
            intent.putExtra("happening", task.getCompletion());
            intent.putExtra("recording", task.getWorkRecord());
            intent.putExtra("remark", task.getMemo());
            startActivity(intent);
            oilapp.setFinsh(0);
            DetailsTaskActivity.this.finish();

        return super.onKeyDown(keyCode, event);
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_detail_btn_back1:
                /**
                 * 这是用全局变量来判断，当在TaskFinishActivity中点击后，
                 * 在点返回让能准确的判断是那页点击的，让可以回到那页
                 */

                    Intent intent=new Intent(this,TaskFinishActivity.class);
                    intent.putExtra("taskId", task.getTaskId());
                    intent.putExtra("actId", task.getActId());
                    intent.putExtra("historyId", task.getHistoryId());
                    intent.putExtra("taskName", task.getName());
                    intent.putExtra("intentFrom", "TaskDetailActivity");
                    intent.putExtra("task", task);
                    intent.putExtra("happening", task.getCompletion());
                    intent.putExtra("recording", task.getWorkRecord());
                    intent.putExtra("remark", task.getMemo());
                    startActivity(intent);
                    oilapp.setFinsh(0);

            case R.id.task_detail_operation_card1:
                Intent intent2 = new Intent(DetailsTaskActivity.this, ImageActivity.class);
                intent2.putExtra("imgUrl", task.getOperateCardUrl());
                startActivity(intent2);
                break;

            default:
                break;
        }
    }


    ////   不用读相对卡的步骤
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


}