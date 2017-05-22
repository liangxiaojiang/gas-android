/**
 *LanbaooBottom
 *11/20/14 8:23 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

import com.joe.oil.R;

import java.util.regex.Pattern;

/**
 * User: demo wuchangqi@meet-future.com Date: 11/20/14 Time: 8:23 AM
 */
public class LanbaooBottom extends RelativeLayout {

	public TextView midText;
	private TextView mLeftBtn;
	private TextView mRightBtn;

	public LanbaooBottom(Context context, Object mTopLeftObj, Object text, Object mTopRightObj) {
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.bottombar, this);

		// int height = (int)
		// context.getResources().getDimension(R.dimen.demin_title_height);
		// setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));

		// GradientDrawable bgShape = new GradientDrawable();
		// bgShape.setColor(Color.parseColor("#06B4F9"));
		// bgShape.setAlpha(99);
		// setBackgroundDrawable(bgShape);

		mLeftBtn = (TextView) view.findViewById(R.id.bottomLeft);
		mRightBtn = (TextView) view.findViewById(R.id.bottomRight);
		midText = (TextView) view.findViewById(R.id.bottomMid);

		mLeftBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		mRightBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
		midText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

		setFocusable(true);

		if (mTopLeftObj != null) {
			addLeftButton(mTopLeftObj);
		}
		if (text != null) {
			addTitle(text);
		}
		if (mTopRightObj != null) {
			addRightButon(mTopRightObj);
		}

	}

	public TextView getmLeftBtn() {
		return mLeftBtn;
	}

	public TextView getmRightBtn() {
		return mRightBtn;
	}

	public TextView getMidText() {
		return midText;
	}

	private void addRightButon(Object mTopRightObj) {

		if (mTopRightObj instanceof StateListDrawable) {
			mRightBtn.setCompoundDrawablesWithIntrinsicBounds((StateListDrawable) mTopRightObj, null, null, null);
		}

		if (mTopRightObj instanceof String) {
			mRightBtn.setText((String) mTopRightObj);
		} else if (mTopRightObj instanceof CharSequence) {
			mRightBtn.setText((CharSequence) mTopRightObj);
		}

		if (mTopRightObj instanceof Integer) {
			if (isRes(getContext().getString((Integer) mTopRightObj))) {
				mRightBtn.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable((Integer) mTopRightObj), null, null, null);
			} else {
				mRightBtn.setText((Integer) mTopRightObj);
				mRightBtn.setBackgroundColor(Color.TRANSPARENT);
				mRightBtn.setTextColor(Color.parseColor("#5FA863"));
				// mRightBtn.setTextSize (TypedValue.COMPLEX_UNIT_SP,20);
			}
		}
	}

	private void addLeftButton(Object mTopRightObj) {

		if (mTopRightObj instanceof StateListDrawable) {
			mLeftBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, (StateListDrawable) mTopRightObj, null);
		}

		if (mTopRightObj instanceof String) {
			mLeftBtn.setText((String) mTopRightObj);
		} else if (mTopRightObj instanceof CharSequence) {
			mLeftBtn.setText((CharSequence) mTopRightObj);
		}

		if (mTopRightObj instanceof Integer) {
			if (isRes(getContext().getString((Integer) mTopRightObj))) {
				mLeftBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable((Integer) mTopRightObj), null);
			} else {
				mLeftBtn.setText((Integer) mTopRightObj);
				mLeftBtn.setBackgroundColor(Color.TRANSPARENT);
				mLeftBtn.setTextColor(Color.parseColor("#5FA863"));
				// mLeftBtn.setTextSize (TypedValue.COMPLEX_UNIT_SP,20);
			}
		}
	}

	private void addTitle(Object text) {
		if (text instanceof Integer) {
			midText.setText((Integer) text);
		}
		if (text instanceof String) {
			midText.setText((String) text);
		} else if (text instanceof CharSequence) {
			midText.setText((CharSequence) text);
		}
	}

	protected boolean isRes(String str) {
		Pattern pattern = Pattern.compile(".*/.*");
		return pattern.matcher(str).matches();
	}

	private void addTitle(String text) {
		midText.setText(text);
	}

	private void addTitle(int text) {
		midText.setText(text);
	}

	public void onTitleClicked(OnClickListener onClickListener) {
		if (midText != null) {
			midText.setOnClickListener(onClickListener);
		}
	}

	public void onLeftClicked(OnClickListener onClickListener) {
		if (mLeftBtn != null) {
			mLeftBtn.setOnClickListener(onClickListener);
		}
	}

	public void onRightClicked(OnClickListener onClickListener) {
		if (mRightBtn != null) {
			mRightBtn.setOnClickListener(onClickListener);
		}
	}

	public void onRightLongClicked(OnLongClickListener onLongClickListener) {
		if (mRightBtn != null) {
			mRightBtn.setOnLongClickListener(onLongClickListener);
		}
	}

	public void setText(String text) {
		midText.setText(text);
		invalidate();
	}

	public void setText(int text) {
		midText.setText(text);
		invalidate();
	}
}
