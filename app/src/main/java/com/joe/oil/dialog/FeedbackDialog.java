package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.PicSelectedEnsureActivity;
import com.joe.oil.entity.FeedBack;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.List;

/**
 * Created by liangxiaojiang on 2017/5/8.
 */

public class FeedbackDialog extends Dialog implements View.OnClickListener{

    private Context context;
    private TextView point;//标题
    private TextView type;
    private TextView description;//意见描述
    private TextView time;//填写时间
    private TextView confirm;//确定
    private TextView photo;
    private FeedBack feedBack;
    private SqliteHelper sqliteHelper;

    private OnDialogConfirmListener listener;

    public FeedbackDialog(Context context, FeedBack feedBack) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_feedback);
        this.context = context;
        this.feedBack=feedBack;
        initView();
        initMembers();
        setData();
    }

    public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
        this.listener = listener;
    }

    private void initView(){
        point = (TextView) this.findViewById(R.id.dialog_feed_preview_point);
        description = (TextView) this.findViewById(R.id.dialog_history_preview_description);
        time = (TextView) this.findViewById(R.id.dialog_feed_preview_time);
        confirm = (TextView) this.findViewById(R.id.dialog_feed_preview_confirm);
        photo = (TextView) this.findViewById(R.id.dialog_feed_preview_photo);

        confirm.setOnClickListener(this);
        photo.setOnClickListener(this);
    }

    private void initMembers(){
        sqliteHelper = new SqliteHelper(context);
    }

    private void setData(){
        point.setText(feedBack.getTitle());
        description.setText(feedBack.getDescription());
        time.setText(feedBack.getTime());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_feed_preview_confirm:
                if (listener != null) {
                    listener.onDialogConfirm();
                }
                dismiss();
                break;

            case R.id.dialog_feed_preview_photo:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(feedBack.getTime());
                if (imgData != null && imgData.size() > 0) {
                    Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 5);
                    intent.putExtra("typeOfId", feedBack.getTime());
                    intent.putExtra("imageSelected", imageGroup);
                    context.startActivity(intent);
                }
                else {
                    Constants.showToast(context, "您没有选择照片！");
                }
                break;

            default:
                break;
        }
    }
    public interface OnDialogConfirmListener {
        void onDialogConfirm();
    }
}
