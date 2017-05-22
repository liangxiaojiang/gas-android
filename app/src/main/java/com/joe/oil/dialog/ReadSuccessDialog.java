
package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import com.joe.oil.R;
import com.joe.oil.util.Constants;

public class ReadSuccessDialog extends Dialog {
	private Context context;
	
	public ReadSuccessDialog(Context context, final boolean isReadFirst) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_read_success);


		new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					Thread.sleep(2000);
					Log.d("ReadSuccessDialog", Constants.IS_WORKING  + "   " + isReadFirst);
					if(isReadFirst && Constants.IS_WORKING == false){
//						Message msg2 = TaskDetailActivity.secondReadSuccessHandler.obtainMessage();
//						msg2.sendToTarget();
//						dismiss();
					}
					else if (isReadFirst && Constants.IS_WORKING == true) {
						dismiss();

					}
					else if(!isReadFirst){
//						Message msg2 = TaskDetailActivity.secondReadSuccessHandler.obtainMessage();
//						msg2.sendToTarget();
//						dismiss();
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}
	
}
