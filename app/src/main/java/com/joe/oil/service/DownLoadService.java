package com.joe.oil.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.joe.oil.util.NotifyUtil;

import java.io.File;

/**
 * 下载文档的service，2017-4-26
 */
public class DownLoadService extends Service {
    String download_url;
    String download_name;

    private int requestCode = (int) SystemClock.uptimeMillis();
    private NotifyUtil currentNotify;
    File mFile;

//
//    private URL url = null;
//    FileUtils fileUtils = new FileUtils();
//    public int downfile(String urlStr,String path,String fileName)
//    {
//        if(fileUtils.isFileExist(path + fileName))
//        {
//            return 1;
//        }
//        else
//        {
//            try{
//                InputStream input = null;
//                input = getInputStream(urlStr);
//                File resultFile = fileUtils.write2SDFromINput(path, fileName, input);
//                if(resultFile == null)
//                {
//                    return -1;
//                }
//            }
//            catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//        return 0;
//    }
//    //由于得到一个InputStream对象是所有文件处理前必须的操作，所以将这个操作封装成了一个方法
//    public InputStream getInputStream(String urlStr) throws IOException
//    {
//        InputStream is = null;
//        try
//        {
//            url = new URL(urlStr);
//            HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
//            urlConn.setRequestMethod("GET");
//            urlConn.setDoInput(true);
//            urlConn.setDoOutput(true);
//            urlConn.setUseCaches(false);
//            urlConn.setConnectTimeout(5000);
//            urlConn.setReadTimeout(5000);
//
//
//            //实现连接
//            urlConn.connect();
//            if (urlConn.getResponseCode() == 200)
//            {
//
//                is  = urlConn.getInputStream()	;
//            }
//
//        }
//        catch(MalformedURLException e)
//        {
//            e.printStackTrace();
//        }
//        return is;
//    }

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


        download_url=intent.getStringExtra("download_url");
        download_name=intent.getStringExtra("download_name");
        String savePath= Environment.getExternalStorageDirectory().getPath()+download_name;
        mFile=new File(savePath);
        Log.e("test","执行onStartCommand");

        //设置想要展示的数据内容
        Intent intent_noti = new Intent();
        intent_noti.setAction(Intent.ACTION_VIEW);
        //文件的类型，从tomcat里面找
        intent_noti.setDataAndType(Uri.fromFile(mFile), "pdf");
        PendingIntent rightPendIntent = PendingIntent.getActivity(this,
                requestCode, intent_noti, PendingIntent.FLAG_UPDATE_CURRENT);
        int smallIcon =0;
        String ticker = "正在更新";
        //实例化工具类，并且调用接口
        NotifyUtil notify7 = new NotifyUtil(this, 7);
        notify7.notify_progress(rightPendIntent, smallIcon, ticker,download_name, "正在下载中",
                false, false, false, download_url, savePath, new NotifyUtil.DownLoadListener() {
                    @Override
                    public void OnSuccess(File file) {
                        mFile=file;
                        DownLoadService.this.stopSelf();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {

                    }
                });
        currentNotify = notify7;
        return super.onStartCommand(intent, flags, startId);

    }



}
