package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.animation.TranslateAnimation;

/**
 * ViewPager边界回弹
 * 
 * @Description TODO
 * @author Liangzc
 * @date 2016年6月7日
 */
public class BounceBackViewPager extends ViewPager {

	private Rect mRect = new Rect();// 用来记录初始位置
	private boolean handleDefault = true;
	private float preX = 0f;
	private static final float RATIO = 0.5f;// 摩擦系数
	private static final float SCROLL_WIDTH = 10f;

	public BounceBackViewPager(Context context) {
		super(context);
	}

	public BounceBackViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			preX = ev.getX();// 记录起点
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_UP:
			onTouchActionUp();
			break;
		case MotionEvent.ACTION_MOVE:
			float nowX = ev.getX();
			float offset = nowX - preX;
			preX = nowX;
			if (getAdapter().getCount() == 1) {
				if (offset > SCROLL_WIDTH) {// 手指滑动的距离大于设定值
					whetherConditionIsRight(offset);
				} else if (offset < -SCROLL_WIDTH) {
					whetherConditionIsRight(offset);
				} else if (!handleDefault) {// 这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
					if (getLeft() + (int) (offset * RATIO) != mRect.left) {
						layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
					}
				}
			} else if ((getCurrentItem() == 0 || getCurrentItem() == getAdapter().getCount() - 1)) {
				if (getCurrentItem() == 0) {
					if (offset > SCROLL_WIDTH) {// 手指滑动的距离大于设定值
						whetherConditionIsRight(offset);
					} else if (!handleDefault) {// 这种情况是已经出现缓冲区域了，手指慢慢恢复的情况
						if (getLeft() + (int) (offset * RATIO) >= mRect.left) {
							layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
						}
					}
				} else {
					if (offset < -SCROLL_WIDTH) {
						whetherConditionIsRight(offset);
					} else if (!handleDefault) {
						if (getRight() + (int) (offset * RATIO) <= mRect.right) {
							layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
						}
					}
				}
			} else {
				handleDefault = true;
			}

			if (!handleDefault) {
				return true;
			}
			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	private void whetherConditionIsRight(float offset) {
		if (mRect.isEmpty()) {
			mRect.set(getLeft(), getTop(), getRight(), getBottom());
		}
		handleDefault = false;
		layout(getLeft() + (int) (offset * RATIO), getTop(), getRight() + (int) (offset * RATIO), getBottom());
	}

	private void onTouchActionUp() {
		if (!mRect.isEmpty()) {
			recoveryPosition();
		}
	}

	private void recoveryPosition() {
		TranslateAnimation ta = new TranslateAnimation(getLeft(), mRect.left, 0, 0);
		ta.setDuration(300);
		startAnimation(ta);
		layout(mRect.left, mRect.top, mRect.right, mRect.bottom);
		mRect.setEmpty();
		handleDefault = true;
	}

}