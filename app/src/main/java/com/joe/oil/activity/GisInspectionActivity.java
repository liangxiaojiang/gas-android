package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.joe.oil.R;

/**
 * Gis巡护页面
 */
public class GisInspectionActivity extends BaseActivity implements OnClickListener {

    private Context context;
    private OilApplication application;

//    private TextView tvLineInspection;
//    private TextView tvWayInspection;
//    private TextView tvExceptionInspection;

    private RelativeLayout rlLineInspection;
    private RelativeLayout rlWayInspection;
    private RelativeLayout rlExceptionInspection;

    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvRight;

    private String officeId;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
                finish();
                break;

            case R.id.tv_right:
                Intent intent = new Intent(context, GisHistroyActivity.class);
                startActivity(intent);
                break;

            case R.id.rl_line_inspection:
                jumpLineInspection();
                break;

            case R.id.rl_way_inspection:
                jumpWayInspection();
                break;

            case R.id.rl_exception_inspection:
                jumpExceptionInspection();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_inspection);

        initView();
        initDataSet();
        initEvent();
    }

    private void jumpLineInspection() {

        Intent intent = new Intent(context, GisActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void jumpWayInspection() {

        Intent intent = new Intent(context, GisActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void jumpExceptionInspection() {

        Intent intent = new Intent(context, GisExceptionInspectionActivity.class);
        intent.putExtra("officeId", officeId);
        startActivity(intent);
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvRight = (TextView) findViewById(R.id.tv_right);

//        tvExceptionInspection = (TextView) findViewById(R.id.tv_exception_inspection);
//        tvWayInspection = (TextView) findViewById(R.id.tv_way_inspection);
//        tvLineInspection = (TextView) findViewById(R.id.tv_line_inspection);

        rlExceptionInspection = (RelativeLayout) findViewById(R.id.rl_exception_inspection);
        rlLineInspection = (RelativeLayout) findViewById(R.id.rl_line_inspection);
        rlWayInspection = (RelativeLayout) findViewById(R.id.rl_way_inspection);
    }

    private void initDataSet() {
        context = this;
        application = (OilApplication) getApplication();

        officeId = getIntent().getStringExtra("officeId");

        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvRight.setText("历史");
        tvTitle.setText("GIS巡护");
    }

    private void initEvent() {

        rlLineInspection.setOnClickListener(this);
        rlWayInspection.setOnClickListener(this);
        rlExceptionInspection.setOnClickListener(this);

        tvLeft.setOnClickListener(this);
        tvRight.setOnClickListener(this);
    }
}
