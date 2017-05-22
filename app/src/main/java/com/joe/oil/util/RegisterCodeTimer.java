package com.joe.oil.util;

import android.os.CountDownTimer;
import android.os.Handler;

import com.joe.oil.activity.OilApplication;

/**
 * 计时器
 *
 * @author zihao
 */
public class RegisterCodeTimer extends CountDownTimer {
    private static Handler mHandler;
    public static final int IN_RUNNING = 1001;
    public static int END_RUNNING = 1002;
    public   static long jishi=0;
    public static long zjp;
    public   static long timeStart;
    private boolean flag;//标志位

    private OilApplication oilapp;


    /**
     * @param millisInFuture    // 倒计时的时长
     * @param countDownInterval // 间隔时间
     * @param handler           // 通知进度的Handler
     */
    public RegisterCodeTimer(long millisInFuture, long countDownInterval,
                             Handler handler) {
        super(millisInFuture, countDownInterval);
        mHandler = handler;
    }


    // 结束
    @Override
    public void onFinish() {
        // TODO Auto-generated method stub

        if (mHandler != null) {
            mHandler.obtainMessage(END_RUNNING,"已超时间 " ).sendToTarget();

//

        }
    }



    @Override
    public void onTick(long millisUntilFinished) {
        // TODO Auto-generated method stub
        if (mHandler != null) {

            Integer ss = 1000;
            Integer mi = ss * 60;
            Integer hh = mi * 60;
            Integer dd = hh * 24;

            Long day = millisUntilFinished / dd;
            Long hour = (millisUntilFinished - day * dd) / hh;
            Long minute = (millisUntilFinished - day * dd - hour * hh) / mi;
            Long second = (millisUntilFinished - day * dd - hour * hh - minute * mi) / ss;
            mHandler.obtainMessage(IN_RUNNING,
                    "剩余作业时间 ：" + day + "天" + hour + "时" + minute + "分" + second + "秒"
            ).sendToTarget();
//            }
        }
    }



}
