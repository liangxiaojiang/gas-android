package com.joe.oil.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.entity.MessageInfo;

import java.util.List;

/**
 * 信息列表适配器
 * Created by scar1et on 15-6-30.
 */
public class MessageListAdapter extends BaseAdapter {

    private Context context;
    private List<MessageInfo> messageInfos;

    public MessageListAdapter(Context context, List<MessageInfo> messageInfos) {

        this.context = context;
        this.messageInfos = messageInfos;
    }

    @Override
    public int getCount() {
        return messageInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return messageInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_list, null);
            holder = new ViewHolder();

            holder.tvName = (TextView) view.findViewById(R.id.tv_name);
            holder.tvContent = (TextView) view.findViewById(R.id.tv_content);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            holder.tvAuthor = (TextView) view.findViewById(R.id.tv_author);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        MessageInfo messageInfo = messageInfos.get(position);

        Resources resources = context.getResources();

        if (messageInfo.isRead()) {
            holder.tvName.setTextColor(resources.getColor(R.color.gray));
            holder.tvContent.setTextColor(resources.getColor(R.color.gray));
        } else {
            holder.tvName.setTextColor(resources.getColor(R.color.black));
            holder.tvContent.setTextColor(resources.getColor(R.color.black));
        }

        holder.tvName.setText(messageInfo.getTitle() + "");
        holder.tvContent.setText(messageInfo.getContent() + "");
        holder.tvAuthor.setText(messageInfo.getFromName() + "");
        holder.tvTime.setText(messageInfo.getCreatedDate().substring(0, messageInfo.getCreatedDate().length() - 3) + "");

        return view;
    }

    private static class ViewHolder {

        private TextView tvName;
        private TextView tvContent;
        private TextView tvTime;
        private TextView tvAuthor;
    }
}
