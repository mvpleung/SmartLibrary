package com.exiaobai.library.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.exiaobai.library.R;
import com.lidroid.xutils.util.LogUtils;

/**
 * Created with IntelliJ IDEA. Author: wangjie email:tiantian.china.2@gmail.com
 * Updated Liangzichao Date: 13-10-10 Time: 上午9:25
 */
public class FragmentTabAdapter implements RadioGroup.OnCheckedChangeListener {

	private ArrayList<Fragment> fragments; // 一个tab页面对应一个Fragment
	private RadioGroup rgs; // 用于切换tab
	private FragmentManager mFragmentManager;
	private int fragmentContentId; // Activity中所要被替换的区域的id

	private Fragment mPreFragment, mCurrentFragment;

	private int mCurrentCheckedId;
	private int preTab; // 上一个tab索引
	private int currentTab; // 当前Tab页面索引

	private OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener; // 用于让调用者在切换tab时候增加新的功能

	private boolean isNeedAnimation; // 是否需要动画

	/**
	 * 
	 * @param fragmentActivity
	 * @param fragments
	 * @param fragmentContentId
	 * @param rgs
	 * @param currentTab
	 *            当前页卡
	 */
	public FragmentTabAdapter(FragmentManager mFragmentManager, ArrayList<Fragment> fragments, int fragmentContentId, int currentTab, boolean isNeedAnimation) {
		init(mFragmentManager, fragments, fragmentContentId, null, currentTab, isNeedAnimation);
	}

	/**
	 * 
	 * @param fragmentActivity
	 * @param fragments
	 * @param fragmentContentId
	 * @param rgs
	 * @param currentTab
	 *            当前页卡
	 * @param isNeedAnimation
	 *            是否需要切换动画
	 */
	public FragmentTabAdapter(FragmentManager mFragmentManager, ArrayList<Fragment> fragments, int fragmentContentId, RadioGroup rgs, int currentTab, boolean isNeedAnimation) {
		init(mFragmentManager, fragments, fragmentContentId, rgs, currentTab, isNeedAnimation);
	}

	public void init(FragmentManager mFragmentManager, ArrayList<Fragment> fragments, int fragmentContentId, RadioGroup rgs, int currentTab, boolean isNeedAnimation) {
		this.fragments = fragments != null ? fragments : new ArrayList<Fragment>();
		this.rgs = rgs;
		this.currentTab = currentTab;
		this.mFragmentManager = mFragmentManager;
		this.fragmentContentId = fragmentContentId;
		this.isNeedAnimation = isNeedAnimation;

		// 默认显示第一页
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.add(fragmentContentId, getCurrentFragment());
		ft.commit();

		if (rgs != null && rgs.getVisibility() == View.VISIBLE) {
			rgs.setOnCheckedChangeListener(this);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
		for (int i = 0; i < rgs.getChildCount(); i++) {
			if (rgs.getChildAt(i).getId() == checkedId && currentTab != i) {
				changeTab(radioGroup, checkedId, i);
			}
		}

	}

	/**
	 * 切换TAB
	 * 
	 * @param radioGroup
	 * @param checkedId
	 * @param i
	 */
	public void changeTab(RadioGroup radioGroup, int checkedId, int i) {
		boolean isChangedEnable = true;
		if (null != onRgsExtraCheckedChangedListener) {
			isChangedEnable = onRgsExtraCheckedChangedListener.onRgsExtraBeforeChanged(radioGroup, checkedId, i);
		}
		if (isChangedEnable) {
			setCurrentTab(i);
			mCurrentCheckedId = checkedId;

			// 如果设置了切换tab额外功能功能接口
			if (null != onRgsExtraCheckedChangedListener) {
				onRgsExtraCheckedChangedListener.OnRgsExtraCheckedChanged(radioGroup, checkedId, i);
			}
		} else {
			if (mCurrentCheckedId == 0) {
				((RadioButton) radioGroup.getChildAt(i)).setChecked(false);
				((RadioButton) radioGroup.getChildAt(currentTab)).setChecked(true);
			} else
				radioGroup.check(mCurrentCheckedId);
			LogUtils.i("currentTab : " + currentTab);
		}

	}

	/**
	 * 切换TAB
	 * 
	 * @param radioGroup
	 * @param checkedId
	 * @param i
	 */
	public void setCurrentTab(int index) {

		getCurrentFragment().onPause(); // 暂停当前tab
		// getCurrentFragment().onStop(); // 暂停当前tab

		preTab = currentTab;
		showTab(index); // 显示目标tab

		Fragment fragment = getCurrentFragment();
		if (fragment.isAdded()) {
			// fragment.onStart(); // 启动目标tab的onStart()
			fragment.onResume(); // 启动目标tab的onResume()
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
			if (!fragment.isAdded()) {
				ft.add(fragmentContentId, fragment);
			}
			if (idx == i) {
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

	public OnRgsExtraCheckedChangedListener getOnRgsExtraCheckedChangedListener() {
		return onRgsExtraCheckedChangedListener;
	}

	public void setOnRgsExtraCheckedChangedListener(OnRgsExtraCheckedChangedListener onRgsExtraCheckedChangedListener) {
		this.onRgsExtraCheckedChangedListener = onRgsExtraCheckedChangedListener;
	}

	/**
	 * 切换tab额外功能功能接口
	 */
	public interface OnRgsExtraCheckedChangedListener {
		public boolean onRgsExtraBeforeChanged(RadioGroup radioGroup, int checkedId, int index);

		public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index);
	}

}
