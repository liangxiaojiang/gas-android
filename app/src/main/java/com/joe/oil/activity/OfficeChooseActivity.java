package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.joe.oil.R;
import com.joe.oil.adapter.ChooseOfficeAdapter;
import com.joe.oil.entity.Office;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class OfficeChooseActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private ImageView back;
    private ListView listView;
    private String what;
    private HttpRequest http;
    private Context context;
    private List<Office> officeIds;
    private SqliteHelper sqliteHelper;
    private OfficeHandler officeHandler;
    private String code;
    private List<PlanTemplateDetail> planDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_choose_office);

        initView();
    }

    private void initView() {
        context = OfficeChooseActivity.this;

        back = (ImageView) this.findViewById(R.id.choose_office_btn_back);
        listView = (ListView) this.findViewById(R.id.office_choose_listview);

        back.setOnClickListener(this);
        listView.setOnItemClickListener(this);

        what = getIntent().getStringExtra("what");
        http = HttpRequest.getInstance(context);
        officeIds = new ArrayList<Office>();
        officeHandler = new OfficeHandler();
        http.requestGetOffice(officeHandler);
        Constants.showDialog(context);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.choose_office_btn_back:
                this.finish();
                break;
            default:
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private class OfficeHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    officeIds = new ArrayList<Office>();
                    List<Office> tempOffices = (List<Office>) msg.obj;
                    sqliteHelper = new SqliteHelper(context);
                    sqliteHelper.insertOffice(tempOffices);
                    for (int i = 0; i < tempOffices.size(); i++) {
                        if (tempOffices.get(i).getGrade().equals("1") || tempOffices.get(i).getGrade().equals("2") &&
                                tempOffices.get(i).getType().equals("2")) {
                            officeIds.add(tempOffices.get(i));
                        }
                    }
                    ChooseOfficeAdapter adapter = new ChooseOfficeAdapter(officeIds, context);
                    listView.setAdapter(adapter);
                    Constants.dismissDialog();
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        code = officeIds.get(position).getCode();
        switch (what) {
            case "baseData": {
                Intent intent = new Intent(OfficeChooseActivity.this, DownloadActivity.class);
                intent.putExtra("officeId", officeIds.get(position).getOfficeId());
                intent.putExtra("code", officeIds.get(position).getCode());
                intent.putExtra("what", what);
                startActivity(intent);
                break;
            }
            case "itemUpdate": {
                Intent intent = new Intent(OfficeChooseActivity.this, DownloadActivity.class);
                intent.putExtra("officeId", officeIds.get(position).getOfficeId());
                intent.putExtra("code", officeIds.get(position).getCode());
                intent.putExtra("what", what);
                startActivity(intent);
                break;
            }
            default: {
                Intent intent = new Intent(OfficeChooseActivity.this, OtherDownloadActivity.class);
                intent.putExtra("what", what);
                intent.putExtra("officeId", officeIds.get(position).getOfficeId());
                intent.putExtra("code", officeIds.get(position).getCode());
                startActivity(intent);
                break;
            }
        }
    }
}
