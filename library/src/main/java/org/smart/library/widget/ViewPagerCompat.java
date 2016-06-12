package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Scroller;

import org.smart.library.control.L;

import java.lang.reflect.Field;

/**
 * Tab ViewPager
 * 
 *         created on LiangZC Updated by 2014-8-3下午11:01:26
 */
@SuppressLint("ClickableViewAccessibility")
public class ViewPagerCompat extends ViewPager {

	// mViewTouchMode表示ViewPager是否全权控制滑动事件，默认为false，即不控制
	private boolean mViewTouchMode = false;
	private boolean mScrollEnable = true;// 是否可以滑动

	private ViewGroup viewGroup;
	private GestureDetector mGestureDetector;

	private static final int MOVE_LIMITATION = 100;// 触发移动的像素距离
	private float mLastMotionX; // 手指触碰屏幕的最后一次x坐标

	private Scroller mScroller; // 滑动控件

	public ViewPagerCompat(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ViewPagerCompat(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	/**
	 * 初始化
	 */
	private void init(Context context) {
		mScroller = new Scroller(context);
		mGestureDetector = new GestureDetector(getContext(), new YScrollDetector());
	}

	/**
	 * The Parent
	 * 
	 * @param viewGroup
	 */
	public void setViewGroup(ViewGroup viewGroup) {
		this.viewGroup = viewGroup;
	}

	public void setViewTouchMode(boolean b) {
		if (b && !isFakeDragging()) {
			// 全权控制滑动事件
			beginFakeDrag();
		} else if (!b && isFakeDragging()) {
			// 终止控制滑动事件
			endFakeDrag();
		}
		mViewTouchMode = b;
	}

	/**
	 * 在mViewTouchMode为true的时候，ViewPager不拦截点击事件，点击事件将由子View处理
	 * 用于拦截手势事件的，每个手势事件都会先调用这个方法。Layout里的onInterceptTouchEvent默认返回值是false,
	 * 这样touch事件会传递到childview控件 ，如果返回false子控件可以响应，否则了控件不响应，这里主要是拦截子控件的响应，
	 * 对ViewGroup不管返回值是什么都会执行onTouchEvent
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mViewTouchMode) {
			return false;
		}
		if (mScrollEnable) {
			final float x = ev.getX();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				/*
				 * If being flinged and user touches, stop the fling. isFinished
				 * will be false if being flinged.
				 */
				if (viewGroup != null) {
					viewGroup.requestDisallowInterceptTouchEvent(!mGestureDetector.onTouchEvent(ev));
				}
				mLastMotionX = x;
				break;

			case MotionEvent.ACTION_MOVE:
				if (viewGroup != null) {
					viewGroup.requestDisallowInterceptTouchEvent(!mGestureDetector.onTouchEvent(ev));
				}
				break;

			case MotionEvent.ACTION_UP:
				if (viewGroup != null) {
					viewGroup.requestDisallowInterceptTouchEvent(false);
				}
				break;
			case MotionEvent.ACTION_CANCEL:
				if (viewGroup != null) {
					viewGroup.requestDisallowInterceptTouchEvent(false);
				}
			}
			return super.onInterceptTouchEvent(ev);
		}
		return false;
	}

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		super.computeScroll();

		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			invalidate();
		}

	}

	/**
	 * 根据滑动的距离判断移动到第几个视图
	 */
	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	/**
	 * 滚动到制定的视图
	 * 
	 * @param whichScreen
	 *            视图下标
	 */
	public void snapToScreen(int whichScreen) {
		// whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() -
		// 1));
		if (getScrollX() != (whichScreen * getWidth())) {

			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
			invalidate();
		}
	}

	class YScrollDetector extends SimpleOnGestureListener {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			/**
			 * if we're scrolling more closer to x direction, return false, let
			 * subview to process it
			 */
			return (Math.abs(distanceY) > Math.abs(distanceX));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mScrollEnable)
			try {
				final int action = ev.getAction();
				final float x = ev.getX();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					mLastMotionX = x;
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if (Math.abs(x - mLastMotionX) < MOVE_LIMITATION) {
						// snapToDestination(); // 跳到指定页
						snapToScreen(getCurrentItem());
						return true;
					}
					break;
				default:
					break;
				}
				return super.onTouchEvent(ev);
			} catch (Exception e) {
				return false;
			}
		return false;
	}

	/**
	 * 在mViewTouchMode为true或者滑动方向不是左右的时候，ViewPager将放弃控制点击事件，
	 * 这样做有利于在ViewPager中加入ListView等可以滑动的控件，否则两者之间的滑动将会有冲突
	 */
	@Override
	public boolean arrowScroll(int direction) {
		if (mViewTouchMode)
			return false;
		if (direction != FOCUS_LEFT && direction != FOCUS_RIGHT)
			return false;
		return super.arrowScroll(direction);
	}

	public void setScrollEnable(boolean mScrollEnable) {
		this.mScrollEnable = mScrollEnable;
	}

	@Override
	public void setOffscreenPageLimit(int limit) {
		// TODO Auto-generated method stub
		if (limit == 0) {
			try {
				Field mField = getClass().getDeclaredField("mOffscreenPageLimit");
				mField.set(this, limit);
				mField = getClass().getDeclaredField("DEFAULT_OFFSCREEN_PAGES");
				mField.set(this, limit);
			} catch (Exception e) {
				// TODO: handle exception
				L.e(e.getMessage(), e);
			}
		} else
			super.setOffscreenPageLimit(limit);
	}
}
