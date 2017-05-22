package com.joe.oil.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

/**
 * Created by Administrator on 2014/12/02.
 */
public class BaseActivity extends FragmentActivity {

    public SqliteHelper sqliteHelper;
    public OilApplication application;
    private boolean isExceptionOccurred = false;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isExceptionOccurred = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        application = (OilApplication) getApplication();
        sqliteHelper = new SqliteHelper(this);
        if (application.getUser() == null) {
            User user = sqliteHelper.getLoginUser();
            if (user != null) {
                application.setUser(user);
            }
        }
        GisFinish data = sqliteHelper.getGisFinishNotFinish();
        if (data != null) {
            Constants.IS_LINE_START = true;
            Constants.GIS_START_NUM = data.getTaskNo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doWithException();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void doWithException() {
        if (isExceptionOccurred) {
            Log.d("BaseActivity", "================== Exception Display ================!");
            // Constants.showToast(this,
            // "================== Exception Display ================");
            if (application.getUser() == null) {
                User user = sqliteHelper.getLoginUser();
                if (user != null) {
                    application.setUser(user);
                }
            }
            GisFinish data = sqliteHelper.getGisFinishNotFinish();
            if (data != null) {
                Constants.IS_LINE_START = true;
                Constants.GIS_START_NUM = data.getTaskNo();
            }
            isExceptionOccurred = false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_HOME:
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

}
