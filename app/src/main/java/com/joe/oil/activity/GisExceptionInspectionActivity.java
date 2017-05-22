package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.util.Constants;

import java.util.List;

public class GisExceptionInspectionActivity extends BaseActivity implements OnClickListener {

    private Context context;
    private OilApplication application;

    private TextView tvSubmit;
    private TextView notSubmit;
    private ImageView ivUploadException;

    private TextView tvLeft;
    private TextView tvTitle;
//    private TextView tvRight;

    private String officeId;
    private User user;
    private List<GisFinish> gisFinishs;
    private HttpRequest http;
    private Handler mHandler = new Handler();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;

            case R.id.tv_submit:
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
                            if (Constants.checkNetWork(context)) {
                                Constants.showToast(context, "巡护作业数据正在提交，请稍后");
                            } else {
                                Constants.showToast(context, "网络不给力，数据已转至后台上传");
                            }
                        }
                    }, 2000);

                } else {
                    Constants.showToast(context, "没有需要提交的数据");
                }
                break;

            case R.id.iv_upload_exception:

                if (Constants.IS_LINE_START) {
                    Constants.showToast(context, "请先结束当前巡护作业");
                } else {
                    Intent intent = new Intent(context, GisUploadActivity.class);
                    intent.putExtra("isExceptionInspection", true);
                    startActivity(intent);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_exception_inspection);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
//        tvRight = (TextView) findViewById(R.id.tv_right);

        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        notSubmit = (TextView) this.findViewById(R.id.gis_tv_not_submit);
        ivUploadException = (ImageView) findViewById(R.id.iv_upload_exception);
    }

    private void initDataSet() {
        context = this;
        application = (OilApplication) getApplication();

        http = HttpRequest.getInstance(context);
        user = application.getUser();
        officeId = getIntent().getStringExtra("officeId");

        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("异常巡护");

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

    private void initEvent() {

        tvLeft.setOnClickListener(this);
//        tvRight.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
        ivUploadException.setOnClickListener(this);
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
}
