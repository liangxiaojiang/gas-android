package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.CheckItem;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.entity.Tank;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Well;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;

import java.util.List;

public class OtherDownloadActivity extends BaseActivity implements OnClickListener {

    private TextView title;
    private ImageView back;
    private TextView download;
    private CheckBox clear;
    private CheckBox user;
    private CheckBox well;
    private CheckBox checkItem;
    private CheckBox plan;

    private CheckBox tank;
    private List<Tank> vehicle;

    private String what;
    private String officeId;
    private String officeCode;
    private HttpRequest http;
    private SqliteHelper sqliteHelper;
    private SqliteHelperForItem sqliteHelperForItem;
    private Context context;
    private Handler downloadHandler;
    private List<User> users;
    private List<Well> wells;
    private List<CheckItem> checkItems;
    private List<PlanTemplateDetail> planTemplateDetails;
    private int planCount;
    private int planCount2;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_other_download);

        initView();
        initMembers();
        setHandler();
    }

    private void initView() {
        title = (TextView) this.findViewById(R.id.other_download_title);
        download = (TextView) this.findViewById(R.id.other_download_start);
        back = (ImageView) this.findViewById(R.id.other_download_back);
        clear = (CheckBox) this.findViewById(R.id.other_download_clear);
        user = (CheckBox) this.findViewById(R.id.other_download_user);
        well = (CheckBox) this.findViewById(R.id.other_download_well);
        checkItem = (CheckBox) this.findViewById(R.id.other_download_check_item);
        plan = (CheckBox) this.findViewById(R.id.other_download_plan);

        tank = (CheckBox) this.findViewById(R.id.other_download_tank);


        back.setOnClickListener(this);
        download.setOnClickListener(this);
    }

    private void initMembers() {
        context = OtherDownloadActivity.this;
        what = getIntent().getStringExtra("what");
        officeId = getIntent().getStringExtra("officeId");
        officeCode = getIntent().getStringExtra("code");
        if (what.equals("userData")) {
            title.setText("用户数据下载");
            clear.setText("已清空本地用户表");
            user.setVisibility(View.VISIBLE);
            plan.setVisibility(View.GONE);
            well.setVisibility(View.GONE);
            checkItem.setVisibility(View.GONE);
            tank.setVisibility(View.GONE);
        } else if (what.equals("wellData")) {
            title.setText("单井数据下载");
            clear.setText("已清空本地单井表和巡检项表");
            user.setVisibility(View.GONE);
            plan.setVisibility(View.GONE);
            well.setVisibility(View.VISIBLE);
            checkItem.setVisibility(View.GONE);
            tank.setVisibility(View.GONE);
        } else if (what.equals("tank")) {
            title.setText("车辆信息下载");
            clear.setText("已清空本地车辆信息表");
            user.setVisibility(View.GONE);
            plan.setVisibility(View.GONE);
            well.setVisibility(View.GONE);
            checkItem.setVisibility(View.GONE);
            tank.setVisibility(View.VISIBLE);
        } else {
            title.setText("巡检模板数据下载");
            clear.setText("已清空本地巡检模板表");
            plan.setVisibility(View.VISIBLE);
            user.setVisibility(View.GONE);
            well.setVisibility(View.GONE);
            checkItem.setVisibility(View.GONE);
            tank.setVisibility(View.GONE);
        }
        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        sqliteHelperForItem = new SqliteHelperForItem(context);
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        downloadHandler = new Handler() {

            @SuppressWarnings("unchecked")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpRequest.DOWNLOAD_USER:
                        users = (List<User>) msg.obj;
                        int userCount = sqliteHelperForItem.insertUser(users);
                        user.setChecked(true);
                        user.setText(getResources().getString(R.string.user_data_download_finish) + userCount + "条");
                        application.setCeshhi(1);
                        download.setText("下载成功！");
                        break;

                    case HttpRequest.DOWNLOAD_PLAN:
                        planCount2 = 0;
                        planTemplateDetails = (List<PlanTemplateDetail>) msg.obj;
                        planCount2 = sqliteHelperForItem.insertPlanTemplate(planTemplateDetails);
                        http.requestGetPlanTemplate(downloadHandler, officeCode, "2");
                        break;

                    case HttpRequest.DOWNLOAD_PLAN_TEMPLATE:
                        planCount = 0;
                        planTemplateDetails.clear();
                        planTemplateDetails = (List<PlanTemplateDetail>) msg.obj;
                        planCount = sqliteHelperForItem.insertPlanTemplate(planTemplateDetails);
                        plan.setChecked(true);
                        plan.setText(getResources().getString(R.string.plan_data_download_finish) + (planCount + planCount2) + "条");
                        Intent intent = new Intent();
                        intent.setAction(Constants.IS_DOWNLOAD_PLAN);
                        sendBroadcast(intent);
                        download.setText("下载成功！");
                        break;

                    case HttpRequest.DOWNLOAD_WELL:
                        wells = (List<Well>) msg.obj;
                        int wellCount = sqliteHelperForItem.insertWell(wells);
                        well.setChecked(true);
                        well.setText(getResources().getString(R.string.well_data_download_finish) + wellCount + "条");
//					http.requestGetGasCheckItem(downloadHandler, officeId);
                        download.setText("下载成功！");
                        break;

                    case HttpRequest.DOWNLOAD_XJ_ITEM:
                        checkItems = (List<CheckItem>) msg.obj;
                        sqliteHelperForItem = new SqliteHelperForItem(context);
                        int checkItemCount = sqliteHelperForItem.insertItem(checkItems);
                        checkItem.setChecked(true);
                        checkItem.setText(getResources().getString(R.string.check_item_data_download_finish) + checkItemCount + "条");
                        download.setText("下载成功！");
                        break;

                    case HttpRequest.DOWNLOAD_CL_TANK:
                        vehicle = (List<Tank>) msg.obj;
                        int tankCount = sqliteHelperForItem.insertTank(vehicle);
                        tank.setChecked(true);
                        tank.setText(getResources().getString(R.string.tank_date_download_finish) + tankCount + "条");
                        download.setText("下载成功");
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.other_download_back:
                this.finish();
                break;

            case R.id.other_download_start:
                download.setText("下载中，请稍后...");
                download.setEnabled(false);
                download.setClickable(false);
                if (what.equals("userData")) {
                    sqliteHelper.deleteUser();
                    http.requestGetUser(downloadHandler, officeId);
                } else if (what.equals("wellData")) {
                    sqliteHelper.deleteWell();
                    sqliteHelper.deleteCheckItem();
                    http.requestGetWell(downloadHandler, officeId);
                }else if (what.equals("tank")){
                    sqliteHelper.deleteTank();
                    http.requestGetTank(downloadHandler, officeId);
                }
                else {
                    sqliteHelper.deleteAllPlanDetail();
                    sqliteHelper.deletePlanTemplate();
                    http.requestGetPlanTemplate(downloadHandler, officeCode, "1");
                }
                clear.setChecked(true);
                break;
            default:
                break;
        }
    }

}
