package com.exiaobai.library.tools;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.exiaobai.library.R;

/**
 * Fragment操作类
 * 
 * @author LiangZiChao
 * @Data 2015年7月16日
 * @Package com.exiaobai.library.tools
 */
public class FragmentProxy {

	private ArrayList<Fragment> fragments; // 一个tab页面对应一个Fragment
	private FragmentManager mFragmentManager; //
	private int fragmentContentId; // Activity中所要被替换的区域的id
	private Fragment mCurrentFragment, mPreFragment;
	private int preTab; // 上一个Tab页面索引
	private int currentTab = 0; // 当前Tab页面索引
	private boolean isNeedAnimation; // 是否需要动画
	private OnRgsExtraChangedListener onRgsExtraChangedListener; // 用于让调用者在切换tab时候增加新的功能

	/**
	 * 
	 * @param fragmentActivity
	 * @param fragmentContentId
	 * @param currentTab
	 * @param isNeedAnimation
	 *            是否需要动画
	 */
	public FragmentProxy(FragmentManager mFragmentManager, int fragmentContentId, boolean isNeedAnimation) {
		this.fragments = new ArrayList<Fragment>();
		this.mFragmentManager = mFragmentManager;
		this.fragmentContentId = fragmentContentId;
		this.isNeedAnimation = isNeedAnimation;
	}

	/**
	 * 
	 * @param fragmentActivity
	 * @param fragmentContentId
	 * @param currentTab
	 * @param isNeedAnimation
	 *            是否需要动画
	 */
	public FragmentProxy(FragmentManager mFragmentManager, Fragment fragment, int fragmentContentId, boolean isNeedAnimation) {
		this.fragments = new ArrayList<Fragment>();
		this.fragments.add(currentTab, fragment);
		this.mFragmentManager = mFragmentManager;
		this.fragmentContentId = fragmentContentId;
		this.isNeedAnimation = isNeedAnimation;

		// 默认显示第一页
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.add(fragmentContentId, getCurrentFragment());
		ft.commit();
	}

	public FragmentProxy(FragmentManager mFragmentManager, ArrayList<Fragment> fragments, int fragmentContentId, int currentTab, boolean isNeedAnimation) {
		this.fragments = fragments;
		this.currentTab = currentTab;
		this.mFragmentManager = mFragmentManager;
		this.fragmentContentId = fragmentContentId;
		this.isNeedAnimation = isNeedAnimation;

		// 默认显示第一页
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.add(fragmentContentId, getCurrentFragment());
		ft.commit();
	}

	public void addFragment(Fragment mFragment) {
		addFragment(fragments.size(), mFragment);
	}

	public void addFragment(int position, Fragment mFragment) {
		if (fragments == null)
			fragments = new ArrayList<Fragment>();
		fragments = new ArrayList<Fragment>();
		fragments.add(position, mFragment);
	}

	/**
	 * 切换TAB
	 * 
	 * @param radioGroup
	 * @param checkedId
	 * @param i
	 */
	public void setCurrentTab(int index) {
		boolean isChangedEnable = true;
		if (null != onRgsExtraChangedListener) {
			isChangedEnable = onRgsExtraChangedListener.onRgsExtraBeforeChanged(index);
		}
		if (isChangedEnable) {
			preTab = currentTab;
			getCurrentFragment().onPause(); // 暂停当前tab
			// getCurrentFragment().onStop(); // 暂停当前tab

			showTab(index); // 显示目标tab
			Fragment fragment = getCurrentFragment();
			if (fragment.isAdded()) {
				// fragment.onStart(); // 启动目标tab的onStart()
				fragment.onResume(); // 启动目标tab的onResume()
			}
			// 如果设置了切换tab额外功能功能接口
			if (null != onRgsExtraChangedListener)
				onRgsExtraChangedListener.OnRgsExtraChanged(currentTab);
		}
	}

	/**
	 * 显示目标tab
	 * 
	 * @param idx
	 */
	private void showTab(int idx) {
		for (int i = 0; i < fragments.size(); i++) {
			Fragment fragment = fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(idx);
			if (idx == i) {
				if (!fragment.isAdded()) {
					ft.add(fragmentContentId, fragment);
				}
				mCurrentFragment = fragment;
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}
		currentTab = idx; // 更新目标tab为当前tab
	}

	/**
	 * 获取一个带动画的FragmentTransaction
	 * 
	 * @param index
	 * @return
	 */
	private FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		if (isNeedAnimation)
			// 设置切换动画
			if (index > currentTab) {
				ft.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
			} else {
				ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_right_out);
			}
		return ft;
	}

	/**
	 * 得到上一个tab
	 * 
	 * @return
	 */
	public int getPreTab() {
		return preTab;
	}

	public int getCurrentTab() {
		return currentTab;
	}

	public Fragment getPreFragment() {
		if (mPreFragment == null)
			mPreFragment = fragments.get(preTab);
		return mPreFragment;
	}

	public Fragment getCurrentFragment() {
		if (mCurrentFragment == null)
			mCurrentFragment = fragments.get(currentTab);
		return mCurrentFragment;
	}

	public OnRgsExtraChangedListener getOnRgsExtraCheckedChangedListener() {
		return onRgsExtraChangedListener;
	}

	public void setOnRgsExtraCheckedChangedListener(OnRgsExtraChangedListener onRgsExtraChangedListener) {
		this.onRgsExtraChangedListener = onRgsExtraChangedListener;
	}

	/**
	 * 切换tab额外功能功能接口
	 */
	public interface OnRgsExtraChangedListener {
		public boolean onRgsExtraBeforeChanged(int index);

		public void OnRgsExtraChanged(int index);
	}

}
