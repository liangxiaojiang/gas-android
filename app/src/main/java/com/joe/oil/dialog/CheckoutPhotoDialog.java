package com.joe.oil.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Window;

import com.joe.oil.R;
import com.joe.oil.imagehandle.ZoomImageView;

public class CheckoutPhotoDialog extends Dialog {
	
	private ZoomImageView img;

	public CheckoutPhotoDialog(Context context) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_check_out_photo);
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_img);
		
		img = (ZoomImageView) this.findViewById(R.id.dialog_check_out_img);
		img.setImageBitmap(bitmap);
	}

}
