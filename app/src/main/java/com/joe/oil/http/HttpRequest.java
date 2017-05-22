package com.joe.oil.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.joe.oil.entity.DictDetail;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.UploadHseSupervision;
import com.joe.oil.entity.User;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author baiqiao
 * @Description 所有的网络请求
 * @data: 2014年6月4日 下午1:46:20
 * @email baiqiao@lanbaoo.com
 */
@SuppressLint("SimpleDateFormat")
public class HttpRequest {

    private static String BASE_URL = Constants.BASE_URL;

    private static final String TAG = "RequestHttp";
    /**
     * 网络请求失败
     */
    public static final int REQUEST_FAILER = 0;

    /**
     * 网络请求成功
     */
    public static final int REQUEST_SUCCESS = 1;

    /**
     * 网络连接异常
     */
    public static final int NO_NETWORK_CONNECTION = 0;

    /**
     * 请求参数不正确
     */
    public static final int ERROR_PARAM = 400;

    /**
     * 访问资源不存在
     */
    public static final int UNKNOW_RESOURCES = 404;

    /**
     * 错误请求方式
     */
    public static final int ERROR_REQUEST_METHOD = 405;

    /**
     * 未知错误
     */
    public static final int UNKNOW_ERROR = 500;
    /**
     * 服务器异常
     */
    public static final int SERVICE_ERROR = 502;
    public static final int DOWNLOAD_USER = 1; // 下载用户数据成功
    public static final int DOWNLOAD_XJ_ITEM = 2;
    public static final int DOWNLOAD_DEVICE = 3;
    public static final int DOWNLOAD_WELL = 4;
    public static final int DOWNLOAD_LINE = 5;
    public static final int DOWNLOAD_PLAN = 6;
    public static final int DOWNLOAD_DICT = 7;
    public static final int DOWNLOAD_DEVICE_TREE = 8;
    public static final int DOWNLOAD_DEVICE_TREE_2 = 10;
    public static final int DOWNLOAD_DEVICE_TREE_3 = 11;
    public static final int DOWNLOAD_DEVICE_TREE_4 = 12;
    public static final int DOWNLOAD_PLAN_TEMPLATE = 9;

    public static final int DOWNLOAD_CL_TANK=13;

    public static final int DOWNLOAD_CL_TASK_TEMPLATE=14;

    private static HttpRequest Instance;
    private FinalHttp finalHttp;
    private AjaxParams params;
    private JsonHelper jsonHelper;
    private static Context context;

    public static Handler urlHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BASE_URL = Constants.BASE_URL;
            Log.d(TAG, "baseUrl:  " + BASE_URL);
        }
    };

    public static HttpRequest getInstance(Context context) {
        if (Instance == null) {
            Instance = new HttpRequest(context);
        }
        return Instance;
    }

    private HttpRequest(Context mContext) {
        if (finalHttp == null) {

            finalHttp = new FinalHttp();
        }
        if (jsonHelper == null) {

            jsonHelper = new JsonHelper();
        }
        context = mContext;
    }

    public FinalHttp getFinalHttp() {
        return finalHttp;
    }

    /**
     * 网络请求失败，Toast提示
     *
     * @param errNo  服务器返回的错误码
     * @param events 功能
     */
    public static void badRequest(int errNo, String events) {
        switch (errNo) {
            case NO_NETWORK_CONNECTION:
                // Constants.showToast(context, "网络异常，请检查");
                break;
            case ERROR_REQUEST_METHOD:
                Constants.showToast(context, "请求方式错误，请更换请求方式");
                break;

            case ERROR_PARAM:
                // if (events.equals("login")) {
                // Constants.showToast(context, "登录名或密码错误，请重试");
                // } else {
                Constants.showToast(context, "提交参数不正确");
                // }
                break;

            case UNKNOW_ERROR:
                Constants.showToast(context, "未知错误,请稍后重试");
                break;

            case SERVICE_ERROR:
                Constants.showToast(context, "服务器异常,请稍后重试");
                break;

            case UNKNOW_RESOURCES:
                Constants.showToast(context, "访问资源不存在，请检查IP设置");
                break;

            default:
                Constants.showToast(context, "未知错误,请稍后重试");
                break;
        }
    }
    public void requestGetSingle(final Handler mHandler, String uid) {
        String url = BASE_URL + "/api/vehicle/way/bills/search";
        params = new AjaxParams();
        params.put("charger.id",uid);
        // params.put("flag", "todo");
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                Log.d("zjpzjpzjp", "路单=="+t.toString());
                msg.obj = jsonHelper.getSingle(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @param uid
     * @Description 派工任务列表请求
     * @date 2014年7月7日 下午2:46:42
     */
    public void requestGetTask(final Handler mHandler, String uid) {
        String url = BASE_URL + "/api/work/list/mobel";
        params = new AjaxParams();
        params.put("uid", uid);
        // params.put("flag", "todo");
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getTaskList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public void requestGetTask1(final Handler mHandler, String userId) {
        String url = BASE_URL + "/api/work/list/task/end";
        params = new AjaxParams();
        params.put("uid", userId);
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getTaskSuccess:  ";
                Log.d("zjp", tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getTaskList1(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * 根据作业卡编号返回相关信息填充作业点
     * @param mHandler
     * @param code
     */
    public void requestGetNameByCode(final Handler mHandler, String code) {
        String url = BASE_URL + "/api/hse/readRf";
        params = new AjaxParams();
        params.put("code", code);
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getNameList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public void requestGetTaskInfo(final Handler mHandler, String userId) {
        String url = BASE_URL + "/api/work/user/task/history";
        params = new AjaxParams();
        params.put("userId", userId);
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getTaskSuccess:  ";
                Log.d("zjp", tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getTaskInfoList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param url   赵俊鹏
     * @param param
     * @return
     */

    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
                Log.d("zjp", "============================" + result + "============");
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @param mHandler
     * @param taskId
     * @param startDate
     * @param endTime
     * @param uid
     * @param isAgree
     * @param memo
     * @Description 派工任务完成数据提交请求
     * @date 2014年7月7日 下午2:46:11
     */
//    public void requestFinishTask(final Handler mHandler, String actId, String taskId, String startDate, String endTime, String uid, String isAgree, String memo, String pics, String value1, String value2, String jilu,String tankcar,String tank) {
    public void requestFinishTask(final Handler mHandler, String actId, String taskId, String startDate, String endTime, String uid, String isAgree, String memo, String pics, String beforeAdd, String addValue, String jilu,
                                  String vehicleid,String afterAdd,String vehicleNumber,String custom,String qualified,String concentration,String driver,String escort,String poundweight,String density,
                                  String beforereadflow,String afterreadflow,String outgoingquantity,String riskidentification,String preventivemeasures,String startWorkTime,String endWorkTime,String integral) {
        Log.d("lxj-85",beforeAdd+addValue+afterAdd+vehicleid);
        String url = BASE_URL + "/api/work/handle";
        params = new AjaxParams();
        params.put("actId", actId);
        params.put("isAgree", isAgree);
        params.put("workId", taskId);
        params.put("creator.id", uid);
        params.put("memo", memo);
        params.put("endDate", endTime);
        params.put("startDate", startDate);
        params.put("pics", pics);
        params.put("beforeAdd", beforeAdd);
        params.put("addValue", addValue);
        params.put("logging", jilu);
        params.put("afterAdd",afterAdd);
        params.put("vehicleId",vehicleid);
        params.put("vehicleNumber",vehicleNumber);
        params.put("custom",custom);
        params.put("isQualified",qualified);
        params.put("concentration",concentration);

        params.put("driver",driver);
        params.put("escort",escort);
        params.put("poundWeight",poundweight);
        params.put("density",density);
        params.put("beforeReadflow",beforereadflow);
        params.put("afterReadflow",afterreadflow);
        params.put("loadflowMeter",outgoingquantity);
        params.put("riskIdentification",riskidentification);
        params.put("riskAwareness",preventivemeasures);
        params.put("startWorkTime",startWorkTime);
        params.put("endWorkTime",endWorkTime);
        params.put("integral",integral);

        Log.d("梁小江",startWorkTime+"======="+endWorkTime);
        Log.d(TAG, params.getParamString());

        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "FinishTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "FinishTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }
    public void taskTemplate(final Handler mHandler, List<DictDetail> dictDetails) {
        String url = BASE_URL + "/api/work/save/detail";
        params = new AjaxParams();
       for (int i=0;i<dictDetails.size();i++){
           params.put("workDetails[" + i + "].workId",dictDetails.get(i).getTaskId());
           params.put("workDetails[" + i + "].title",dictDetails.get(i).getTitle());
           params.put("workDetails[" + i + "].hint",dictDetails.get(i).getHint());
           params.put("workDetails[" + i + "].type",dictDetails.get(i).getType()+"");
           params.put("workDetails[" + i + "].isNull",dictDetails.get(i).getIsNull());
           params.put("workDetails[" + i + "].content",dictDetails.get(i).getContent());
       }
        Log.d(TAG, params.getParamString());

        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "FinishTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "FinishTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    public void requestFinishTaskMuti(final Handler mHandler, List<Task> tasks, User user) {
        String url = BASE_URL + "/api/work/handle/muti";
        params = new AjaxParams();
        for (int i = 0; i < tasks.size(); i++) {
            params.put("histories[" + i + "].actId", tasks.get(i).getActId());
            params.put("histories[" + i + "].isAgree", tasks.get(i).getIsAgree() + "");
            params.put("histories[" + i + "].workId", tasks.get(i).getTaskId());
            params.put("histories[" + i + "].memo", tasks.get(i).getFinishedMemo());
            params.put("histories[" + i + "].endDate", tasks.get(i).getEndDate());
            params.put("histories[" + i + "].startDate", tasks.get(i).getStartDate());
            params.put("histories[" + i + "].pics", tasks.get(i).getPics());
            params.put("histories[" + i + "].creator.id", user.getUserId());
        }
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "FinishTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "FinishTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }


    public void requestSingle(final Handler mHandler,  String startTime, String endTime ,String id,String vehicleId,String creatorId,String actName) {

        String url = BASE_URL + "/api/vehicle/apply/save";
        params = new AjaxParams();
        params.put("realStartTime",startTime);
        params.put("realEndTime",endTime);
        params.put("id",id);
        params.put("vehicleId",vehicleId);
        params.put("creatorId",creatorId);
        params.put("actName",actName);
//        params.put("memo","123");
        Log.d(TAG, params.getParamString());

        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "FinishTaskFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "FinishTaskSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @param picName
     * @param path
     * @throws FileNotFoundException
     * @Description 上传图片
     * @date 2014年7月7日 下午2:43:26
     */
    public void requestUploadPic(final Handler mHandler, String uid, String picName, String path) throws FileNotFoundException {

        String url = BASE_URL + "/commons/attachment/upload";
        Log.d("tag", url);
        params = new AjaxParams();
        params.put("file", new File(path));
        params.put("name", picName);
        params.put("uid", uid);
        params.put("mime", "image/png");
        Log.d("tag", params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Log.d("UploadPic", "图片上传失败" + " " + errorNo);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Log.d("UploadPic", t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                try {
                    JSONObject jsonObject = new JSONArray(t.toString()).getJSONObject(0);
                    msg.obj = jsonObject.get("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.sendToTarget();
            }
        });
    }

    /**
     * 这是甲醇作业
     *
     * @throws FileNotFoundException
     */

    public void zjp(final Handler mHandler, String workNo, String keyMeth, String valueMeth, String keyName, String valueType) throws FileNotFoundException {
        String url = BASE_URL + "/api/work/save/methanol";
        params = new AjaxParams();
        params.put("workId", workNo);
        params.put("keyMeth", keyMeth);
        params.put("valueMeth", valueMeth);
        params.put("keyName", keyName);
        params.put("typeId", valueType);

        finalHttp.post(url, params, new AjaxCallBack<Object>() {
            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }


    /**
     * 根据CODE读取设备信息接口
     * @param mHandler
     * @param code
     * @param userId
     * @param optionName
     */
    public void requestDeviceByCode(final Handler mHandler, String code, String userId, String optionName) throws FileNotFoundException{
        String url = BASE_URL + "/api/device/feature/list";
        params = new AjaxParams();
        params.put("code", code);
        params.put("userId", userId);
        params.put("optionName", optionName);
        Log.d("zjp", params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getDeviceFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d("zjp", tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getDeviceSuccess:  ";
                Log.d("zjp", tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getNameList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public void requestTaskInfo(final Handler mHandler,String userId){
        String url = BASE_URL + "/api/user/integral/rank";
        params = new AjaxParams();
        params.put("userId", userId);
        Log.d("zjp", params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "getDeviceFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d("zjp", tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "getDeviceSuccess:  ";
                Log.d("zjp", tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getTaskInfo(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param
     * @param
     * @Description zjp登陆用户后持续发送POST消息（终端编号、位置信息、登陆人、等。。。）
     * @date 2016年9月8日 下午2:43:26
     */
    public void requestUploadGis(final Handler mHandler, String lat, String lng, String userId, String deviceId) throws FileNotFoundException {

        String url = BASE_URL + "/api/gis/save/terminal";
        params = new AjaxParams();
        params.put("userId", userId);
        params.put("lat", lat);
        params.put("lng", lng);
        params.put("deviceId", deviceId);
        Log.d("tag", params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Log.d("UploadGis", "gis上传失败zjp" + " " + errorNo);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
                Log.d("Tag", "success：  " + t.toString());

            }
        });
    }

    public void requestAfterFinishTask(final Handler mHandler, final String workId, String startDate, String endDate, String userCode, String isAgree, String memo, String pics) {
        params = new AjaxParams();
        params.put("workId", workId);
        params.put("creator.usercode", userCode);
        params.put("isAgree", isAgree);
        params.put("memo", memo);
        params.put("endDate", endDate);
        params.put("startDate", startDate);
        params.put("pics", pics);
        String url = BASE_URL + "/api/work/handle";
        Log.d("Tag", "params:  " + params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = workId;
                msg.sendToTarget();
                Log.d("Tag", "errNo:  " + errorNo);
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
                Log.d("Tag", "success：  " + t.toString());
            }
        });
    }

    /**
     * @param mHandler
     * @Description 获取用户列表，并保存数据库
     * @date 2014年7月8日 上午10:10:51
     */
    public void requestGetUser(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/user/list";
        Log.d("requestGetUser", url);
        params = new AjaxParams();
        params.put("officeId", officeId);
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetUserFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetUserSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_USER;
                msg.obj = jsonHelper.getUserList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * 任务模板接口
     * @param mHandler
     * @param officeId
     */
    public void requestGetTank(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/work/list/methtank/info";
        Log.d("requestGetTank", url);
        params = new AjaxParams();
        params.put("officeId", "");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetTankFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetTankSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_CL_TANK;
                msg.obj = jsonHelper.getTankList(t.toString());
                msg.sendToTarget();
            }
        });
    }


    public void requestGetTaskTemplaet(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/work/list/dictdetail";
        Log.d("requestGetTank", url);
        params = new AjaxParams();
        params.put("officeId", "");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetTankFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetTankSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_CL_TASK_TEMPLATE;
                msg.obj = jsonHelper.getTaskTemplateList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @Description 获取gas巡检项并保存到数据库
     * @author baiqiao
     * @date 2014年7月28日 下午4:04:18
     */
    public void requestGetGasCheckItem(final Handler mHandler, String officeId,
                                       String page, String updateTime, String officeCode) {
        String url = BASE_URL + "/api/item/list";
        params = new AjaxParams();
        params.put("officeId", officeId);
        params.put("updateTime", updateTime);
        if (officeCode.length() > 6) {
            officeCode = officeCode.substring(0, 6);
        }
        params.put("code", officeCode);
        params.put("p", page);
        params.put("s", 5000 + "");
        Log.d("params", params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetGasCheckItemFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetGasCheckItemSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_XJ_ITEM;
                msg.obj = jsonHelper.getCheckItemList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public void requestGetDevice(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/device/list";
        params = new AjaxParams();
        params.put("officeId", officeId);
        String tags = "GetDeviceBegin:  ";
        Log.d(TAG, tags + params.toString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDeviceFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDeviceSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DEVICE;
                msg.obj = jsonHelper.getDevices(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @Description 获取井数据
     * @author baiqiao
     */
    public void requestGetWell(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/well/list";
        params = new AjaxParams();
        params.put("officeId", officeId);
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetWellFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetWellSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_WELL;
                msg.obj = jsonHelper.getWells(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler chargerId
     *                 负责人Id
     *                 status
     *                 状态 1：未开始 2：未完成 3：已完成
     * @Description 获取巡检计划
     * @author baiqiao
     * @date 2014年7月29日 上午9:11:51
     */
    public void requestGetGasPlan(final Handler mHandler) {
        String url = BASE_URL + "/api/patrol/plan/detail/list";
        params = new AjaxParams();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new Date());
        params.put("patrolTime", date);
        params.put("status", "3");
        // params.put("charger.id", chargerId);
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetGasPlanFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetGasPlanSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_PLAN;
                msg.obj = jsonHelper.getPlanDetailList(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @param gis
     * @Description 巡检时实时上传gis数据
     * @author baiqiao
     * @date 2014年7月27日 上午10:28:36
     */
    public void requestSubmitGisData(final Handler mHandler, final Gis gis, String lineId, String chargerId) {
        String url = BASE_URL + "/api/gis/save";
        Log.d("requestSubmitGisData", url);
        params = new AjaxParams();
        params.put("task.taskNo", gis.getNum());
        params.put("task.line.id", lineId);
        params.put("lat", gis.getLatitude());
        params.put("lng", gis.getLongitude());
        params.put("createTime", gis.getTime());
        params.put("creatBy.id", gis.getUserId());
        params.put("imei", gis.getDeviceId());
        params.put("status", gis.getExceptionStatus());
        params.put("abnormalContent", gis.getMemo());
        if (gis.getPics().equals("") || gis.getPics() == null) {
            params.put("picIds", gis.getPics());
        } else {
            if (gis.getPics().equals("null")) {
                params.put("picIds", "");
            } else {
                params.put("picIds", gis.getPics() + ",");
            }
        }
        params.put("id", gis.getGisId());
        params.put("tag", gis.getTaskType());
        params.put("charger.id", chargerId);
        Log.d("tag", params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "SubmitGisDataFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "SubmitGisDataSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.obj = t.toString();
                msg.what = REQUEST_SUCCESS;
                msg.sendToTarget();
            }
        });
    }

    public void requestSubmitGisList(final Handler mHandler, List<Gis> gises) {
        String url = BASE_URL + "/api/gis/save/muti";
        params = new AjaxParams();
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
                params.put("taskDetails[" + i + "].picIds", gises.get(i).getPics() + ",");
            }
            params.put("taskDetails[" + i + "].id", gises.get(i).getGisId());
            params.put("taskDetails[" + i + "].charger.id", gises.get(i).getUserId());
            params.put("taskDetails[" + i + "].creatBy.id", gises.get(i).getUserId());
            params.put("taskDetails[" + i + "].task.line.id", gises.get(i).getLindId());
            params.put("taskDetails[" + i + "].tag", gises.get(i).getTaskType());
        }
        Log.d(TAG, "GIs  " + params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "SubmitGisListFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "SubmitGisListSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @param planDetails
     * @param flag
     * @param chargerId
     * @deprecated
     */
    public void requestSubmitGasList(final Handler mHandler, List<PlanDetail> planDetails, String flag, String chargerId) {
        String url = BASE_URL + "/api/patrol/plan/update";
        params = new AjaxParams();
        params.put("flag", flag);
        for (int i = 0; i < planDetails.size(); i++) {
            params.put("children[" + i + "].item.id", planDetails.get(i).getItemId());
            params.put("children[" + i + "].patrolTime", planDetails.get(i).getPatrolTime());
            params.put("children[" + i + "].handleAdvice", planDetails.get(i).getHandleAdvice());
            params.put("children[" + i + "].memo", planDetails.get(i).getMemo());
            if (planDetails.get(i).getPicId().equals("") || planDetails.get(i).getPicId() == null) {
                params.put("children[" + i + "].picIds", planDetails.get(i).getPicId());
            } else {
                params.put("children[" + i + "].picIds", planDetails.get(i).getPicId() + ",");
            }
            params.put("children[" + i + "].result", planDetails.get(i).getResult());
            params.put("children[" + i + "].status", planDetails.get(i).getExceptionStatus());
            params.put("children[" + i + "].videoId", planDetails.get(i).getVideoId());
            params.put("children[" + i + "].updateTime", planDetails.get(i).getUpdateTime());
            params.put("children[" + i + "].handleMemo", planDetails.get(i).getHandleMemo());
            params.put("children[" + i + "].charger.id", chargerId);
            Log.d(TAG, "itemId: " + planDetails.get(i).getItemId() + "  PatrolTime: " + planDetails.get(i).getPatrolTime() + "   updateTime:  " + planDetails.get(i).getUpdateTime());
        }
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "SubmitGasListFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo + strMsg);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "SubmitGasListSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    // 管线基础数据下载
    public void requestGetLine(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/line/list";
        params = new AjaxParams();
        params.put("officeId", officeId);
        params.put("type", "1");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetLineFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetLineSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_LINE;
                msg.obj = jsonHelper.getLines(t.toString());
                msg.sendToTarget();
            }
        });
    }

    // 获取作业区数据
    public void requestGetOffice(final Handler mHandler) {
        String url = BASE_URL + "/api/office/list";
        params = new AjaxParams();
        params.put("grade", "3");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetOfficeFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetOfficeSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getOffice(t.toString());
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @Description 获取字典基础数据
     * @author baiqiao
     * @date 2014年9月3日 上午10:40:18
     */
    public void requestGetDict(final Handler mHandler) {
        String url = BASE_URL + "/api/dict/list";
        params = new AjaxParams();
        params.put("value", "task_type");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDictFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo + "    " + strMsg);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDictSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DICT;
                msg.obj = jsonHelper.getDicts(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public void requestGetDeviceTreeOne(final Handler mHandler) {
        String url = BASE_URL + "/api/device/tree";
        params = new AjaxParams();
        params.put("cardType", "1");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDeviceTreeFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDeviceTreeSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DEVICE_TREE;
                msg.obj = jsonHelper.getDeviceTrees(t.toString(), "1");
                msg.sendToTarget();
            }
        });
    }

    public void requestGetDeviceTreeTwo(final Handler mHandler) {
        String url = BASE_URL + "/api/device/tree";
        params = new AjaxParams();
        params.put("cardType", "2");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDeviceTreeFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDeviceTreeSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DEVICE_TREE_2;
                msg.obj = jsonHelper.getDeviceTrees(t.toString(), "2");
                msg.sendToTarget();
            }
        });
    }

    public void requestGetDeviceTreeThree(final Handler mHandler) {
        String url = BASE_URL + "/api/device/tree";
        params = new AjaxParams();
        params.put("cardType", "3");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDeviceTreeFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDeviceTreeSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DEVICE_TREE_3;
                msg.obj = jsonHelper.getDeviceTrees(t.toString(), "3");
                msg.sendToTarget();
            }
        });
    }

    public void requestGetDeviceTreeFour(final Handler mHandler) {
        String url = BASE_URL + "/api/device/tree";
        params = new AjaxParams();
        params.put("cardType", "4");
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetDeviceTreeFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetDeviceTreeSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_DEVICE_TREE_4;
                msg.obj = jsonHelper.getDeviceTrees(t.toString(), "4");
                msg.sendToTarget();
            }
        });
    }

    // 主界面的异常提交
    public void requestUploadWork(final Handler mHandler, String deviceNames, String deviceCode, String workTypeId, String chargerId, String category, String pics, String riskTips,String lat,String lng,String uploadType) {
        String url = BASE_URL + "/api/work/save";
        params = new AjaxParams();
        params.put("deviceNames", deviceNames);
        Log.d(TAG, deviceNames);
        params.put("deviceCode", deviceCode);
        params.put("workType.id", workTypeId);
        params.put("charger.id", chargerId);
        params.put("creator.id", chargerId);
        params.put("category", category);
        params.put("history.pics", pics);
        params.put("riskTips", riskTips);
        params.put("lat",lat);
        params.put("lng",lng);
        params.put("uploadType",uploadType);
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "UploadWorkFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo + " " + strMsg);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "UploadWorkSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    // Hse提交
    public void requestUploadHse(final Handler mHandler, UploadHseSupervision uploadHseSupervision) {
        String url = Constants.BASE_URL + "/api/hse/save";

        params = new AjaxParams();
        params.put("beCheckedOffice.id", uploadHseSupervision.getBeCheckedOffice());
        params.put("beCheckedOrgan", uploadHseSupervision.getBeCheckedOrgan());
        params.put("createdDate", uploadHseSupervision.getCreatedDate());
        params.put("checkerIds", uploadHseSupervision.getCheckerIds());
        params.put("checkerNames", uploadHseSupervision.getCheckerNames());
        params.put("issue", uploadHseSupervision.getIssue());
        params.put("checkOffice.id", uploadHseSupervision.getCheckOffice());
        params.put("suggestion", uploadHseSupervision.getSuggestion());

        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "UploadWorkFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo + " " + strMsg);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "UploadWorkSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    // 提交巡检异常并保存派工任务
    public void requestUploadWorkMuti(final Handler mHandler, List<PlanDetail> planDetails, String chargerId) {
        String url = BASE_URL + "/api/work/save/muti";
        params = new AjaxParams();
        for (int i = 0; i < planDetails.size(); i++) {
            PlanDetail planDetail = planDetails.get(i);
            params.put("works[" + i + "].deviceNames", planDetail.getPointName());
            params.put("works[" + i + "].deviceCode", planDetail.getCode());
            params.put("works[" + i + "].deadTime", planDetail.getUpdateTime());
            params.put("works[" + i + "].charger.id", chargerId);
            params.put("works[" + i + "].workType.id", planDetail.getCode());
            params.put("works[" + i + "].creator.id", chargerId);
        }
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "UploadWorkFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "UploadWorkSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                try {
                    JSONObject object = new JSONObject(t.toString());
                    msg.obj = object.getString("workId");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.sendToTarget();
            }
        });
    }

    // 主界面的异常处理
    public void requestHandleWork(final Handler mHandler, String workId, String chargerId) {
        String url = BASE_URL + "/api/work/handle";
        params = new AjaxParams();
        params.put("workId", workId);
        params.put("creator.id", chargerId);
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "HandleWorkFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "HandleWorkSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                try {
                    JSONObject object = new JSONObject(t.toString());
                    msg.obj = object.getString("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                msg.sendToTarget();
            }
        });
    }

    public void requestDeleteWork(final Handler mHandler, String uid, String id) {
        String url = BASE_URL + "/api/work/del";
        params = new AjaxParams();
        params.put("id", id);
        params.put("uid", uid);
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "DeleteWorkFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "DeleteWorkSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    /**
     * @param mHandler
     * @param code
     * @param pointType
     * @Description 下载计划模板
     * @author joe
     * @date 2014年9月5日 下午2:07:05
     */
    public void requestGetPlanTemplate(final Handler mHandler, String code, final String pointType) {
        String url = BASE_URL + "/api/patrol/template/list";
        params = new AjaxParams();
        params.put("office.code", code);
        params.put("pointType", pointType);
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetPlanTemplateFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetPlanTemplateSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                if (pointType.equals("1")) {
                    msg.what = DOWNLOAD_PLAN;
                } else {
                    msg.what = DOWNLOAD_PLAN_TEMPLATE;
                }
                msg.obj = jsonHelper.getPlanTemplateDetails(t.toString(), pointType);
                msg.sendToTarget();
            }
        });
    }

    public void requestCheckLogin(int status, String userName) {
        String url;
        if (status == 1) {
            url = BASE_URL + "/api/login/rest";
        } else {
            url = BASE_URL + "/api/logout/rest";
        }
        params = new AjaxParams();
        params.put("username", userName);
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                Log.d(TAG, errorNo + "");
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Log.d(TAG, t.toString());
            }
        });
    }

    public void requestCheckLogin(final Handler mHandler, final int status, String userName, String password) {
        String url;
        if (status == 1) {
            url = BASE_URL + "/api/login/rest";
        } else {
            url = BASE_URL + "/api/logout/rest";
        }
        params = new AjaxParams();
        params.put("username", userName);
        params.put("password", password);
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                if (status == 1) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = REQUEST_FAILER;
                    msg.obj = "登录失败";
                    msg.sendToTarget();
                }
                Log.d(TAG, errorNo + "");
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                Log.d(TAG, t.toString());
                if (status == 1) {
                    User user = new User();
                    String status = "";
                    try {
                        JSONObject object = new JSONObject(t.toString());
                        status = object.getString("status");
                        if (status.equals("success")) {
                            JSONObject obj = object.getJSONObject("user");
                            user.setUserId(obj.getString("id"));
                            user.setLoginName(obj.getString("loginName"));
                            user.setPassword(obj.getString("password"));
                            user.setName(obj.getString("name"));
                            user.setPhone(obj.getString("phone"));
                            user.setMobile(obj.getString("mobile"));
                            user.setHost(obj.getString("host"));
                            user.setLoginDate(obj.getString("loginDate"));
                            user.setRoleId(obj.getString("roleId"));
                            user.setRoleName(obj.getString("roleName"));
                            user.setRoleEnname(obj.getString("roleEnname"));
                            user.setOfficeId(obj.getString("officeId"));
                            user.setOfficeCode(obj.getString("officeCode"));
                            user.setOfficeName(obj.getString("officeName"));
                            user.setLoginStatus("1");
                        } else {
                            Message msg = mHandler.obtainMessage();
                            msg.what = REQUEST_FAILER;
                            msg.obj = "账号或密码错误";
                            msg.sendToTarget();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (user.getOfficeCode().length() < 6 || user.getOfficeCode().length() >= 6 && (Constants.CURRENT_AREA + "").contains(user.getOfficeCode().substring(5, 6))) {
                        Message msg = mHandler.obtainMessage();
                        msg.what = REQUEST_SUCCESS;
                        msg.obj = user;
                        msg.sendToTarget();
                    } else {
                        Message msg = mHandler.obtainMessage();
                        msg.what = REQUEST_FAILER;
                        msg.obj = "非本区作业人员";
                        msg.sendToTarget();
                    }
                }
            }
        });
    }

    public void requestUpdateTaskPic(final Handler mHandler, List<Picture> pics) {
        String url = BASE_URL + "/api/work/update/history/muti";
        params = new AjaxParams();
        for (int i = 0; i < pics.size(); i++) {
            params.put("histories[" + i + "].id", pics.get(i).getTypeOfId());
            params.put("histories[" + i + "].pics", pics.get(i).getPicId());
        }

        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "UpdateTaskPicFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "UpdateTaskPicSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    public void requestGisFinish(final Handler mHandler, GisFinish gisFinish) {
        String url = BASE_URL + "/api/gis/save/name";
        params = new AjaxParams();
        params.put("category", gisFinish.getCategory() + "");
        params.put("taskNo", gisFinish.getTaskNo());
        params.put("name", gisFinish.getLineName());
        params.put("startTime", gisFinish.getCreatTime());
        params.put("endTime", gisFinish.getEndTime());
        params.put("createBy.id", gisFinish.getUserId());
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GisFinishFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GisFinishSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

    public void requestGetGasException(final Handler mHandler, String officeId) {
        String url = BASE_URL + "/api/patrol/last/abnormal";
        params = new AjaxParams();
        params.put("officeId", officeId);
        Log.d(TAG, params.getParamString());
        finalHttp.get(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "GetGasExceptionFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "GetGasExceptionSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = jsonHelper.getExceptions(t.toString());
                msg.sendToTarget();
            }
        });
    }

    public static void requestUploadAppException(final String exceptionString) {
        String url = BASE_URL + "/api/log/save";
        AjaxParams params = new AjaxParams();
        params.put("type", "2");
        params.put("createBy.id", "119");
        params.put("createDate", DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        params.put("remoteAddr", "");
        params.put("method", "");
        params.put("params", "");
        params.put("userAgent", "");
        params.put("exception", exceptionString);
        Log.d(TAG, url + " " + params.getParamString() + "\n" + exceptionString);

        new FinalHttp().post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "requestUploadAppExceptionFailure:  ";

                Log.d(TAG, tags + errorNo);
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "requestUploadAppExceptionSuccess:  ";
                Log.d(TAG, tags + t.toString());
            }
        });

    }
    // 反馈提交
    public void requestUploadFeedback(final Handler mHandler,String uid,String title,String description,String pics,String time) {
        String url = BASE_URL + "/api/message/saves";
        AjaxParams params = new AjaxParams();
//        params.put("id","2");
        params.put("name", uid);
        params.put("title", title);
        params.put("content", description);
        params.put("pictures",pics);
        params.put("created_date", time);
        Log.d("title",params.getParamString());
        Log.d(TAG, params.getParamString());
        finalHttp.post(url, params, new AjaxCallBack<Object>() {

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                super.onFailure(t, errorNo, strMsg);
                String tags = "UploadFeedbackFailer:  ";
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_FAILER;
                msg.obj = errorNo;
                Log.d(TAG, tags + errorNo + " " + strMsg);
                msg.sendToTarget();
            }

            @Override
            public void onSuccess(Object t) {
                super.onSuccess(t);
                String tags = "UploadFeedbackSuccess:  ";
                Log.d(TAG, tags + t.toString());
                Message msg = mHandler.obtainMessage();
                msg.what = REQUEST_SUCCESS;
                msg.obj = t.toString();
                msg.sendToTarget();
            }
        });
    }

}
