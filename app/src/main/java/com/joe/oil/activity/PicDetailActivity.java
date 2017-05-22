package com.joe.oil.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.adapter.PicPagerAdapter;

import java.util.ArrayList;

/**
 * 消息详情页面
 * Created by scar1et on 15-6-30.
 */
public class PicDetailActivity extends BaseActivity {

    private Context context;
    private TextView tvLeft;
    private TextView tvTitle;

    //接收上一页面传来的数据
    private int position;
    private ArrayList<String> pics;

    private ViewPager vpPic;

    private PicPagerAdapter picPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic_detail);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        vpPic = (ViewPager) findViewById(R.id.vp_pic);
    }

    private void initDataSet() {
        context = this;

        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("图片详情");

        pics = (ArrayList<String>) getIntent().getSerializableExtra("pics");
        position = getIntent().getIntExtra("position", 0);

        picPagerAdapter = new PicPagerAdapter(context, pics);
        vpPic.setAdapter(picPagerAdapter);

        vpPic.setCurrentItem(position);
    }

    private void initEvent() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
