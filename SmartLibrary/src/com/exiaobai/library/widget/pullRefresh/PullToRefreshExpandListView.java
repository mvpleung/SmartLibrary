package com.exiaobai.library.widget.pullRefresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.ExpandableListView;

import com.exiaobai.library.R;
import com.exiaobai.library.tools.BitmapHelper;
import com.exiaobai.library.widget.ExpandListView;
import com.exiaobai.library.widget.pullRefresh.ILoadingLayout.State;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

/**
 * 这个类实现了ListView下拉刷新，上加载更多和滑到底部自动加载
 * 
 * @author Li Hong
 * @since 2013-8-15
 */
public class PullToRefreshExpandListView extends PullToRefreshBase<ExpandableListView> implements OnScrollListener {

	/** ListView */
	private ExpandListView mListView;
	/** 用于滑到底部自动加载的Footer */
	private LoadingLayout mLoadMoreFooterLayout;
	/** 滚动的监听器 */
	private OnScrollListener mScrollListener;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public PullToRefreshExpandListView(Context context) {
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
	public PullToRefreshExpandListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPullLoadEnabled(false);
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected ExpandableListView createRefreshableView(Context context, AttributeSet attrs) {
		mListView = new ExpandListView(context);
		mListView.setBackgroundColor(Color.TRANSPARENT);
		mListView.setCacheColorHint(Color.TRANSPARENT);
		mListView.setSelector(R.drawable.listview_gray_selector);
		mListView.setGroupIndicator(null);
		mListView.setFooterDividersEnabled(false);
		mListView.setDivider(new BitmapDrawable());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			mListView.setFitsSystemWindows(true);
		mListView.setOnScrollListener(new PauseOnScrollListener(BitmapHelper.getInstance().getImageLoader(), false, true, this));

		return mListView;
	}

	/**
	 * 设置是否有更多数据的标志
	 * 
	 * @param hasMoreData
	 *            true表示还有更多的数据，false表示没有更多数据了
	 */
	public void setHasMoreData(boolean hasMoreData) {
		if (!hasMoreData) {
			if (null != mLoadMoreFooterLayout) {
				mLoadMoreFooterLayout.setState(State.NO_MORE_DATA);
			}

			LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
			if (null != footerLoadingLayout) {
				footerLoadingLayout.setState(State.NO_MORE_DATA);
			}
		} else {
			if (null != mLoadMoreFooterLayout) {
				mLoadMoreFooterLayout.setState(State.NONE);
			}

			LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
			if (null != footerLoadingLayout) {
				footerLoadingLayout.setState(State.NONE);
			}
		}
	}

	/**
	 * 设置加载更多状态
	 * 
	 */
	public void setFooterState(State state) {
		onPullUpRefreshComplete();
		if (null != mLoadMoreFooterLayout) {
			mLoadMoreFooterLayout.setState(state);
		}

		LoadingLayout footerLoadingLayout = getFooterLoadingLayout();
		if (null != footerLoadingLayout) {
			footerLoadingLayout.setState(state);
		}
	}

	/**
	 * 设置滑动的监听器
	 * 
	 * @param l
	 *            监听器
	 */
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	protected boolean isReadyForPullUp() {
		return isLastItemVisible();
	}

	@Override
	protected boolean isReadyForPullDown() {
		return isFirstItemVisible();
	}

	@Override
	protected void startLoading() {
		super.startLoading();

		if (null != mLoadMoreFooterLayout) {
			mLoadMoreFooterLayout.setState(State.REFRESHING);
		}
	}

	@Override
	public void onPullUpRefreshComplete(CharSequence lastUpdate) {
		super.onPullUpRefreshComplete(lastUpdate);

		if (null != mLoadMoreFooterLayout) {
			mLoadMoreFooterLayout.setState(State.RESET);
		}
	}

	@Override
	public void setScrollLoadEnabled(boolean scrollLoadEnabled) {
		if (isScrollLoadEnabled() == scrollLoadEnabled) {
			return;
		}

		super.setScrollLoadEnabled(scrollLoadEnabled);

		if (scrollLoadEnabled) {
			// 设置Footer
			if (null == mLoadMoreFooterLayout) {
				mLoadMoreFooterLayout = new FooterLoadingLayout(getContext());
				mListView.addFooterView(mLoadMoreFooterLayout, null, false);
			}

			mLoadMoreFooterLayout.show(true);
		} else {
			if (null != mLoadMoreFooterLayout) {
				mLoadMoreFooterLayout.show(false);
			}
		}
	}

	@Override
	public LoadingLayout getFooterLoadingLayout() {
		if (isScrollLoadEnabled()) {
			return mLoadMoreFooterLayout;
		}

		return super.getFooterLoadingLayout();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (isScrollLoadEnabled() && hasMoreData()) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE || scrollState == OnScrollListener.SCROLL_STATE_FLING) {
				if (isReadyForPullUp()) {
					startLoading();
				}
			}
		}

		if (null != mScrollListener) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (null != mScrollListener) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	/**
	 * 表示是否还有更多数据
	 * 
	 * @return true表示还有更多数据
	 */
	public boolean hasMoreData() {
		if ((null != mLoadMoreFooterLayout) && (mLoadMoreFooterLayout.getState() == State.NO_MORE_DATA)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断第一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isFirstItemVisible() {
		final Adapter adapter = mListView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			return true;
		}

		int mostTop = (mListView.getChildCount() > 0) ? mListView.getChildAt(0).getTop() : 0;
		if (mostTop >= 0) {
			return true;
		}

		return false;
	}

	/**
	 * 判断最后一个child是否完全显示出来
	 * 
	 * @return true完全显示出来，否则false
	 */
	private boolean isLastItemVisible() {
		final Adapter adapter = mListView.getAdapter();

		if (null == adapter || adapter.isEmpty()) {
			return true;
		}

		final int lastItemPosition = adapter.getCount() - 1;
		final int lastVisiblePosition = mListView.getLastVisiblePosition();

		/**
		 * This check should really just be: lastVisiblePosition ==
		 * lastItemPosition, but ListView internally uses a FooterView which
		 * messes the positions up. For me we'll just subtract one to account
		 * for it and rely on the inner condition which checks getBottom().
		 */
		if (lastVisiblePosition >= lastItemPosition - 1) {
			final int childIndex = lastVisiblePosition - mListView.getFirstVisiblePosition();
			final int childCount = mListView.getChildCount();
			final int index = Math.min(childIndex, childCount - 1);
			final View lastVisibleChild = mListView.getChildAt(index);
			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= mListView.getBottom();
			}
		}

		return false;
	}
}
