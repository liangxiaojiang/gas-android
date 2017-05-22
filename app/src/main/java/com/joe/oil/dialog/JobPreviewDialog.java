package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.OilApplication;
import com.joe.oil.adapter.FinishAdapter;
import com.joe.oil.entity.Picture;
import com.joe.oil.entity.Task;
import com.joe.oil.entity.User;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liangxiaojiang on 2016/8/1.
 */
public class JobPreviewDialog extends Dialog implements View.OnClickListener,Serializable
//        ,JobTaskFinishListener
{


    private Context context;
    private TextView happening;//工作完成情况
    private TextView recording;//工作记录
    private TextView remark;//备注
    private TextView photo;
    private TextView confirm;

    private ImageGroup imageGroup;
    private ImageView back;//后退
    private ImageView read;

    private SqliteHelper sqliteHelper;
    private Task task;

    private OnDialogConfirmListener listener;
    private Picture pic=new Picture();
    private GridView gvPics;
    private User user;
    public static int job;
    private List<ImageBean> imageBeens;
    private FinishAdapter finishAdapter;
    private OilApplication oilApplication = new OilApplication();

    public JobPreviewDialog(Context context,Task task) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_job_preview);
        this.context = context;
        this.task=task;


        initView();
        initMembers();

        setData();
    }

    public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
        this.listener = listener;
    }
    private void initView(){
        happening= (TextView) this.findViewById(R.id.dialog_job_preview_happening);
        recording= (TextView) this.findViewById(R.id.dialog_job_preview_recording);
        remark= (TextView) this.findViewById(R.id.dialog_job_preview_remark);
        back= (ImageView) this.findViewById(R.id.task_btn_back);
        read= (ImageView) this.findViewById(R.id.task_detail_iv_read_card);

        gvPics= (GridView) this.findViewById(R.id.finish_add_picture);

        back.setOnClickListener(this);
        read.setOnClickListener(this);
    }
    private void initMembers(){
        sqliteHelper = new SqliteHelper(context);


    }
    private void setData(){
        happening.setText(task.getCompletion());
        recording.setText(task.getWorkRecord());
        remark.setText(task.getMemo());
//        pic.getUrl();
        imageBeens=sqliteHelper.getLocalPics(pic.getUrl());
        finishAdapter=new FinishAdapter(context,imageBeens);
        gvPics.setAdapter(finishAdapter);
        finishAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.task_btn_back:
                dismiss();

                break;
            case R.id.task_detail_iv_read_card:
                job=1;
                if (listener != null) {
                    listener.onDialogConfirm();
                }

                dismiss();

                break;
//           case R.id.dialog_job_preview_photo:
//                //HistoryId必须要在实体类让可以调用到
//               List<ImageBean> imgData= sqliteHelper.getLocalPics(task.getHistoryId());
//
//                if (imgData != null && imgData.size() > 0) {
//                   Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
//                   ImageGroup imageGroup = new ImageGroup("ALL", imgData);
//                   Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
//                   intent.putExtra("intentFrom", 5);
//                   intent.putExtra("typeOfId", task.getHistoryId());
//                   intent.putExtra("imageSelected", imageGroup);
//                   context.startActivity(intent);
//               }
//               else {
//                   Constants.showToast(context, "您没有选择照片！");
//               }
//           break;
            default:
                break;
        }


//            ImageGroup newImageGroup = (ImageGroup) data.getSerializableExtra("imageSelected");
//            if (newImageGroup != null && newImageGroup.getImageSets().size() > 0) {
//                imageSets.clear();
//                imageGroup = newImageGroup;
//                List<ImageBean> newData = imageGroup.getImageSets();
//                for (int i = 0; i < newData.size(); i++) {
//                    imageSets.add(newData.get(i));
//                }
//                finishAdapter.notifyDataSetChanged();
//            }
//        }

    }



    public interface OnDialogConfirmListener {
            void onDialogConfirm();
    }


}