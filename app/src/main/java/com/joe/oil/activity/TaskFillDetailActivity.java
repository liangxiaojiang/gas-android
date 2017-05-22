package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.entity.WorkDetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liangxiaojiang on 2017/3/31.
 */
public class TaskFillDetailActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView back;
    private TextView taskName;
    private TextView operationCard;
    private TextView charger;
    private TextView partner;
    private TextView memo;
    private TextView vehicleNumber;
    private TextView vehicleDriverName;
    private TextView vehicleDriverPhone;
    private TextView riskTips;
    private TextView controlTips;
    private Task task;
    private Task taskGx;

    private LinearLayout titles_ll;//任务填报页的布局
    private List<Map<String, Object>> list;
    private List<WorkDetail> workDetails;

    private RelativeLayout rl_zhedie;
    private ScrollView scrollview;
    private ImageView taskdetailhmore, taskdetailhmore1;
    private int by;
    private User user;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task_fill_detail);
        initMembers();
        initView();
        initData();
        context = this;

    }



    private void initView() {
        back = (ImageView) this.findViewById(R.id.task_fill_detail_btn_back);
        taskName = (TextView) this.findViewById(R.id.task_fill_detail_tv_name);
        operationCard = (TextView) this.findViewById(R.id.task_fill_detail_operation_card);
        charger = (TextView) this.findViewById(R.id.fill_charger);
        partner = (TextView) this.findViewById(R.id.fill_partner);
        memo = (TextView) this.findViewById(R.id.fill_memo);
        vehicleNumber = (TextView) this.findViewById(R.id.fill_vehicleNumber);
        vehicleDriverName = (TextView) this.findViewById(R.id.fill_vehicleDriverName);
        vehicleDriverPhone = (TextView) this.findViewById(R.id.fill_vehicleDriverPhone);
        riskTips = (TextView) this.findViewById(R.id.fill_riskTips);
        controlTips = (TextView) this.findViewById(R.id.fill_controlTips);


        back.setOnClickListener(this);
        operationCard.setOnClickListener(this);

        rl_zhedie = (RelativeLayout) findViewById(R.id.rl_fill_zhedie);
        scrollview = (ScrollView) findViewById(R.id.fill_scrollview);
        taskdetailhmore = (ImageView) findViewById(R.id.fill_taskdetailhmore);
        taskdetailhmore1 = (ImageView) findViewById(R.id.fill_taskdetailhmore1);
        rl_zhedie.setOnClickListener(new View.OnClickListener() {
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

        titles_ll = (LinearLayout) findViewById(R.id.fill_titles_ll);

    }

    /**
     * 动态添加模板
     */
    private void template() {
        titles_ll.removeAllViews();
        workDetails = sqliteHelper.getWorkDetail(task.getTaskId());
//        workDetails=task.getWorkDetails();
        for (int i = 0; i < workDetails.size(); i++) {
            final RelativeLayout ll = (RelativeLayout) LayoutInflater.from(TaskFillDetailActivity.this).inflate(R.layout.combin, null);
            final TextView btn = (TextView) ll.findViewById(R.id.fill_top_one);
            Map<String, Object> map = new HashMap<String, Object>();
            btn.setText(workDetails.get(i).getTitle());
            final TextView et1 = (TextView) ll.findViewById(R.id.fill_top_et1);
            et1.setText(workDetails.get(i).getContent());
            titles_ll.addView(ll);
        }
    }

    private void initMembers() {
        context = TaskFillDetailActivity.this;
        task = (Task) getIntent().getSerializableExtra("task");
        taskGx=(Task)getIntent().getSerializableExtra("taskGx");
        user = application.getUser();
    }

    private void initData() {
        template();
        String name = task.getName();
        String chargerName = task.getChargerName();
        String partnerName = task.getPartnerName();
        String vehicleNumbers = task.getVehicleNumber();
        String vehicleDriverNames = task.getVehicleDriverName();
        String vehicleDriverPhones = task.getVehicleDriverPhone();
        String riskTipss = task.getRiskTips();
        String controlTipss = task.getControlTips();


        taskName.setText(name);
        taskName.getPaint().setFakeBoldText(true);// 加粗
        charger.setText(chargerName);
        partner.setText(partnerName);
        vehicleNumber.setText(vehicleNumbers);
        vehicleDriverName.setText(vehicleDriverNames);
        vehicleDriverPhone.setText(vehicleDriverPhones);
        riskTips.setText(riskTipss);
        vehicleDriverPhone.setText(vehicleDriverPhones);
        controlTips.setText(controlTipss);
        memo.setText(task.getMemo());
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
                TaskFillDetailActivity.this.finish();
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_fill_detail_btn_back:
                if (getIntent().getStringExtra("intentFrom").equals("personal")){
                    Intent intent=new Intent(this,PersonalActivity.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("OfficeName", user.getOfficeName());
                    startActivity(intent);
                    this.finish();
                }else {
                    Intent intent1 = new Intent(this, TaskActivity.class);
                    startActivity(intent1);
                    this.finish();
                }
                break;

            case R.id.task_fill_detail_operation_card:
                Intent intent = new Intent(TaskFillDetailActivity.this, ImageListActivity.class);
                intent.putExtra("task", task);
                intent.putExtra("imgUrl", task.getOperateCardUrl());
                startActivity(intent);
                break;

            default:
                break;
        }
    }



}