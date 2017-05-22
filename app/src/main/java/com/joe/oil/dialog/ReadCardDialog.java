
package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.joe.oil.R;

public class ReadCardDialog extends Dialog {

	public ReadCardDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_read_card);
	}
}
