package com.joe.oil.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joe.oil.R;
import com.joe.oil.adapter.TaskAdapter;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.http.HttpRequest;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;
import com.joe.oil.util.HttpDownloader;
import com.joe.oil.view.CommentListView;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author baiqiao
 * @description 派工任务列表
 * @data: 2014年7月7日 上午8:55:32
 * @email baiqiao@lanbaoo.com
 */
@SuppressLint({"HandlerLeak", "ShowToast", "SimpleDateFormat"})
public class TaskActivity extends BaseActivity implements OnClickListener, OnItemClickListener {

    private Context context;
    private ImageView back;
    private TextView synchronous;
    //    private TextView upload;
    private TextView history;
    private TextView noTask;
    private CommentListView listView;
    private CommentListView listView1;
    private Intent intent;
    private HttpRequest http;
    private List<Task> tasks;
    private List<Task> taskall, taskWork;
    private List<Task> currentUploadTasks;
    private TaskAdapter taskAdapter;
    private GetTaskHandler getTaskHandler;
    public static Handler refreshHandler;
    private SqliteHelper sqliteHelper;
    private OilApplication application;
    private User user;
    private ProgressDialog taskUploadDialog;
    private int uploadSuccessCount = 0;
    private int uploadFailCount = 0;
    private int currentUpload = 0;
    private int uploadPicCount = 0;
    private Handler uploadHandler;
    private UploadPicHandler uploadPicHandler;
    private Task currentUploadTask;
    private AfterSubmitHandler afterSubmitHandler;
    private List<Task> notUploadTask;
    private String deadTime = "";
    private String title = "";

    private int isFinishing = 0;
    private RelativeLayout rl_zhengzaigongzuo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_task);

        initView();
        initMembers();
        setHandler();

        if (getTaskFromDbFinished() > 0) {
            Constants.showToast(context, "你有已经完成的任务没有提交，请提交");
        }
        /*
        下面是让从数据库更新数据的，让显示在Listview中
         */
//        else {
//
//            http.requestGetTask(getTaskHandler, user.getUserId());
//            Constants.showDialog(context);
//        }
    }


    private void initView() {
        back = (ImageView) this.findViewById(R.id.task_btn_back);
        synchronous = (TextView) this.findViewById(R.id.task_synchronous);
        history = (TextView) this.findViewById(R.id.task_history);
        noTask = (TextView) this.findViewById(R.id.task_no_task);
        listView = (CommentListView) this.findViewById(R.id.task_listview);

        listView1 = (CommentListView) this.findViewById(R.id.task_listview1);

        back.setOnClickListener(this);
        synchronous.setOnClickListener(this);
        history.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setPullLoadEnable(false);
        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Task itemPosition = taskWork.get(i - 1);
                if (itemPosition.getName().contains("甲醇取样")) {
                    intent = new Intent(TaskActivity.this, TaskFinishActivity.class);
                    intent.putExtra("task", itemPosition);
                    intent.putExtra("taskId", itemPosition.getTaskId());
                    intent.putExtra("actId", itemPosition.getActId());
                    intent.putExtra("historyId", itemPosition.getHistoryId());
//                        intent.putExtra("imgUrl", itemPosition.getOperateCardUrl());
                    intent.putExtra("taskName", itemPosition.getName());
                    intent.putExtra("intentFrom", "TaskDetailActivity");
                    startActivity(intent);
                } else {

                    intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                    intent.putExtra("task", taskWork.get(i - 1));
                    intent.putExtra("intentFrom", "task");
                    startActivity(intent);
                }
            }
        });
        listView1.setPullLoadEnable(false);
        rl_zhengzaigongzuo = (RelativeLayout) findViewById(R.id.rl_zhengzaigongzuo);
    }

    private void initMembers() {
        context = TaskActivity.this;
        application = (OilApplication) getApplication();
        user = application.getUser();
        http = HttpRequest.getInstance(context);
        sqliteHelper = new SqliteHelper(context);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        deadTime = format.format(new Date());

        Task task = new Task();
        isFinishing = task.getIsFinished();

        tasks = new ArrayList<Task>();
        taskall = new ArrayList<Task>();
        getTaskHandler = new GetTaskHandler();
        getNotFinishedTaskFromDb();
        getnoItemTaskNotFinish();
        notUploadTask = sqliteHelper.getTaskFinishNotUpload(user.getUserId());
        if (getTaskFromDbFinished() <= 0) {
            http.requestGetTask(getTaskHandler, user.getUserId());
            Constants.showDialog(context);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.task_btn_back:
//                this.finish();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                this.finish();
                Constants.TASK_TYPE = null;
                break;

            case R.id.task_synchronous:
                currentUpload = 0;
                if (getTaskFromDbFinished() > 0) {
                    Constants.showToast(context, "请等待已完成的任务上传后才能同步数据");

                    taskUploadDialog = new ProgressDialog(context);
                    taskUploadDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    taskUploadDialog.setTitle("任务提交");
                    taskUploadDialog.setMessage("数据提交中...");
                    taskUploadDialog.show();
                    currentUploadTasks = sqliteHelper.getTaskHavePic();
                    afterSubmitHandler = new AfterSubmitHandler();

                    uploadPicCount = currentUploadTasks.size();
                    if (uploadPicCount > 0) {
                        uploadPicHandler = new UploadPicHandler();
                        currentUploadTask = currentUploadTasks.get(currentUpload);
                        try {
                            http.requestUploadPic(uploadPicHandler, user.getUserId(), currentUploadTask.getPicUrl(), currentUploadTask.getPicUrl());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        http.requestFinishTaskMuti(afterSubmitHandler, notUploadTask, user);
                    }

                } else {
                    initMembers();
                }
                break;

            case R.id.task_history:
                intent = new Intent(TaskActivity.this, HistoryActivity.class);
                startActivity(intent);
                break;

            default:
                break;
        }
    }

    /**
     * 这是判断是否有正在工作的任务
     */
    private void getgItemTaskNotFinish() {
        taskWork.clear();
        taskWork = sqliteHelper.getItemTaskNotFinish(user.getUserId());
        Collections.sort(taskWork);
        taskAdapter = new TaskAdapter(context, taskWork);
        listView1.setAdapter(taskAdapter);
    }

    private int getTaskFromDbFinished() {
        return sqliteHelper.getTaskFinishNotUpload(user.getUserId()).size();
    }

    private void getTaskFromDbNotFinish() {
        taskall.clear();
        if ((getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "").equals("任务")) {
            taskall = sqliteHelper.getTaskScanner(user.getUserId(), getIntent().getStringExtra("code"));
        } else {
            taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
        }
        Collections.sort(taskall);
        taskAdapter = new TaskAdapter(context, taskall);
        listView.setAdapter(taskAdapter);
    }

    /**
     * 这是判断是否有正在工作的任务，然后做逻辑判断
     */
    private void getNotFinishedTaskFromDb() {
        taskall.clear();
        title = getIntent().getStringExtra("title");
        if (title != null) {
            if (getIntent().getStringExtra("title").equals("任务")) {
                taskall = sqliteHelper.getTaskScanner(user.getUserId(), getIntent().getStringExtra("code"));
            } else {
                taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
            }
        } else {
            taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
        }

        if (taskall.size() == 0) {
            noTask.setVisibility(View.VISIBLE);
        } else {
            noTask.setVisibility(View.GONE);
        }
        Collections.sort(taskall);
        taskAdapter = new TaskAdapter(context, taskall);
        listView.setAdapter(taskAdapter);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(false);
    }

    private void getnoItemTaskNotFinish() {
//        taskWork.clear();
        taskWork = sqliteHelper.getnoItemTask(user.getUserId(), deadTime);
        if (taskWork.size() == 0) {
            listView1.setVisibility(View.GONE);
            rl_zhengzaigongzuo.setVisibility(View.GONE);
        } else {
            listView1.setVisibility(View.VISIBLE);
        }
        Collections.sort(taskWork);
        taskAdapter = new TaskAdapter(context, taskWork);
        listView1.setAdapter(taskAdapter);
    }

    @SuppressLint("HandlerLeak")
    private void setHandler() {
        refreshHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                getNotFinishedTaskFromDb();
                getnoItemTaskNotFinish();
            }
        };

        uploadHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 200:
                        taskUploadDialog.dismiss();
                        Toast.makeText(context, "上传成功" + uploadSuccessCount + "个任务，上传失败" + uploadFailCount + "个任务。", Toast.LENGTH_LONG).show();
                        getTaskFromDbNotFinish();
                        getgItemTaskNotFinish();
                        break;

                    case 202:
                        http.requestFinishTaskMuti(afterSubmitHandler, notUploadTask, user);

                        break;

                    case 203:
                        taskUploadDialog.dismiss();
                        Toast.makeText(context, "上传失败，请检查网络", Toast.LENGTH_LONG).show();
                        break;


                    default:
                        break;
                }

            }
        };
    }

    @SuppressLint("HandlerLeak")
    private class GetTaskHandler extends Handler {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    Constants.dismissDialog();
                    tasks = (List<Task>) msg.obj;
                    taskall.clear();
                    if (tasks.size() == 10) {
                        listView.setPullLoadEnable(true);
                    } else {
                        listView.setPullLoadEnable(false);
                    }
                    taskall.addAll(tasks);
                    if (taskall.size() == 0) {
                        noTask.setVisibility(View.VISIBLE);
                        Constants.showToast(context, "没有任务数据");
                    } else {
                        noTask.setVisibility(View.GONE);
                        Constants.showToast(context, "任务数据同步成功");
                    }

                    sqliteHelper.insert(taskall);

                    for (int i = 0; i < tasks.size(); i++) {
                        if (tasks.get(i).getDictDetails().size() != 0) {
                            sqliteHelper.insertTemplate(tasks.get(i).getDictDetails());
                        }

                        final HttpDownloader httpDownloader= new HttpDownloader();
                        //获得SD卡路径
                        final String sdpath = Environment.getExternalStorageDirectory()+"/Download/"+tasks.get(i).getTaskTypeName()+"/";
                        final String str =tasks.get(i).getDocumentUrl();
                        final String strImager=tasks.get(i).getOperateCardUrl();
                        final String downloaderName=tasks.get(i).getDocumentName();
                        final String imageNmae=tasks.get(i).getTaskTypeName();
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!str.equals("null")) {
                                            String result = httpDownloader.download1(str, sdpath, downloaderName);
                                            Log.d("文档下载", "" + result );
                                        }
                                        if (!strImager.equals("null")) {
                                            String result1 = httpDownloader.download1(strImager, sdpath, imageNmae + "操作卡.jpg");
                                            Log.d("图片下载", "" + result1);

                                        }
                                    }
                                }
                        ).start();

                    }

                    if ((getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "").equals("任务")) {
                        taskall = sqliteHelper.getTaskScanner(user.getUserId(), getIntent().getStringExtra("code"));
                    } else {
                        taskall = sqliteHelper.getTaskNotFinish(user.getUserId(), deadTime);
                    }
                    Collections.sort(taskall);
                    taskAdapter = new TaskAdapter(context, taskall);
                    taskAdapter.notifyDataSetChanged();
                    listView.setAdapter(taskAdapter);
//                    taskAdapter.notifyDataSetChanged();
                    Intent intent = new Intent(Constants.STATION_PLAN);
                    sendBroadcast(intent);
                    break;

                case HttpRequest.REQUEST_FAILER:
                    Constants.dismissDialog();
                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
                    break;


                default:
                    break;
            }
        }
    }



    private class UploadPicHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_SUCCESS:
                    uploadSuccessCount++;//上传成功计数
                    currentUpload++;//当前上传
                    currentUploadTask.setPics(msg.obj.toString());
                    sqliteHelper.updateTaskFinishState(currentUploadTask);
                    if (currentUpload < uploadPicCount) {
                        currentUploadTask = currentUploadTasks.get(currentUpload);
                        try {
                            http.requestUploadPic(uploadPicHandler, user.getUserId(), currentUploadTask.getPicUrl(), currentUploadTask.getPicUrl());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (uploadSuccessCount + uploadFailCount == currentUploadTasks.size()) {
                        Log.d("NewTaskActivity", "uploadFailCount:  " + uploadFailCount);
                        uploadHandler.sendEmptyMessage(202);
                    }
                    break;

                case HttpRequest.REQUEST_FAILER:

                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
                    uploadFailCount++;
                    currentUpload++;
                    if (currentUpload < uploadPicCount) {
                        currentUploadTask = currentUploadTasks.get(currentUpload);
                        try {
                            http.requestUploadPic(uploadPicHandler, user.getUserId(), currentUploadTask.getPicUrl(), currentUploadTask.getPicUrl());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    if (uploadSuccessCount + uploadFailCount == currentUploadTasks.size()) {
                        Log.d("NewTaskActivity", "uploadFailCount:  " + uploadFailCount);
                        uploadHandler.sendEmptyMessage(203);
                    }
                    break;

                default:
                    break;
            }
        }

    }

    private class AfterSubmitHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HttpRequest.REQUEST_FAILER:
                    taskUploadDialog.dismiss();
                    HttpRequest.badRequest(Integer.parseInt(msg.obj.toString()), null);
                    break;

                case HttpRequest.REQUEST_SUCCESS:
                    taskUploadDialog.dismiss();
                    Constants.showToast(context, "上传成功！");
                    for (int i = 0; i < notUploadTask.size(); i++) {
                        notUploadTask.get(i).setIsFinished(2);
                    }
                    sqliteHelper.updateTasksFinishState(notUploadTask);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        application = (OilApplication) getApplicationContext();
        Task itemPosition = taskall.get(position - 1);
//        Constants.showToast(context,"++++"+itemPosition.getVehicleNumber());
        if (itemPosition.getName().contains("甲醇取样")) {
            if (taskWork.size() > 0) {
                Constants.showToast(context, "有未完成的工作");
            } else {
                intent = new Intent(TaskActivity.this, TaskFinishActivity.class);
                intent.putExtra("task", itemPosition);
                intent.putExtra("taskId", itemPosition.getTaskId());
                intent.putExtra("actId", itemPosition.getActId());
                intent.putExtra("historyId", itemPosition.getHistoryId());
                intent.putExtra("taskName", itemPosition.getName());
//                intent.putExtra("imgUrl", itemPosition.getOperateCardUrl());
                intent.putExtra("intentFrom", "TaskDetailActivity");
                startActivity(intent);
            }
        }
//        else if (itemPosition.getName().contains("甲醇注入")){
//            if (!itemPosition.getVehicleNumber().equals("")&&!sqliteHelper.getAreaByNum(itemPosition.getVehicleNumber()).equals("")){
//                intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
//                intent.putExtra("task", itemPosition);
//                intent.putExtra("intentFrom", "task");
//                startActivity(intent);
//            }else {
//                Constants.showToast(context, "请联系工作人员分配沙卡车辆");
//            }
//        }
        else if (itemPosition.getName().contains("甲醇装车")) {
            if (sqliteHelper.getTankBy() != 0) {
                intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
                intent.putExtra("task", itemPosition);
                intent.putExtra("intentFrom", "task");
                startActivity(intent);
            } else {
                Constants.showToast(context, "请联系工作人员设置罐车体积");
            }
        } else {
            intent = new Intent(TaskActivity.this, TaskDetailActivity.class);
            intent.putExtra("task", itemPosition);
            intent.putExtra("intentFrom", "task");
            startActivity(intent);
        }
    }


}
