package com.joe.oil.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.sqlite.DBManager;
import com.joe.oil.sqlite.sqlit;
import com.joe.oil.util.SignCalendar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 签到
 * Created by liangxiaojiang on 2016/9/23.
 */

public final class SignActivity extends BaseActivity {

    private TextView tvLeft;
    private TextView tvTitle;

    private String date = null;// 设置默认选中的日期  格式为 “2014-04-05” 标准DATE格式
    private TextView popupwindow_calendar_month;
    private SignCalendar calendar;
    private Button btn_signIn;//签到按钮（这是当点击签到后执行逻辑的按钮）
    private List<String> list = new ArrayList<String>(); //设置标记列表
    DBManager dbManager;
    boolean isinput = false;//这是做判断的标识符
    private String date1 = null;//单天日期
    private String name1 = null;//当前用户


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);

        initcalendar();//签到日历
        initView();
        initData();
    }



    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);

    }

    //签到日历
    private void initcalendar() {
        // 初始化DBManager
        dbManager = new DBManager(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        date1 = formatter.format(curDate);//当前时间，转化为string字符串
        name1 = getIntent().getStringExtra("name");//先定义一个用户名为当前用户名，然后给赋值为当前用户
        popupwindow_calendar_month = (TextView) findViewById(R.id.popupwindow_calendar_month);
        btn_signIn = (Button) findViewById(R.id.btn_signIn);
        calendar = (SignCalendar) findViewById(R.id.popupwindow_calendar);
        popupwindow_calendar_month.setText(calendar.getCalendarYear() + "年"
                + calendar.getCalendarMonth() + "月");

        if (null != date) {
            int years = Integer.parseInt(date.substring(0,
                    date.indexOf("-")));
            int month = Integer.parseInt(date.substring(
                    date.indexOf("-") + 1, date.lastIndexOf("-")));
            popupwindow_calendar_month.setText(years + "年" + month + "月");

            calendar.showCalendar(years, month);
            calendar.setCalendarDayBgColor(date,
                    R.drawable.calendar_date_focused);
        }

        query();
        if (isinput) {//这的意思是当是今天，该用户已经签过到，这边就显示出已经签到，让不能在签到
            btn_signIn.setText("今日已签，明日继续");
//            bqiandao.setText("已签到");
            btn_signIn.setBackgroundResource(R.drawable.button_gray);
            btn_signIn.setEnabled(false);
        }
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date today = calendar.getThisday();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
           /* calendar.removeAllMarks();
           list.add(df.format(today));
           calendar.addMarks(list, 0);*/
                //将当前日期标示出来
                add(df.format(today));
                //calendar.addMark(today, 0);
                query();
                HashMap<String, Integer> bg = new HashMap<String, Integer>();
                calendar.setCalendarDayBgColor(today, R.drawable.bg_sign_today);
                btn_signIn.setText("今日已签，明日继续");
//                bqiandao.setText("已签到");

                btn_signIn.setBackgroundResource(R.drawable.button_gray);
                btn_signIn.setEnabled(false);
            }
        });

        //监听当前月份
        calendar.setOnCalendarDateChangedListener(new SignCalendar.OnCalendarDateChangedListener() {
            public void onCalendarDateChanged(int year, int month) {
                popupwindow_calendar_month
                        .setText(year + "年" + month + "月");
            }
        });
    }

    /**
     * 这是给集合添加数据
     *
     * @param date
     */
    public void add(String date) {
        ArrayList<sqlit> persons = new ArrayList<sqlit>();

        sqlit person1 = new sqlit(date, "true", name1);//这是根据sqlit类中的参数，进行添加

        persons.add(person1);

        dbManager.add(persons);
    }


    /**
     * 这是执行逻辑的
     */
    public void query() {
        List<sqlit> persons = dbManager.query(name1);//这边是用实参的方式让值传过去
        for (sqlit person : persons) {
            list.add(person.date);
            if (date1.equals(person.getDate()) && name1.equals(person.getName())) {
                /*
                下面注的代码也能实现当是当前用户的时候以前签到的都能实
                 */
                //这是做判断，当时间等于当前时间，用户等于当前用户
//                for(sqlit nowsqlit : persons){//遍历这个集合
//                    if(nowsqlit.getName().equals(name1)){//这是当这个集合中的用户名就是当前用户时，让直行下面的逻辑
//                        list.add(nowsqlit.date);//这是集合中的用户名就是当前用户时，就把这个标记存到list标记列表中
//                    }
//                }

                isinput = true;
            }
        }
        calendar.addMarks(list, 0);//当上面进行完毕时，就把上面的标记列表存进calendar中
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();// 释放数据库资源
    }

    private void initData() {
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("签到");
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
//                Intent intent=new Intent(SignActivity.this,PersonalActivity.class);
//                startActivity(intent);
            }
        });

    }
}
