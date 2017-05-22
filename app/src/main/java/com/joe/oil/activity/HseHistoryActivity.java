package com.joe.oil.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.adapter.HseHistoryAdapter;
import com.joe.oil.entity.UploadHseSupervision;
import com.joe.oil.view.CommentListView;

import java.util.List;

/**
 * Hse历史记录页面
 * Created by scar1et on 15-6-30.
 */
public class HseHistoryActivity extends BaseActivity {

    private Context context;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvEmpty;
    private CommentListView lvHistory;
    private List<UploadHseSupervision> uploadHseSupervisions;
    private HseHistoryAdapter hseHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hse_history);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        lvHistory = (CommentListView) findViewById(R.id.lv_history);
    }

    private void initDataSet() {
        context = this;
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("Hse历史");

        getUploadHseSupervisions();
        hseHistoryAdapter = new HseHistoryAdapter(context, uploadHseSupervisions);
        lvHistory.setAdapter(hseHistoryAdapter);
        lvHistory.setPullLoadEnable(false);
        lvHistory.setPullRefreshEnable(false);
        lvHistory.setEmptyView(tvEmpty);
    }

    private void initEvent() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUploadHseSupervisions() {

        uploadHseSupervisions = sqliteHelper.getAllHseData();
    }
}
