package com.joe.oil.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.joe.oil.R;
import com.joe.oil.util.CustomUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by Ayanami_Rei on 2014/8/16.
 * 放大照片，支持滑动
 */
public class PicPagerAdapter extends PagerAdapter {

    private List<String> pics;
    private Context context;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    public PicPagerAdapter(Context context, List<String> pics) {

        this.context = context;
        this.pics = pics;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_pager_pic, container, false);
        ImageView image = (ImageView) view.findViewById(R.id.iv_pic);

        String url = pics.get(position);
        imageLoader.displayImage(url, image, CustomUtil.getDefaultOptions());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return pics.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object o) {
        return view.equals(o);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}
