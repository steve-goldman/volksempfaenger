package net.x4a42.volksempfaenger.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class SquareView extends ViewGroup {
	/*
	 * Based on
	 * http://stackoverflow.com/questions/6557516/making-grid-view-items
	 * -square/6558022#6558022
	 */

	public SquareView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int l, int u, int r, int d) {
		getChildAt(0).layout(0, 0, r - l, d - u);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		LayoutParams layoutParams = getLayoutParams();
		if (layoutParams.width == LayoutParams.WRAP_CONTENT
				&& layoutParams.height != LayoutParams.WRAP_CONTENT) {
			View child = getChildAt(0);
			int childMeasureSpec = MeasureSpec.makeMeasureSpec(
					MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
			child.measure(childMeasureSpec, childMeasureSpec);
			int width = resolveSize(child.getMeasuredWidth(), widthMeasureSpec);
			setMeasuredDimension(width, width);
		} else if (layoutParams.height == LayoutParams.WRAP_CONTENT
				&& layoutParams.width != LayoutParams.WRAP_CONTENT) {
			View child = getChildAt(0);
			int childMeasureSpec = MeasureSpec
					.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),
							MeasureSpec.EXACTLY);
			child.measure(childMeasureSpec, childMeasureSpec);
			int height = resolveSize(child.getMeasuredWidth(),
					heightMeasureSpec);
			setMeasuredDimension(height, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
}