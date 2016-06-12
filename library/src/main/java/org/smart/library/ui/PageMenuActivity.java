package org.smart.library.ui;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.adapter.ChoiceAdapter;
import org.smart.library.control.AppManager;
import org.smart.library.tools.FragmentProxy;
import org.smart.library.tools.ImageUtils;
import org.smart.library.tools.JudgmentLegal;
import org.smart.library.tools.UITools;
import org.smart.library.widget.PagerSlidingTabStrip;
import org.smart.library.control.L;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TitleMenu（切换Fragment,不包含ViewPager）
 * 
 * @author LiangZiChao
 *         created on 2015年7月13日
 */
public class PageMenuActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

	/** LayoutResId */
	public final static String CONTENT_RESOURCE_LAYOUT = "contentViewResourceLayout";

	/** 显示PagerSlidingTabTrip */
	public final static String SHOW_PAGER_TAB_TRIP = "showPagerSlidingTabTrip";

	/** Fragments */
	public final static String TABS = "tabs";

	/** Fragment */
	public final static String TAB = "tab";
	/** 标题 */
	public final static String TITLE = "title";

	/** Class */
	public final static String FRAGMENT_CLASSES = "fragmentClasses";

	/** TabArrays */
	public final static String PAGER_TAB_ARRAY = "pagerTabArray";

	/** TabMenus */
	public final static String TAB_MENUS = "tabMenus";

	public TextView mToolBarTitle;
	public PagerSlidingTabStrip mPagerSlidingTabStrip;

	/** DownMenu */
	private boolean showDownMenu;
	private Drawable mUpDrawable, mDownDrawable;
	private ListView mMenuListView = null;
	private PopupWindow mMenuWindow = null;
	@SuppressWarnings("rawtypes")
	private ChoiceAdapter mChoiceAdapter;

	public int mCurrentIndex; // 当前点击的页面
	public ArrayList<Fragment> mFragments;
	public FragmentProxy mFragmentProxy;

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
		Intent mIntent = getIntent();
		boolean showPagerTabTrip = mIntent.getBooleanExtra(SHOW_PAGER_TAB_TRIP, false);
		int mResourceLayout = mIntent.getIntExtra(CONTENT_RESOURCE_LAYOUT, showPagerTabTrip ? R.layout.activity_pager_bar_framelayout : R.layout.activity_toolbar_framelayout);
		setContentView(getContentViewResource() == 0 ? mResourceLayout : getContentViewResource());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			mTintManager.setNavigationBarTintEnabled(true);
			mTintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
		}
		findViewById(R.id.im_title_back).setOnClickListener(this);
		mToolBarTitle = (TextView) findViewById(R.id.tv_title);
		mToolBarTitle.setText(getIntent().getCharSequenceExtra(TITLE));
		if (showPagerTabTrip) {
			mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.pager_tabs);
			configPagerTabTrip();
			String[] pagerTabArray = mIntent.getStringArrayExtra(PAGER_TAB_ARRAY);
			if (pagerTabArray != null)
				mPagerSlidingTabStrip.addTextTabs(pagerTabArray);
		}
		String[] tabMenus = mIntent.getStringArrayExtra(TAB_MENUS);
		if (tabMenus != null && tabMenus.length > 0) {
			if (tabMenus.length > 1) {
				addToolBarDownMenu(tabMenus, tabMenus[0]);
			} else {
				mToolBarTitle.setText(tabMenus[0]);
			}
		}
		try {
			getWindow().addFlags(WindowManager.LayoutParams.class.getField("FLAG_NEEDS_MENU_KEY").getInt(null));
		} catch (NoSuchFieldException e) {
			// Ignore since this field won't exist in most versions of Android
		} catch (IllegalAccessException e) {
			L.w("Could not access FLAG_NEEDS_MENU_KEY in addLegacyOverflowButton()", e);
		}
		Class<?>[] classes = getFragmentClass();
		if (classes == null && mIntent.hasExtra(FRAGMENT_CLASSES)) {
			classes = (Class<?>[]) mIntent.getSerializableExtra(FRAGMENT_CLASSES);
		}
		if (classes != null) {
			try {
				int length = classes.length;
				for (int i = 0; i < length; i++) {
					if (mFragments == null)
						mFragments = new ArrayList<Fragment>();
					mFragments.add(i, createFragment(classes[i]));
				}
			} catch (Exception e) {
				L.e(e.getMessage(), e);
			}
		} else {
			mFragments = getFragments();
		}

		if (mFragments == null) {
			BaseFragment mBaseFragment = getFragment();
			if (mBaseFragment != null) {
				if (TextUtils.isEmpty(mToolBarTitle.getText()))
					mToolBarTitle.setText(mBaseFragment.getTitle());
				mFragments = new ArrayList<Fragment>();
				mFragments.add(mBaseFragment);
			}
		}
		if (mFragments != null)
			mFragmentProxy = new FragmentProxy(getSupportFragmentManager(), mFragments, R.id.content, mCurrentIndex, false);
	}

	public void initData() {

	}

	/**
	 * mPagerSlidingTabStrip默认值配置
	 * 
	 * @param resId
	 *            PagerSlidingTabStrip资源ID
	 */
	public void initTabsValue(int resId) {
		if (mPagerSlidingTabStrip == null && resId > 0)
			mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(resId);
		configPagerTabTrip();
	}

	/**
	 * mPagerSlidingTabStrip默认值配置
	 * 
	 */
	public void configPagerTabTrip() {
		if (mPagerSlidingTabStrip != null) {
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
			mPagerSlidingTabStrip.setOnPageChangeListener(this);
		}
	}

	public <T> void onToolBarMenuClick(int position, T item) {
		setCurrentTab(position);
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

	/**
	 * 切换当前Tab
	 * 
	 * @param index
	 */
	public void setCurrentTab(int index) {
		if (mCurrentIndex != index) {
			mCurrentIndex = index;
			mFragmentProxy.setCurrentTab(index);
		}
	}

	/**
	 * 得到当前Fragment
	 * 
	 * @return
	 */
	public BaseFragment getCurrentFragment() {
		return (BaseFragment) mFragmentProxy.getCurrentFragment();
	}

	public BaseFragment getPreFragment() {
		return (BaseFragment) mFragmentProxy.getPreFragment();
	}

	public int getPreTab() {
		return mFragmentProxy.getPreTab();
	}

	/**
	 * 是否是当前Fragment
	 * 
	 * @param baseFragment
	 * @return
	 */
	public boolean isCurrentFragment(Fragment baseFragment) {
		try {
			return getCurrentFragment().equals(baseFragment);
		} catch (Exception e) {
			L.e(e.getMessage(), e);
			return false;
		}
	}

	public BaseFragment createFragment(Class<?> cls) throws Exception {
		return (BaseFragment) cls.newInstance();
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

	/**
	 * 获取要添加的Fragment
	 * 
	 * @return
	 */
	public Class<?>[] getFragmentClass() {
		return null;
	}

	/**
	 * 获取ContentView
	 * 
	 * @return
	 */
	public int getContentViewResource() {
		return 0;
	}

	/**
	 * 标题菜单是否可见
	 * 
	 * @param showDownMenu
	 */
	public void setToolBarDownMenuVisibility(boolean showDownMenu) {
		this.showDownMenu = showDownMenu;
		if (!showDownMenu) {
			dimissMenuWindow();
			mToolBarTitle.setCompoundDrawables(null, null, null, null);
		} else {
			mToolBarTitle.setCompoundDrawables(null, null, mDownDrawable, null);
		}
	}

	/**
	 * 添加标题栏下拉菜单
	 * 
	 * @param <T>
	 * @param <T>
	 */
	public <T> void addToolBarDownMenu(T[] items, T current) {
		if (items != null)
			addToolBarDownMenu(Arrays.asList(items), current);
	}

	/**
	 * 添加标题栏下拉菜单
	 * 
	 * @param <T>
	 * @param currentIndex
	 */
	public <T> void addToolBarDownMenu(List<T> items, int currentIndex) {
		int fullSize = items != null ? items.size() : 0;
		if (fullSize > 0) {
			addToolBarDownMenu(items, fullSize > currentIndex ? items.get(currentIndex) : null);
		} else {
			setToolBarDownMenuVisibility(false);
		}
	}

	/**
	 * 添加标题栏下拉菜单
	 * 
	 * @param <T>
	 * @param <T>
	 */
	@SuppressWarnings("unchecked")
	public <T> void addToolBarDownMenu(List<T> items, T current) {
		if (JudgmentLegal.isListFull(items) && mToolBarTitle != null) {
			if (mUpDrawable == null && mDownDrawable == null) {
				mUpDrawable = getResources().getDrawable(R.drawable.arrow);
				int bound = getResources().getDimensionPixelSize(R.dimen.small_arrow_img_width);
				mUpDrawable = ImageUtils.rotate(mUpDrawable, 90);
				mDownDrawable = ImageUtils.rotate(mUpDrawable, 180);
				mUpDrawable.setBounds(0, 0, bound, bound);
				mDownDrawable.setBounds(0, 0, bound, bound);
				mToolBarTitle.setCompoundDrawablePadding(bound);
			}
			mToolBarTitle.setText((CharSequence) current);
			mToolBarTitle.setOnClickListener(this);
			mToolBarTitle.setCompoundDrawables(null, null, mDownDrawable, null);
			if (mMenuWindow == null || mMenuListView == null || mChoiceAdapter == null) {
				mMenuListView = UITools.createCustomListView(this);
				mChoiceAdapter = new ChoiceAdapter<T>(this, items, current);
				mChoiceAdapter.setListView(mMenuListView);
				mMenuListView.setAdapter(mChoiceAdapter);
				mMenuListView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						// TODO Auto-generated method stub
						dimissMenuWindow();
						mChoiceAdapter.setCurrentItems(mChoiceAdapter.getItem(position));
						onToolBarMenuClick(position, mChoiceAdapter.getItem(position));
					}
				});
				mMenuListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				LinearLayout layout = new LinearLayout(this);
				layout.setBackgroundColor(Color.BLACK);
				layout.getBackground().setAlpha(100);
				layout.addView(mMenuListView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dimissMenuWindow();
					}
				});
				mMenuWindow = UITools.createCustomPopupWindow(layout);
				ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
				colorDrawable.setAlpha(150);
				mMenuWindow.setBackgroundDrawable(colorDrawable);
				mMenuWindow.update(); // 更新窗口的状态
				mMenuWindow.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						mToolBarTitle.setCompoundDrawables(null, null, mDownDrawable, null);
					}
				});
			} else {
				mChoiceAdapter.replaceAll(items, current);
			}
			showDownMenu = true;
		} else {
			setToolBarDownMenuVisibility(false);
		}
	}

	/**
	 * 添加标题栏下拉菜单
	 * 
	 * @param <T>
	 * @param mChoiceAdapter
	 */
	public <T> ListView addToolBarDownMenu(List<T> items, ChoiceAdapter<T> mChoiceAdapter) {
		if (items != null && mToolBarTitle != null) {
			if (mUpDrawable == null && mDownDrawable == null) {
				mUpDrawable = getResources().getDrawable(R.drawable.arrow);
				int bound = getResources().getDimensionPixelSize(R.dimen.small_arrow_img_width);
				mUpDrawable = ImageUtils.rotate(mUpDrawable, 90);
				mDownDrawable = ImageUtils.rotate(mUpDrawable, 180);
				mUpDrawable.setBounds(0, 0, bound, bound);
				mDownDrawable.setBounds(0, 0, bound, bound);
				mToolBarTitle.setCompoundDrawablePadding(bound);
			}
			mToolBarTitle.setOnClickListener(this);
			mToolBarTitle.setCompoundDrawables(null, null, mDownDrawable, null);
			if (mMenuWindow == null || mMenuListView == null) {
				mMenuListView = UITools.createCustomListView(this);
				mMenuListView.setAdapter(mChoiceAdapter);
				mMenuListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				LinearLayout layout = new LinearLayout(this);
				layout.setBackgroundColor(Color.BLACK);
				layout.getBackground().setAlpha(100);
				layout.addView(mMenuListView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				layout.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dimissMenuWindow();
					}
				});
				mMenuWindow = UITools.createCustomPopupWindow(layout);
				ColorDrawable colorDrawable = new ColorDrawable(Color.BLACK);
				colorDrawable.setAlpha(150);
				mMenuWindow.setBackgroundDrawable(colorDrawable);
				mMenuWindow.update(); // 更新窗口的状态
				mMenuWindow.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						mToolBarTitle.setCompoundDrawables(null, null, mDownDrawable, null);
					}
				});
			} else {
				mMenuListView.setAdapter(mChoiceAdapter);
			}
			mChoiceAdapter.setListView(mMenuListView);
			showDownMenu = true;
		}
		return mMenuListView;
	}

	/**
	 * 关闭菜单
	 */
	public void dimissMenuWindow() {
		if (mMenuWindow != null && mMenuWindow.isShowing())
			mMenuWindow.dismiss();
	}

	/**
	 * 显示菜单
	 */
	public void showMenuWindow() {
		if (mMenuWindow != null && !mMenuWindow.isShowing()) {
			mMenuWindow.showAsDropDown(mToolBarTitle);
			mToolBarTitle.setCompoundDrawables(null, null, mUpDrawable, null);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.im_title_back) {
			finish();
		} else if (id == R.id.tv_title) {
			if (showDownMenu)
				showMenuWindow();
		}
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Fragment> getFragments() {
		return (ArrayList<Fragment>) getIntent().getSerializableExtra(TABS);
	}

	public BaseFragment getFragment() {
		BaseFragment mBaseFragment = (BaseFragment) getIntent().getSerializableExtra(TAB);
		if (mBaseFragment != null)
			mBaseFragment.setArguments(getIntent().getExtras());
		return mBaseFragment;
	}
}
