package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.joe.oil.R;
import com.joe.oil.dialog.GisExceptionPreviewDialog;
import com.joe.oil.entity.Gis;
import com.joe.oil.entity.GisFinish;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.CustomUtil;
import com.joe.oil.util.DateUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

@SuppressLint({"SimpleDateFormat", "ShowToast"})
public class GisUploadActivity extends BaseActivity implements OnClickListener {

    private ImageView back;
    private EditText reason;
    private TextView takePhoto;
    private TextView submit;
    private TextView history;
    private Context context;
    private HttpRequest http;
    private OilApplication oilApplication;
    private User user;
    private SqliteHelper sqliteHelper;
    private String time = "";
    private Handler mHandler = new Handler();

    private boolean isExceptionInspection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gis_upload);

        initView();
        initMembers();
    }

    private void initView() {

        back = (ImageView) this.findViewById(R.id.gis_uplaod_btn_back);
        reason = (EditText) this.findViewById(R.id.gis_upload_msg);
        takePhoto = (TextView) this.findViewById(R.id.gis_upload_take_photo);
        submit = (TextView) this.findViewById(R.id.gis_upload_submit);
        history = (TextView) this.findViewById(R.id.gis_upload_history);

        back.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
        submit.setOnClickListener(this);
        history.setOnClickListener(this);
    }

    private void initMembers() {
        isExceptionInspection = getIntent().getBooleanExtra("isExceptionInspection", false);

        context = GisUploadActivity.this;
        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        oilApplication = (OilApplication) getApplication();
        user = oilApplication.getUser();
        // time = Constants.GPS_TIME;
        time = DateUtils.getDateTime();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gis_uplaod_btn_back:
                this.finish();
                break;

            case R.id.gis_upload_take_photo:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
                if (imgData != null && imgData.size() > 0) {
                    Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(GisUploadActivity.this, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 4);
                    intent.putExtra("typeOfId", time);
                    intent.putExtra("imageSelected", imageGroup);
                    startActivity(intent);
                } else {
                    Log.d("Image Select Flag", "imgData null");
                    Intent intent = new Intent(GisUploadActivity.this, ImagePickerActivity.class);
                    intent.putExtra("intentFrom", 4);
                    intent.putExtra("typeOfId", time);
                    startActivity(intent);
                }
                break;

            case R.id.gis_upload_submit:
                submit();
                break;

            case R.id.gis_upload_history:
                Intent intent = new Intent(GisUploadActivity.this, GisUploadHistoryActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    private void submit() {
        if (reason.getText().toString().length() < 0 || reason.getText().toString().equals("")) {

            Constants.showToast(context, "请输入异常原因");
        } else if (Constants.CURRENT_LAT == 0 && Constants.CURRENT_LNG == 0) {

            Constants.showToast(context, "暂无您当前的位置信息，请稍后再试");
        } else {
            String lineId = "";
            final Gis gis = new Gis();
            gis.setMemo(reason.getText().toString());
            gis.setDeviceId(Constants.DEVICE_ID);
            gis.setLatitude(Constants.CURRENT_LAT + "");
            gis.setLongitude(Constants.CURRENT_LNG + "");
            if (Constants.IS_LINE_START) {
                gis.setNum(Constants.GIS_START_NUM);
                lineId = Constants.CURRENT_LINE_ID;
            } else if (isExceptionInspection) {

                CustomUtil.createNewTaskNo(user);
                GisFinish gisFinish = new GisFinish();
                gisFinish.setCreatTime(time);
                gisFinish.setTaskNo(Constants.GIS_START_NUM);
                gisFinish.setCategory(2);
                gisFinish.setStatus("0");
                gisFinish.setGisNum(1);
                gisFinish.setUserId(user.getUserId());
                gisFinish.setLineName(user.getName() + Constants.GIS_START_NUM);
                gisFinish.setEndTime(time);
                SqliteHelper sqliteHelper = new SqliteHelper(context);
                sqliteHelper.insertGisFinish(gisFinish);

                gis.setNum(Constants.GIS_START_NUM);

                http.requestGisFinish(new FinishHandler(gisFinish), gisFinish);
            }
            gis.setLindId(lineId);
            gis.setUserId(user.getUserId());
            gis.setTaskType(Constants.TASK_TYPE);
            gis.setStatus("0");
            gis.setTime(time);
            gis.setIsPicIdUpload("0");
            List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
            if (imgData != null && imgData.size() > 0) {
                gis.setPics("");
            } else {
                gis.setPics("null");
            }
            gis.setGisId("");
            gis.setExceptionStatus("2");
            sqliteHelper.insertGis(gis);

            GisExceptionPreviewDialog gisExceptionPreviewDialog = new GisExceptionPreviewDialog(context, gis);
            final String finalLineId = lineId;
            gisExceptionPreviewDialog.setOnDialogOkListener(new GisExceptionPreviewDialog.OnDialogOkListener() {
                @Override
                public void onDialogOk() {
                    Constants.showDialog(context);
                    http.requestSubmitGisData(new SubmitGisHandler(gis), gis, finalLineId, user.getUserId());
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doWithUploadFinish();
                        }
                    }, 2000);
                }
            });
            gisExceptionPreviewDialog.show();
        }
    }

    private void doWithUploadFinish() {
        if (reason != null) {
            reason.setText("");
        }
        Constants.dismissDialog();
        if (!Constants.checkNetWork(this)) {
            Constants.showToast(this, "网络不给力，数据已转至后台上传");
        }
    }

    private class FinishHandler extends Handler {
        private GisFinish gisFinish;

        public FinishHandler(GisFinish gisFinish) {
            this.gisFinish = gisFinish;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    gisFinish.setStatus("2");
                    sqliteHelper.updateGisFinish(gisFinish);
                    break;

                case HttpRequest.REQUEST_FAILER:
                    gisFinish.setStatus("1");
                    sqliteHelper.updateGisFinish(gisFinish);
                    break;

                default:
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class SubmitGisHandler extends Handler {
        private Gis gis;

        public SubmitGisHandler(Gis gis) {
            this.gis = gis;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Gis gisLocal = sqliteHelper.getGisByTime(gis.getTime());
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    reason.setText("");
                    doWithUploadFinish();
                    if (gisLocal == null) {
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(msg.obj.toString());
                        String status = obj.getString("status");
                        String taskDetailId = obj.getString("taskDetailId");
                        if (status.equals("success")) {
                            // if (!gisLocal.getPics().equals("")) {
                            // sqliteHelper.deleteGisByTime(gisLocal);
                            // return;
                            // }
                            if (!gisLocal.getPics().equals("")) {
                                gisLocal.setIsPicIdUpload("1");
                            }
                            gisLocal.setStatus("2");
                            gisLocal.setGisId(taskDetailId);
                            sqliteHelper.updateGis(gisLocal);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case HttpRequest.REQUEST_FAILER:
                    if (gisLocal != null && gisLocal.getStatus().equals("0")) {
                        gisLocal.setStatus("1");
                        sqliteHelper.updateGis(gisLocal);
//                        sqliteHelper.updateGisByTime(gisLocal);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            takePhoto.setText("查看图片");
        } else {
            takePhoto.setText("添加图片");
        }
    }
}
