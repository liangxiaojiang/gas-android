package com.joe.oil.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liangxiaojiang on 2016/8/31.
 */
public class TaskFinishService extends Service{
    Timer timer = new Timer(true);
    int time = 0;

    // Handler handlerTimer;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();

        startGis();
    }

    public void startGis() {
        TimerTask task = new TimerTask() {
            public void run() {
                time++;
                Intent intent = new Intent("com.example.sendTimerBroadCast");
                intent.putExtra("time", time);
                sendBroadcast(intent);
                // Message message = new Message();
                // message.what = 1;
                // message.obj = time;
                // handlerTimer.sendMessage(message);
            }
        };

        timer.schedule(task, 0, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}

