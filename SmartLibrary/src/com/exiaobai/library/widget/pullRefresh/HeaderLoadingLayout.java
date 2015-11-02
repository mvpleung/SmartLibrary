package com.exiaobai.library.widget.pullRefresh;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exiaobai.library.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 * @since 2013-7-30
 */
public class HeaderLoadingLayout extends LoadingLayout {
	/** 旋转动画时间 */
	private static final int ROTATE_ANIM_DURATION = 150;
	/** Header的容器 */
	private RelativeLayout mHeaderContainer;
	/** 箭头图片 */
	private ImageView mArrowImageView;
	/** 进度条 */
	private ProgressBar mProgressBar;
	/** 状态提示TextView */
	private TextView mHintTextView;
	/** 最后更新时间的TextView */
	private TextView mHeaderTimeView;
	/** 最后更新时间的标题 */
	private TextView mHeaderTimeViewTitle;
	/** 向上的动画 */
	private Animation mRotateUpAnim;
	/** 向下的动画 */
	private Animation mRotateDownAnim;

	private Context context;

	/** 正在刷新文本 */
	private CharSequence refreshLable;
	/** 释放文本 */
	private CharSequence releaseLabel;
	/** 下拉文本 */
	private CharSequence pullLabel;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public HeaderLoadingLayout(Context context) {
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
	public HeaderLoadingLayout(Context context, AttributeSet attrs) {
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
		this.context = context;
		mHeaderContainer = (RelativeLayout) findViewById(R.id.pull_to_refresh_header_content);
		mArrowImageView = (ImageView) findViewById(R.id.pull_to_refresh_header_arrow);
		mHintTextView = (TextView) findViewById(R.id.pull_to_refresh_header_hint_textview);
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_refresh_header_progressbar);
		mHeaderTimeView = (TextView) findViewById(R.id.pull_to_refresh_header_time);
		mHeaderTimeViewTitle = (TextView) findViewById(R.id.pull_to_refresh_last_update_time_text);

		float pivotValue = 0.5f; // SUPPRESS CHECKSTYLE
		float toDegree = -180f; // SUPPRESS CHECKSTYLE
		// 初始化旋转动画
		mRotateUpAnim = new RotateAnimation(0.0f, toDegree, Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		mRotateDownAnim = new RotateAnimation(toDegree, 0.0f, Animation.RELATIVE_TO_SELF, pivotValue, Animation.RELATIVE_TO_SELF, pivotValue);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
		// 如果最后更新的时间的文本是空的话，隐藏前面的标题
		mHeaderTimeViewTitle.setVisibility(TextUtils.isEmpty(label) ? View.INVISIBLE : View.VISIBLE);
		mHeaderTimeView.setText(label);
	}

	@Override
	public int getContentSize() {
		if (null != mHeaderContainer) {
			return mHeaderContainer.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 60);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View container = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, this, false);
		return container;
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mArrowImageView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);

		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mArrowImageView.clearAnimation();
		mHintTextView.setText(R.string.pull_refresh_down_text);
	}

	@Override
	protected void onPullToRefresh() {
		if (State.RELEASE_TO_REFRESH == getPreState()) {
			mArrowImageView.clearAnimation();
			mArrowImageView.startAnimation(mRotateDownAnim);
		}

		mHintTextView.setText(TextUtils.isEmpty(pullLabel) ? context.getString(R.string.pull_refresh_down_text) : pullLabel);
	}

	@Override
	protected void onReleaseToRefresh() {
		mArrowImageView.clearAnimation();
		mArrowImageView.startAnimation(mRotateUpAnim);
		mHintTextView.setText(TextUtils.isEmpty(releaseLabel) ? context.getString(R.string.pull_refresh_release_text) : releaseLabel);
	}

	@Override
	protected void onRefreshing() {
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		mHintTextView.setText(TextUtils.isEmpty(refreshLable) ? context.getString(R.string.pull_is_loading) : refreshLable);
	}

	@Override
	public void setLoadingDrawable(Drawable drawable) {
		// TODO Auto-generated method stub
		mArrowImageView.setImageDrawable(drawable);
	}

	@Override
	public void setPullLabel(CharSequence pullLabel) {
		// TODO Auto-generated method stub
		this.pullLabel = pullLabel;
	}

	@Override
	public void setRefreshingLabel(CharSequence refreshingLabel) {
		// TODO Auto-generated method stub
		this.refreshLable = refreshingLabel;
	}

	@Override
	public void setReleaseLabel(CharSequence releaseLabel) {
		// TODO Auto-generated method stub
		this.releaseLabel = releaseLabel;
	}

}
