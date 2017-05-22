package com.joe.oil.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.joe.oil.activity.InspectionActivity;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.entity.CheckItem;
import com.joe.oil.entity.FeedBack;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.Office;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.PlanTemplateDetail;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.UploadException;
import com.joe.oil.entity.UploadHseSupervision;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.http.XUtilsHttp;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.SqliteHelperForItem;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
import com.joe.oil.util.StringUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class OilService extends Service {

    private static final String tag = "creat_plan";
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private SqliteHelper sqliteHelper;
    private SqliteHelperForItem sqliteHelperForItem;
    private List<PlanDetail> planDetails;
    private OilApplication application;
    private User user;
    private SharedPreferences sharedPreferences;
    private Editor editor;
    private Context mContext;
    private String wifiIpString;
    private String togIpString;
    private List<CheckItem> checkItems;
    private static final int CREATE_STATION_PLAN = 3;
    private static final int CREATE_WELL_PLAN = 4;
    private static final int CREATE_STATION_PLAN_FINISH = 6;
    private static final int CREATE_WELL_PLAN_FINISH = 7;
    private static final int UPLOAD_DATA_PICTURE = 5;
    private static final int WELL_PLAN_EXIST = 100;
    private static final int STATION_PLAN_EXIST = 101;
    private int netChangeTime = 0; // 网络变化次数
    private Thread mThread; // 数据和图片上传的线程
    private long historyRecordTime = 0; // 数据和图片上传的计时变量
    private XUtilsHttp xUtilsHttp;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    private TelephonyManager mTm;

    private Handler uploadHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    for (int i = 0; i < planDetails.size(); i++) {
                        planDetails.get(i).setStatus("4");
                    }
                    sqliteHelper.updateUploadPlanStatus(planDetails);

                    String currentInspection = sharedPreferences.getString("currentInspection", "station");
                    if (currentInspection.equals("station")) {
                        String currentStation = sharedPreferences.getString(Constants.CURRENT_STATION, "0");
                        if (!currentStation.equals("-1") && InspectionActivity.refreshHandler != null) {

                            Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                        } else if (currentStation.equals("-1") && InspectionActivity.refreshHandler != null) {
                            Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                        }
                    } else {
                        String currentWell = sharedPreferences.getString(Constants.CURRENT_WELL, "0");
                        if (!currentWell.equals("-1") && InspectionActivity.refreshHandler != null) {
                            Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                        } else if (currentWell.equals("-1") && InspectionActivity.refreshHandler != null) {
                            Message msg2 = InspectionActivity.refreshHandler.obtainMessage();
                            msg2.sendToTarget();
                        }
                    }
                    break;

                case HttpRequest.REQUEST_FAILER:
                    // http.requestSubmitGasList(uploadHandler, planDetails,
                    // "normal", user.getUserId());
                    break;

                case 200:
                    Constants.showToast(mContext, "生成巡检计划成功！");
                    editor.remove(Constants.CURRENT_STATION);
                    editor.remove(Constants.CURRENT_WELL);
                    editor.remove(Constants.STATION_COUNT);
                    editor.remove(Constants.WELL_COUNT);
                    editor.commit();
                    break;

                default:
                    break;
            }
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, final Intent intent) {
            mTm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

            // 网络状态发生变化时提交未上传的巡检数据
            final String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                Log.d(tag, "网络状态已经改变");
                wifiIpString = sharedPreferences.getString("wifiIp", Constants.WIFI_IP);
                togIpString = sharedPreferences.getString("2GIp", Constants.BASE_URL);
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable()) {
                    if (info != null && info.isAvailable()) {
                        if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                            if (wifiIpString.length() > 12)
                                Constants.BASE_URL = wifiIpString;
                            netChangeTime++;
                            if (netChangeTime % 4 == 0) {
                                Constants.showToast(context, "当前为Wifi状态下IP");
                            }
                        } else {
                            if (togIpString.length() > 12)
                                Constants.BASE_URL = togIpString;
                            netChangeTime++;
                            if (netChangeTime % 4 == 0) {
                                Constants.showToast(context, "当前为2G网状态下IP");
                            }
                        }
                        Message urlMsg = HttpRequest.urlHandler.obtainMessage();
                        urlMsg.sendToTarget();
                    }
                    String name = info.getTypeName();
                    Log.d(tag, "当前网络名称：" + name);
                    long currentTime = System.currentTimeMillis();
                    if (Constants.checkNetWork(OilService.this) && currentTime - historyRecordTime > 15 * 1000) {
                        historyRecordTime = currentTime;
                        letUsGo();
                    }
                } else {
                    Constants.showToast(context, "请检查网络连接");
                }
            }
            if (action.equals("creat_plan")) {
                user = application.getUser();

                final String from = intent.getStringExtra("from");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (from.equals("main")) {
                            String officeId = user.getOfficeId();
                            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            String dateString = format.format(new Date());
                            // 检测日计划是否存在，如果不存在，生成当天的日计划
                            if (sqliteHelper.isDayPlanExist(dateString)) {
                                Log.d(tag, "场站巡护日计划存在");
                                handWorkHandler.sendEmptyMessage(STATION_PLAN_EXIST);
                            } else {
                                handWorkHandler.sendEmptyMessage(CREATE_STATION_PLAN);
                                sqliteHelper.deletePlanDetail("1", "1");
                                new TomorrowPlan(action, "1", officeId).startCreatePlan();
                                handWorkHandler.sendEmptyMessage(CREATE_STATION_PLAN_FINISH);
                            }
                            // 检测是否存在单井巡护月计划，如果不存在，则生成月计划
                            String timeMonth = sqliteHelper.getUpAndDownTime("3", "2");
                            if (timeMonth != null) {
                                String upTime = "";
                                String downTime = "";
                                String[] timeArray;
                                timeArray = timeMonth.split(",");
                                upTime = timeArray[0];
                                downTime = timeArray[1];
                                if (sqliteHelper.isMonthOrWeekPlanExist("3") && isDatePass(upTime, downTime, new Date())) {
                                    Log.d(tag, "单井巡护月计划存在");
                                    handWorkHandler.sendEmptyMessage(WELL_PLAN_EXIST);
                                } else {
                                    sqliteHelper.deletePlanDetail("3", "2");
                                    handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN);
                                    new TomorrowPlan(action, "3", officeId).startCreatePlan();
                                    handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN_FINISH);
                                    Log.d(tag, "单井巡护月计划过期");
                                }
                            } else {
                                Log.d(tag, "单井巡护月计划不存在");
                                handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN);
                                new TomorrowPlan(action, "3", officeId).startCreatePlan();
                                handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN_FINISH);
                            }
                            Intent intent = new Intent(Constants.DATA_LOADING_FINISH);
                            sendBroadcast(intent);
                        } else {
                            Intent intent_local = new Intent(Constants.DATA_LOADING_START);
                            sendBroadcast(intent_local);
                            String officeId = intent.getStringExtra("officeId");
                            if (sharedPreferences.getString("currentInspection", "station").equals("station")) {
                                handWorkHandler.sendEmptyMessage(CREATE_STATION_PLAN);
                                new TomorrowPlan(action, "1", officeId).startCreatePlan();
                                handWorkHandler.sendEmptyMessage(CREATE_STATION_PLAN_FINISH);
                            } else {
                                handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN);
                                new TomorrowPlan(action, "3", officeId).startCreatePlan();
                                handWorkHandler.sendEmptyMessage(CREATE_WELL_PLAN_FINISH);
                            }
                            intent_local = new Intent(Constants.DATA_LOADING_FINISH);
                            sendBroadcast(intent_local);
                        }
                    }
                }).start();
            }
        }
    };

    /**
     * @return
     * @Description
     * @author joe
     * @date 2014年10月31日 上午11:21:39
     */
    private boolean isDatePass(String upTime, String downTime, Date now) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date upDate = null;
        Date downDate = null;
        try {
            upDate = format.parse(upTime);
            downDate = format.parse(downTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (now.after(downDate) && now.before(upDate)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDataSet();
        startGis();
        startDataAndPicsUploadThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void initDataSet() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mFilter.addAction(Intent.ACTION_TIME_TICK);
        mFilter.addAction(Constants.IS_DOWNLOAD_PLAN);
        mFilter.addAction(Constants.XJ_ITEM);
        mFilter.addAction("creat_plan");
        registerReceiver(mReceiver, mFilter);
        checkItems = new ArrayList<CheckItem>();

        mContext = OilService.this;
        application = (OilApplication) getApplication();
        sqliteHelper = new SqliteHelper(OilService.this);
        sqliteHelperForItem = new SqliteHelperForItem(OilService.this);
        sharedPreferences = getSharedPreferences("oil", 0);
        editor = sharedPreferences.edit();
        xUtilsHttp = XUtilsHttp.getInstance(mContext);
    }

    /**
     * 启动数据和图片计时上传线程
     */
    private void startDataAndPicsUploadThread() {
        mThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    long currentTime = System.currentTimeMillis();
                    if (Constants.checkNetWork(OilService.this) && currentTime - historyRecordTime > 3 * 30 * 1000) {
                        historyRecordTime = currentTime;
                        Message msg = handWorkHandler.obtainMessage();
                        msg.what = UPLOAD_DATA_PICTURE;
                        msg.sendToTarget();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                    }
                }
            }
        });
        mThread.start();
    }

    private void letUsGo() {
        Log.d("OilService", "=================================================================");
        // 按照一定的顺序执行，能最大限度的速度同步数据！
        // 1.数据打头阵
        uploadAllNewRecordHse();
        uploadAllNewRecordException();
        uploadAllNewRecordExceptionTask();
//        uploadAllNewRecordExceptionFeedBack();
        patrolPlanHandle();
        patrolPlanHandle(false);
        gisSaveMutiHandle(false);
        handleWithException();
        // 2.图片随后输出
        uploadAllNewSelectedPics();
        // 3.绑定更新操作断后
        updateHistoryMuti();
        patrolPlanHandle(true);
        gisSaveMutiHandle(true);
        gisSaveNameUpload();
        Log.d("OilService", "=================================================================");
    }


    /**
     * 生成第二天巡检计划
     */
    private class TomorrowPlan {
        private String action;
        private String planType;
        private String officeId;

        public TomorrowPlan(String action, String planType, String officeId) {
            this.action = action;
            this.planType = planType;
            this.officeId = officeId;
        }

        public void startCreatePlan() {
            Log.d(tag, "生成计划");
            Log.d(tag, "planType:  " + planType);
            Date tomorrow;
            if (action.equals(Intent.ACTION_TIME_TICK)) {
                tomorrow = DateUtils.addDays(new Date(), 1);
            } else {
                tomorrow = new Date();
            }

            List<PlanDetail> details = new ArrayList<PlanDetail>();
            String xofficeId = sqliteHelper.getOfficeId(user.getOfficeName());

            if (officeId.equals("3")) {
                officeId = "";
            }
            List<PlanTemplateDetail> templates = new ArrayList<PlanTemplateDetail>();
            List<PlanTemplateDetail> templatestemp = sqliteHelper.getPlanTemplateDetails(planType, officeId);
            for (int i = 0; i < templatestemp.size(); i++) {
                templates.add(templatestemp.get(i));
            }
            templatestemp.clear();
            if (xofficeId != null) {
                templatestemp = sqliteHelper.getPlanTemplateDetails(planType, xofficeId);
                for (int i = 0; i < templatestemp.size(); i++) {
                    templates.add(templatestemp.get(i));
                }
            }
            for (PlanTemplateDetail template : templates) {
                Log.d(tag, template.getPlanType());
                if (StringUtils.isBlank(template.getExecutionTime())) {
                    continue;
                }
                Office office = new Office();
                office.setId(template.getOfficeId());
                office.setName(template.getOfficeName());
                List<CheckItem> items = new ArrayList<CheckItem>();
                items = sqliteHelper.getCheckItemsByPoint(template.getPointId(), template.getPointType());
                List<CheckItem> tempItems = new ArrayList<CheckItem>();
                for (int i = 0; i < items.size(); i++) {
                    Log.d(tag, "itemCode + " + items.get(i).getCode());
                    if (items.get(i).getCode().length() == 14 || items.get(i).getCode().length() == 15) {
                        tempItems.add(items.get(i));
                    } else {
                        continue;
                    }
                }
                for (CheckItem item : tempItems) {
                    String[] patrolTemes = template.getExecutionTime().split(",");
                    switch (Integer.parseInt(template.getPlanType())) {
                        case 1:
                            for (int i = 0; i < patrolTemes.length; i++) {
                                PlanDetail planDetail = new PlanDetail();
                                planDetail.setDuration(template.getDuration());
                                planDetail.setSort(template.getSort());
                                planDetail.setItemId(item.getItemId());
                                planDetail.setItemName(item.getName());
                                planDetail.setItemUnit(item.getUnit());
                                planDetail.setOfficeId(office.getId());
                                planDetail.setCode(item.getCode());
                                planDetail.setHandleMemoUpload("0");
                                planDetail.setExceptionStatus("1");
                                planDetail.setOfficeName(office.getName());
                                planDetail.setPointId(Integer.parseInt(template.getPointId()));
                                planDetail.setPointName(template.getPointName());
                                planDetail.setUpdateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                                planDetail.setPatrolDate(DateUtils.formatDate(tomorrow, "yyyy-MM-dd"));
                                planDetail.setPlanType(template.getPlanType());
                                planDetail.setType(template.getPointType());
                                planDetail.setUpValue(item.getUpValue());
                                planDetail.setDownValue(item.getDownValue());
                                // String currentTimeMillis =
                                // System.currentTimeMillis()
                                // + "";
                                // String s =
                                // currentTimeMillis.substring(currentTimeMillis.length()
                                // - 3, currentTimeMillis.length());
                                // planDetail.setCreateTime(DateUtils.getDateTime()
                                // +
                                // s);
                                planDetail.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss SSS"));
                                // planDetail.setUnit(item.getUnit());
                                planDetail.setItemType(item.getType());
                                planDetail.setStatus("1");
                                planDetail.setTips(item.getMemo());
                                String tag = item.getTag();
                                if (tag.equals("null")) {
                                    tag = "";
                                }
                                if (tag.endsWith(",")) {
                                    tag = tag.substring(0, tag.length() - 1);
                                }
                                planDetail.setTag(tag);
                                planDetail.setPicId("");
                                planDetail.setIsPicIdUpdate("0");
                                planDetail.setPatrolTime(DateUtils.formatDate(tomorrow, "yyyy-MM-dd") + " " + patrolTemes[i] + ":00");
                                if (planDetail.getPlanType().equals("1") && planDetail.getPatrolTime().length() > 13) {// 日计划通过波动时间算出巡检时间上限和下限
                                    String hour = planDetail.getPatrolTime().substring(11, 13);
                                    int afterAddHour = 0;
                                    if (hour.length() > 0) {
                                        afterAddHour = Integer.parseInt(hour) + Integer.parseInt(planDetail.getDuration());
                                        if (afterAddHour >= 10 && afterAddHour <= 24) {
                                            hour = afterAddHour + "";
                                        } else if (afterAddHour > 24) {
                                            hour = "24";
                                        } else if (afterAddHour < 10 && afterAddHour >= 0) {
                                            hour = "0" + afterAddHour;
                                        } else {
                                            hour = afterAddHour + "";
                                        }
                                    }
                                    planDetail.setDownTime(planDetail.getPatrolTime());
                                    planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 11) + hour + planDetail.getPatrolTime().substring(13, 19));
                                } else if (planDetail.getPlanType().equals("2") || planDetail.getPlanType().equals("3") && planDetail.getPatrolTime().length() > 13) {
                                    String day = planDetail.getPatrolTime().substring(8, 10);
                                    if (Constants.GPS_TIME.length() > 0) {
                                        int month = Integer.parseInt(planDetail.getPatrolTime().substring(5, 7));
                                        switch (month) {
                                            case 1:
                                            case 3:
                                            case 5:
                                            case 7:
                                            case 8:
                                            case 10:
                                            case 12:
                                                int afterDay = 0;
                                                if (day.length() > 0) {
                                                    afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                    // if (afterDay > 10 && afterDay <=
                                                    // 31)
                                                    // {
                                                    day = afterDay + "";
                                                    // } else {
                                                    // day = "0" + afterDay;
                                                    // }
                                                }
                                                break;

                                            case 4:
                                            case 6:
                                            case 9:
                                            case 11:
                                                if (day.length() > 0) {
                                                    afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                    // if (afterDay > 10 && afterDay <=
                                                    // 30)
                                                    // {
                                                    day = afterDay + "";
                                                    // } else {
                                                    // day = "0" + afterDay;
                                                    // }
                                                }
                                                break;

                                            case 2:
                                                if (day.length() > 0) {
                                                    int year = Integer.parseInt(Constants.GPS_TIME.substring(0, 4));
                                                    if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        if (afterDay > 10 && afterDay <= 29) {
                                                            day = afterDay + "";
                                                        } else {
                                                            day = "0" + afterDay;
                                                        }
                                                    } else {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        // if (afterDay > 10 && afterDay
                                                        // <=
                                                        // 28) {
                                                        day = afterDay + "";
                                                        // } else {
                                                        // day = "0" + afterDay;
                                                        // }
                                                    }
                                                }
                                                break;
                                            default:
                                                break;
                                        }

                                    }

                                    planDetail.setDownTime(planDetail.getPatrolTime());
                                    planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 8) + day + planDetail.getPatrolTime().substring(10, 19));
                                }
                                details.add(planDetail);
                            }
                            break;
                        case 2:
                            for (int i = 0; i < patrolTemes.length; i++) {
                                String dateString = DateUtils.formatDate(tomorrow, "E");
                                if (dateString.equals(patrolTemes[i])) {
                                    PlanDetail planDetail = new PlanDetail();
                                    planDetail.setDuration(template.getDuration());
                                    planDetail.setItemId(item.getItemId());
                                    planDetail.setItemName(item.getName());
                                    planDetail.setSort(template.getSort());
                                    planDetail.setItemUnit(item.getUnit());
                                    planDetail.setOfficeId(office.getId());
                                    planDetail.setCode(item.getCode());
                                    planDetail.setHandleMemoUpload("0");
                                    planDetail.setExceptionStatus("1");
                                    planDetail.setOfficeName(office.getName());
                                    planDetail.setPointId(Integer.parseInt(template.getPointId()));
                                    planDetail.setPointName(template.getPointName());
                                    planDetail.setPlanType(template.getPlanType());
                                    planDetail.setType(template.getPointType());
                                    planDetail.setStatus("1");
                                    planDetail.setUpValue(item.getUpValue());
                                    planDetail.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss SSS"));
                                    planDetail.setDownValue(item.getDownValue());
                                    // planDetail.setUnit(item.getUnit());
                                    planDetail.setItemType(item.getType());
                                    planDetail.setTips(item.getMemo());
                                    String tag = item.getTag();
                                    if (tag.equals("null")) {
                                        tag = "";
                                    }
                                    if (tag.endsWith(",")) {
                                        tag = tag.substring(0, tag.length() - 1);
                                    }
                                    planDetail.setTag(tag);
                                    planDetail.setPicId("");
                                    planDetail.setIsPicIdUpdate("0");
                                    planDetail.setUpdateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                                    planDetail.setPatrolDate(DateUtils.formatDate(tomorrow, "yyyy-MM-dd"));
                                    planDetail.setPatrolTime(DateUtils.formatDate(tomorrow, "yyyy-MM-dd") + " " + "00:00:00");
                                    if (planDetail.getPlanType().equals("1") && planDetail.getPatrolTime().length() > 13) {// 日计划通过波动时间算出巡检时间上限和下限
                                        String hour = planDetail.getPatrolTime().substring(11, 13);
                                        int afterAddHour = 0;
                                        if (hour.length() > 0) {
                                            afterAddHour = Integer.parseInt(hour) + Integer.parseInt(planDetail.getDuration());
                                            if (afterAddHour >= 10 && afterAddHour <= 24) {
                                                hour = afterAddHour + "";
                                            } else if (afterAddHour > 24) {
                                                hour = "24";
                                            } else {
                                                hour = "0" + afterAddHour;
                                            }
                                        }
                                        planDetail.setDownTime(planDetail.getPatrolTime());
                                        planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 11) + hour + planDetail.getPatrolTime().substring(13, 19));
                                    } else if (planDetail.getPlanType().equals("2") || planDetail.getPlanType().equals("3") && planDetail.getPatrolTime().length() > 13) {
                                        String day = planDetail.getPatrolTime().substring(8, 10);
                                        if (Constants.GPS_TIME.length() > 0) {
                                            int month = Integer.parseInt(planDetail.getPatrolTime().substring(5, 7));
                                            switch (month) {
                                                case 1:
                                                case 3:
                                                case 5:
                                                case 7:
                                                case 8:
                                                case 10:
                                                case 12:
                                                    int afterDay = 0;
                                                    if (day.length() > 0) {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        if (afterDay > 10 && afterDay <= 31) {
                                                            day = afterDay + "";
                                                        } else {
                                                            day = afterDay + "";
                                                        }
                                                    }
                                                    break;

                                                case 4:
                                                case 6:
                                                case 9:
                                                case 11:
                                                    if (day.length() > 0) {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        if (afterDay > 10 && afterDay <= 30) {
                                                            day = afterDay + "";
                                                        } else {
                                                            day = afterDay + "";
                                                        }
                                                    }
                                                    break;

                                                case 2:
                                                    if (day.length() > 0) {
                                                        int year = Integer.parseInt(Constants.GPS_TIME.substring(0, 4));
                                                        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                                                            afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                            // if (afterDay > 10 &&
                                                            // afterDay
                                                            // <= 29) {
                                                            day = afterDay + "";
                                                            // } else {
                                                            // day = "0" + afterDay;
                                                            // }
                                                        } else {
                                                            afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                            // if (afterDay > 10 &&
                                                            // afterDay
                                                            // <= 28) {
                                                            day = afterDay + "";
                                                            // } else {
                                                            // day = "0" + afterDay;
                                                            // }
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }

                                        }

                                        planDetail.setDownTime(planDetail.getPatrolTime());
                                        planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 8) + day + " " + "24:00:00");
                                    }
                                    details.add(planDetail);
                                }
                            }
                            break;
                        case 3:
                            for (int i = 0; i < patrolTemes.length; i++) {
                                int x = Integer.parseInt(DateUtils.formatDate(tomorrow, "dd"));
                                int y = Integer.parseInt(patrolTemes[i]) + +Integer.parseInt(template.getDuration());
                                Log.d("OilService", "X: " + x + "           y: " + y);
                                if (x <= y) {
                                    PlanDetail planDetail = new PlanDetail();
                                    planDetail.setDuration(template.getDuration());
                                    planDetail.setItemName(item.getName());
                                    planDetail.setItemId(item.getItemId());
                                    planDetail.setItemUnit(item.getUnit());
                                    planDetail.setSort(template.getSort());
                                    planDetail.setOfficeId(office.getId());
                                    planDetail.setOfficeName(office.getName());
                                    planDetail.setHandleMemoUpload("0");
                                    planDetail.setExceptionStatus("1");
                                    planDetail.setCode(item.getCode());
                                    planDetail.setPointId(Integer.parseInt(template.getPointId()));
                                    planDetail.setPointName(template.getPointName());
                                    planDetail.setPlanType(template.getPlanType());
                                    planDetail.setType(template.getPointType());
                                    planDetail.setStatus("1");
                                    planDetail.setUpValue(item.getUpValue());
                                    planDetail.setCreateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss SSS"));
                                    planDetail.setDownValue(item.getDownValue());
                                    // planDetail.setUnit(item.getUnit());
                                    planDetail.setItemType(item.getType());
                                    planDetail.setTips(item.getMemo());
                                    String tag = item.getTag();
                                    if (tag.equals("null")) {
                                        tag = "";
                                    }
                                    if (tag.endsWith(",")) {
                                        tag = tag.substring(0, tag.length() - 1);
                                    }
                                    planDetail.setTag(tag);
                                    planDetail.setPicId("");
                                    planDetail.setIsPicIdUpdate("0");
                                    planDetail.setUpdateTime(DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                                    String yyyyMM = DateUtils.formatDate(new Date(), "yyyy-MM");
                                    planDetail.setPatrolDate(yyyyMM + "-" + patrolTemes[i]);
                                    planDetail.setPatrolTime(yyyyMM + "-" + patrolTemes[i] + " " + "00:00:00");
                                    if (planDetail.getPlanType().equals("1") && planDetail.getPatrolTime().length() > 13) {// 日计划通过波动时间算出巡检时间上限和下限
                                        String hour = planDetail.getPatrolTime().substring(11, 13);
                                        int afterAddHour = 0;
                                        if (hour.length() > 0) {
                                            afterAddHour = Integer.parseInt(hour) + Integer.parseInt(planDetail.getDuration());
                                            if (afterAddHour >= 10 && afterAddHour <= 24) {
                                                hour = afterAddHour + "";
                                            } else if (afterAddHour > 24) {
                                                hour = "24";
                                            } else {
                                                hour = "0" + afterAddHour;
                                            }
                                        }
                                        planDetail.setDownTime(planDetail.getPatrolTime());
                                        planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 11) + hour + planDetail.getPatrolTime().substring(13, 19));
                                    } else if (planDetail.getPlanType().equals("2") || planDetail.getPlanType().equals("3") && planDetail.getPatrolTime().length() > 13) {
                                        String day = planDetail.getPatrolTime().substring(8, 10);
                                        if (Constants.GPS_TIME.length() > 0) {
                                            int month = Integer.parseInt(planDetail.getPatrolTime().substring(5, 7));
                                            switch (month) {
                                                case 1:
                                                case 3:
                                                case 5:
                                                case 7:
                                                case 8:
                                                case 10:
                                                case 12:
                                                    int afterDay = 0;
                                                    if (day.length() > 0) {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        if (afterDay >= 10 && afterDay <= 31) {
                                                            day = afterDay + "";
                                                        } else if (afterDay > 31) {
                                                            day = 31 + "";
                                                        } else {
                                                            day = "0" + afterDay;
                                                        }
                                                    }
                                                    break;

                                                case 4:
                                                case 6:
                                                case 9:
                                                case 11:
                                                    if (day.length() > 0) {
                                                        afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                        if (afterDay >= 10 && afterDay <= 30) {
                                                            day = afterDay + "";
                                                        } else if (afterDay > 30) {
                                                            day = 30 + "";
                                                        } else {
                                                            day = "0" + afterDay;
                                                        }
                                                    }
                                                    break;

                                                case 2:
                                                    if (day.length() > 0) {
                                                        int year = Integer.parseInt(Constants.GPS_TIME.substring(0, 4));
                                                        if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                                                            afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                            if (afterDay >= 10 && afterDay <= 29) {
                                                                day = afterDay + "";
                                                            } else if (afterDay > 29) {
                                                                day = 29 + "";
                                                            } else {
                                                                day = "0" + afterDay;
                                                            }
                                                        } else {
                                                            afterDay = Integer.parseInt(day) + Integer.parseInt(planDetail.getDuration());
                                                            if (afterDay >= 10 && afterDay <= 28) {
                                                                day = afterDay + "";
                                                            } else if (afterDay > 28) {
                                                                day = 28 + "";
                                                            } else {
                                                                day = "0" + afterDay;
                                                            }
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }

                                        }

                                        planDetail.setDownTime(planDetail.getPatrolTime());
                                        planDetail.setUpTime(planDetail.getPatrolTime().substring(0, 8) + day + " " + "24:00:00");
                                    }
                                    details.add(planDetail);
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            // 新添加-向数据库写入数据之前，先删除同样计划类型的数据。
            try {
                if (planType.equals("1")) {
                    sqliteHelper.deletePlanDetail("1", "1", officeId);
                } else if (planType.equals("2")) {
                    sqliteHelper.deletePlanDetail("3", "2", officeId);
                }
                sqliteHelperForItem.insertPlanDetail(details);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // =====================================================================================================================================================
    //
    // =====================================================================================================================================================

    private Handler handWorkHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    break;

                case CREATE_STATION_PLAN:
                    Constants.showToast(mContext, "正在生成场站巡护计划");
                    break;

                case CREATE_WELL_PLAN:
                    Constants.showToast(mContext, "正在生成单井巡护计划");
                    break;

                case CREATE_STATION_PLAN_FINISH:
                    Constants.showToast(mContext, "场站巡护计划生成成功√");
                    break;

                case CREATE_WELL_PLAN_FINISH:
                    Constants.showToast(mContext, "单井巡护计划生成成功√");
                    break;

                case WELL_PLAN_EXIST:
                    Constants.showToast(mContext, "单井巡护计划存在");
                    break;

                case STATION_PLAN_EXIST:
                    Constants.showToast(mContext, "场站巡护计划存在");
                    break;

                case UPLOAD_DATA_PICTURE:
                    user = sqliteHelper.getLoginUser();
                    application.setUser(user);
                    letUsGo();
                    break;

                default:
                    break;
            }
        }
    };

    // =====================================================================================================================================================
    //
    // =====================================================================================================================================================

    /**
     * @Flag 图片上传
     * @Description 后台上传所有未上传图片
     * @author Administrator
     * @date 2014年11月27日
     */
    private void uploadAllNewSelectedPics() {
        Log.d("OilService", "--- START 上传图片 START");
        List<Picture> pictures = sqliteHelper.getPictures();
        if (pictures != null && pictures.size() > 0) {
            Log.d("OilService", "未上传图片：" + pictures.size() + "张");
            for (int i = 0; i < pictures.size(); i++) {
                new MyUploadPicture(pictures.get(i)).startUpload();
            }
        } else {
            Log.d("OilService", "没有需要上传的图片");
        }
        Log.d("OilService", "--- END 上传图片 END \n");
    }

    /**
     * @Flag 图片上传
     * @Description 将上传成功的图片与上传成功的数据绑定
     * @author Administrator
     * @date 2014年11月27日
     */
    private void updateHistoryMuti() {
        Log.d("OilService", "%%% START 绑定图片数据 START");
        List<Picture> result = sqliteHelper.getPicturesUnBind();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未与数据绑定图片：" + result.size() + "张");
            new MyUpdateHistoryMuti(result).startBind();
        } else {
            Log.d("OilService", "没有需要绑定的图片");
        }
        Log.d("OilService", "%%% END 绑定图片数据 END \n");
    }

    /**
     * 上传Hse数据
     */
    private void uploadAllNewRecordHse() {

        List<UploadHseSupervision> result = sqliteHelper.getAllUnUploadHse();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未上传的上报异常数据：：" + result.size() + "条");
            for (int i = 0; i < result.size(); i++) {
                new MyUploadHse(result.get(i)).startUpload();
            }
        }
    }

    /**
     * @Flag 上报异常模块
     * @Description 后台上传所有未上传的异常记录
     * @author Administrator
     * @date 2014年11月27日
     */
    private void uploadAllNewRecordException() {
        Log.d("OilService", "||| START 上传上报异常数据 START");
        List<UploadException> result = sqliteHelper.getAllUnUploadException();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未上传的上报异常数据：：" + result.size() + "条");
            for (int i = 0; i < result.size(); i++) {
                new MyUploadException(result.get(i)).startUpload();
            }
        } else {
            Log.d("OilService", "没有需要上传的上报异常数据");
        }
        Log.d("OilService", "||| END 上传上报异常数据 END \n");
    }

    /**
     * @Flag 上报异常模块
     * @Description 后台处理所有未处理的异常记录
     * @author Administrator
     * @date 2014年11月27日
     */
    private void handleWithException() {
        Log.d("OilService", "+++ START 处理上报异常数据 START");
        List<UploadException> result = sqliteHelper.getAllUnHandleException();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未处理的上报异常数据：" + result.size() + "条");
            for (int i = 0; i < result.size(); i++) {
                new MyHandleException(result.get(i)).startHandle();
            }
        } else {
            Log.d("OilService", "没有需要处理的上报异常数据");
        }
        Log.d("OilService", "+++ END 上传上报异常数据 END \n");
    }

    /**
     * @param isUpdate 标志数据是上传操作还是更新操作，true表示更新
     * @Flag GIS数据处理模块
     * @description 后台处理GIS数据
     * @author Administrator
     * @date 2014年12月1日
     */
    private void gisSaveMutiHandle(boolean isUpdate) {
        if (user == null || user.getLoginName().equals("admin")) {
            return;
        }
        String TAG = "";
        if (isUpdate) {
            TAG = "更新";
        } else {
            TAG = "上传";
        }
        Log.d("OilService", "??? START GIS数据" + TAG + " START");
        List<Gis> data = null;
        if (isUpdate) {
            // data = sqliteHelper.getGisPicIdNotSubmit(user.getUserId());
            data = sqliteHelper.getGisPicIdNotSubmit();
        } else {
            // data = sqliteHelper.getGisNotSubmit(user.getUserId());
            data = sqliteHelper.getGisNotSubmit();
        }
        if (data != null && data.size() > 0) {
            Log.d("OilService", "未" + TAG + "GIS数据：" + data.size() + "条");
            // 分40条数据为一组进行上传
            int size = data.size();
            final int upload_size = 40;
            int requestTimes = size / upload_size;
            for (int i = 0; i < requestTimes + 1; i++) {
                int bottom = i * upload_size;
                int top = (i + 1) * upload_size;
                List<Gis> gises = null;
                if (top > size) {
                    gises = data.subList(bottom, size);
                } else {
                    gises = data.subList(bottom, top);
                }
                new MyGisSaveMutiHandle(gises, isUpdate).startHandle();
            }
        } else {
            Log.d("OilService", "没有需要" + TAG + "的GIS数据");
        }
        Log.d("OilService", "??? END GIS数据" + TAG + " END \n");

    }

    private HttpRequest http = HttpRequest.getInstance(OilService.this);
    private final Timer timer = new Timer(true);
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    Log.d("zjp", "=======================failer=================================");
                    break;
                case HttpRequest.REQUEST_SUCCESS:
                    Log.d("zjp", "========================success================================");
                    break;
            }
        }
    };
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            // 得到上传数据
            try {
                //设备唯一编号
                String deviceId = mTm.getDeviceId();
                Log.d("zjp", "========================" + deviceId + "===================================");
                String lat = application.getLat();
                String lng = application.getLng();
                // 当前登录用户ID
                if (application.getCeshhi() == 3) {
                    String userId = application.getUser().getUserId() + "";
                    if (lat != null && lng != null && userId != null && deviceId != null) {
                        Log.d("zjp", "===经度===" + lat + "========纬度====" + lng + "==========================");
                        http.requestUploadGis(mHandler, lat, lng, userId, deviceId);
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            super.handleMessage(msg);
        }
    };

    public void startGis() {
        TimerTask task = new TimerTask() {
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task, 6000, 60000);
    }

    /**
     * @Flag GIS数据处理模块
     * @description 后台处理GIS结束管线数据上传
     * @author Administrator
     * @date 2014年12月2日
     */
    private void gisSaveNameUpload() {
        if (user == null || user.getLoginName().equals("admin")) {
            return;
        }
        Log.d("OilService", "### START GIS数据结束管线信息 START");
        List<GisFinish> data = sqliteHelper.getGisFinishNotSubmit(user.getUserId());
        if (data != null && data.size() > 0) {
            Log.d("OilService", "未上传GIS结束管线数据：" + data.size() + "条");
            for (int i = 0; i < data.size(); i++) {
                new MyGisSaveNameUpload(data.get(i)).startUpload();
            }
        } else {
            Log.d("OilService", "没有需要上传的GIS结束管线数据");
        }
        Log.d("OilService", "### END GIS数据结束管线信息 END \n");
    }

    /**
     * @param isUpdate 标志数据是上传操作还是更新操作，true表示更新
     * @Flag 计划任务处理模块
     * @description 后台处理计划任务信息
     * @author Administrator
     * @date 2014年11月29日
     */
    private void patrolPlanHandle(boolean isUpdate) {
        if (user == null || user.getLoginName().equals("admin")) {
            return;
        }
        String TAG = "";
        if (isUpdate) {
            TAG = "更新";
        } else {
            TAG = "上传";
        }
        Log.d("OilService", "*** START 计划任务" + TAG + " START");
        List<PlanDetail> data_normal = null;
        if (isUpdate) {
            data_normal = sqliteHelper.getNotUpdataPicPlanDetail();
        } else {
            data_normal = sqliteHelper.getNotUploadPlanDetails();
        }
        if (data_normal != null && data_normal.size() > 0) {
            Log.d("OilService", "未" + TAG + "计划任务：" + data_normal.size() + "条");
            // 分30条数据为一组进行上传
            int size = data_normal.size();
            final int upload_size = 30;
            int requestTimes = size / upload_size;
            for (int i = 0; i < requestTimes + 1; i++) {
                int bottom = i * upload_size;
                int top = (i + 1) * upload_size;
                List<PlanDetail> planDetails = null;
                if (top > size) {
                    planDetails = data_normal.subList(bottom, size);
                } else {
                    planDetails = data_normal.subList(bottom, top);
                }
                new MyPatrolPlanHandle(planDetails, "normal", user, isUpdate).startHandle();
            }
        } else {
            Log.d("OilService", "没有需要" + TAG + "的计划任务数据");
        }
        Log.d("OilService", "*** END 计划任务" + TAG + " END \n");
    }

    /**
     * @Flag 计划任务处理模块
     * @description 后台上传当前巡检时间段之前完成巡检但未读第二次卡的PlanDetails (处理过期未上传的计划任务)
     * @author Administrator
     * @date 2014年11月29日
     */
    private void patrolPlanHandle() {
        if (user == null || user.getLoginName().equals("admin")) {
            return;
        }
        Log.d("OilService", "*** START 过期未上传的计划任务上传 START");
        List<PlanDetail> data_normal = null;
        data_normal = sqliteHelperForItem.getPlanDetailsOutOfTimeUNUpload();
        if (data_normal != null && data_normal.size() > 0) {
            Log.d("OilService", "过期未上传的未上传计划任务：" + data_normal.size() + "条");
            new MyPatrolPlanHandle(data_normal, "normal", user, false).startHandle();
        } else {
            Log.d("OilService", "没有需要上传的过期未上传的计划任务数据");
        }
        Log.d("OilService", "*** END 过期未上传的计划任务上传 END \n");
    }

    /**
     * @author Administrator
     * @Flag 图片上传
     * @description 上传图片类
     * @date 2014年11月27日
     */
    private class MyUploadPicture {
        private Picture unUploadPicture;

        public MyUploadPicture(Picture picture) {
            this.unUploadPicture = picture;
        }

        public void startUpload() {
            Log.d("OilService", "图片上传操作！");
            Picture data = sqliteHelper.getPicByCreateTime(unUploadPicture.getCreateTime());
            if (data == null || data.getIsUploadSuccess() != 0) { // 无效上传任务
                Log.d("OilService", "图片上传  无效上传任务");
                return;
            }
            try {
                String url = Constants.BASE_URL + "/commons/attachment/upload";
                Log.d("tag", url);
                AjaxParams params = new AjaxParams();
                String[] paths = data.getUrl().split(";");
                List<File> files = new ArrayList<File>();
                for (int i = 0; i < paths.length; i++) {
                    File file = new File(paths[i]);
                    if (file.exists()) {
                        files.add(file);
                    }
                }

                for (int i = 0; i < files.size(); i++) {
                    Log.d("tag", "图片上传操作 " + i);
                    params.put("file" + i, files.get(i));
                }
                params.put("uid", unUploadPicture.getChargerId());
                params.put("mime", "image/png");
                Log.d("tag", params.getParamString());
                FinalHttp finalHttp = new FinalHttp();
                finalHttp.post(url, params, new AjaxCallBack<Object>() {

                    @Override
                    public void onFailure(Throwable t, int errorNo, String strMsg) {
                        super.onFailure(t, errorNo, strMsg);
                        String tags = "uploadTaskPicFailer:  ";
                        Log.d("tag", tags + errorNo + " " + strMsg);
                    }

                    @Override
                    public void onSuccess(Object t) {
                        super.onSuccess(t);
                        String tags = "uploadTaskPicSuccess:  ";
                        Log.d("tag", tags + t.toString());
                        String picId = t.toString();
                        Picture picture = sqliteHelper.getPicByCreateTime(unUploadPicture.getCreateTime());
                        if (picture == null) {
                            return;
                        }
                        // 由图片上传返回的picId更新本地数据库记录，包括：对图片信息记录；图片id与数据绑定（后者包括更新本地和服务器数据）
                        // 1
                        picture.setIsUploadSuccess(2);
                        picture.setPicId(picId);
                        sqliteHelper.updatePic(picture);
                        // 2 记录和绑定
                        // 更具图片信息的生成时间查找该图片的最新的数据库记录
                        int picType = picture.getType();
                        switch (picType) {
                            case 0:
                            case 1:
                                String itemId = "";
                                try {
                                    String typeOfId = picture.getTypeOfId(); // typeOfId的组成形式为xj_31231
                                    itemId = typeOfId.substring(3, typeOfId.length());
                                } catch (Exception e) {
                                    itemId = "";
                                    e.printStackTrace();
                                }
                                Log.d("tag", "itemId " + itemId + "");
                                // 0、1更新两处的picId
                                // 第一处 更新异常数据表
                                UploadException exception_01 = sqliteHelper.geteExceptionByHistoryId(picture.getTypeOfId());
                                Task task_o1 = sqliteHelper.geteExceptionByHistoryId1(picture.getTypeOfId());
                                if (exception_01 == null) {
                                    exception_01 = sqliteHelper.geteExceptionByItemId(itemId);
                                }
                                if (task_o1 == null) {
                                    task_o1 = sqliteHelper.geteExceptionByItemId1(itemId);
                                }
                                if (task_o1 != null) {
                                    task_o1.setPics(picId);
                                    sqliteHelper.updetaException1(task_o1);
                                    String historyId = task_o1.getHistoryId();
                                    if (historyId != null && !historyId.equals("")) {
                                        if (picture.getTypeOfId() != historyId) {
                                            picture.setTypeOfId(historyId);
                                            sqliteHelper.updatePic(picture);
                                        }
                                        // （绑定 2）判断更新服务器上数据记录的picId
                                        List<Picture> data = new ArrayList<Picture>();
                                        data.add(picture);
                                        new MyUpdateHistoryMuti(data).startBind();
                                    }
                                }
                                if (exception_01 != null) {
                                    exception_01.setPicId(picId);
                                    sqliteHelper.updetaException(exception_01);
                                    String historyId = exception_01.getHistoryId();
                                    if (historyId != null && !historyId.equals("")) {
                                        if (picture.getTypeOfId() != historyId) {
                                            picture.setTypeOfId(historyId);
                                            sqliteHelper.updatePic(picture);
                                        }
                                        // （绑定 2）判断更新服务器上数据记录的picId
                                        List<Picture> data = new ArrayList<Picture>();
                                        data.add(picture);
                                        new MyUpdateHistoryMuti(data).startBind();
                                    }
                                }
                                // 第二处 更新plan_details表
                                PlanDetail planDetail = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(itemId, picture.getPatrolTime());
                                if (planDetail == null && task_o1 != null) {
                                    planDetail = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(task_o1.getTaskId(), task_o1.getEndDate());
                                } else {
                                    picture.setIsWrokUpdate(1);
                                    sqliteHelper.updatePic(picture);
                                }
                                if (planDetail == null && exception_01 != null) {
                                    planDetail = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(exception_01.getItemId(), exception_01.getPatrolTime());
                                } else {
                                    picture.setIsWrokUpdate(1);
                                    sqliteHelper.updatePic(picture);
                                }
                                if (planDetail == null) {
                                    return;
                                }
                                Log.d("tag", tags + "plan_details updateDetailPlanofPhotoId");
                                planDetail.setPicId(picture.getPicId());
                                sqliteHelper.updateDetailPlanByItemId(planDetail);
                                // （绑定 0、1）判断更新服务器上数据
                                if (user == null || user.getLoginName().equals("admin")) {
                                    return;
                                }
                                List<PlanDetail> data_01 = new ArrayList<PlanDetail>();
                                data_01.add(planDetail);
                                new MyPatrolPlanHandle(data_01, "normal", user, true).startHandle();
                                break;

                            case 2:
                                UploadException exception_2 = sqliteHelper.geteExceptionByTime(picture.getTypeOfId());
                                FeedBack feedBack_o2 = sqliteHelper.geteExceptionByTime2(picture.getTypeOfId());
                                if (feedBack_o2 == null) {
                                    feedBack_o2 = sqliteHelper.geteExceptionByHistoryId2(picture.getTypeOfId());
                                }
                                if (feedBack_o2 == null) {
                                    return;
                                }
                                feedBack_o2.setPicId(picId);
                                sqliteHelper.updetaExceptionFeedBack(feedBack_o2);
                                String historyId2 = feedBack_o2.getTime();
                                if (historyId2 != null && !historyId2.equals("")) {
                                    if (picture.getTypeOfId() != historyId2) {
                                        picture.setTypeOfId(historyId2);
                                        sqliteHelper.updatePic(picture);
                                    }
                                    // （绑定 2）判断更新服务器上数据记录的picId
                                    List<Picture> data = new ArrayList<Picture>();
                                    data.add(picture);
                                    new MyUpdateHistoryMuti(data).startBind();
                                    uploadAllNewRecordExceptionFeedBack();
                                }
                                if (exception_2 == null) {
                                    exception_2 = sqliteHelper.geteExceptionByHistoryId(picture.getTypeOfId());
                                }
                                if (exception_2 == null) {
                                    return;
                                }
                                Log.d("tag", tags + "exception_2 updetaException");
                                exception_2.setPicId(picId);
                                sqliteHelper.updetaException(exception_2);
                                String historyId = exception_2.getHistoryId();
                                if (historyId != null && !historyId.equals("")) {
                                    if (picture.getTypeOfId() != historyId) {
                                        picture.setTypeOfId(historyId);
                                        sqliteHelper.updatePic(picture);
                                    }
                                    // （绑定 2）判断更新服务器上数据记录的picId
                                    List<Picture> data = new ArrayList<Picture>();
                                    data.add(picture);
                                    new MyUpdateHistoryMuti(data).startBind();
                                }
                                break;

                            case 3:
                                // （绑定 3）判断更新服务器上数据记录的picId
                                List<Picture> data_3 = new ArrayList<Picture>();
                                data_3.add(picture);
                                new MyUpdateHistoryMuti(data_3).startBind();
                                break;

                            case 4:
                                picture.setIsWrokUpdate(1);
                                sqliteHelper.updatePic(picture);
                                Gis gis = sqliteHelper.getGisByTime(picture.getTypeOfId());
                                if (gis == null) {
                                    return;
                                }
                                // 用条件来控制数据的更新
                                if (gis.getExceptionStatus().equals("2")) {
                                    gis.setPics(picture.getPicId());
                                    sqliteHelper.updateGisPicIds(gis);
                                    // （绑定 4）判断更新服务器上数据记录的picId
                                    List<Gis> data_gis = new ArrayList<Gis>();
                                    data_gis.add(gis);
                                    new MyGisSaveMutiHandle(data_gis, true).startHandle();
                                }
                                break;

                            default:
                                break;
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @author Administrator
     * @Flag 图片绑定
     * @description 图片数据绑定类
     * @date 2014年11月27日
     */
    private class MyUpdateHistoryMuti {
        private List<Picture> pictures;

        public MyUpdateHistoryMuti(List<Picture> pictures) {
            this.pictures = pictures;
        }

        public void startBind() {
            Log.d("OilService", "图片数据绑定操作！");
            String url = Constants.BASE_URL + "/api/work/update/history/muti";
            Log.d("tag", url);
            FinalHttp finalHttp = new FinalHttp();
            AjaxParams params = new AjaxParams();
            for (int i = 0; i < pictures.size(); i++) {
                params.put("histories[" + i + "].id", pictures.get(i).getTypeOfId());
                params.put("histories[" + i + "].pics", pictures.get(i).getPicId() + ",");
            }
            Log.d("tag", params.getParamString());
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    String tags = "BindTaskPicFailer:  ";
                    Log.d("tag", tags + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "BindTaskPicSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    for (int i = 0; i < pictures.size(); i++) {
                        pictures.get(i).setIsWrokUpdate(1);
                        sqliteHelper.updatePic(pictures.get(i));
                    }
                }
            });
        }
    }

    /**
     * @author Administrator
     * @Flag GIS数据操作
     * @description GIS数据操作类
     * @date 2014年11月27日
     */
    private class MyGisSaveMutiHandle {
        private List<Gis> gises;
        private boolean isUpdatePicId;

        public MyGisSaveMutiHandle(List<Gis> gises, boolean isUpdatePicId) {
            this.gises = gises;
            this.isUpdatePicId = isUpdatePicId;
        }

        public void startHandle() {
            Log.d("OilService", "GIS数据操作！");
            String url = Constants.BASE_URL + "/api/gis/save/muti";
            Log.d("tag", url);
            FinalHttp finalHttp = new FinalHttp();
            AjaxParams params = new AjaxParams();
            for (int i = 0; i < gises.size(); i++) {
                params.put("taskDetails[" + i + "].task.taskNo", gises.get(i).getNum());
                params.put("taskDetails[" + i + "].lat", gises.get(i).getLatitude());
                params.put("taskDetails[" + i + "].lng", gises.get(i).getLongitude());
                params.put("taskDetails[" + i + "].createTime", gises.get(i).getTime());
                params.put("taskDetails[" + i + "].creatBy.id", gises.get(i).getUserId());
                params.put("taskDetails[" + i + "].imei", gises.get(i).getDeviceId());
                params.put("taskDetails[" + i + "].status", gises.get(i).getExceptionStatus());
                params.put("taskDetails[" + i + "].abnormalContent", gises.get(i).getMemo());
                if (gises.get(i).getPics().equals("") || gises.get(i).getPics() == null) {
                    params.put("taskDetails[" + i + "].picIds", gises.get(i).getPics());
                } else {
                    if (gises.get(i).getPics().equals("null")) {
                        params.put("taskDetails[" + i + "].picIds", "");
                    } else {
                        params.put("taskDetails[" + i + "].picIds", gises.get(i).getPics() + ",");
                    }
                }
                params.put("taskDetails[" + i + "].id", gises.get(i).getGisId());
                params.put("taskDetails[" + i + "].charger.id", gises.get(i).getUserId());
                params.put("taskDetails[" + i + "].creatBy.id", gises.get(i).getUserId());
                params.put("taskDetails[" + i + "].task.line.id", gises.get(i).getLindId());
                params.put("taskDetails[" + i + "].tag", gises.get(i).getTaskType());
            }
            Log.d("tag", "gises size: " + gises.size() + " " + params.getParamString());
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    String tags = "GisSaveMutiFailer:  ";
                    Log.d("tag", tags + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "GisSaveMutiSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (status.equals("success")) {
                            if (isUpdatePicId) {
                                for (int i = 0; i < gises.size(); i++) {
                                    Gis gis = gises.get(i);
                                    // 同步图片Id信息成功后，此条数据直接删除
                                    // sqliteHelper.deleteGisByTime(gis);
                                    if (!gis.getPics().equals("")) {
                                        gis.setIsPicIdUpload("1");
                                    }
                                    gis.setStatus("2");
                                    sqliteHelper.updateGis(gis);
                                }
                            } else {
                                for (int i = 0; i < gises.size(); i++) {
                                    Gis gis = gises.get(i);
                                    // if (gis.getExceptionStatus().equals("1")
                                    // || !gis.getPics().equals("")) {
                                    if (gis.getExceptionStatus().equals("1")) {
                                        // 不包含图片信息的数据在这里就直接删除
                                        sqliteHelper.deleteGisByTime(gis);
                                        continue;
                                    }
                                    if (!gis.getPics().equals("")) {
                                        gis.setIsPicIdUpload("1");
                                    }
                                    gis.setStatus("2");
                                    sqliteHelper.updateGis(gis);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @author Administrator
     * @Flag Gis模块
     * @description Gis数据巡检结束路线上传操作
     * @date 2014年11月27日
     */
    private class MyGisSaveNameUpload {
        private GisFinish gisFinish;

        public MyGisSaveNameUpload(GisFinish gisFinish) {
            this.gisFinish = gisFinish;
        }

        public void startUpload() {
            Log.d("OilService", "Gis结束路线提交操作！");
            GisFinish data = sqliteHelper.getGisFinishNotSubmitByCreateTime(gisFinish.getCreatTime());
            if (data.getStatus().equals("2")) {
                Log.d("OilService", "Gis结束路线提交  无效上传任务");
                return;
            } else if (data.getGisNum() == 0) {
                if (!data.getStatus().equals("4")) {
                    data.setStatus("4");
                    sqliteHelper.updateGisFinish(data);
                }
                Log.d("OilService", "Gis结束路线提交  无效上传任务,无GIS数据");
                return;
            }
            String url = Constants.BASE_URL + "/api/gis/save/name";
            AjaxParams params = new AjaxParams();
            params.put("category", gisFinish.getCategory() + "");
            params.put("taskNo", gisFinish.getTaskNo());
            params.put("name", gisFinish.getLineName());
            params.put("startTime", gisFinish.getCreatTime());
            params.put("endTime", gisFinish.getEndTime());
            params.put("createBy.id", gisFinish.getUserId());
            Log.d("tag", params.getParamString());
            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    String tags = "GisSaveNameUploadFailer:  ";
                    Log.d("tag", tags + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "GisSaveNameUploadSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    gisFinish.setStatus("2");
                    sqliteHelper.updateGisFinish(gisFinish);
                }
            });
        }
    }

    /**
     * @author Administrator
     * @Flag 计划任务处理
     * @description 计划任务信息上传、更新类
     * @date 2014年11月28日
     */
    private class MyPatrolPlanHandle {
        private String flag;
        private List<PlanDetail> planDetails;
        private User user;
        private boolean isUpdataPlan; // 标志上传计划任务还是更新计划任务

        public MyPatrolPlanHandle(List<PlanDetail> planDetails, String flag, User user, boolean isUpdataPlan) {
            this.planDetails = planDetails;
            this.flag = flag;
            this.user = user;
            this.isUpdataPlan = isUpdataPlan;
        }

        public void startHandle() {
            if (isUpdataPlan) {
                Log.d("OilService", "计划任务更新操作！");
            } else {
                Log.d("OilService", "计划任务上传操作！");
            }
            String url = Constants.BASE_URL + "/api/patrol/plan/update";
            Log.d("tag", url);

            /***************************************************************************/
            RequestParams params = new RequestParams();
            params.addBodyParameter("flag", flag);
            for (int i = 0; i < planDetails.size(); i++) {
                params.addBodyParameter("children[" + i + "].item.id", planDetails.get(i).getItemId());
                params.addBodyParameter("children[" + i + "].patrolTime", planDetails.get(i).getPatrolTime());
                params.addBodyParameter("children[" + i + "].handleAdvice", planDetails.get(i).getHandleAdvice());
                params.addBodyParameter("children[" + i + "].memo", planDetails.get(i).getMemo());
                if (planDetails.get(i).getPicId().equals("") || planDetails.get(i).getPicId() == null) {
                    params.addBodyParameter("children[" + i + "].picIds", planDetails.get(i).getPicId());
                } else {
                    params.addBodyParameter("children[" + i + "].picIds", planDetails.get(i).getPicId() + ",");
                }
                params.addBodyParameter("children[" + i + "].result", planDetails.get(i).getResult());
                params.addBodyParameter("children[" + i + "].status", planDetails.get(i).getExceptionStatus());
                params.addBodyParameter("children[" + i + "].videoId", planDetails.get(i).getVideoId());
                params.addBodyParameter("children[" + i + "].updateTime", planDetails.get(i).getUpdateTime());
                params.addBodyParameter("children[" + i + "].handleMemo", planDetails.get(i).getHandleMemo());
                params.addBodyParameter("children[" + i + "].charger.id", user.getUserId());
                // Log.d("tag", "itemId: " + planDetails.get(i).getItemId() +
                // "  PatrolTime: " + planDetails.get(i).getPatrolTime() +
                // "   updateTime:  " + planDetails.get(i).getUpdateTime());
            }
            Log.d("tag", params.toString());
            xUtilsHttp.getHttpUtil().send(com.lidroid.xutils.http.client.HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    String tags = "MyPatrolPlanHandleFailer:  ";
                    if (isUpdataPlan) {
                        Log.d("tag", tags + " 更新 " + arg1);
                    } else {
                        Log.d("tag", tags + " 上传 " + arg1);
                    }
                }

                @Override
                public void onSuccess(ResponseInfo<String> arg0) {
                    String tags = "MyPatrolPlanHandleSuccess:  ";
                    Log.d("tag", tags + arg0.result);
                    try {
                        JSONObject object = new JSONObject(arg0.result);
                        String status = object.getString("status");
                        if (status.equals("success")) {
                            if (isUpdataPlan) {
                                Log.d("OilService", "计划任务更新操作success！");
                                for (int i = 0; i < planDetails.size(); i++) {
                                    PlanDetail planDetail = planDetails.get(i);
                                    planDetails.get(i).setIsPicIdUpdate("1");
                                    sqliteHelper.updatePlanDetailIsPicIdUpdate(planDetail);
                                }
                            } else {
                                Log.d("OilService", "计划任务上传操作success！");
                                for (int i = 0; i < planDetails.size(); i++) {
                                    planDetails.get(i).setStatus("4");
                                }
                                sqliteHelper.updateUploadPlanStatus(planDetails);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            /**************************************************************************************/

            // FinalHttp finalHttp = new FinalHttp();
            // AjaxParams params = new AjaxParams();
            // params.put("flag", flag);
            // for (int i = 0; i < planDetails.size(); i++) {
            // params.put("children[" + i + "].item.id",
            // planDetails.get(i).getItemId());
            // params.put("children[" + i + "].patrolTime",
            // planDetails.get(i).getPatrolTime());
            // params.put("children[" + i + "].handleAdvice",
            // planDetails.get(i).getHandleAdvice());
            // params.put("children[" + i + "].memo",
            // planDetails.get(i).getMemo());
            // if (planDetails.get(i).getPicId().equals("") ||
            // planDetails.get(i).getPicId() == null) {
            // params.put("children[" + i + "].picIds",
            // planDetails.get(i).getPicId());
            // } else {
            // params.put("children[" + i + "].picIds",
            // planDetails.get(i).getPicId() + ",");
            // }
            // params.put("children[" + i + "].result",
            // planDetails.get(i).getResult());
            // params.put("children[" + i + "].status",
            // planDetails.get(i).getExceptionStatus());
            // params.put("children[" + i + "].videoId",
            // planDetails.get(i).getVideoId());
            // params.put("children[" + i + "].updateTime",
            // planDetails.get(i).getUpdateTime());
            // params.put("children[" + i + "].handleMemo",
            // planDetails.get(i).getHandleMemo());
            // params.put("children[" + i + "].charger.id", user.getUserId());
            // Log.d("tag", "itemId: " + planDetails.get(i).getItemId() +
            // "  PatrolTime: " + planDetails.get(i).getPatrolTime() +
            // "   updateTime:  " + planDetails.get(i).getUpdateTime());
            // }
            // Log.d("tag", params.getParamString());
            // finalHttp.post(url, params, new AjaxCallBack<Object>() {
            //
            // @Override
            // public void onFailure(Throwable t, int errorNo, String strMsg) {
            // super.onFailure(t, errorNo, strMsg);
            // String tags = "MyPatrolPlanHandleFailer:  ";
            // if (isUpdataPlan) {
            // Log.d("tag", tags + errorNo + " 更新 " + strMsg);
            // } else {
            // Log.d("tag", tags + errorNo + " 上传 " + strMsg);
            // }
            // }
            //
            // @Override
            // public void onSuccess(Object t) {
            // super.onSuccess(t);
            // String tags = "MyPatrolPlanHandleSuccess:  ";
            // Log.d("tag", tags + t.toString());
            // try {
            // JSONObject object = new JSONObject(t.toString());
            // String status = object.getString("status");
            // if (status.equals("success")) {
            // if (isUpdataPlan) {
            // Log.d("OilService", "计划任务更新操作success！");
            // for (int i = 0; i < planDetails.size(); i++) {
            // PlanDetail planDetail = planDetails.get(i);
            // planDetails.get(i).setIsPicIdUpdate("1");
            // sqliteHelper.updatePlanDetailIsPicIdUpdate(planDetail);
            // }
            // } else {
            // Log.d("OilService", "计划任务上传操作success！");
            // for (int i = 0; i < planDetails.size(); i++) {
            // planDetails.get(i).setStatus("4");
            // }
            // sqliteHelper.updateUploadPlanStatus(planDetails);
            // }
            // }
            // } catch (JSONException e) {
            // e.printStackTrace();
            // }
            // }
            // });
        }
    }

    /**
     * 上传Hse数据
     */
    private class MyUploadHse {
        private UploadHseSupervision uploadHseSupervision;

        public MyUploadHse(UploadHseSupervision uploadHseSupervision) {
            this.uploadHseSupervision = uploadHseSupervision;
        }

        public void startUpload() {
            UploadHseSupervision data = sqliteHelper.getHseByTime(uploadHseSupervision.getCreatedDate());
            if (data.getIsSuccess() == 1) { // 无效上传任务
                return;
            }
            String url = Constants.BASE_URL + "/api/hse/save";
            AjaxParams params = new AjaxParams();
            params.put("beCheckedOffice.id", uploadHseSupervision.getBeCheckedOffice());
            params.put("beCheckedOrgan", uploadHseSupervision.getBeCheckedOrgan());
            params.put("createdDate", uploadHseSupervision.getCreatedDate());
            params.put("checkerIds", uploadHseSupervision.getCheckerIds());
            params.put("checkerNames", uploadHseSupervision.getCheckerNames());
            params.put("issue", uploadHseSupervision.getIssue());
            params.put("checkOffice.id", uploadHseSupervision.getCheckOffice());
            params.put("suggestion", uploadHseSupervision.getSuggestion());

            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Log.d("tag", "UploadExceptionFaliure" + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "UploadExceptionSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (!status.endsWith("success")) {
                            return;
                        }
                        uploadHseSupervision.setIsSuccess(1);
                        sqliteHelper.updetaHse(uploadHseSupervision);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @author Administrator
     * @Flag 上报异常模块
     * @description 异常数据上传类
     * @date 2014年11月27日
     */
    private class MyUploadException {
        private UploadException unUploadException;

        public MyUploadException(UploadException exception) {
            this.unUploadException = exception;
        }

        public void startUpload() {
            Log.d("OilService", "上报异常上传操作！");
            UploadException data = sqliteHelper.geteExceptionByTime(unUploadException.getTime());
            if (!data.getWorkId().equals("") || !data.getHistoryId().equals("") || data.getIsUploadSuccess().equals("1")) { // 无效上传任务
                Log.d("OilService", "上报异常  无效上传任务");
                return;
            }
            String url = Constants.BASE_URL + "/api/work/save";
            Log.d("tag", url);
            AjaxParams params = new AjaxParams();
            params.put("deviceNames", unUploadException.getDeviceName());
            params.put("deviceCode", unUploadException.getDeviceCode());
            params.put("workType.id", unUploadException.getWorkTypeId());
            params.put("charger.id", unUploadException.getUserId());
            params.put("creator.id", unUploadException.getUserId());
            params.put("category", unUploadException.getCategory());
            params.put("history.pics", unUploadException.getPicId());
            params.put("riskTips", unUploadException.getDescription());
            Log.d("tag", "UploadException " + params.getParamString());
            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Log.d("tag", "UploadExceptionFaliure" + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "UploadExceptionSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (!status.endsWith("success")) {
                            return;
                        }
                        String workId = object.getString("workId");
                        String historyId = object.getString("historyId");
                        // 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
                        unUploadException.setWorkId(workId);
                        unUploadException.setHistoryId(historyId);
                        sqliteHelper.updetaException(unUploadException);
                        // 请求服务器处理此条上报异常数据
                        new MyHandleException(unUploadException).startHandle();

                        // 更新此条异常数据对应的计划信息（如果此条异常是由计划数据生成）
                        PlanDetail planDetail = sqliteHelper.getPlanDetailByItemIdAndPatrolTime(unUploadException.getItemId(), unUploadException.getPatrolTime());
                        if (planDetail != null) {
                            planDetail.setWorkId(workId);
                            sqliteHelper.updateDetailPlanByItemId(planDetail);
                        }

                        // 更新此条上报异常数据对应的图片信息
                        Picture picture = sqliteHelper.getPicByTypeOfId(unUploadException.getTime());
                        if (picture == null) {
                            picture = sqliteHelper.getPicByTypeOfId("xj_" + unUploadException.getItemId());
                        }
                        if (picture != null) {
                            Log.d("tag", tags + "更新此条上报异常数据对应的图片信息");
                            picture.setTypeOfId(historyId);
                            sqliteHelper.updatePic(picture);
                            // 判断更新服务器上上报异常数据记录的picId
                            if (picture.getIsUploadSuccess() != 0 && !picture.getPicId().equals("") && picture.getIsWrokUpdate() == 0) {
                                List<Picture> data = new ArrayList<Picture>();
                                data.add(picture);
                                new MyUpdateHistoryMuti(data).startBind();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * @author Administrator
     * @Flag 上报异常模块
     * @description 上传成功的上报异常数据的处理类
     * @date 2014年11月27日
     */
    private class MyHandleException {
        private UploadException unHandleException;

        public MyHandleException(UploadException exception) {
            this.unHandleException = exception;
        }

        public void startHandle() {
            Log.d("OilService", "上报异常异常处理操作！");
            UploadException data = sqliteHelper.geteExceptionByTime(unHandleException.getTime());
            if (data.getIsUploadSuccess().equals("1")) {
                Log.d("OilService", "上报异常异常处理  无效上传任务");
                return;
            }
            String url = Constants.BASE_URL + "/api/work/handle";
            Log.d("tag", url);
            AjaxParams params = new AjaxParams();
            params.put("workId", unHandleException.getWorkId());
            params.put("creator.id", unHandleException.getUserId());
            Log.d("tag", params.getParamString());

            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    String tags = "HandleWorkFailer:  ";
                    Log.d("tag", tags + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "HandleWorkSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (status.equals("success")) {
                            // 下面更新异常的数据库记录isUploadSuccess字段，标记此条数据已经上传并处理成功
                            unHandleException.setIsUploadSuccess("1");
                            sqliteHelper.updetaException(unHandleException);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 任务图片上传成功
     */
    private void uploadAllNewRecordExceptionTask() {
        Log.d("OilService", "||| START 上传任务完成情况 START");
        List<Task> result = sqliteHelper.getAllUnUploadExceptionTask();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未上传的上报异常数据：：" + result.size() + "条");
            for (int i = 0; i < result.size(); i++) {
                new MyUploadExceptionTask(result.get(i)).startUpload();
            }
        } else {
            Log.d("OilService", "没有需要上传的上报异常数据");
        }
        Log.d("OilService", "||| END 上传上报异常数据 END \n");
    }

    private class MyUploadExceptionTask {
        private Task tasks;

        public MyUploadExceptionTask(Task task) {
            this.tasks = task;
        }

        public void startUpload() {
            Log.d("OilService", "任务数据上传操作！");
            Task data = sqliteHelper.geteExceptionByTime1(tasks.getEndDate());
            if (!data.getHistoryId().equals("") || data.getIsFinished() != 2) { // 无效上传任务
                Log.d("OilService", "任务数据上传  无效上传任务");
                return;
            }
            String url = Constants.BASE_URL + "/api/work/handle";
            Log.d("tag", url);
            AjaxParams params = new AjaxParams();
            params.put("creator.id", user.getUserId());
            params.put("endDate", tasks.getEndDate());
            params.put("pics", tasks.getPics());
            params.put("integral", tasks.getIntegral());
            Log.d("tag", "UploadException " + params.getParamString());
            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Log.d("tag", "UploadExceptionFaliure" + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "UploadExceptionSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (!status.endsWith("success")) {
                            return;
                        }
                        String workId = object.getString("workId");
                        String historyId = object.getString("historyId");
                        // 下面更新异常的数据库记录 workId、historyId字段，以此来标识此条数据已经上传至服务器
                        tasks.setTaskId(workId);
                        tasks.setHistoryId(historyId);
                        sqliteHelper.updetaException1(tasks);
                        // 请求服务器处理此条上报异常数据
                        new MyHandleExceptionTask(tasks).startHandle();

                        // 更新此条上报异常数据对应的图片信息
                        Picture picture = sqliteHelper.getPicByTypeOfId(tasks.getEndDate());
                        if (picture == null) {
                        }
                        if (picture != null) {
                            Log.d("tag", tags + "更新此条上报异常数据对应的图片信息");
                            picture.setTypeOfId(historyId);
                            sqliteHelper.updatePic(picture);
                            // 判断更新服务器上上报异常数据记录的picId
                            if (picture.getIsUploadSuccess() != 0 && !picture.getPicId().equals("") && picture.getIsWrokUpdate() == 0) {
                                List<Picture> data = new ArrayList<Picture>();
                                data.add(picture);
                                new MyUpdateHistoryMuti(data).startBind();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private class MyHandleExceptionTask {
        private Task taskUpload;

        public MyHandleExceptionTask(Task exception) {
            this.taskUpload = exception;
        }

        public void startHandle() {
            Log.d("OilService", "上报任务填报操作！");
            Task data = sqliteHelper.geteExceptionByTime1(taskUpload.getEndDate());
            if (data.getIsFinished() != 2) {
                Log.d("OilService", "上报任务填报操作  无效上传任务");
                return;
            }
            String url = Constants.BASE_URL + "/api/work/handle";
            Log.d("tag", url);
            AjaxParams params = new AjaxParams();
            params.put("workId", taskUpload.getTaskId());
            params.put("creator.id", user.getUserId());
            Log.d("tag", params.getParamString());

            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    String tags = "HandleWorkFailer:  ";
                    Log.d("tag", tags + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "HandleWorkSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        String status = object.getString("status");
                        if (status.equals("success")) {
                            // 下面更新异常的数据库记录isUploadSuccess字段，标记此条数据已经上传并处理成功
                            taskUpload.setIsFinished(2);
                            sqliteHelper.updetaException1(taskUpload);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void uploadAllNewRecordExceptionFeedBack() {

        Log.d("OilService", "||| START 上传任务完成情况 START");
        List<FeedBack> result = sqliteHelper.getAllUnUploadExceptionFeedBack();
        if (result != null && result.size() > 0) {
            Log.d("OilService", "未上传的上报异常数据：：" + result.size() + "条");
            for (int i = 0; i < result.size(); i++) {
                new MyUploadExceptionFeedBack(result.get(i)).startUpload();
            }
        } else {
            Log.d("OilService", "没有需要上传的上报异常数据");
        }
        Log.d("OilService", "||| END 上传上报异常数据 END \n");
    }

    private class MyUploadExceptionFeedBack {
        private FeedBack feedback;

        public MyUploadExceptionFeedBack(FeedBack feed) {
            this.feedback = feed;
        }

        public void startUpload() {
            Log.d("OilService", "任务数据上传操作！");
            FeedBack data = sqliteHelper.geteExceptionByTime2(feedback.getTime());
            if (data.getIsUploadSuccess().equals("1")) { // 无效上传任务
                Log.d("OilService", "任务数据上传  无效上传任务");
                return;
            }
            String url = Constants.BASE_URL + "/api/message/saves";
            Log.d("tag", url);
            AjaxParams params = new AjaxParams();
            params.put("name", user.getName());
            params.put("title", feedback.getTitle());
            params.put("content", feedback.getDescription());
            params.put("pictures", feedback.getPicId());
            params.put("created_date", feedback.getTime());
            Log.d("tag", "UploadException " + params.getParamString());
            FinalHttp finalHttp = new FinalHttp();
            finalHttp.post(url, params, new AjaxCallBack<Object>() {

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    super.onFailure(t, errorNo, strMsg);
                    Log.d("tag", "UploadExceptionFaliure" + errorNo + " " + strMsg);
                }

                @Override
                public void onSuccess(Object t) {
                    super.onSuccess(t);
                    String tags = "UploadExceptionSuccess:  ";
                    Log.d("tag", tags + t.toString());
                    feedback.setIsUploadSuccess("1");
                    sqliteHelper.updetaExceptionFeedBack(feedback);
                    // 更新此条上报异常数据对应的图片信息
                    Picture picture = sqliteHelper.getPicByTypeOfId(feedback.getTime());
                    if (picture == null) {
                    }
                    if (picture != null) {
                        Log.d("tag", tags + "更新此条上报异常数据对应的图片信息");
                        // 判断更新服务器上上报异常数据记录的picId
                        if (picture.getIsUploadSuccess() != 0 && !picture.getPicId().equals("") && picture.getIsWrokUpdate() == 0) {
                            List<Picture> data = new ArrayList<Picture>();
                            data.add(picture);
                            new MyUpdateHistoryMuti(data).startBind();
                        }
                    }
                }
            });
        }
    }
}
