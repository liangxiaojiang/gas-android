package com.joe.oil.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2014/9/18.
 */
public class ResizeLayout extends RelativeLayout {

    private OnResizeListener mListener;

    public interface OnResizeListener {
        void OnResize(int w, int h, int oldWidth, int oldHeight);
    }

    public void setOnResizeListener(OnResizeListener l) {
        mListener = l;
    }

    public ResizeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldWidth, int oldHeight) {
        super.onSizeChanged(w, h, oldWidth, oldHeight);
        if (mListener != null) {
            mListener.OnResize(w, h, oldWidth, oldHeight);
        }
    }
}