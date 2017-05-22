package com.joe.oil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.entity.UploadHseSupervision;

import java.util.List;

/**
 * Hse历史记录适配器
 * Created by scar1et on 15-6-30.
 */
public class HseHistoryAdapter extends BaseAdapter {

    private Context context;
    private List<UploadHseSupervision> uploadHseSupervisions;

    public HseHistoryAdapter(Context context, List<UploadHseSupervision> uploadHseSupervisions) {

        this.context = context;
        this.uploadHseSupervisions = uploadHseSupervisions;
    }

    @Override
    public int getCount() {
        return uploadHseSupervisions.size();
    }

    @Override
    public Object getItem(int position) {
        return uploadHseSupervisions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        ViewHolder holder = null;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_hse_history, null);
            holder = new ViewHolder();

            holder.tvCheckedPoint = (TextView) view.findViewById(R.id.tv_checked_point);
            holder.tvIssues = (TextView) view.findViewById(R.id.tv_issues);
            holder.tvSuggestion = (TextView) view.findViewById(R.id.tv_suggestion);
            holder.tvTime = (TextView) view.findViewById(R.id.tv_time);
            holder.tvUploadStatus = (TextView) view.findViewById(R.id.tv_upload_status);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        UploadHseSupervision uploadHseSupervision = uploadHseSupervisions.get(position);

        holder.tvSuggestion.setText(uploadHseSupervision.getSuggestion());
        holder.tvCheckedPoint.setText(uploadHseSupervision.getCheckedPoint());
        holder.tvTime.setText(uploadHseSupervision.getCreatedDate());
        holder.tvIssues.setText(uploadHseSupervision.getIssue());
        if (uploadHseSupervision.getIsSuccess() == 1) {
            holder.tvUploadStatus.setText("上传成功");
            holder.tvUploadStatus.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvUploadStatus.setText("等待上传");
            holder.tvUploadStatus.setTextColor(context.getResources().getColor(R.color.qianhong));
        }
        return view;
    }

    private static class ViewHolder {

        private TextView tvCheckedPoint;
        private TextView tvIssues;
        private TextView tvSuggestion;
        private TextView tvTime;
        private TextView tvUploadStatus;
    }
}
