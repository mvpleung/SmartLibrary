package org.smart.library.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.adapter.FragmentViewPagerAdapter;
import org.smart.library.control.AppManager;
import org.smart.library.widget.PagerSlidingTabStrip;
import org.smart.library.control.L;

import java.util.ArrayList;

/**
 * TabIndicator
 * 
 * @author LiangZiChao
 *         created on 2015年6月19日
 */
public class PagerActivity extends FragmentActivity implements View.OnClickListener {

	/* 当前选中项* */
	public static final String CURRENT = "currentTab";
	/** Fragment集合 */
	public final static String TABS = "tabs";
	/** Fragment */
	public final static String TAB = "tab";
	/** 标题 */
	public final static String TITLE = "title";

	public int mCurrentTab = 0;
	protected int mLastTab = -1;

	public TextView mToolBarTitle;
	public PagerSlidingTabStrip mPagerSlidingTabStrip;
	public ViewPager mViewPagerCompat;

	public FragmentViewPagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTheme(android.R.style.Theme_Light_NoTitleBar);
		initUI();
		initData();
	}

	public void initUI() {
		setContentView(R.layout.activity_tab_bar_fragment);
		mViewPagerCompat = (ViewPager) findViewById(R.id.viewpager);
		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_tabs);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			mTintManager.setNavigationBarTintEnabled(true);
			mTintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
		}
		findViewById(R.id.im_title_back).setOnClickListener(this);
		mToolBarTitle = (TextView) findViewById(R.id.tv_title);
	}

	public void initData() {
		Intent intent = getIntent();
		mToolBarTitle.setText(intent.getCharSequenceExtra(TITLE));
		mCurrentTab = intent.getIntExtra(CURRENT, mCurrentTab);
		ArrayList<BaseFragment> mFragments = getFragments();
		if (mFragments != null) {
			mPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPagerCompat, mFragments);
			// 设置viewpager内部页面之间的间距
			// mViewPagerCompat.setPageMargin(getResources().getDimensionPixelSize(R.dimen.page_margin_width));
			// 设置viewpager内部页面间距的drawable
			// mPager.setPageMarginDrawable(R.color.page_viewer_margin_color);
			// mPager.setOffscreenPageLimit(mTabs.size());
			L.i("OffscreenPageLimit:" + mViewPagerCompat.getOffscreenPageLimit());
			// mViewPagerCompat.clearAnimation();
			// mViewPagerCompat.setOnPageChangeListener(this);
			if (mFragments.size() > 0) {
				initTabsValue();
				mPagerSlidingTabStrip.setViewPager(mViewPagerCompat);
			} else {
				mPagerSlidingTabStrip.setVisibility(View.GONE);
			}
		} else {
			BaseFragment mBaseFragment = getFragment();
			if (mBaseFragment != null) {
				if (TextUtils.isEmpty(mToolBarTitle.getText()))
					mToolBarTitle.setText(mBaseFragment.getTitle());
				mPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), mViewPagerCompat, mBaseFragment);
				mPagerSlidingTabStrip.setVisibility(View.GONE);
			}
		}

	}

	/**
	 * mPagerSlidingTabStrip默认值配置
	 * 
	 */
	private void initTabsValue() {
		Resources mResources = getResources();
		// 底部游标颜色
		mPagerSlidingTabStrip.setIndicatorColor(mResources.getColor(R.color.pstsIndicatorColor));
		// tabbar背景
		mPagerSlidingTabStrip.setBackgroundColor(mResources.getColor(R.color.pstsBackgroundColor));
		// tab的分割线颜色
		mPagerSlidingTabStrip.setDividerColor(mResources.getColor(R.color.pstsDividerColor));
		// tab底线高度
		mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
		// 游标高度
		mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
		// 选中的文字颜色
		mPagerSlidingTabStrip.setSelectedTextColor(mResources.getColor(R.color.pstsSelectedTabTextColor));
		// 正常文字颜色
		mPagerSlidingTabStrip.setTextColor(mResources.getColor(R.color.pstsTextColor));
		// Tab充满屏幕
		mPagerSlidingTabStrip.setShouldExpand(true);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// for fix a known issue in support library
		// https://code.google.com/p/android/issues/detail?id=19917
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<BaseFragment> getFragments() {
		return (ArrayList<BaseFragment>) getIntent().getSerializableExtra(TABS);
	}

	public BaseFragment getFragment() {
		BaseFragment mBaseFragment = (BaseFragment) getIntent().getSerializableExtra(TAB);
		if (mBaseFragment != null)
			mBaseFragment.setArguments(getIntent().getExtras());
		return mBaseFragment;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.im_title_back) {
			finish();
		}
	}

	/**
	 * 得到当前Fragment
	 * 
	 * @return
	 */
	public BaseFragment getCurrentFragment() {
		return mPagerAdapter.getCurrentFragment();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			getCurrentFragment().onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			L.e(e.getMessage(), e);
		}
	}
}
