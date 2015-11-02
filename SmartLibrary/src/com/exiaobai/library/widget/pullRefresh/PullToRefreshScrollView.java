package com.exiaobai.library.widget.pullRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import com.exiaobai.library.widget.pullRefresh.ILoadingLayout.State;

/**
 * 封装了ScrollView的下拉刷新
 * 
 * @since 2013-8-22
 */
public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshScrollView(Context context) {
		this(context, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public PullToRefreshScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @see com.nj1s.lib.pullrefresh.PullToRefreshBase#createRefreshableView(android.content.Context,
	 *      android.util.AttributeSet)
	 */
	@SuppressLint("NewApi")
	@Override
	protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
		ScrollView scrollView = new ScrollView(context, attrs);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			scrollView.setFitsSystemWindows(true);
		return scrollView;
	}

	/**
	 * @see com.nj1s.lib.pullrefresh.PullToRefreshBase#isReadyForPullDown()
	 */
	@Override
	protected boolean isReadyForPullDown() {
		return mRefreshableView.getScrollY() == 0;
	}

	/**
	 * @see com.nj1s.lib.pullrefresh.PullToRefreshBase#isReadyForPullUp()
	 */
	@Override
	protected boolean isReadyForPullUp() {
		View scrollViewChild = mRefreshableView.getChildAt(0);
		if (null != scrollViewChild) {
			return mRefreshableView.getScrollY() >= (scrollViewChild.getHeight() - getHeight());
		}

		return false;
	}

	/**
	 * 设置是否有更多数据的标志
	 * 
	 * @param hasMoreData
	 *            true表示还有更多的数据，false表示没有更多数据了
	 */
	public void setHasMoreData(boolean hasMoreData) {
		if (!hasMoreData) {
			LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
			if (null != footerLoadingLayout) {
				footerLoadingLayout.setState(State.NO_MORE_DATA);
			}
		} else {
			LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
			if (null != footerLoadingLayout) {
				footerLoadingLayout.setState(State.NONE);
			}
		}
	}
}
