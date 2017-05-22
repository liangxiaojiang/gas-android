/**
 *LanbaooRelativeLayout
 *1/8/14 9:41 AM
 *demo
 **/
package com.joe.oil.imagepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.widget.RelativeLayout;

/**
 * User: demo wuchangqi@meet-future.com Date: 1/8/14 Time: 9:41 AM
 */
public class LanbaooRelativeLayout extends RelativeLayout {
	private final GradientDrawable bgShape;
	private Paint paint = new Paint();

	public LanbaooRelativeLayout(Context context) {
		super(context);
		bgShape = new GradientDrawable();
		bgShape.setColor(Color.parseColor("#FFFFFF"));
		setBackgroundDrawable(bgShape);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		paint.setColor(Color.parseColor("#D0D0D0"));
		paint.setStrokeWidth(3);
		canvas.drawLine(0, this.getHeight(), this.getWidth(), this.getHeight(), paint);

		super.onDraw(canvas);
	}
}
