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
import android.widget.ImageView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.dialog.GisTimeSettingDialog;
import com.joe.oil.dialog.LineDialog;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.List;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class GisActivity extends BaseActivity implements OnClickListener {

    private ImageView back;
    private ImageView start;
    private TextView submit;
    private TextView upload;
    //	private TextView history;
    private TextView setting;
    private TextView notSubmit;
    private TextView title;
    private TextView gis_tv_notice;
    private Context context;
    private SqliteHelper sqliteHelper;
    private List<GisFinish> gisFinishs;
    private OilApplication application;
    private User user;
    private HttpRequest http;
    public static Handler gisHandler;
    private Handler mHandler = new Handler();
    private String officeId;
    private String lineId;
    private Intent intent;
    private LineDialog lineDialog;

    /**
     * 0 管线
     * 1 道路
     */
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gis);

        initView();
        initMembers();
        setHandler();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.gis_btn_back);
        start = (ImageView) this.findViewById(R.id.gis_img_start);
        submit = (TextView) this.findViewById(R.id.gis_submit);
        upload = (TextView) this.findViewById(R.id.gis_upload);
//		history = (TextView) this.findViewById(R.id.gis_history);
        setting = (TextView) this.findViewById(R.id.gis_time_setting);
        notSubmit = (TextView) this.findViewById(R.id.gis_tv_not_submit);
        title = (TextView) this.findViewById(R.id.gis_title_name);
        gis_tv_notice = (TextView) this.findViewById(R.id.gis_tv_notice);

        if (Constants.IS_LINE_START) {
            gis_tv_notice.setVisibility(View.VISIBLE);
            start.setBackgroundResource(R.drawable.selector_btn_complete);
        } else {
            gis_tv_notice.setVisibility(View.GONE);
            start.setBackgroundResource(R.drawable.selector_btn_start);
        }

        back.setOnClickListener(this);
        start.setOnClickListener(this);
        submit.setOnClickListener(this);
        upload.setOnClickListener(this);
        setting.setOnClickListener(this);
//		history.setOnClickListener(this);
    }

    private void initMembers() {
        context = GisActivity.this;
        sqliteHelper = new SqliteHelper(context);
        application = (OilApplication) getApplication();
        user = application.getUser();

        officeId = getIntent().getStringExtra("officeId");
        type = getIntent().getIntExtra("type", 0);

        if (type == 0) {

            title.setText("管线巡护");
        } else {

            title.setText("道路巡护");
        }

        if (!Constants.IS_LINE_START) {

            Constants.CUR_GIS_INSPECTION_TYPE = type;
        }

        http = HttpRequest.getInstance(context);
        if (user != null) {
            gisFinishs = sqliteHelper.getGisFinishNotSubmit(user.getUserId());
        }
        notSubmit.setVisibility(View.VISIBLE);
        if (gisFinishs != null && gisFinishs.size() > 0) {
            notSubmit.setText("你还有" + gisFinishs.size() + "条巡护记录没有提交，请提交");
        } else {
            notSubmit.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gis_btn_back:
                this.finish();
                break;

            case R.id.gis_time_setting:
                GisTimeSettingDialog dialog = new GisTimeSettingDialog(context);
                dialog.show();
                break;

            case R.id.gis_upload:
                intent = new Intent(GisActivity.this, GisUploadActivity.class);
                startActivity(intent);
                break;

            case R.id.gis_submit:
                if (gisFinishs != null && gisFinishs.size() > 0) {
                    Constants.showDialog(context);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < gisFinishs.size(); i++) {
                                GisFinish data = gisFinishs.get(i);
                                GisFinish local_data = sqliteHelper.getGisFinishNotSubmitByCreateTime(data.getCreatTime());
                                if (!local_data.getStatus().equals("1")) { // 无效上传数据处理
                                    if (gisFinishs.size() - 1 > 0) {
                                        notSubmit.setText("你还有" + (gisFinishs.size() - 1) + "条巡护记录没有提交，请提交");
                                    } else {
                                        notSubmit.setText("");
                                        gisFinishs.clear();
                                        Constants.dismissDialog();
                                        Constants.showToast(context, "上传成功");
                                        return;
                                    }
                                } else {
                                    http.requestGisFinish(new FinishHandler(data), data);
                                }
                            }
                            Constants.dismissDialog();
                            if (Constants.checkNetWork(GisActivity.this)) {
                                Constants.showToast(context, "巡护作业数据正在提交，请稍后");
                            } else {
                                Constants.showToast(GisActivity.this, "网络不给力，数据已转至后台上传");
                            }
                        }
                    }, 2000);

                } else {
                    Constants.showToast(context, "没有需要提交的数据");
                }
                break;

            case R.id.gis_img_start:
                if (lineDialog != null) {
                    lineDialog.dismiss();
                }
                lineDialog = new LineDialog(context, 0);
                lineDialog.setCanceledOnTouchOutside(false);
                lineDialog.show();
                break;

//            case R.id.gis_history:
//                intent = new Intent(GisActivity.this, GisHistroyActivity.class);
//                startActivity(intent);
//                break;

            default:
                break;
        }
    }

    private class FinishHandler extends Handler {
        private GisFinish gisFinish;

        public FinishHandler(GisFinish gisFinish) {
            this.gisFinish = gisFinish;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    gisFinish.setStatus("2");
                    sqliteHelper.updateGisFinish(gisFinish);
                    break;

                case HttpRequest.REQUEST_FAILER:
                    gisFinish.setStatus("1");
                    sqliteHelper.updateGisFinish(gisFinish);
                    break;

                default:
                    break;
            }
            gisFinishs = sqliteHelper.getGisFinishNotSubmit(user.getUserId());
            if (gisFinishs != null && gisFinishs.size() > 0) {
                notSubmit.setText("你还有" + gisFinishs.size() + "条巡护记录没有提交，请提交");
            } else {
                notSubmit.setText("");
                Constants.showToast(context, "上传成功");
            }
        }
    }

    private void setHandler() {
        gisHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        if (officeId != null) {
                            int id = Integer.parseInt(officeId);
                            switch (id) {
                                case 3:
                                    officeId = "01";
                                    break;

                                case 4:
                                    officeId = "02";
                                    break;

                                case 5:
                                    officeId = "03";
                                    break;

                                case 6:
                                    officeId = "04";
                                    break;

                                default:
                                    break;
                            }
                        }
                        Message msg2 = MainActivity.gisHandler.obtainMessage();
                        msg2.what = MainActivity.LOCATION_STOP;
                        GisFinish data = sqliteHelper.getGisFinishNotFinish();
                        if (data != null) { // 巡护任务进行中
                            gis_tv_notice.setVisibility(View.VISIBLE);
                            Constants.IS_LINE_START = true;
                            start.setBackgroundResource(R.drawable.selector_btn_complete);
                            msg2.obj = 5;
                            notSubmit.setVisibility(View.VISIBLE);
                        } else { // 巡护任务完成
                            Constants.IS_LINE_START = false;
                            start.setBackgroundResource(R.drawable.selector_btn_start);
                            msg2.obj = 20;
                            if (user != null) {
                                gisFinishs.clear();
                                gisFinishs = sqliteHelper.getGisFinishNotSubmit(user.getUserId());
                            }
                            if (gisFinishs != null && gisFinishs.size() > 0) {
                                notSubmit.setText("你还有" + gisFinishs.size() + "条巡护记录没有提交，请提交");
                            } else {
                                notSubmit.setText("");
                            }
                            gis_tv_notice.setVisibility(View.GONE);
                            notSubmit.setVisibility(View.VISIBLE);
                        }

                        msg2.sendToTarget();
                        break;

                    default:
                        break;
                }
            }
        };
    }
}
