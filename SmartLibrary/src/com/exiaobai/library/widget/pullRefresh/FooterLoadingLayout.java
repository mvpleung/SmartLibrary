package com.exiaobai.library.widget.pullRefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.exiaobai.library.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @since 2013-7-30
 */
public class FooterLoadingLayout extends LoadingLayout {
	/** 进度条 */
	private ProgressBar mProgressBar;
	/** 显示的文本 */
	private TextView mHintView;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public FooterLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public FooterLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
		mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);

		setState(State.RESET);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(R.layout.pull_to_load_footer, this, false);
		return container;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
	}

	@Override
	public int getContentSize() {
		View view = findViewById(R.id.pull_to_load_footer_content);
		if (null != view) {
			return view.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 40);
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mProgressBar.setVisibility(View.GONE);
		mHintView.setVisibility(View.INVISIBLE);

		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mHintView.setText(R.string.pull_is_loading);
	}

	@Override
	protected void onPullToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_up_to_load);
	}

	@Override
	protected void onReleaseToRefresh() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_load_release_text);
	}

	@Override
	protected void onRefreshing() {
		mProgressBar.setVisibility(View.VISIBLE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_is_loading);
	}

	@Override
	protected void onNoMoreData() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_no_more_msg);
	}

	@Override
	protected void onFail() {
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_fail_msg);
	}

	public void setFooterLable(CharSequence lable) {
		mHintView.setText(lable);
	}
	
	@Override
	public void setNoMoreLabel(CharSequence label) {
		mHintView.setText(label);
	}
}
