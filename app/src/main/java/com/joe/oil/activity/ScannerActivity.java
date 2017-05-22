package com.joe.oil.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.joe.oil.R;

/**
 * Created by Administrator on 2016/11/22.
 */
public class ScannerActivity extends Activity  implements AdapterView.OnItemClickListener {

    private ListView  mListViewScannerLv;
    private String[] optionList= {"概况","任务","隐患上报","作业记录"};
    private Intent intent;
    private Context context;
    private String getCodeIntent,getUserIdIntent;
    private ImageView imageView;
    public ScannerActivity(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_scanner);
        context = ScannerActivity.this;
        getCodeIntent = getIntent().getStringExtra("code");
        getUserIdIntent = getIntent().getStringExtra("userId");
        Log.d("zjp","getCodeIntent="+getCodeIntent+",getUserIdIntent="+getUserIdIntent);
        initView();
    }

    private void initView() {
        mListViewScannerLv = (ListView) findViewById(R.id.lv_scanning);
        mListViewScannerLv.setAdapter(new ArrayAdapter(this, R.layout.scanner_item, optionList));
        mListViewScannerLv.setOnItemClickListener(this);
        imageView= (ImageView) findViewById(R.id.checkitem_btn_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (optionList[position]){
            case "概况":
                intent = new Intent(context, DeviceInfoActivity.class);
                intent.putExtra("title","概况");
                intent.putExtra("code",getCodeIntent);
                intent.putExtra("userId",getUserIdIntent);
                startActivity(intent);
                break;
            case "任务":
                intent = new Intent(context, TaskActivity.class);
                intent.putExtra("title","任务");
                intent.putExtra("code",getCodeIntent);
                intent.putExtra("userId",getUserIdIntent);
                startActivity(intent);
                break;
            case "隐患":
//                intent = new Intent(context, DeviceInfoActivity.class);
//                intent.putExtra("title","隐患");
//                startActivity(intent);
                break;
            case "作业纪录":
                intent = new Intent(context, HistoryActivity.class);
                intent.putExtra("title","作业纪录");
                intent.putExtra("code",getCodeIntent);
                intent.putExtra("userId",getUserIdIntent);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}