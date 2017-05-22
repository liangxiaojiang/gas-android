package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.joe.oil.R;
import com.joe.oil.adapter.OperationAdapter;
import com.joe.oil.entity.Task;
import com.joe.oil.util.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liangxiaojiang on 2017/4/27.
 */
public class OperationDisplayActivity extends Activity implements AdapterView.OnItemClickListener {

    private Task task;
    private ImageView back;
    private ListView mShowPathLv;
    private Context context;
    List<String> imagePathList = new ArrayList<String>();
    List<File> list = new ArrayList<File>();
    OperationAdapter operationAdapter;
    private String filePath;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_operation_display);
        initView();
        getImagePathFromSD();
        operationAdapter = new OperationAdapter(OperationDisplayActivity.this, list);
        mShowPathLv.setAdapter(operationAdapter);
        getAllFiles();
    }

    private void initView() {
        context = OperationDisplayActivity.this;
        task=(Task) getIntent().getSerializableExtra("task");
        mShowPathLv= (ListView) findViewById(R.id.operationList);
        mShowPathLv.setOnItemClickListener(this);
        back = (ImageView) findViewById(R.id.operation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

//        adapter = new ArrayAdapter<String>(this,
//                R.layout.item_operation);
//        adapter.addAll(imagePathList);
//        mShowPathLv.setAdapter(adapter);
    }


    public void getAllFiles() {
        list.clear();
        File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator
                + "Download/"+task.getTaskTypeName());
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    list.add(file2);
                }
            }
        }
//        // 文件排序
//        sort();

        // 数据改变之后刷新
        // notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容,
        // 可以实现动态的刷新列表的功能
        operationAdapter.notifyDataSetChanged();
    }

    /**
     * 从sd卡获取图片资源
     * @return
     */
    private List<String> getImagePathFromSD() {
        // 图片列表

        // 得到sd卡内image文件夹的路径   File.separator(/)
       filePath = Environment.getExternalStorageDirectory().toString() + File.separator
                + "Download/"+task.getTaskTypeName();
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(filePath);
        File[] files = fileAll.listFiles();
        // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
        if (files!=null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String FileEnd = file.getPath().substring(file.getPath().lastIndexOf("/") + 1,
                        file.getPath().length()).toLowerCase();
                if (checkIsImageFile(FileEnd)) {
                    imagePathList.add(FileEnd);
                }
            }

        }else {
            Constants.showToast(context, "没有文档或者操作卡");
        }
        // 返回得到的图片列表
        return imagePathList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     * @param fName  文件名
     * @return
     */
    @SuppressLint("DefaultLocale")
    private boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png") || FileEnd.equals("gif")
                || FileEnd.equals("jpeg")|| FileEnd.equals("bmp") ||FileEnd.equals("pdf")  ) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImageAdapter.AnimateFirstDisplayListener.displayedImages.clear();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        File file = list.get(position);
        Intent intent;
        if (file.getName().endsWith(".jpg")
                || file.getName().endsWith(".png")
                || file.getName().endsWith(".gif")){
            //使用Intent
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(list.get(position)), "image/*");
            startActivity(intent);
        }else if ( file.getName().endsWith(".pdf")){
            intent=new Intent(this,PDFviewActivity.class);
            intent.putExtra("fileName",filePath+"/"+file.getName());
            startActivity(intent);
        }else if (file.getName().endsWith(".txt")){
            intent=new Intent(this,TxtReadActivity.class);
            intent.putExtra("fileName",filePath+"/"+file.getName());
            startActivity(intent);
        }else if (file.getName().endsWith(".doc")){
            intent=new Intent(this,TxtReadActivity.class);
            intent.putExtra("fileName",filePath+"/"+file.getName());
            startActivity(intent);

        }else if (file.getName().endsWith(".docx")) {
            intent = new Intent(this, TxtReadActivity.class);
            intent.putExtra("fileName", filePath + "/" + file.getName());
            startActivity(intent);
        }else if (file.getName().endsWith(".xls")) {
            intent = new Intent(this, TxtReadActivity.class);
            intent.putExtra("fileName", filePath + "/" + file.getName());
            startActivity(intent);
        }else if (file.getName().endsWith(".xlsx")) {
            intent = new Intent(this, TxtReadActivity.class);
            intent.putExtra("fileName", filePath + "/" + file.getName());
            startActivity(intent);
        }
    }

}