package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.adapter.GvPicAdapter;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.UploadException;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常历史详情页面
 * Created by scar1et on 15-6-30.
 */
public class GisUploadHistoryDetailActivity extends BaseActivity {

    private Context context;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvCurLat;
    private TextView tvCurLng;
    private TextView tvMessageDescription;
    private TextView tvFillTime;
    private TextView tvUploadStatus;
    private GridView gvPics;
    private ArrayList<String> pics;
    private GvPicAdapter gvPicAdapter;
    private Gis gis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_upload_history_detail);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvCurLat = (TextView) findViewById(R.id.tv_cur_lat);
        tvCurLng = (TextView) findViewById(R.id.tv_cur_lng);
        tvMessageDescription = (TextView) findViewById(R.id.tv_message_description);
        tvFillTime = (TextView) findViewById(R.id.tv_fill_time);
        tvUploadStatus = (TextView) findViewById(R.id.tv_upload_status);
        gvPics = (GridView) findViewById(R.id.gv_pics);
    }

    private void initDataSet() {
        context = this;

        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("详情");

        gis = (Gis) getIntent().getSerializableExtra("gis");

        tvCurLat.setText(gis.getLatitude());
        tvCurLng.setText(gis.getLongitude());
        tvFillTime.setText(gis.getTime());
        tvMessageDescription.setText(gis.getMemo());

        if (gis.getStatus().equals("2")) {
            if (gis.getPics().equals("")) {
                tvUploadStatus.setText("图片等待上传");
                tvUploadStatus.setTextColor(context.getResources().getColor(R.color.qianhong));
            } else {
                tvUploadStatus.setText("上传成功");
                tvUploadStatus.setTextColor(context.getResources().getColor(R.color.green));
            }
        } else {
            tvUploadStatus.setText("等待上传");
            tvUploadStatus.setTextColor(context.getResources().getColor(R.color.qianhong));
        }

        pics = new ArrayList<>();
        getPics();
        gvPicAdapter = new GvPicAdapter(context, pics);

        gvPics.setAdapter(gvPicAdapter);
    }

    private void getPics() {
        ImageGroup imageGroup = (ImageGroup) getIntent().getSerializableExtra("imageGroup");
        List<ImageBean> imageBeans = null;
        if (imageGroup != null) {
            imageBeans = imageGroup.getImageSets();
        }else {
            imageBeans = new ArrayList<>();
        }
        pics.clear();
        for (ImageBean imageBean : imageBeans) {
            pics.add("file://" + imageBean.getPath());
        }
    }
    private void initEvent() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gvPics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Intent intent = new Intent(context, PicDetailActivity.class);
                intent.putExtra("pics", pics);
                intent.putExtra("position", position);

                startActivity(intent);
            }
        });
    }
}
