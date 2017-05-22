package com.joe.oil.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.joe.oil.R;

/**
 * Created by liangxiaojiang on 2016/9/19.
 */
public class EvaluationActivity extends BaseActivity {

    private TextView tvLeft;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        initView();
        initData();
    }

    private void initView() {
        tvLeft= (TextView) findViewById(R.id.tv_left);
        tvTitle= (TextView) findViewById(R.id.tv_title);
    }
    private void initData(){
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("评价");
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
