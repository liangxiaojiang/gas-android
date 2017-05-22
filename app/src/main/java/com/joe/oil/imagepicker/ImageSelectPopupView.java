/**
 *ImageSelectPopup
 *11/20/14 1:04 PM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * User: demo wuchangqi@meet-future.com Date: 11/20/14 Time: 1:04 PM
 */
public class ImageSelectPopupView extends RelativeLayout {
	private final ListView mListView;

	public ImageSelectPopupView(Context context) {
		super(context);
		setGravity(Gravity.NO_GRAVITY);
		setBackgroundColor(Color.parseColor("#FFFFFF"));
		// int screenHeight = getResources().getDisplayMetrics().heightPixels;
		// LayoutParams mLayoutParams = new LayoutParams
		// (ViewGroup.LayoutParams.MATCH_PARENT, screenHeight/2);
		// setLayoutParams (mLayoutParams);

		mListView = new ListView(context);
		LayoutParams mListViewRLP = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		mListViewRLP.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		addView(mListView, mListViewRLP);
		// int screenWidth = getResources().getDisplayMetrics().widthPixels;
	}

	public ListView getmListView() {
		return mListView;
	}
}
