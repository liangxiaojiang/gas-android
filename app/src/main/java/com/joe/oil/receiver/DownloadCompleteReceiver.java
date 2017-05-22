package com.joe.oil.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.joe.oil.entity.Version;
import com.joe.oil.util.Constants;

/**
 * 单例广播接收者
 * Created by scar1et on 15-6-10.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {

    private Version appVersion;
    private Context context;
    private String filePath;
    private static DownloadCompleteReceiver receiver = new DownloadCompleteReceiver();

    public static DownloadCompleteReceiver getInstance() {
        return receiver;
    }

    public Context getContext() {
        return context;
    }

    public void setAppVersion(Version appVersion) {
        this.appVersion = appVersion;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {


            if (this.context != null && filePath != null && filePath.length() > 0) {

                Intent intent_mainReceiver = new Intent(Constants.DAWNLOAD_FINISH);
                context.sendBroadcast(intent_mainReceiver);

                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }
        }
    }

    private DownloadCompleteReceiver() {
    }
}
