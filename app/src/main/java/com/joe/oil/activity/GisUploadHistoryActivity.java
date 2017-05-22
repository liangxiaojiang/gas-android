package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.adapter.GisUploadHistoryAdapter;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.User;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.view.CommentListView;

import java.util.List;

public class GisUploadHistoryActivity extends BaseActivity implements OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private TextView notice_none_data;
    private CommentListView mListView;
    private SqliteHelper sqliteHelper;
    private List<Gis> gisUploads;
    private OilApplication application;
    private User user;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.history_btn_back:
                this.finish();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<ImageBean> imgData = sqliteHelper.getLocalPics(gisUploads.get(position - 1).getTime());
        ImageGroup imageGroup = null;
        if (imgData != null && imgData.size() > 0) {
            imageGroup = new ImageGroup("ALL", imgData);
        } else {
            imageGroup = new ImageGroup();
        }
        Intent intent = new Intent(context, GisUploadHistoryDetailActivity.class);
        intent.putExtra("gis", gisUploads.get(position - 1));
        intent.putExtra("imageGroup", imageGroup);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gis_upload_history);

        initView();
        initMembers();
        setListView();
    }

    private void initView() {
        notice_none_data = (TextView) this.findViewById(R.id.history_no_data);
        mListView = (CommentListView) this.findViewById(R.id.history_gis_upload);

        this.findViewById(R.id.history_btn_back).setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    private void initMembers() {
        context = GisUploadHistoryActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        sqliteHelper = new SqliteHelper(context);
        gisUploads = sqliteHelper.getAllGisUpload(user.getUserId());
        if (gisUploads != null && gisUploads.size() > 0) {
            notice_none_data.setVisibility(View.GONE);
        } else {
            notice_none_data.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void setListView() {
        if (gisUploads != null && gisUploads.size() > 0) {
            GisUploadHistoryAdapter gisUploadAdapter = new GisUploadHistoryAdapter(context, gisUploads);

            mListView.setAdapter(gisUploadAdapter);
            mListView.setPullLoadEnable(false);
            mListView.setPullRefreshEnable(false);
        }
    }
}
