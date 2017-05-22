package com.joe.oil.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.dialog.FeedbackDialog;
import com.joe.oil.entity.FeedBack;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.imagepicker.ImagePickerActivity;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;

import java.util.List;

/**
 * Created by liangxiaojiang on 2017/5/8.
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    private Context context;
    private ImageView back;
    private String time;//时间
    private EditText feedEddit;//标题
    private TextView confirm;//确定
    private RelativeLayout popconfirm;//选择图片
    private EditText description;//反馈描述
    private TextView takePhoto;
    private TextView photoNum;
    private User user;
    private SqliteHelper sqliteHelper;
    private HttpRequest http;
    private OilApplication application;
    private String curTotalPicId = "";
    private boolean isHavePic = false;
    private String intentFrom;
    private Handler mHandler = new Handler();

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        showPhotoNum();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_feedback);

        initView();
        initMembers();
        initData();
    }



    private void initView() {
        back= (ImageView) findViewById(R.id.feed_btn_back);
        feedEddit= (EditText) findViewById(R.id.feed_et);
        confirm= (TextView) findViewById(R.id.feedback_confirm);
        description= (EditText) findViewById(R.id.feedback_et_description);
        popconfirm= (RelativeLayout) findViewById(R.id.feedback_photo_rl);
        takePhoto= (TextView) findViewById(R.id.feedback_photo);
        photoNum= (TextView) findViewById(R.id.feedback_photo_num);
        back.setOnClickListener(this);
        confirm.setOnClickListener(this);
        popconfirm.setOnClickListener(this);

        photoNum.setText("");
        photoNum.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.feed_btn_back:
                handleWithFinish();
                break;
            case R.id.feedback_confirm:
                preview();
                break;
            case R.id.feedback_photo_rl:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
                if (imgData != null && imgData.size() > 0) {
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(FeedbackActivity.this, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    intent.putExtra("imageSelected", imageGroup);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FeedbackActivity.this, ImagePickerActivity.class);
                    intent.putExtra("intentFrom", 2);
                    intent.putExtra("typeOfId", time);
                    startActivity(intent);
                }
                break;
        }

    }

    private void preview() {
        String title=feedEddit.getText().toString();
        String feeddescription=description.getText().toString();

        if (title.length()<=0){
            Constants.showToast(context, "请填写标题");
            return;
        } else if (feeddescription.length() <= 0) {
            Constants.showToast(context, "请填写问题描述");
            return;
        }

        FeedBack feedBack=new FeedBack();

        feedBack.setTime(time);
        feedBack.setTitle(title);
        feedBack.setUserName(user.getName());
        feedBack.setDescription(feeddescription);
        feedBack.setIsUploadSuccess("0");
        feedBack.setPicId(curTotalPicId);
        FeedbackDialog ePreviewDialog = new FeedbackDialog(context, feedBack);
        ePreviewDialog.setOnDialogConfirmListener(new FeedbackDialog.OnDialogConfirmListener() {
            @Override
            public void onDialogConfirm() {
                uploadData();
            }
        });
        ePreviewDialog.show();
    }

    private void initMembers() {
        context = FeedbackActivity.this;
        sqliteHelper = new SqliteHelper(context);
        http = HttpRequest.getInstance(context);
        application = (OilApplication) getApplication();
        user = application.getUser();
    }

    private void initData() {
        time = DateUtils.getDateTime();
        intentFrom = getIntent().getStringExtra("intentFrom");
        showPhotoNum();
    }

    private void handleWithFinish() {
        /**
         * 必要操作，手动退出时，表示并未提交，此时将用户选择的图片的数据库记录删除
         */
        sqliteHelper.deletePics(time);
        sqliteHelper.deleteLocalPics(time);
        this.finish();
    }

    private void uploadData() {
        String sDescription = description.getText().toString();
        String title=feedEddit.getText().toString();
        if (title.length()<=0){
            Constants.showToast(context, "请填写标题");
            return;
        }else if (sDescription.length() <= 0) {
            Constants.showToast(context, "请填写问题描述");
            return;
        }

        curTotalPicId = "";
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            isHavePic = true;
            for (int i = 0; i < imgData.size(); i++) {
                curTotalPicId += ",";
            }
        } else {
            isHavePic = false;
        }
        FeedBack feedback = new FeedBack();
        feedback.setTime(time);
        feedback.setUserName(user.getName());
        feedback.setDescription(sDescription);
        feedback.setIsUploadSuccess("0");
        feedback.setPicId(curTotalPicId);
        feedback.setTitle(title);
//        // 向数据库中保存该条异常数据
        sqliteHelper.insertFeed(feedback);
        // 向服务器提交该条异常数据
        http.requestUploadFeedback(new UploadWorkHandler(feedback), user.getName(), title, sDescription,curTotalPicId,time);
        Constants.showDialog(context);

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doWithUploadFinish();
            }
        }, 2000);
    }

    private void doWithUploadFinish() {
        feedEddit.setText("");
        description.setText("");

        if (intentFrom.equals("InspectionActivity")) {
            Message msgMessage = InspectionActivity.handleExceptionHandler.obtainMessage();
            msgMessage.sendToTarget();
            FeedbackActivity.this.finish();
        }
        Constants.dismissDialog();
        if (Constants.checkNetWork(this)) {
        } else {
            Constants.showToast(this, "网络不给力，数据已转至后台上传");
        }
        // 更新新上传异常数据时间
        time = DateUtils.getDateTime();
        showPhotoNum();
    }

    private void showPhotoNum() {
        List<ImageBean> imgData = sqliteHelper.getLocalPics(time);
        if (imgData != null && imgData.size() > 0) {
            takePhoto.setText("查看图片");
            photoNum.setText(imgData.size() + "");
            photoNum.setVisibility(View.VISIBLE);
        } else {
            takePhoto.setText("添图");
            photoNum.setText("");
            photoNum.setVisibility(View.GONE);
        }
    }

    private class UploadWorkHandler extends Handler {
        private FeedBack feedBack;

        public UploadWorkHandler(FeedBack feedBack) {
            this.feedBack = feedBack;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    Log.d("FeedbackActivity", "数据上传失败啦");
                    Constants.showToast(FeedbackActivity.this, "数据上传失败啦");
                    // HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()),
                    // null);
                    break;

                case HttpRequest.REQUEST_SUCCESS:
                    Constants.showToast(FeedbackActivity.this, "数据上传成功啦");
                    break;

                default:
                    break;
            }
        }
    }
}