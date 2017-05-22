package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Single;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.parolmap.NavigationActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liangxiaojiang on 2016/11/17.
 */
public class SingleDetailsActivity extends BaseActivity implements View.OnClickListener {
    private TextView singleTask, singleDispatchTime, singleCollectingTime, singlePassenger, singleVehicleNumber, plateNumber;
    private TextView singleActualTime, singleactualTimeCollect;//实际出车时间、实际收车时间
    private TextView Route;
    private TextView driverPhone, driverName;
    private Button button, button1;
    private int b;
    private ImageView back;
    private Intent intent;
    private boolean b0 = true;
    private boolean b1 = true;
    private ImageView imageView;
    private HttpRequest http;
    private Context context;
    private Single single;
    private Single notFinshSingle;
    private SqliteHelper sqliteHelper;
    private String singleId;
    private boolean isSuccess = false;
    private MyHandler myHandler;
    private RelativeLayout rlAnniu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_details);
        myHandler = new MyHandler();
        initDate();
        initView();
//        if (b0==false) {
            setTaskServ();
//        }

    }

    private void setTaskServ() {
        Log.d("lkj",getIntent().getStringExtra("singleId"));
        singleActualTime.setText(sqliteHelper.getSingleById(getIntent().getStringExtra("singleId")).get(0).getRealStartTime());
        singleactualTimeCollect.setText(sqliteHelper.getSingleById(getIntent().getStringExtra("singleId")).get(0).getRealEndTime());
        if (!singleActualTime.getText().equals("")&&!singleactualTimeCollect.getText().equals("")){
            rlAnniu.setVisibility(View.GONE);
        }
    }

    private void initDate() {
        sqliteHelper = new SqliteHelper(SingleDetailsActivity.this);
        notFinshSingle = sqliteHelper.getSingleById(getIntent().getStringExtra("singleId")).get(0);
//        notFinshSingle=new Single();
    }


    private void initView() {
        singleId = getIntent().getStringExtra("singleId");
        singleTask = (TextView) findViewById(R.id.risk);//任务名称
        singleDispatchTime = (TextView) findViewById(R.id.control);//出车时间
        singleCollectingTime = (TextView) findViewById(R.id.time);//收车时间
        singleActualTime = (TextView) findViewById(R.id.chargerTime);//实际出车时间
        singleactualTimeCollect = (TextView) findViewById(R.id.partner_time);//实际收车时间
        singlePassenger = (TextView) findViewById(R.id.vehiclerDrive_name);//乘车人
        singleVehicleNumber = (TextView) findViewById(R.id.vehicleDriver_phone);//乘车人电话电话
        plateNumber = (TextView) findViewById(R.id.vehicleNumber);//车牌号
        driverName = (TextView) findViewById(R.id.Drive_name);
        driverPhone = (TextView) findViewById(R.id.Driver_phone);
        single = (Single) getIntent().getExtras().getSerializable("single");
        singleTask.setText(getIntent().getStringExtra("vehicleTask"));
        singleDispatchTime.setText(getIntent().getStringExtra("dispatchTime"));
        singleCollectingTime.setText(getIntent().getStringExtra("collectingTime"));
        singlePassenger.setText(getIntent().getStringExtra("passenger"));
        singleVehicleNumber.setText(getIntent().getStringExtra("vehicleNumber"));
        plateNumber.setText(getIntent().getStringExtra("number"));
        Route = (TextView) findViewById(R.id.Route);
        Route.setText(getIntent().getStringExtra("route"));
        driverName.setText(getIntent().getStringExtra("driverName"));
        driverPhone.setText(getIntent().getStringExtra("driverPhone"));
        button = (Button) findViewById(R.id.button_single_start);
        button1 = (Button) findViewById(R.id.button_single_end);
        if (!singleActualTime.getText().equals("")) {
            button.setBackgroundColor(Color.parseColor("#cdcdcd"));
        }
        button1.setOnClickListener(this);
        button.setOnClickListener(this);
        back = (ImageView) findViewById(R.id.task_single_btn_back);
        back.setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.iv_daohang);
        imageView.setOnClickListener(this);
        http = HttpRequest.getInstance(context);
        rlAnniu= (RelativeLayout) findViewById(R.id.rl_anniu);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_single_start:
                if (b0 == true && singleActualTime.getText().equals("")) {
                    dialog();
                    b0 = false;
                }
                break;
            case R.id.task_single_btn_back:
                intent = new Intent(SingleDetailsActivity.this, SingleActivity.class);
                startActivity(intent);
                break;
            case R.id.button_single_end:
                if (b1 == true) {
                    if (!singleActualTime.getText().equals("null")) {
                        dialog1();
                        b1 = false;
                    } else {
                        Constants.showToast(SingleDetailsActivity.this, "请先出车");
                    }

                }

                break;
            case R.id.iv_daohang:
                uploadData();
                intent = new Intent(SingleDetailsActivity.this, NavigationActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                intent.putExtra("pt_id", "00");
                intent.putExtra("vehicleCode", getIntent().getStringExtra("vehicleCode"));
                startActivity(intent);
                break;
        }
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleDetailsActivity.this);
        builder.setMessage("确认出车吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String endDate = format.format(date);
                singleActualTime.setText(endDate);
                single.setRealStartTime(singleActualTime.getText().toString());
//                button.setText("上传");
                String startTime = singleActualTime.getText().toString();
                String endTime = singleactualTimeCollect.getText().toString();
                String id = single.getSingleId();
                String vehicleId = single.getVehicleId();
                String creatorId = single.getCreatorId();
                notFinshSingle.setActName("已出车");
                sqliteHelper.updateSingleFinishState(notFinshSingle);
                http.requestSingle(myHandler, startTime, endTime, id, vehicleId, creatorId,  "已出车");
                uploadData();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
//        uploadData();
    }

    protected void dialog1() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleDetailsActivity.this);
        builder.setMessage("确认收车吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String endDate = format.format(date);
                singleactualTimeCollect.setText(endDate);

                String startTime = singleActualTime.getText().toString();
                String endTime = singleactualTimeCollect.getText().toString();
                String id = single.getSingleId();
                String vehicleId = single.getVehicleId();
                String creatorId = single.getCreatorId();
                notFinshSingle.setActName("已收车");
                sqliteHelper.updateSingleFinishState(notFinshSingle);
                http.requestSingle(myHandler, startTime, endTime, id, vehicleId, creatorId, "已收车");
                uploadData();
                dialog.dismiss();
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


    private void uploadData() {
        notFinshSingle.setRealStartTime(singleActualTime.getText().toString().trim());
        notFinshSingle.setRealEndTime(singleactualTimeCollect.getText().toString().trim());
        sqliteHelper.updateSingleFinishState(notFinshSingle);
    }


    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Constants.dismissDialog();
                    Log.d("zjpzjp", "-================success=============== ");
                    intent = new Intent(SingleDetailsActivity.this, SingleActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case HttpRequest.REQUEST_FAILER:
                    Log.d("zjpzjp", "-=================false============== ");
                    Constants.dismissDialog();
                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
                    break;
                default:
                    break;
            }
        }
    }


}