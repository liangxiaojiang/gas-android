package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.joe.oil.R;
import com.joe.oil.activity.TaskFinishActivity;

public class TaskConfirmDialog extends Dialog implements android.view.View.OnClickListener{
	
	private TextView confirm;
	private TextView cancel;
	private Context context;

	public TaskConfirmDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_task_confirm);
		this.context = context;
		initView();

	}

//


	private void initView(){
		confirm = (TextView) this.findViewById(R.id.dialog_task_confirm_confirm);
		cancel = (TextView) this.findViewById(R.id.dialog_task_confirm_cancel);
		
		confirm.setOnClickListener(this);
		cancel.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dialog_task_confirm_cancel:
			dismiss();
			break;
			
		case R.id.dialog_task_confirm_confirm:
//

			Message msg = TaskFinishActivity.finishHandler.obtainMessage();

			msg.arg1=3;
			msg.sendToTarget();
			dismiss();


			break;

		default:
			break;
		}

	}

//

}
