/**
 *GalleryPickItem
 *1/13/14 9:07 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joe.oil.R;

/**
 * User: demo wuchangqi@meet-future.com Date: 1/13/14 Time: 9:07 AM
 */
public class GalleryPickItem extends RelativeLayout {
	private final TextView mCheck;
	private LanbaooImageView shadeView;
	private LanbaooImageView mImageView;

	public GalleryPickItem(Context context) {
		super(context);
		setGravity(Gravity.CENTER);
		mImageView = new LanbaooImageView(getContext());
		mImageView.setAdjustViewBounds(true);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		mCheck = new TextView(getContext());
		// mCheck.setBackgroundDrawable (LanbaooHelper.LanbaooCheckDrawableList
		// (getContext (), R.drawable.checked_tv,0));
		// mCheck.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.pictures_select_icon));
		mCheck.setBackgroundResource(R.drawable.pictures_select_icon);
		int pad = getResources().getDimensionPixelSize(R.dimen.checkbox_padding);
		mCheck.setPadding(pad, pad, pad, pad);
		shadeView = new LanbaooImageView(getContext());

		int dp_150 = (int) getResources().getDimension(R.dimen.dp_150);
		LayoutParams mImageViewRLP = new LayoutParams(dp_150, dp_150);
		mImageViewRLP.addRule(RelativeLayout.CENTER_IN_PARENT);
		mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
		addView(mImageView, mImageViewRLP);

		shadeView.setBackgroundResource(R.drawable.iconfont_login_shade);
		shadeView.setVisibility(View.GONE);
		addView(shadeView, mImageViewRLP);

		LayoutParams mCheckRLP = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		mCheckRLP.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		mCheckRLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		mCheckRLP.topMargin = (int) pad;
		mCheckRLP.rightMargin = (int) pad;
		addView(mCheck, mCheckRLP);
		// setPadding (LanbaooHelper.px2dip (20),LanbaooHelper.px2dip
		// (20),LanbaooHelper.px2dip (20),LanbaooHelper.px2dip (20));
	}

	public LanbaooImageView getmImageView() {
		return mImageView;
	}

	public TextView getmCheck() {
		return mCheck;
	}

	public LanbaooImageView getShadeView() {
		return shadeView;
	}
}
