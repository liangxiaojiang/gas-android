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
import com.joe.oil.entity.UploadException;
import com.joe.oil.imagepicker.ImageBean;
import com.joe.oil.imagepicker.ImageGroup;
import com.joe.oil.sqlite.SqliteHelper;
import com.joe.oil.util.Constants;

import java.util.List;

public class ExceptionPreviewDialog extends Dialog implements android.view.View.OnClickListener{
	
	private Context context;
	private TextView point;
	private TextView type;
	private TextView description;
	private TextView time;
	private TextView confirm;
	private TextView photo;
	private UploadException exception;
	private SqliteHelper sqliteHelper;

	private OnDialogConfirmListener listener;

	public ExceptionPreviewDialog(Context context, UploadException exception) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_exception_preview);
		this.context = context;
		this.exception = exception;
		
		initView();
		initMembers();
		setData();
	}

	public void setOnDialogConfirmListener(OnDialogConfirmListener listener) {
		this.listener = listener;
	}

	private void initView(){
		point = (TextView) this.findViewById(R.id.dialog_history_preview_point);
		type = (TextView) this.findViewById(R.id.dialog_history_preview_type);
		description = (TextView) this.findViewById(R.id.dialog_history_preview_description);
		time = (TextView) this.findViewById(R.id.dialog_history_preview_time);
		confirm = (TextView) this.findViewById(R.id.dialog_history_preview_confirm);
		photo = (TextView) this.findViewById(R.id.dialog_history_preview_photo);
		
		confirm.setOnClickListener(this);
		photo.setOnClickListener(this);
	}
	
	private void initMembers(){
		sqliteHelper = new SqliteHelper(context);
	}
	
	private void setData(){
		point.setText(exception.getPointName()+"_"+exception.getDeviceName());
		type.setText(exception.getWorkTypeName());
		description.setText(exception.getDescription());
		time.setText(exception.getTime());
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
			List<ImageBean> imgData = sqliteHelper.getLocalPics(exception.getTime());
			if (imgData != null && imgData.size() > 0) {
				Log.d("Image Select Flag", "imgData.size(): " + imgData.size());
				ImageGroup imageGroup = new ImageGroup("ALL", imgData);
				Intent intent = new Intent(context, PicSelectedEnsureActivity.class);
				intent.putExtra("intentFrom", 5);
				intent.putExtra("typeOfId", exception.getTime());
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
