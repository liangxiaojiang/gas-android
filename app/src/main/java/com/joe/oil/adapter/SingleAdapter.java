package com.joe.oil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.entity.Single;

import java.util.List;

/**
 * Created by liangxiaojiang on 2016/11/16.
 */

public class SingleAdapter extends BaseAdapter{

    private Context context;
    private List<Single> single;
    private LayoutInflater inflater;


    public SingleAdapter(Context context, List<Single> single) {
        this.context = context;
        this.single = single;
    }

    @Override
    public int getCount() {
        return single.size();
    }

    @Override
    public Object getItem(int position) {

        return position;
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder ;
        inflater = LayoutInflater.from(context);
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_single, null);

            viewHolder.number=(TextView) convertView
                    .findViewById(R.id.singlenumber_ve);//车牌号
            viewHolder.vehicleNnumber=(TextView) convertView
                    .findViewById(R.id.singlenumber1);//电话
            viewHolder.name=(TextView) convertView
                    .findViewById(R.id.name);//驾驶员名称
            viewHolder.endTime=(TextView) convertView
                    .findViewById(R.id.item_single_time1);//计划结束时间

            viewHolder.vehicleTask = (TextView) convertView
                    .findViewById(R.id.singleName);
            viewHolder.startTime = (TextView) convertView
                    .findViewById(R.id.item_single_time);
//            viewHolder.status=(TextView)convertView.findViewById(R.id.item_status);
            viewHolder.state= (TextView) convertView.findViewById(R.id.single_state);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Single singles = single.get(position);
        viewHolder.number.setText(singles.getNumber());
        viewHolder.vehicleNnumber.setText(singles.getDriverPhone());
        viewHolder.name.setText(singles.getDriverName());
        viewHolder.endTime.setText("收车时间："+singles.getEndTime());

        String s=singles.getVehicleTask();
//        if (!singles.getVehicleTask().equals("null")) {
            if (s.length() > 20) {
                s.substring(0, s.indexOf(",", s.indexOf(",") + 1));
                viewHolder.vehicleTask.setText(s.substring(0, s.indexOf(",", s.indexOf(",") + 1)));
            } else {
                viewHolder.vehicleTask.setText(s);
            }
//        }
        viewHolder.startTime.setText("出车时间："+singles.getStartTime());
       if (!singles.getRealStartTime().equals("")&&singles.getRealEndTime().equals("")){
           viewHolder.state.setText("车辆行驶中");
           viewHolder.state.setTextColor(context.getResources().getColor(R.color.blue1));
       }else if (singles.getRealStartTime().equals("")&&singles.getRealEndTime().equals("")){
           viewHolder.state.setText("等待出车");
       }
        if (!singles.getRealEndTime().equals("")&&!singles.getRealStartTime().equals("")){
            viewHolder.state.setText("已经收车");
            viewHolder.state.setTextColor(context.getResources().getColor(R.color.blue2));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView vehicleTask;
        TextView startTime;
        TextView vehicleNnumber;
        TextView number;
        TextView name;
        TextView endTime;
        TextView state;
    }
}
