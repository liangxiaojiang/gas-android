package com.joe.oil.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.IBinder;
import com.joe.oil.entity.Version;
import com.joe.oil.receiver.DownloadCompleteReceiver;
import com.joe.oil.util.FileUtils;

import java.io.File;

/**
 * 更新服务
 * Created by scar1et on 15-6-9.
 */
public class UpdateService extends Service {

    private static final String TAG = "UpdateService";
    private DownloadCompleteReceiver receiver;
    private Context context;
    private Context mContext;
    private DownloadManager manager;
    private Version appVersion;
    private String apkName;

    @Override
    public void onCreate() {
        super.onCreate();

        receiver = DownloadCompleteReceiver.getInstance();
        context = getApplicationContext();
        manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        appVersion = (Version) intent.getSerializableExtra("appVersion");
        receiver.setAppVersion(appVersion);

        apkName =  "oil-v" + appVersion.getVer() + ".apk";

        File file = getExternalFilesDir("download");
        if (file != null && file.toString().length() > 0) {
            receiver.setFilePath(file.toString() + "/" + apkName);
            FileUtils.deleteFile(file);
        }

        if (receiver != null) {

            registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }

        //   showDialog();
        downLoadApk();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    private void downLoadApk() {

        //创建下载请求
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(appVersion.getDownloadUrl()));
        //设置允许使用的网络类型，这里只允许wifi下下载
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //禁止发出通知，既后台下载
//        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        //显示下载界面
        down.setVisibleInDownloadsUi(true);
        //设置下载后文件存放的位置
//        down.setDestinationInExternalPublicDir("", "MobileAssistant_1.apk");

        down.setDestinationInExternalFilesDir(context, "download", apkName);
        //将下载请求放入队列
        manager.enqueue(down);
    }
}