/**
 *ImageGroupItem
 *11/20/14 11:42 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;

/**
 * User: demo wuchangqi@meet-future.com Date: 11/20/14 Time: 11:42 AM
 */
public class ImageGroupItem extends LanbaooRelativeLayout {
	private final ImageView mImageView;
	private final TextView mCheck;
	private final TextView mDirName;

	public ImageGroupItem(Context context) {
		super(context);
		setGravity(Gravity.CENTER);
		// LayoutParams mImageViewRLP = new LayoutParams
		// (ViewGroup.LayoutParams.WRAP_CONTENT,
		// ViewGroup.LayoutParams.WRAP_CONTENT);
		// int screenWidth = getResources().getDisplayMetrics().widthPixels;

		int default_padding = getResources().getDimensionPixelSize(R.dimen.demin_default_padding);
		setPadding(default_padding, default_padding, default_padding, default_padding);

		mImageView = new ImageView(getContext());
		mImageView.setId(888);
		mImageView.setAdjustViewBounds(true);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		int imagesize = getResources().getDimensionPixelSize(R.dimen.image_group_size);
		LayoutParams mImageViewRLP = new LayoutParams(imagesize, imagesize);
		mImageViewRLP.addRule(RelativeLayout.CENTER_VERTICAL);
		mImageViewRLP.rightMargin = imagesize / 10;
		mImageViewRLP.leftMargin = imagesize / 10;
		addView(mImageView, mImageViewRLP);

		mDirName = new TextView(context);
		LayoutParams mDirNameRLP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mDirNameRLP.addRule(RelativeLayout.CENTER_VERTICAL);
		mDirNameRLP.addRule(RelativeLayout.RIGHT_OF, mImageView.getId());
		mDirNameRLP.leftMargin = imagesize / 10;
		mDirName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		mDirName.setLineSpacing(4f, 1f);
		addView(mDirName, mDirNameRLP);

		mCheck = new TextView(getContext());
		// mCheck.setBackgroundDrawable (LanbaooHelper.LanbaooCheckDrawableList
		// (getContext (), R.drawable.checked_tv,0));
		mCheck.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.pictures_select_icon2));
		int pad = getResources().getDimensionPixelSize(R.dimen.checkbox_padding);
		mCheck.setPadding(pad, 0, pad, 0);
		LayoutParams mCheckRLP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mCheckRLP.addRule(RelativeLayout.CENTER_VERTICAL);
		mCheckRLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mCheckRLP.rightMargin = pad;
		addView(mCheck, mCheckRLP);
	}

	public ImageView getmImageView() {
		return mImageView;
	}

	public TextView getmCheck() {
		return mCheck;
	}

	public TextView getmDirName() {
		return mDirName;
	}
}
