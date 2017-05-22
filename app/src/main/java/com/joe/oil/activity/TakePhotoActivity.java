package com.joe.oil.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.dialog.CheckoutPhotoDialog;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.PlanDetail;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.DateUtils;
/**
 * @deprecated
 * @author Administrator
 *
 */
@SuppressLint({"SdCardPath", "SimpleDateFormat", "ShowToast"})
public class TakePhotoActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private ImageView back;
    private TextView takePhoto;
    private ImageView photoImg;
    private Context context;
    private File photo; // 拍照获取的照片
    private File takePhotoCompress;
    private String takePhotoCompressName;
    private String takePhotoName;
    private HttpRequest http;
    private String pics = null;
    private SqliteHelper sqliteHelper;
    private PlanDetail planDetail;
    private OilApplication application;
    private User user;
    private String path = Constants.PHOTO_PATH;
    private int intentFrom;
    private String typeOfId;
    private Picture pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_take_photo);

        initView();
        initMembers();
        initFloder();
    }

    private void initView() {
        back = (ImageView) this.findViewById(R.id.take_photo_btn_back);
        takePhoto = (TextView) this.findViewById(R.id.take_photo_take_photo);
        photoImg = (ImageView) this.findViewById(R.id.take_photo_photo);

        back.setOnClickListener(this);
        takePhoto.setOnClickListener(this);
    }

    private void initMembers() {
        context = TakePhotoActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        intentFrom = getIntent().getIntExtra("intentFrom", 1);
        typeOfId = getIntent().getStringExtra("typeOfId");
        if (intentFrom == 1) {
            planDetail = (PlanDetail) getIntent().getSerializableExtra("planDetail");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.take_photo_btn_back:
                this.finish();
                break;

            case R.id.take_photo_take_photo:
                takePhoto();
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckoutPhotoDialog dialog = new CheckoutPhotoDialog(context);
        dialog.show();
    }

    private void initFloder() {
        // if (isHaveSdCard()) {
        File oil = new File(path);

        if (!oil.exists()) {
            oil.mkdirs();
            Log.d("FinishTaskActivity", "创建文件夹_oil");
        }
        // }
        photo = new File(path, getTakePhotoFileName());
        takePhotoCompressName = path + getTakePhotoCompressFileName();
        takePhotoName = path + getTakePhotoFileName();
        takePhotoCompress = new File(takePhotoCompressName);
    }

    // 使用系统当前日期加以调整作为拍照照片的名称
    private String getTakePhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return "takePhoto_" + dateFormat.format(date) + ".png";
    }

    // 使用系统当前日期加以调整作为拍照压缩后照片的名称
    private String getTakePhotoCompressFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        return dateFormat.format(date) + ".png";
    }

    // 拍照
    private void takePhoto() {
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 指定调用相机拍照后照片的储存路径
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        startActivityForResult(cameraintent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Bitmap bitmap = BitmapFactory.decodeFile(takePhotoName); // OOM
        // Bitmap bitmap = Constants.readBitMap(this, takePhotoName);
        Bitmap bitmap = fitSizeImg(takePhotoName);
        if (requestCode == 101 && bitmap != null) {
            // 选择拍照
            Bitmap newBitmap = zoomBitmap(bitmap, 324, 432);
            photoImg.setVisibility(View.VISIBLE);
            photoImg.setImageBitmap(newBitmap);
            FileOutputStream fOut = null;
            try {
                takePhotoCompress.createNewFile();
                fOut = new FileOutputStream(takePhotoCompress);
            } catch (Exception e) {
                e.getStackTrace();
            }
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
            }
//			UpLoadHandler upLoadHandler = new UpLoadHandler();
//							http.requestUploadPic(upLoadHandler, user.getUserId(), takePhotoCompressName, takePhotoCompressName);
            pic = new Picture();
            pic.setChargerId(user.getUserId());
            pic.setCreateTime(DateUtils.getDateTime());
            pic.setName(takePhotoCompressName);
            pic.setType(intentFrom);
            pic.setUrl(takePhotoCompressName);
            pic.setIsWrokUpdate(0);
            String time = DateUtils.getDateTime();
            if (typeOfId.equals("") && intentFrom == 4) {
                typeOfId = time;
                Constants.GIS_GET_PHOTO_TIME = time;
            } else if (typeOfId.equals("") && intentFrom == 2) {
                typeOfId = time;
                Constants.UPLOADE_EXCEPTION_GET_PHOTO_TIME = time;
            }
            pic.setTypeOfId(typeOfId);
            pic.setIsUploadSuccess(0);
            sqliteHelper.insertPic(pic);
//			Constants.showDialog(context);
        }
    }

    @SuppressLint("HandlerLeak")
    private class UpLoadHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    if (intentFrom == 1) {
                        // Message msg1 =
                        // CheckItemTypeChooseDialog.getPicIdHandler.obtainMessage();
                        // msg1.obj = msg.obj.toString();
                        // msg1.sendToTarget();
                    } else if (intentFrom == 0) {
                        // Message msg0 =
                        // CheckItemTypeInputDialog.getPicIdHandler.obtainMessage();
                        // msg0.obj = msg.obj.toString();
                        // msg0.sendToTarget();
                    } else if (intentFrom == 2) {
                        // Message msg2 =
                        // UploadExceptionActivity.getPicIdHandler.obtainMessage();
                        // msg2.obj = msg.obj.toString();
                        // msg2.sendToTarget();
                    } else if (intentFrom == 3) {
//                        Message msg3 = TaskFinishActivity.getPicIdHandler.obtainMessage();
//                        msg3.obj = msg.obj.toString();
//                        msg3.sendToTarget();
                    } else {
//                        Message msg4 = GisUploadActivity.getPicIdHandler.obtainMessage();
//                        msg4.obj = msg.obj.toString();
//                        msg4.sendToTarget();
                    }
                    Log.d("TakePhotoActivity", "图片上传成功" + msg.obj.toString());
                    pic.setPicId(msg.obj.toString());
                    pic.setIsUploadSuccess(1);
                    sqliteHelper.updatePic(pic);
                    Log.d("TakePhotoActivity", "数据库更新成功" + pic.getPicId());
                    Constants.dismissDialog();
                    Toast.makeText(context, "照片上传成功", Toast.LENGTH_SHORT).show();
                    break;

                case HttpRequest.REQUEST_FAILER:
                    if (msg.obj.toString().equals("0")) {
                        Toast.makeText(context, "网络连接异常，请检查！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "上传失败，请重试", Toast.LENGTH_SHORT).show();
                    }
                    Constants.dismissDialog();

                    break;

                default:
                    break;
            }
        }
    }

    /**
     * 缩放Bitmap图片 *
     */

    public Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    public static Bitmap fitSizeImg(String path) {
        if (path == null || path.length() < 1)
            return null;
        File file = new File(path);
        Bitmap resizeBmp = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 数字越大读出的图片占用的heap越小 不然总是溢出
        if (file.length() < 20480) { // 0-20k
            opts.inSampleSize = 1;
        } else if (file.length() < 51200) { // 20-50k
            opts.inSampleSize = 2;
        } else if (file.length() < 307200) { // 50-300k
            opts.inSampleSize = 4;
        } else if (file.length() < 819200) { // 300-800k
            opts.inSampleSize = 6;
        } else if (file.length() < 1048576) { // 800-1024k
            opts.inSampleSize = 8;
        } else {
            opts.inSampleSize = 10;
        }
        resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);
        return resizeBmp;
    }

}
