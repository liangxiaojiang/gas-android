package com.joe.oil.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 倒计时
 * @author talentClass
 */
public class RegisterCodeTimerService extends Service {
    public static final String IN_RUNNING = "com.joe.oil.service.IN_RUNNING";
    public static final String END_RUNNING = "com.joe.oil.service.END_RUNNING";
    private static CountDownTimer mCodeTimer;
    private long  dayCount;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("lxj","========启动service====="+startId);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = sdf.format(curDate);
        String start = intent.getStringExtra("DeadTime");
        Log.d("lxj","==================================="+start+"----------------" +
                "----------------------------------------");
        try {
            long timeStart = sdf.parse(start).getTime();
            long timeEnd = sdf.parse(str).getTime();
            dayCount = ((timeStart - timeEnd));
        } catch (ParseException e) {
            e.printStackTrace();
        }


		// 第一个参数是总时间， 第二个参数是间隔
        mCodeTimer = new CountDownTimer(dayCount, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
				// 广播剩余时间
                broadcastUpdate(IN_RUNNING, millisUntilFinished / 1000 + "");
            }

            @Override
            public void onFinish() {
				// 广播倒计时结束
                broadcastUpdate(END_RUNNING);
                // 停止服务
                stopSelf();
            }
        };
		// 开始倒计时
        mCodeTimer.start();
        return super.onStartCommand(intent, flags, startId);
    }

	// 发送广播
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

	// 发送带有数据的广播
    private void broadcastUpdate(final String action, String time) {
        final Intent intent = new Intent(action);
        intent.putExtra("time", time);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 停止服务
//        stopSelf();
        Log.d("lxj","服务停止");
    }
}
