package com.joe.oil.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;

import java.io.File;
import java.util.List;

/**
 * Created by liangxiaojiang on 2017/4/28.
 */

public class OperationAdapter extends BaseAdapter {
    Context context;
    List<File> list;

    public OperationAdapter(Context context, List<File> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            //布局实例化
            convertView = View.inflate(context, R.layout.item_filelayout, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // 设置数据
        File file = (File) getItem(position);
        if (file.getName().endsWith(".jpg")
                || file.getName().endsWith(".png")
                || file.getName().endsWith(".gif")) {
            viewHolder.img.setImageResource(R.drawable.picture);
        } else if (file.getName().endsWith(".txt") || file.getName().endsWith(".docx") ||
                file.getName().endsWith(".pdf") || file.getName().endsWith(".doc")
                || file.getName().endsWith(".xls") ||
                file.getName().endsWith(".xlsx")) {
            viewHolder.img.setImageResource(R.drawable.filepic);
        } else {
//               / viewHolder.img.setImageResource(R.mipmap.ic_launcher);
        }
//        }
        viewHolder.name.setText(file.getName());
//        viewHolder.time.setText(new SimpleDateFormat("yy-M-d HH:mm:ss")
//                .format(new Date(file.lastModified())));

        return convertView;
    }

    class ViewHolder {
        ImageView img;
        TextView name;
        TextView time;

        public ViewHolder(View convertView) {
            img = (ImageView) convertView.findViewById(R.id.img);
            name = (TextView) convertView.findViewById(R.id.name);
            time = (TextView) convertView.findViewById(R.id.time);
        }

    }

}
