package com.joe.oil.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.joe.oil.R;

/**
 * Created by Administrator on 2016/11/23.
 */
public class TaskDeviceActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_taskdevice);


    }
}