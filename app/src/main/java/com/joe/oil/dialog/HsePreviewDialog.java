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
import com.joe.oil.entity.UploadHseSupervision;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.List;

public class HsePreviewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tvCheckedPoint;
    private TextView tvIssues;
    private TextView tvSuggestion;
    private TextView tvTime;
    private TextView confirm;
    private TextView photo;
    private UploadHseSupervision uploadHseSupervision;
    private SqliteHelper sqliteHelper;

    private OnDialogConfirmListener listener;

    public HsePreviewDialog(Context context, UploadHseSupervision uploadHseSupervision) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_hse_preview);
        this.context = context;
        this.uploadHseSupervision = uploadHseSupervision;

        initView();
        initMembers();
        setData();
    }

    public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
        this.listener = listener;
    }

    private void initView() {
        tvCheckedPoint = (TextView) this.findViewById(R.id.tv_checked_point);
        tvIssues = (TextView) this.findViewById(R.id.tv_issues);
        tvSuggestion = (TextView) this.findViewById(R.id.tv_suggestion);
        tvTime = (TextView) this.findViewById(R.id.tv_time);
        confirm = (TextView) this.findViewById(R.id.dialog_history_preview_confirm);
        photo = (TextView) this.findViewById(R.id.dialog_history_preview_photo);

        confirm.setOnClickListener(this);
        photo.setOnClickListener(this);
    }

    private void initMembers() {
        sqliteHelper = new SqliteHelper(context);
    }

    private void setData() {
        tvSuggestion.setText(uploadHseSupervision.getSuggestion());
        tvCheckedPoint.setText(uploadHseSupervision.getCheckedPoint());
        tvTime.setText(uploadHseSupervision.getCreatedDate());
        tvIssues.setText(uploadHseSupervision.getIssue());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_history_preview_confirm:
                if (listener != null) {
                    listener.onDialogConfirm();
                }
                dismiss();
                break;

            case R.id.dialog_history_preview_photo:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(uploadHseSupervision.getCreatedDate());
                if (imgData != null && imgData.size() > 0) {
                    Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 5);
                    intent.putExtra("typeOfId", uploadHseSupervision.getCreatedDate());
                    intent.putExtra("imageSelected", imageGroup);
                    context.startActivity(intent);
                } else {
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
