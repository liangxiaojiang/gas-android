package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.multidex.MultiDex;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.List;

@SuppressLint("HandlerLeak")
public class OilApplication extends Application {
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public SqliteHelper sqliteHelper;
    public HttpRequest http;
    private Gis gis;
    public User user = null;
    public int ceshhi;
    public int jiac;

    public int getJiac() {
        return jiac;
    }

    public void setJiac(int jiac) {
        this.jiac = jiac;
    }

    public static  String riskidentification;
    public static  String preventivemeasures;

    public static String startWorkingTime;
    public static String endWorkingTime;

    public String getStartWorkingTime() {
        return startWorkingTime;
    }

    public void setStartWorkingTime(String startWorkingTime) {
        OilApplication.startWorkingTime = startWorkingTime;
    }

    public  String getEndWorkingTime() {
        return endWorkingTime;
    }

    public void setEndWorkingTime(String endWorkingTime) {
        OilApplication.endWorkingTime = endWorkingTime;
    }

    public String getRiskidentification() {
        return riskidentification;
    }

    public void setRiskidentification(String riskidentification) {
        this.riskidentification = riskidentification;
    }

    public String getPreventivemeasures() {
        return preventivemeasures;
    }

    public void setPreventivemeasures(String preventivemeasures) {
        this.preventivemeasures = preventivemeasures;
    }

    public int getCeshhi() {
        return ceshhi;
    }

    public void setCeshhi(int ceshhi) {
        this.ceshhi = ceshhi;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String lat;
    public String lng;

    public List<ImageBean> getImages() {
        return images;
    }

    public void setImages(List<ImageBean> images) {
        this.images = images;
    }

    public List<ImageBean>   images;

    public Task task;
    private int number;
    private  boolean ceshi = false;
    private  boolean ceshi1= false;

    public boolean isCeshi1() {
        return ceshi1;
    }

    public void setCeshi1(boolean ceshi1) {
        this.ceshi1 = ceshi1;
    }

    public int getFinsh() {
        return finsh;
    }

    public void setFinsh(int finsh) {
        this.finsh = finsh;
    }

    private int finsh;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        sqliteHelper = new SqliteHelper(getApplicationContext());
        http = HttpRequest.getInstance(getApplicationContext());

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(getApplicationContext())
                .diskCacheSize(15 * 1024 * 1024)
                .diskCacheFileCount(30)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new WeakMemoryCache())
                .memoryCacheSize(1024 * 1024)
                .memoryCacheExtraOptions(480, 800)
//                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);

        GisFinish data = sqliteHelper.getGisFinishNotFinish();
        if (data != null) {
            Constants.IS_LINE_START = true;
            Constants.GIS_START_NUM = data.getTaskNo();
        }

//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());

        setNumber(num);

        CrashReport.initCrashReport(getApplicationContext(), "900058226", false);

        /**
         * 使用的时候记得关注一下在/sdcard/Android/data/[package_name]/cache目录下的缓存的文件。
         * 记得定期清理缓存，否则时间一长，SD卡就会被占满了，同时也可以在ImageLoaderConfiguration中配置SD的缓存策略，
         * 限制缓存文件数量（memoryCacheSizePercentage），限制缓存文件最大尺寸（memoryCacheSize）等选项。
         */
        initImageLoader(getApplicationContext());
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this) ;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


    private  static final int num =1;

    public boolean isCeshi() {
        return ceshi;
    }

    public void setCeshi(boolean ceshi) {
        this.ceshi = ceshi;
    }


    /**
     * 实现实位回调监听
     */
    public class MyLocationListener implements BDLocationListener {

        @SuppressLint("SimpleDateFormat")
        @Override
        public void onReceiveLocation(BDLocation location) {
            // Receive Location
            if (user == null || user.getLoginName().equals("admin")) {
                return;
            }

            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                StringBuffer sb = new StringBuffer(256);
                gis = new Gis();
                gis.setLatitude(location.getLatitude() + "");
                gis.setLongitude(location.getLongitude() + "");
                gis.setStatus("0");
                gis.setTime(location.getTime());
                if (user != null) {
                    gis.setUserId(user.getUserId());
                }
                gis.setPics("");
                gis.setExceptionStatus("1");
                gis.setIsPicIdUpload("0");
                gis.setDeviceId(Constants.DEVICE_ID);
                gis.setTaskType(Constants.TASK_TYPE);

                String lineId = "";
                if (Constants.IS_LINE_START) {
                    gis.setNum(Constants.GIS_START_NUM);
                }
                // Constants.showToast(getApplicationContext(),
                // location.getTime() + "");
                if (Constants.GPS_INTERVAL != 0 && Constants.GPS_LAST_TIME != null && Constants.GPS_LAST_TIME.length() > 0 &&
                        DateUtils.getInterval(Constants.GPS_LAST_TIME, location.getTime()) >= Constants.GPS_INTERVAL) {

                    Constants.GPS_LAST_TIME = location.getTime();

                    updateGisFinishNum();
                    sqliteHelper.insertGis(gis);
                    http.requestSubmitGisData(new SubmitGisListHandler(gis), gis, lineId, user.getUserId());
                }
                Constants.CURRENT_LAT = location.getLatitude();
                Constants.CURRENT_LNG = location.getLongitude();
                Constants.GPS_TIME = location.getTime();

                if (Constants.GPS_LAST_TIME.equals("1991-4-5 12:00:00")) {
                    Constants.GPS_LAST_TIME = location.getTime();
                }

//                sb.append("time : ");
//                sb.append(location.getTime());
//                sb.append("\nerror code : ");
//                sb.append(location.getLocType());
//                sb.append("\nlatitude : ");
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");
//                sb.append(location.getRadius());
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());
//                    sb.append("\ndirection : ");
//                    sb.append("\naddr : ");
//                    sb.append(location.getAddrStr());
//                    sb.append(location.getDirection());
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                    sb.append("\naddr : ");
//                    sb.append(location.getAddrStr());
//                    // 运营商信息
//                    sb.append("\noperationers : ");
//                    sb.append(location.getOperators());
//                }
                // Constants.showToast(getApplicationContext(), sb.toString());
            }
        }
    }

    private void updateGisFinishNum() {
        if (Constants.IS_LINE_START) {
            // 更新GisFinish的Gis数目
            GisFinish gisFinish = sqliteHelper.getGisFinishNotFinish();
            if (gisFinish != null) {
                int gisNum = gisFinish.getGisNum() + 1;
                gisFinish.setGisNum(gisNum);
                sqliteHelper.updateGisFinish(gisFinish);
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class SubmitGisListHandler extends Handler {
        private Gis gis;

        public SubmitGisListHandler(Gis gis) {
            this.gis = gis;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    if (gis.getExceptionStatus().equals("1") || !gis.getPics().equals("")) {
                        sqliteHelper.deleteGisByTime(gis);
                        return;
                    }
                    gis.setStatus("2");
                    sqliteHelper.updateGis(gis);
                    break;

                case HttpRequest.REQUEST_FAILER:
                    gis.setStatus("1");
                    try {
                        sqliteHelper.updateGisByTime(gis);
                        // sqliteHelper.updateGis(gis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**初始化图片加载类配置信息**/
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)//加载图片的线程数
                .denyCacheImageMultipleSizesInMemory() //解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸。
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//设置磁盘缓存文件名称
                .tasksProcessingOrder(QueueProcessingType.LIFO)//设置加载显示图片队列进程
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}
