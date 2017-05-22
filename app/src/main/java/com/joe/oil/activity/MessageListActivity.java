package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.adapter.MessageListAdapter;
import com.joe.oil.entity.MessageInfo;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.util.Constants;
import com.joe.oil.view.CommentListView;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息列表页面
 * Created by scar1et on 15-6-30.
 */
public class MessageListActivity extends BaseActivity {

    private Context context;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvNoDataTips;
    private CommentListView lvMessage;
    private List<MessageInfo> messageInfos;
    private MessageListAdapter messageListAdapter;

    private HttpRequest http;

    private String userId;

    private int curPage = 1;
    private int nextPage = 1;
    private boolean hasNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        initView();
        initDataSet();
        initEvent();
    }

    private void initView() {
        tvLeft = (TextView) findViewById(R.id.tv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvNoDataTips = (TextView) findViewById(R.id.tv_no_data_tips);
        lvMessage = (CommentListView) findViewById(R.id.lv_message);
    }

    private void initDataSet() {
        context = this;
        tvLeft.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.btn_back), null, null, null);
        tvTitle.setText("消息");

        http = HttpRequest.getInstance(context);
        userId = getIntent().getStringExtra("userId");

        messageInfos = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(context, messageInfos);
        lvMessage.setAdapter(messageListAdapter);
        lvMessage.setPullLoadEnable(false);

        tvNoDataTips.setVisibility(View.VISIBLE);
        tvNoDataTips.setText("加载中...");

        getMessageInfos();
    }

    private void initEvent() {
        tvLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MessageListActivity.this.finish();
            }
        });
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                MessageInfo messageInfo = messageInfos.get(position - 1);

                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("messageInfo", messageInfo);

                messageInfo.setIsRead(true);
                messageListAdapter.notifyDataSetChanged();

//                setResult(RESULT_OK);
                startActivity(intent);
            }
        });
        lvMessage.setXListViewListener(new CommentListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                curPage = 1;
                getMessageInfos();
            }

            @Override
            public void onLoadMore() {
                getMessageInfos();
            }
        });
    }

    private void getMessageInfos() {

        AjaxParams params = new AjaxParams();
        params.put("toId", userId);
        params.put("s", "10");
        params.put("p", curPage + "");

        http.getFinalHttp().get(Constants.BASE_URL + "/api/message/list/to", params, new AjaxCallBack<Object>() {
            @Override
            public void onSuccess(Object o) {
                super.onSuccess(o);

                try {
                    JSONObject object = new JSONObject(o.toString());

                    if (object.getInt("totalCount") > 0) {
                        tvNoDataTips.setVisibility(View.GONE);
                    }else {
                        tvNoDataTips.setVisibility(View.VISIBLE);
                        tvNoDataTips.setText("您还没有消息");
                    }

                    hasNext = object.getBoolean("hasNext");
                    nextPage = object.getInt("nextPage");

                    if (curPage == 1) {
                        messageInfos.clear();
                    }

                    if (hasNext) {
                        curPage = nextPage;
                        lvMessage.setPullLoadEnable(true);
                    } else {
                        lvMessage.setPullLoadEnable(false);
                    }

                    JSONArray result = object.getJSONArray("result");
                    if (result != null) {
                        for (int i = 0; i < result.length(); i++) {

                            JSONObject message = result.getJSONObject(i);
                            MessageInfo messageInfo = new MessageInfo();

                            messageInfo.setTitle(message.getString("title"));
                            messageInfo.setId(message.getInt("id"));
                            messageInfo.setContent(message.getString("content"));
                            messageInfo.setCreatedDate(message.getString("createdDate"));
                            messageInfo.setFromName(message.getString("fromName"));
                            messageInfo.setIsRead(message.getBoolean("isRead"));

                            messageInfos.add(messageInfo);
                        }
                        messageListAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    tvNoDataTips.setText("您还没有消息");
                    e.printStackTrace();
                }
                lvMessage.stopLoadMore();
                lvMessage.stopRefresh();
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                tvNoDataTips.setText("您还没有消息");
                lvMessage.stopLoadMore();
                lvMessage.stopRefresh();
            }
        });
    }
}
