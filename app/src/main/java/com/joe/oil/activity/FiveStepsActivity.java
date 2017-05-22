package com.joe.oil.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Task;
import com.joe.oil.util.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liangxiaojiang on 2016/12/13.
 */
public class FiveStepsActivity extends Activity implements View.OnClickListener {

    private Context context;
    private boolean isReadFirst;
    private ImageView imageView;

    private TextView textView1, textView2, textView3, textView4, textView5;
    private RelativeLayout relativeLayout1, relativeLayout2, relativeLayout3, relativeLayout4;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5;

    private EditText editText1, editText2;
    private int by;
    private int a, b, c, d;
    private int value = 0;
    private String risk, preventive;

    private Task task;
    private OilApplication oilapp;//定义的全局宏变量
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_fivesteps);
        context = this;
        initView();
    }



//    public FiveStepsDialog(Context context, final boolean isReadFirst) {
//        super(context);
//        this.context = context;
//        this.isReadFirst = isReadFirst;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.dialog_fivesteps);
//        initView();
//    }


    private void initView() {

        oilapp = new OilApplication();
        task = new Task();

        textView1 = (TextView) findViewById(R.id.tv_stop);
        textView2 = (TextView) findViewById(R.id.tv_think);
        textView3 = (TextView) findViewById(R.id.tv_identify);
        textView4 = (TextView) findViewById(R.id.tv_confirm);
        textView5 = (TextView) findViewById(R.id.tv_do);
        relativeLayout1 = (RelativeLayout) findViewById(R.id.rl_stop);
        relativeLayout2 = (RelativeLayout) findViewById(R.id.rl_think);
        relativeLayout3 = (RelativeLayout) findViewById(R.id.rl_identify);
        relativeLayout4 = (RelativeLayout) findViewById(R.id.rl_confirm);
        textView1.setOnClickListener(this);
        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
//        imageView= (ImageView) findViewById(R.id.image_tu);
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (d == 0) {
//                    Toast.makeText(context, "全部点击后才能进入", Toast.LENGTH_SHORT).show();
                    Constants.showToast(context, "全部点击后才能进入");
                }
//                else if (value == 5) {
                else {
                    SimpleDateFormat formatter =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str = formatter.format(curDate);
                    oilapp.setStartWorkingTime(str);
                    oilapp.setPreventivemeasures(editText2.getText().toString().trim());
                    oilapp.setRiskidentification(editText1.getText().toString().trim());
                    Log.d("lxjaa", oilapp.getPreventivemeasures() + "/-/-/-" + oilapp.getRiskidentification());
//                    new Thread(new Runnable() {
//                        //                public boolean isReadFirst;
//                        @Override
//                        public void run() {
//                            try {
//
//                                Thread.sleep(1000);//run间隔1秒执行
//
//                                Log.d("ReadSuccessDialog", Constants.IS_WORKING + "   " + isReadFirst);
//                                if (isReadFirst && Constants.IS_WORKING == false) {
//                                    Message msg2 = TaskDetailActivity.secondReadSuccessHandler.obtainMessage();
//                                    msg2.sendToTarget();
//                                    dismiss();

                                    Intent intent = new Intent(FiveStepsActivity.this, TaskFinishActivity.class);
                                    intent.putExtra("taskId", getIntent().getStringExtra("taskId"));
                                    intent.putExtra("actId", getIntent().getStringExtra("actId"));
                                    intent.putExtra("historyId", getIntent().getStringExtra("historyId"));
                                    intent.putExtra("taskName", getIntent().getStringExtra("taskName"));
                                    intent.putExtra("imgUrl",getIntent().getStringExtra("imgUrl"));
                                    intent.putExtra("intentFrom", "TaskDetailActivity");
                                    intent.putExtra("task", getIntent().getSerializableExtra("task"));
                                    startActivity(intent);
                                    FiveStepsActivity.this.finish();

//                                }
//                                else if (isReadFirst && Constants.IS_WORKING == true) {
////                                    dismiss();
//                                } else if (!isReadFirst) {
//                                }
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }).start();
                }
//                else {
//                    Toast.makeText(context, "全部点击后并且04（确认）中全部选中才能进入", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        checkBox1 = (CheckBox) findViewById(R.id.cb_1);
        checkBox2 = (CheckBox) findViewById(R.id.cb_2);
        checkBox3 = (CheckBox) findViewById(R.id.cb_3);
        checkBox4 = (CheckBox) findViewById(R.id.cb_4);
        checkBox5 = (CheckBox) findViewById(R.id.cb_5);
        checkBox1.setOnCheckedChangeListener(cb);
        checkBox2.setOnCheckedChangeListener(cb);
        checkBox3.setOnCheckedChangeListener(cb);
        checkBox4.setOnCheckedChangeListener(cb);
        checkBox5.setOnCheckedChangeListener(cb);

        editText1 = (EditText) findViewById(R.id.et_risk);
        editText2 = (EditText) findViewById(R.id.et_preventive);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_stop:
                if (by == 0) {
                    relativeLayout1.setVisibility(View.VISIBLE);
                    by = 1;
                } else {
                    relativeLayout1.setVisibility(View.GONE);
                    by = 0;
                }
                a = 1;
                break;
            case R.id.tv_think:
                if (a == 1) {
                    if (by == 0) {
                        relativeLayout2.setVisibility(View.VISIBLE);
                        by = 1;
                    } else {
                        relativeLayout2.setVisibility(View.GONE);
                        by = 0;
                    }
                    b = 1;
                } else {
//                    Toast.makeText(context, "请点击01后才能点击02", Toast.LENGTH_SHORT).show();
                    Constants.showToast(context, "请点击01后才能点击02");
                }

                break;
            case R.id.tv_identify:
                if (b == 1) {
                    if (by == 0) {
                        relativeLayout3.setVisibility(View.VISIBLE);
                        by = 1;
                    } else {
                        relativeLayout3.setVisibility(View.GONE);
                        by = 0;
                    }
                    c = 1;
                } else {
//                    Toast.makeText(context, "请点击02后才能点击03", Toast.LENGTH_SHORT).show();
                    Constants.showToast(context, "请点击02后才能点击03");
                }
                break;
            case R.id.tv_confirm:
                if (c == 1 && !editText1.getText().toString().equals("") && !editText2.getText().toString().equals("")) {
                    if (by == 0) {
                        risk = editText1.getText().toString().trim();
                        preventive = editText2.getText().toString().trim();
                        relativeLayout4.setVisibility(View.VISIBLE);
                        by = 1;
                    } else {
                        relativeLayout4.setVisibility(View.GONE);
                        by = 0;
                    }
                    d = 1;
                } else {
//                    Toast.makeText(context, "请点击03后才能点击04并且风险识别、防护意识不能为空", Toast.LENGTH_SHORT).show();
                    Constants.showToast(context, "请点击03后才能点击04并且风险识别、防护意识不能为空");
                }
                break;
        }

    }

    private CompoundButton.OnCheckedChangeListener cb = new CompoundButton.OnCheckedChangeListener() { //实例化一个cb
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                value++;
            } else {
                value--;
            }
        }
    };
}