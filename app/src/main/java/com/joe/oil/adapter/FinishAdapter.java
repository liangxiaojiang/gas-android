package com.joe.oil.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.joe.oil.R;
import com.joe.oil.imagepicker.Config;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.LanbaooImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by liangxiaojiang on 2016/8/27.
 */
public class FinishAdapter extends BaseAdapter {

    private Context context;//上下文
    private List<ImageBean> imageSets;
    private ImageLoader imageLoader = ImageLoader.getInstance();



    private final int TYPE_FIN = 0;



    private final int TYPE_FINISH=1;

    public FinishAdapter(Context context, List<ImageBean> imageSets){
        this.context = context;
        this.imageSets = imageSets;
    }

    public int getTYPE_FIN() {
        return TYPE_FIN;
    }
    public int getTYPE_FINISH() {
        return TYPE_FINISH;
    }

    @Override
    public int getCount() {
        int size = imageSets.size();
        if (size == 0) {
            return size + 1;
        } else {
            return imageSets.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (imageSets != null)
//                && imageSets.size() == CustomConstants.MAX_IMAGE_SIZE)
        {
            return imageSets.get(position);
        }

        else if (imageSets == null || position - 1 < 0
                || position > imageSets.size())
        {
            return null;
        }
        else
        {
            return imageSets.get(position - 1);
        }

    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        int size = imageSets.size();
        if (size == 0) {
            return TYPE_FIN;
        }else {
            return TYPE_FINISH;
        }

    }
    @Override
    public int getViewTypeCount() {
        int size = imageSets.size();
        if (size == Config.limit) {
            return 1;
        } else {
            return 2;
        }
    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = new LanbaooImageView(context);
            viewHolder.iv_picfinish = (LanbaooImageView) convertView;
            viewHolder.iv_picfinish.setAdjustViewBounds(true);
            viewHolder.iv_picfinish.setScaleType(ImageView.ScaleType.FIT_XY);
//            viewHolder.iv_picfinish.setImageResource(R.drawable.a_4);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        int type = getItemViewType(position);
        switch (type) {
            case TYPE_FIN:
                viewHolder.iv_picfinish.setImageResource(R.drawable.selector_btn_add_pic);
                break;

            case TYPE_FINISH:
                viewHolder.iv_picfinish.setImageResource(R.drawable.a_4);
                ImageBean bean = (ImageBean) getItem(position);
                imageLoader.displayImage("file://" + bean.path, viewHolder.iv_picfinish, Config.mImageOptions);
                break;

            default:
                break;
        }

        return convertView;
    }

    private class ViewHolder {
        LanbaooImageView iv_picfinish;

    }

}
