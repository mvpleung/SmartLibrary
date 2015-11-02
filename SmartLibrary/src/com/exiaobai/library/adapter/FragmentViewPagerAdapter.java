package com.exiaobai.library.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.exiaobai.library.ui.BaseFragment;
import com.exiaobai.library.widget.PagerSlidingTabStrip.IconTabProvider;

/**
 * 为ViewPager添加布局（Fragment），绑定和处理fragments和viewpager之间的逻辑关系
 * 
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Updated Liangzichao Date: 13-10-11 Time: 下午3:03
 */
public class FragmentViewPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, IconTabProvider {
	private List<BaseFragment> fragments; // 每个Fragment对应一个Page
	private FragmentManager fragmentManager;
	private ViewPager viewPager; // viewPager对象
	private int currentPageIndex = 0; // 当前page索引（切换之前）

	private OnExtraPageChangeListener onExtraPageChangeListener; // ViewPager切换页面时的额外功能添加接口

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, BaseFragment fragment) {
		this(fragmentManager, null, fragment);
	}

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager, BaseFragment fragment) {
		this.fragments = new ArrayList<BaseFragment>();
		fragments.add(fragment);
		this.fragmentManager = fragmentManager;
		if (viewPager != null) {
			this.viewPager = viewPager;
			this.viewPager.setAdapter(this);
			this.viewPager.setOnPageChangeListener(this);
		}
	}

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragments) {
		this(fragmentManager, null, fragments);
	}

	public FragmentViewPagerAdapter(FragmentManager fragmentManager, ViewPager viewPager, List<BaseFragment> fragments) {
		this.fragments = fragments;
		this.fragmentManager = fragmentManager;
		if (viewPager != null) {
			this.viewPager = viewPager;
			this.viewPager.setAdapter(this);
			this.viewPager.setOnPageChangeListener(this);
		}
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		return view == o;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(fragments.get(position).getView()); // 移出viewpager两边之外的page布局
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment fragment = getItem(position);
		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraInstantiateItem(position);
		}
		if (!fragment.isAdded()) { // 如果fragment还没有added
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(fragment, fragment.getClass().getSimpleName());
			ft.commit();
			/**
			 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
			 * 会在进程的主线程中，用异步的方式来执行。 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
			 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
			 */
			fragmentManager.executePendingTransactions();
		}

		if (fragment.getView().getParent() == null) {
			container.addView(fragment.getView()); // 为viewpager增加布局
		}

		return fragment.getView();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return fragments.get(position).getTitle();
	}

	public BaseFragment getItem(int position) {
		return fragments.get(position);
	}

	public BaseFragment getCurrentFragment() {
		return getItem(currentPageIndex);
	}

	/**
	 * 当前page索引（切换之前）
	 * 
	 * @return
	 */
	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public OnExtraPageChangeListener getOnExtraPageChangeListener() {
		return onExtraPageChangeListener;
	}

	/**
	 * 设置页面切换额外功能监听器
	 * 
	 * @param onExtraPageChangeListener
	 */
	public void setOnExtraPageChangeListener(OnExtraPageChangeListener onExtraPageChangeListener) {
		this.onExtraPageChangeListener = onExtraPageChangeListener;
	}

	@Override
	public void onPageScrolled(int i, float v, int i2) {
		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageScrolled(i, v, i2);
		}
	}

	@Override
	public void onPageSelected(int i) {
		fragments.get(currentPageIndex).onPause(); // 调用切换前Fargment的onPause()
		// fragments.get(currentPageIndex).onStop(); // 调用切换前Fargment的onStop()
		if (fragments.get(i).isAdded()) {
			// fragments.get(i).onStart(); // 调用切换后Fargment的onStart()
			fragments.get(i).onResume(); // 调用切换后Fargment的onResume()
		}
		currentPageIndex = i;

		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageSelected(i);
		}

	}

	@Override
	public void onPageScrollStateChanged(int i) {
		if (null != onExtraPageChangeListener) { // 如果设置了额外功能接口
			onExtraPageChangeListener.onExtraPageScrollStateChanged(i);
		}
	}

	/**
	 * page切换额外功能接口
	 */
	public static abstract class OnExtraPageChangeListener {
		public abstract void onExtraPageScrolled(int i, float v, int i2);

		public abstract void onExtraPageSelected(int i);

		/**
		 * 
		 * @param i
		 *            <ol style="list-style:none;">
		 *            <li>i ==1的时辰默示正在滑动</li>
		 *            <li>i==2的时辰默示滑动完毕了</li>
		 *            <li>i==0的时辰默示什么都没做。</li>
		 *            </ol>
		 */
		public abstract void onExtraPageScrollStateChanged(int i);

		public void onExtraInstantiateItem(int i) {
		}
	}

	@Override
	public int getPageIconResId(int position) {
		return 0;
	}
}