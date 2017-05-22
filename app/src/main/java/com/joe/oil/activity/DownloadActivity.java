package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.joe.oil.entity.Device;
import com.joe.oil.entity.DeviceTree;
import com.joe.oil.entity.Dict;
import com.joe.oil.entity.Line;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.entity.Tank;
import com.joe.oil.entity.User;
import com.joe.oil.entity.Well;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("HandlerLeak")
public class DownloadActivity extends BaseActivity implements OnClickListener {

    private ImageView back;
    private TextView download;
    private TextView reset;
    private ImageView finish;
    private CheckBox clear;
    private CheckBox userData;
    private CheckBox checkItem;
    private CheckBox device;
    private CheckBox well;
    private CheckBox line;
    private CheckBox plan;
    private CheckBox dict;
    private HttpRequest http;
    private Context context;
    private Handler downLoadHandler;
    private SqliteHelper sqliteHelper;
    private SqliteHelperForItem sqliteHelperForItem;
    private List<User> users;
    private List<CheckItem> checkItems;
    private List<Device> devices;
    private List<Well> wells;
    private List<Line> lines;
    private List<PlanTemplateDetail> planDetails;
    private List<Dict> dicts;
    private List<DeviceTree> deviceTrees;
    private String officeId;
    private String officeCode;
    private static int currentPage = 1;
    private int checkItemCount = 0;
    private String what;
    private String updateTime;

    private int deviceTreeCountOne;
    private int deviceTreeCountTwo;
    private int deviceTreeCountThree;
    private int dictCount;

    private CheckBox tank;
    private List<Tank> vehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_download);

        initView();
        initMembers();
        setHandler();
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
        back = (ImageView) this.findViewById(R.id.download_back);
        download = (TextView) this.findViewById(R.id.download_start);
        reset = (TextView) this.findViewById(R.id.download_reset);
        finish = (ImageView) this.findViewById(R.id.download_confirm);
        clear = (CheckBox) this.findViewById(R.id.download_clear);
        userData = (CheckBox) this.findViewById(R.id.download_user);
        device = (CheckBox) this.findViewById(R.id.download_device);
        checkItem = (CheckBox) this.findViewById(R.id.download_check_item);
        well = (CheckBox) this.findViewById(R.id.download_well);
        line = (CheckBox) this.findViewById(R.id.download_line);
        plan = (CheckBox) this.findViewById(R.id.download_plan);
        dict = (CheckBox) this.findViewById(R.id.download_dict);


        tank = (CheckBox) this.findViewById(R.id.download_tank);

        // clear.setChecked(false);
        // userData.setChecked(false);
        // device.setChecked(false);
        // well.setChecked(false);
        // line.setChecked(false);
        // plan.setChecked(false);
        // dict.setChecked(false);
        // checkItem.setChecked(false);

        back.setOnClickListener(this);
        download.setOnClickListener(this);
        reset.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    private void initMembers() {
        context = DownloadActivity.this;
        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        sqliteHelperForItem = new SqliteHelperForItem(context);
        users = new ArrayList<User>();
        checkItems = new ArrayList<CheckItem>();
        devices = new ArrayList<Device>();
        lines = new ArrayList<Line>();
        dicts = new ArrayList<Dict>();
        vehicle = new ArrayList<Tank>();
        officeId = getIntent().getStringExtra("officeId");
        officeCode = getIntent().getStringExtra("code");
        what = getIntent().getStringExtra("what");
        if (what.equals("itemUpdate")) {
            SqliteHelperForItem sqliteHelperForItem = new SqliteHelperForItem(context);
            // updateTime = sqliteHelperForItem.getUpdateTime();
            clear.setVisibility(View.GONE);
            userData.setVisibility(View.GONE);
            device.setVisibility(View.GONE);
            checkItem.setVisibility(View.VISIBLE);
            well.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            plan.setVisibility(View.GONE);
            dict.setVisibility(View.GONE);
            tank.setVisibility(View.GONE);
        } else {
            clear.setVisibility(View.VISIBLE);
            userData.setVisibility(View.VISIBLE);
            device.setVisibility(View.VISIBLE);
            checkItem.setVisibility(View.GONE);
            well.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            // plan.setVisibility(View.VISIBLE);
            dict.setVisibility(View.VISIBLE);
//            tank.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_back:
                this.finish();
                break;

            case R.id.download_confirm:
                if (what.equals("itemUpdate")) {
                    if (checkItem.isChecked()) {
                        this.finish();
                    } else {
                        Constants.showToast(context, "更新未完成成，请稍后！");
                    }
                } else {
                    if (clear.isChecked() && userData.isChecked()) {
                        this.finish();
                    } else {
                        Constants.showToast(context, "数据下载未完成，请稍后！");
                    }
                }
                break;

            case R.id.download_start:
                download.setText("下载中,请稍后...");
                download.setEnabled(false);
                download.setClickable(false);
                back.setEnabled(false);
                back.setClickable(false);
                finish.setEnabled(false);
                finish.setClickable(false);

                clear.setChecked(true);

                clear.setEnabled(false);
                clear.setClickable(false);
                userData.setEnabled(false);
                userData.setClickable(false);
                device.setEnabled(false);
                device.setClickable(false);
                well.setEnabled(false);
                well.setClickable(false);
                line.setEnabled(false);
                line.setClickable(false);
                plan.setEnabled(false);
                plan.setClickable(false);
                dict.setEnabled(false);
                dict.setClickable(false);
                checkItem.setEnabled(false);
                checkItem.setClickable(false);
                tank.setEnabled(false);
                tank.setClickable(false);
                if (what.equals("itemUpdate")) {
                    sqliteHelper.deleteCheckItem();
                    Constants.CURRENT_PAGE = 1;
                    Constants.TOTAL_COUNT = 0;
                    Constants.NEXT_PAGE = 1;
                    http.requestGetGasCheckItem(downLoadHandler, officeId, Constants.NEXT_PAGE + "", updateTime, officeCode);
                } else {
                    sqliteHelper.deleteBaseData();
                    http.requestGetUser(downLoadHandler, officeId);
                }
                break;

            case R.id.download_reset:
                http.requestGetGasCheckItem(downLoadHandler, officeId, Constants.NEXT_PAGE + "", "", officeCode);
                reset.setVisibility(View.GONE);
                download.setText("下载中，请稍后...");
                break;

            default:
                break;
        }
    }

    private void setHandler() {
        downLoadHandler = new Handler() {

            @SuppressWarnings({"unchecked", "deprecation"})
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HttpRequest.DOWNLOAD_USER:
                        users = (List<User>) msg.obj;
                        int userCount = sqliteHelperForItem.insertUser(users);
                        userData.setChecked(true);
                        userData.setText(getResources().getString(R.string.user_data_download_finish) + userCount + "条");
                        application.setCeshhi(1);
                        http.requestGetDevice(downLoadHandler, officeId);
                        break;

                    case HttpRequest.DOWNLOAD_XJ_ITEM:
                        checkItems = (List<CheckItem>) msg.obj;
                        sqliteHelperForItem = new SqliteHelperForItem(context);
                        checkItemCount = checkItemCount + sqliteHelperForItem.insertItem(checkItems);
                        if (Constants.NEXT_PAGE == Constants.CURRENT_PAGE) {
                            Constants.CURRENT_PAGE = 1;
                            Constants.TOTAL_COUNT = 0;
                            Constants.NEXT_PAGE = 1;
                            checkItem.setChecked(true);
                            checkItem.setText(getResources().getString(R.string.check_item_data_download_finish) + checkItemCount + "条");
                            download.setText("下载成功！");
                            back.setEnabled(true);
                            back.setClickable(true);
                            finish.setEnabled(true);
                            finish.setClickable(true);
                        } else {
                            http.requestGetGasCheckItem(downLoadHandler, officeId, Constants.NEXT_PAGE + "", updateTime, officeCode);
                            checkItem.setText("巡检项数据已下载" + checkItemCount + "条，共" + Constants.TOTAL_COUNT + "条");
                        }
                        break;

                    case HttpRequest.DOWNLOAD_DEVICE:
                        devices = (List<Device>) msg.obj;
                        int deviceCount = sqliteHelperForItem.insertDevice(devices);
                        device.setChecked(true);
                        device.setText(getResources().getString(R.string.device_data_download_finish) + deviceCount + "条");
                        http.requestGetWell(downLoadHandler, officeId);
                        break;

                    case HttpRequest.DOWNLOAD_WELL:
                        wells = (List<Well>) msg.obj;
                        int wellCount = sqliteHelperForItem.insertWell(wells);
                        well.setChecked(true);
                        well.setText(getResources().getString(R.string.well_data_download_finish) + wellCount + "条");
                        http.requestGetLine(downLoadHandler, officeId);
                        break;

                    case HttpRequest.DOWNLOAD_LINE:
                        lines = (List<Line>) msg.obj;
                        int lineCount = sqliteHelper.insertLine(lines);
                        line.setChecked(true);
                        line.setText(getResources().getString(R.string.line_data_download_finish) + lineCount + "条");
                        http.requestGetDict(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_PLAN:
                        // planDetails = (List<PlanTemplateDetail>) msg.obj;
                        // sqliteHelper.insertPlanTemplate(planDetails);
                        break;

                    case HttpRequest.DOWNLOAD_PLAN_TEMPLATE:
                        // planDetails.clear();
                        // planDetails = (List<PlanTemplateDetail>) msg.obj;
                        // int planCount =
                        // sqliteHelper.insertPlanTemplate(planDetails);
                        // plan.setChecked(true);
                        // plan.setText(getResources().getString(R.string.plan_data_download_finish)
                        // + planCount + "条");
                        // http.requestGetDict(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_CL_TANK:

                        vehicle = (List<Tank>) msg.obj;
                        int tankCount = sqliteHelperForItem.insertTank(vehicle);
                        tank.setChecked(true);
                        tank.setText(getResources().getString(R.string.tank_date_download_finish) + tankCount + "条");
                        download.setText("下载成功");
                        http.requestGetTank(downLoadHandler, officeId);
                        break;

                    case HttpRequest.DOWNLOAD_DICT:
                        dicts = (List<Dict>) msg.obj;
                        dictCount = sqliteHelperForItem.insertDict(dicts);
                        http.requestGetDeviceTreeOne(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_DEVICE_TREE:
                        deviceTrees = (List<DeviceTree>) msg.obj;
                        deviceTreeCountOne = sqliteHelperForItem.insertDiviceTree(deviceTrees);
                        http.requestGetDeviceTreeTwo(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_DEVICE_TREE_2:
                        deviceTrees = (List<DeviceTree>) msg.obj;
                        deviceTreeCountTwo = sqliteHelperForItem.insertDiviceTree(deviceTrees);
                        http.requestGetDeviceTreeThree(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_DEVICE_TREE_3:
                        deviceTrees = (List<DeviceTree>) msg.obj;
                        deviceTreeCountThree = sqliteHelperForItem.insertDiviceTree(deviceTrees);
                        http.requestGetDeviceTreeFour(downLoadHandler);
                        break;

                    case HttpRequest.DOWNLOAD_DEVICE_TREE_4:
                        deviceTrees = (List<DeviceTree>) msg.obj;
                        int deviceTreeCountFour = sqliteHelperForItem.insertDiviceTree(deviceTrees);
                        int deviceTotalCount = dictCount + deviceTreeCountOne + deviceTreeCountTwo + deviceTreeCountThree + deviceTreeCountFour;
                        dict.setChecked(true);
                        dict.setText(getResources().getString(R.string.dict_data_download_finish) + deviceTotalCount + "条");
                        download.setText("下载成功！");
                        back.setEnabled(true);
                        back.setClickable(true);
                        finish.setEnabled(true);
                        finish.setClickable(true);
                        // http.requestGetGasCheckItem(downLoadHandler, officeId,
                        // Constants.CURRENT_PAGE + "");
                        break;

                    case HttpRequest.REQUEST_FAILER:
                        HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
                        download.setText("下载失败！");
                        back.setEnabled(true);
                        back.setClickable(true);
                        finish.setEnabled(true);
                        finish.setClickable(true);
                        if (Constants.TOTAL_COUNT > 0) {
                            reset.setVisibility(View.VISIBLE);
                        }
                        break;




                    default:
                        break;
                }
            }
        };
    }
}
