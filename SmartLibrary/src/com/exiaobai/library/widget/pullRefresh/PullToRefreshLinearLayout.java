package com.exiaobai.library.widget.pullRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class PullToRefreshLinearLayout extends PullToRefreshBase<LinearLayout> {

	public PullToRefreshLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public PullToRefreshLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected boolean isReadyForPullDown() {
		if (mRefreshableView.getChildCount() > 0 && mRefreshableView.getChildAt(0) instanceof ScrollView) {
			ScrollView mScrollView = (ScrollView) mRefreshableView.getChildAt(0);
			return mScrollView.getScrollY() == 0;
		}
		return mRefreshableView.getScrollY() == 0;
	}

	@Override
	protected boolean isReadyForPullUp() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			if (scrollViewChild instanceof ScrollView) {
				ScrollView mScrollView = (ScrollView) scrollViewChild;
				scrollViewChild = mScrollView.getChildAt(0);
				if (null != scrollViewChild)
					return mScrollView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
			}
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}

		return false;
	}

	@SuppressLint("NewApi")
	@Override
	protected LinearLayout createRefreshableView(Context context, AttributeSet attrs) {
		LinearLayout linearLayout = new LinearLayout(context, attrs);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			linearLayout.setFitsSystemWindows(true);
		return linearLayout;
	}

}
