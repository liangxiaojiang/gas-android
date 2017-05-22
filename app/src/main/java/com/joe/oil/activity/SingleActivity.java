package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.SingleAdapter;
import com.joe.oil.entity.Single;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.view.CommentListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liangxiaojiang on 2016/11/16.
 */
public class SingleActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView back;
    private Intent intent;
    private CommentListView listView;
    private List<Single> single;
    private List<Single> singleGX;
    private List<Single> singleAll;
    private HttpRequest http;
    private getSingleHandler singleHandler;
    private User user;
    private Context context;
    private TextView noTask;
    private SqliteHelper sqliteHelper;
    private SingleAdapter singleAdapter;
    private OilApplication application;
    private Single single2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single);

        initView();
        initMembers();

        application = (OilApplication) getApplication();
        user = application.getUser();
        http.requestGetSingle(singleHandler,user.getUserId());
        getnoItemSingleNotFinish();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.single_btn_back);
        listView = (CommentListView) findViewById(R.id.single_listview);
        noTask = (TextView) this.findViewById(R.id.single_no_task);
        back.setOnClickListener(this);
        listView.setOnItemClickListener(this);

    }

    private void initMembers() {
        context = SingleActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        sqliteHelper = new SqliteHelper(context);
        single2=new Single();
        single = new ArrayList<Single>();
        singleAll = new ArrayList<Single>();
        singleGX = new ArrayList<Single>();
        http = HttpRequest.getInstance(context);
        singleHandler = new getSingleHandler();


    }

    private void getnoItemSingleNotFinish(){
//        single.clear();
        Log.d("liangxiaojiang","========"+user.getUserId());
        single=sqliteHelper.getSingleNotFinish(user.getUserId());
        if (single.size()==0){
            listView.setVisibility(View.GONE);
            noTask.setVisibility(View.VISIBLE);
        }else {
            listView.setVisibility(View.VISIBLE);
            noTask.setVisibility(View.GONE);
        }
//        Collections.sort(single);
        Collections.reverse(single);//List集合倒序
        singleAdapter=new SingleAdapter(context,single);
        listView.setAdapter(singleAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single_btn_back:
                 intent=new Intent(this,MainActivity.class);
                startActivity(intent);
                SingleActivity.this.finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                intent=new Intent(this,MainActivity.class);
                startActivity(intent);
               SingleActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);

    }

    @SuppressLint("HandlerLeak")
    private class getSingleHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Constants.dismissDialog();
                    singleAll = (List<Single>) msg.obj;
                    single.clear();
                        if (singleAll.size() == 10) {
                            listView.setPullLoadEnable(true);
                        } else {
                            listView.setPullLoadEnable(false);
                        }

                        single.addAll(singleAll);
//                        singleAll.addAll(single);

                        if (single.size() == 0) {
                            noTask.setVisibility(View.VISIBLE);
                            Constants.showToast(context, "没有路单");
                        } else {
                            noTask.setVisibility(View.GONE);
                            Constants.showToast(context, "同步成功");
                        }
                        sqliteHelper.insertSingle(single);
                        Collections.reverse(single);//List集合倒序
                        singleAdapter = new SingleAdapter(context, single);
                        listView.setAdapter(singleAdapter);
                        singleAdapter.notifyDataSetChanged();
                        Intent intent = new Intent(Constants.STATION_PLAN);
                        sendBroadcast(intent);

                        break;
                        case HttpRequest.REQUEST_FAILER:
                            Constants.dismissDialog();
                            HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);

                            break;
                        default:
                            break;
                    }
            }
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Single itemPosition = single.get(position - 1);
            intent = new Intent(SingleActivity.this, SingleDetailsActivity.class);
            intent.putExtra("single", itemPosition);
            intent.putExtra("vehicleTask", itemPosition.getVehicleTask());
            intent.putExtra("dispatchTime", itemPosition.getStartTime());
            intent.putExtra("collectingTime", itemPosition.getEndTime());
            intent.putExtra("passenger", itemPosition.getChargerName());
            intent.putExtra("vehicleNumber", itemPosition.getPassengerPhone());
            intent.putExtra("number", itemPosition.getNumber());
            intent.putExtra("route", itemPosition.getVehicleRoute());
            intent.putExtra("driverPhone", itemPosition.getDriverPhone());
            intent.putExtra("driverName", itemPosition.getDriverName());
            intent.putExtra("singleId", itemPosition.getSingleId());
            intent.putExtra("vehicleCode", itemPosition.getVehicleCode());
            startActivity(intent);
        }
    }