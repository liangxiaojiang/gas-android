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
import com.joe.oil.entity.Gis;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.List;

public class GisExceptionPreviewDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tvCurLat;
    private TextView tvCurLng;
    private TextView tvMessageDescription;
    private TextView tvFillTime;
    private TextView tvOk;
    private TextView tvPreviewPic;
    private SqliteHelper sqliteHelper;
    private Gis gis;

    private OnDialogOkListener listener;

    public GisExceptionPreviewDialog(Context context, Gis gis) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_gis_exception_preview);
        this.context = context;
        this.gis = gis;

        initView();
        initMembers();
        setData();
    }

    public void setOnDialogOkListener(OnDialogOkListener listener) {
        this.listener = listener;
    }

    private void initView() {

        tvCurLat = (TextView) this.findViewById(R.id.tv_cur_lat);
        tvCurLng = (TextView) this.findViewById(R.id.tv_cur_lng);
        tvMessageDescription = (TextView) this.findViewById(R.id.tv_message_description);
        tvFillTime = (TextView) this.findViewById(R.id.tv_fill_time);
        tvOk = (TextView) this.findViewById(R.id.tv_ok);
        tvPreviewPic = (TextView) this.findViewById(R.id.tv_preview_pic);

        tvPreviewPic.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }

    private void initMembers() {
        sqliteHelper = new SqliteHelper(context);
    }

    private void setData() {
        tvCurLat.setText(gis.getLatitude());
        tvCurLng.setText(gis.getLongitude());
        tvMessageDescription.setText(gis.getMemo());
        tvFillTime.setText(gis.getTime());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                if (listener != null) {
                    listener.onDialogOk();
                }
                dismiss();
                break;

            case R.id.tv_preview_pic:
                List<ImageBean> imgData = sqliteHelper.getLocalPics(gis.getTime());
                if (imgData != null && imgData.size() > 0) {
                    Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
                    ImageGroup imageGroup = new ImageGroup("ALL", imgData);
                    Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
                    intent.putExtra("intentFrom", 5);
                    intent.putExtra("typeOfId", gis.getTime());
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

    public interface OnDialogOkListener {
        void onDialogOk();
    }
}
