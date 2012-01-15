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
		View child = getChildAt(0);
		int childMeasureSpec = MeasureSpec.makeMeasureSpec(
				MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
		child.measure(childMeasureSpec, childMeasureSpec);
		int width = resolveSize(child.getMeasuredWidth(), widthMeasureSpec);
		setMeasuredDimension(width, width);
	}
}