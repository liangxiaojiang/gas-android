package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.adapter.TaskAdapter;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.DBManager;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.sqlite.sqlit;
import com.joe.oil.util.CicleImageVIew;
import com.joe.oil.util.Constants;
import com.joe.oil.util.ImageTools;
import com.joe.oil.view.CommentListView1;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 这是个人主页
 * Created by liangxiaojiang on 2016/9/9.
 */
public  class PersonalActivity extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ScrollView scrollView;
    private ImageView back;
    private CommentListView1 listview;
    private TextView noTask,mSumTaskTv,mSumTaskTimeTv,mSumUpQuestionTv,mSumScoreTv;
    private Context context;
    private SqliteHelper sqliteHelper;
    private List<Task> tasks;
    private TaskAdapter taskAdapter;
    private User user;
    private OilApplication application;
    private TextView tname;//这是主页显示用户名字
    private TextView bname;//这是标题显示用户姓名
    private Button bqiandao;//上面的点击签到按钮（这是点击后出现签到日历）
    private Button bpingjia;//评价
    private RelativeLayout footprint;
    private List<Map<String,Object>> infos;
    private TextView dingwei;//这是显示地理位置的文本
    private TextView tphone;//这是显示用户电话

    DBManager dbManager;
    private String date1 = null;//单天日期
    private String name1 = null;//当前用户
    private String isselct=null;//判断当天的是否签到

    private static final int SCALE = 5;//照片缩小比例
    private CicleImageVIew ivhead = null;
    private static final int TAKE_PICTURE = 0;
    private static final int CHOOSE_PICTURE = 1;
    private static final int CROP = 2;
    private static final int CROP_PICTURE = 3;

    private ImageView mHistorySynchronizationIv;
    private List<Task> taskall;
    private List<Task> taskGx;
    private HttpRequest http;
    private getTaskHandler TaskHandler;
    private getTaskInfoHandler TaskInfoHandler;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_personal);

        initView();
        initMembers();
        initData();
        //设置监听器
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar arg0, float arg1, boolean arg2) {
            }
        });
        judgmentSign();
    }




    private void initView() {
        back = (ImageView) this.findViewById(R.id.perso_btn_back);
        listview = (CommentListView1) this.findViewById(R.id.history_task);
        listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
        noTask = (TextView) this.findViewById(R.id.history_no_task);
        mSumTaskTv = (TextView) this.findViewById(R.id.tv_sum_task);
        mSumTaskTimeTv = (TextView) this.findViewById(R.id.tv_sum_tasktime);
        mSumUpQuestionTv = (TextView) this.findViewById(R.id.tv_sum_updataquestion);
        mSumScoreTv = (TextView) this.findViewById(R.id.iv_di);
        back.setOnClickListener(this);
        listview.setOnItemClickListener(this);

        tphone = (TextView) findViewById(R.id.tv_phone);//这是显示部门的文本
        tphone.setText(getIntent().getStringExtra("OfficeName"));
        dingwei = (TextView) findViewById(R.id.badud);//这是显示位置的文本

        tname = (TextView) findViewById(R.id.tv_1);
        bname = (TextView) findViewById(R.id.perso_title_name1);
        tname.setText(getIntent().getStringExtra("name"));
        name1=getIntent().getStringExtra("name");
        bname.setText("个人主页");
//        date2 = (LinearLayout) findViewById(R.id.date);//这是评价
        bqiandao = (Button) findViewById(R.id.b_qiandao);//这是签到按钮
        bqiandao.setOnClickListener(this);
//        pingjia = (LinearLayout) findViewById(R.id.ll_pingjia);
        bpingjia = (Button) findViewById(R.id.b_pingjia);
        bpingjia.setOnClickListener(this);

//        footprint = (RelativeLayout) findViewById(R.id.rl_footprint);
//        footprint.setOnClickListener(this);


        /**
         *  这是根据网上的，让ScrollView置顶，这样Listview就能全部显示出来（没有显示完全，只显示两条）
         *  重写自定义listview中的onMeasure方法，得到屏幕的宽高，（还是没有出来）
         *  还有一种方法，就是在ScrollView布局中写 android:fillViewport="true"，（还是没有实现）
         */
        scrollView = (ScrollView) findViewById(R.id.scrollview1);
        scrollView.smoothScrollTo(0, 0);

        ivhead=(CicleImageVIew) findViewById(R.id.iv_tou);
        ivhead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicturePicker(PersonalActivity.this,false);
            }
        });
        mHistorySynchronizationIv= (ImageView) findViewById(R.id.iv_tongbu);
        mHistorySynchronizationIv.setOnClickListener(this);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //设置每次更改的最小长度
        ratingBar.setStepSize((float) 0.5);


    }
    public void showPicturePicker(Context context, boolean isCrop){
        final boolean crop = isCrop;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle("图片来源");
//		builder.setNegativeButton("取消", null);
        builder.setItems(new String[]{"相机","更换头像"}, new DialogInterface.OnClickListener() {
            //类型码
            int REQUEST_CODE;

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case TAKE_PICTURE:
                        Uri imageUri = null;
                        String fileName = null;
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (crop) {
                            REQUEST_CODE = CROP;
                            //删除上一次截图的临时文件
                            SharedPreferences sharedPreferences = getSharedPreferences("temp",Context.MODE_WORLD_WRITEABLE);
                            ImageTools.deletePhotoAtPathAndName(Environment.getExternalStorageDirectory().getAbsolutePath(), sharedPreferences.getString("tempName", ""));

                            //保存本次截图临时文件名字
                            fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("tempName", fileName);
                            editor.commit();
                        }else {
                            REQUEST_CODE = TAKE_PICTURE;
                            fileName = "image.jpg";
                        }
                        imageUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),fileName));
                        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(openCameraIntent, REQUEST_CODE);
                        break;

                    case CHOOSE_PICTURE:
                        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        if (crop) {
                            REQUEST_CODE = CROP;
                        }else {
                            REQUEST_CODE = CHOOSE_PICTURE;
                        }
                        openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        startActivityForResult(openAlbumIntent, REQUEST_CODE);
                        break;

                    default:
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void initMembers() {
        context = PersonalActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        sqliteHelper = new SqliteHelper(context);
        tasks = new ArrayList<Task>();

        taskall = new ArrayList<Task>();
        taskGx = new ArrayList<Task>();
        http = HttpRequest.getInstance(context);
        TaskHandler = new getTaskHandler();
        TaskInfoHandler = new getTaskInfoHandler();
    }

    private void initData() {

        http.requestTaskInfo(TaskInfoHandler,user.getUserId());

        tasks = sqliteHelper.getTaskNotFinish2(user.getUserId());
        if (tasks.size() == 0) {
            noTask.setVisibility(View.VISIBLE);
        } else {
            noTask.setVisibility(View.GONE);
            listview.setVisibility(View.VISIBLE);
        }
        Collections.reverse(tasks);//集合倒序
        taskAdapter = new TaskAdapter(context, tasks);
        Log.d("initData: ",tasks.size()+"");
        listview.setAdapter(taskAdapter);
        setListViewHeight(listview);
        listview.setPullLoadEnable(false);
        listview.setPullRefreshEnable(false);

    }

    /**
     * 重新计算ListView的高度，解决ScrollView和ListView两个View都有滚动的效果，在嵌套使用时起冲突的问题
     *
     * @param listView
     */
    public void setListViewHeight(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    /**
     * 这是下面的listview（完成任务显示）的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Intent intent=new Intent(PersonalActivity.this,TaskFillDetailActivity.class);
        intent.putExtra("task", tasks.get(position-1));
        intent.putExtra("intentFrom", "personal");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.perso_btn_back:
                this.finish();
                break;
            case R.id.b_qiandao:
                Intent Signintent=new Intent(this,SignActivity.class);
                Signintent.putExtra("name",getIntent().getStringExtra("name"));
                startActivity(Signintent);
                break;
            case R.id.b_pingjia:

                Intent Evaluationintent=new Intent(this,EvaluationActivity.class);
                startActivity(Evaluationintent);

                break;
            case R.id.iv_tongbu:
                http.requestGetTaskInfo(TaskHandler, user.getUserId());
                break;
        }
    }



    private void judgmentSign() {
        dbManager = new DBManager(this);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        date1 = formatter.format(curDate);//当前时间，转化为string字符串
        query();
    }

    /**
     * 这是查询数据库中，看是否签到，当签到后这边的签到按钮就会变成已签到
     */
    public void query() {
        List<sqlit> persons = dbManager.query(name1);//这边是用实参的方式让值传过去
        for (sqlit person : persons) {
            if (date1.equals(person.getDate()) && name1.equals(person.getName())) {
                bqiandao.setText("已签到");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    //将保存在本地的图片取出并缩小后显示在界面上
                    Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/image.jpg");
                    Bitmap newBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / SCALE, bitmap.getHeight() / SCALE);
                    //由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
                    bitmap.recycle();

                    //将处理过的图片显示在界面上，并保存到本地
                    ivhead.setImageBitmap(newBitmap);
                    ImageTools.savePhotoToSDCard(newBitmap, Environment.getExternalStorageDirectory().getAbsolutePath(), String.valueOf(System.currentTimeMillis()));

                    break;

                case CHOOSE_PICTURE:
                    ContentResolver resolver = getContentResolver();
                    //照片的原始资源地址
                    Uri originalUri = data.getData();
                    try {
                        //使用ContentProvider通过URI获取原始图片
                        Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);
                        if (photo != null) {
                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE, photo.getHeight() / SCALE);
                            //释放原始图片占用的内存，防止out of memory异常发生
                            photo.recycle();

                            ivhead.setImageBitmap(smallBitmap);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;


                case CROP_PICTURE:
                    Bitmap photo = null;
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        photo = BitmapFactory.decodeFile(photoUri.getPath());
                    }
                    if (photo == null) {
                        Bundle extra = data.getExtras();
                        if (extra != null) {
                            photo = (Bitmap) extra.get("data");
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        }
                    }
                    ivhead.setImageBitmap(photo);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class getTaskHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Constants.dismissDialog();
                    taskall = (List<Task>) msg.obj;
                    Log.d("zjp","taskall = "+taskall.toString());

                    if (taskall.size() == 10) {
                        listview.setPullLoadEnable(true);
                    } else {
                        listview.setPullLoadEnable(false);
                    }
                    tasks.addAll(taskall);
                    if (taskall.size() == 0) {
                        noTask.setVisibility(View.VISIBLE);
                        Constants.showToast(context, "没有任务数据");
                    } else {
                        noTask.setVisibility(View.GONE);
                        Constants.showToast(context, "任务数据同步成功");
                    }

                    taskGx.addAll(tasks);
                    sqliteHelper.insert(taskGx);
                     tasks.clear();
                    tasks = sqliteHelper.getFinishedTaskIs(user.getUserId());
                    Collections.sort(taskGx);

                    taskAdapter = new TaskAdapter(context, tasks);

                    listview.setAdapter(taskAdapter);
                    taskAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(Constants.STATION_PLAN);
                    sendBroadcast(intent);

                    break;
                case HttpRequest.REQUEST_FAILER:
                    Constants.dismissDialog();
                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()),
                            null);
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private class getTaskInfoHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Constants.dismissDialog();
                  infos = (List<Map<String,Object>>) msg.obj;
                    Log.d("zjp","integrals = "+infos+"|");
                    int integralN = 0;
                    mSumTaskTv.setText(infos.get(0).get("num").toString());
                    if (infos.get(0).get("integral").toString().equals("null")){
                        mSumScoreTv.setText("0");
                    }else {
                        integralN = Integer.parseInt(infos.get(0).get("integral").toString());
                        mSumScoreTv.setText(infos.get(0).get("integral").toString());
                    }
                    mSumTaskTimeTv.setText(infos.get(0).get("times").toString());
                    mSumUpQuestionTv.setText(infos.get(0).get("backNum").toString());
//                    Log.d("zjp","integrals = "+Integer.parseInt(infos.get(0).get("integral").toString()));
                    //设置分数


                    if (integralN<=100){
                        ratingBar.setRating((float) 0.5);
                    }else if(integralN<=200){
                        ratingBar.setRating((float) 1.0);
                    }else if(integralN<=300){
                        ratingBar.setRating((float) 1.5);
                    }else if(integralN<=400){
                        ratingBar.setRating((float) 2.0);
                    }else if(integralN<=500){
                        ratingBar.setRating((float) 2.5);
                    }else if(integralN<=600){
                        ratingBar.setRating((float) 3.0);
                    }else if(integralN<=700){
                        ratingBar.setRating((float) 3.5);
                    }else if(integralN<=800){
                        ratingBar.setRating((float) 4.0);
                    }else if(integralN<=900){
                        ratingBar.setRating((float) 4.5);
                    }else if(integralN<=1000){
                        ratingBar.setRating((float) 5.0);
                    }else{
                        ratingBar.setRating((float) 5.0);
                    }
                    break;
                case HttpRequest.REQUEST_FAILER:
                    Constants.dismissDialog();
                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()),
                            null);
                    break;
                default:
                    break;
            }
        }
    }
}
