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
import com.joe.oil.entity.MessageInfo;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.util.Constants;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 消息详情页面
 * Created by scar1et on 15-6-30.
 */
public class MessageDetailActivity extends BaseActivity {

    private Context context;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvName;
    private TextView tvContent;
    private TextView tvTime;
    private TextView tvAuthor;
    private GridView gvPics;
    private ArrayList<String> pics;
    private GvPicAdapter gvPicAdapter;
    private MessageInfo  messageInfo;

    private HttpRequest http;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_detail);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvContent = (TextView) findViewById(R.id.tv_content);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvAuthor = (TextView) findViewById(R.id.tv_author);
        gvPics = (GridView) findViewById(R.id.gv_pics);
    }

    private void initDataSet() {
        context = this;
        http = HttpRequest.getInstance(context);

        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("详情");

        messageInfo = (MessageInfo) getIntent().getSerializableExtra("messageInfo");

        tvName.setText(messageInfo.getTitle() + "");
        tvContent.setText(messageInfo.getContent() + "");
        tvAuthor.setText(messageInfo.getFromName() + "");
        tvTime.setText(messageInfo.getCreatedDate().substring(0, messageInfo.getCreatedDate().length() - 3) + "");

        pics = new ArrayList<>();
        gvPicAdapter = new GvPicAdapter(context, pics);

        gvPics.setAdapter(gvPicAdapter);

        getMessageInfo();
    }

    private void initEvent() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MessageDetailActivity.this.finish();
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

    private void getMessageInfo() {

        AjaxParams params = new AjaxParams();
        params.put("id", messageInfo.getId() + "");

        http.getFinalHttp().get(Constants.BASE_URL + "/api/message/get", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                try {
                    JSONObject object = new JSONObject(o.toString());
                    JSONObject message = object.getJSONObject("message");

                    MessageInfo messageInfo = new MessageInfo();

                    messageInfo.setTitle(message.getString("title"));
                    messageInfo.setId(message.getInt("id"));
                    messageInfo.setContent(message.getString("content"));
                    messageInfo.setCreatedDate(message.getString("createdDate"));
                    messageInfo.setIsRead(message.getBoolean("isRead"));
                    messageInfo.setFromName(message.getString("fromName"));

                    tvName.setText(messageInfo.getTitle() + "");
                    tvContent.setText(messageInfo.getContent() + "");
                    tvAuthor.setText(messageInfo.getFromName() + "");
                    tvTime.setText(messageInfo.getCreatedDate().substring(0, messageInfo.getCreatedDate().length() - 3) + "");

                    JSONArray picUrls = message.getJSONArray("picUrls");

                    if (picUrls != null && picUrls.length() > 0) {
                        pics.clear();
                        for (int i = 0; i < picUrls.length(); i++) {
                            pics.add(picUrls.getString(i));
                        }
                        gvPicAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tvName.setText(messageInfo.getTitle() + "");
                    tvContent.setText(messageInfo.getContent() + "");
                    tvAuthor.setText(messageInfo.getFromName() + "");
                    tvTime.setText(messageInfo.getCreatedDate().substring(0, messageInfo.getCreatedDate().length() - 3) + "");
                }
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                tvName.setText(messageInfo.getTitle() + "");
                tvContent.setText(messageInfo.getContent() + "");
                tvAuthor.setText(messageInfo.getFromName() + "");
                tvTime.setText(messageInfo.getCreatedDate().substring(0, messageInfo.getCreatedDate().length() - 3) + "");
            }
        });
    }
}
