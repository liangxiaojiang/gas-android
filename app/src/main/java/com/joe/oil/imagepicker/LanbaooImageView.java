/**
 *LanbaooImageView
 *3/2/14 7:56 PM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * User: demo wuchangqi@meet-future.com Date: 3/2/14 Time: 7:56 PM
 */
public class LanbaooImageView extends ImageView {
	private OnMeasureListener onMeasureListener;

	public LanbaooImageView(Context context) {
		super(context);
	}

	public LanbaooImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LanbaooImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setOnMeasureListener(OnMeasureListener onMeasureListener) {
		this.onMeasureListener = onMeasureListener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, widthMeasureSpec);
		if (onMeasureListener != null) {
			onMeasureListener.onMeasureSize(getMeasuredWidth(), getMeasuredHeight());
		}
	}

	public interface OnMeasureListener {
		public void onMeasureSize(int width, int height);
	}

}