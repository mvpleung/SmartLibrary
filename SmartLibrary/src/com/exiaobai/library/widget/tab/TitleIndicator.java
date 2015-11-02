package com.exiaobai.library.widget.tab;

import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exiaobai.library.R;
import com.exiaobai.library.model.TabInfo;
import com.exiaobai.library.widget.CustomTextView;
import com.exiaobai.library.widget.tab.IndicatorState.State;
import com.lidroid.xutils.util.LogUtils;

/**
 * 这是个选项卡式的控件，会随着viewpager的滑动而滑动
 * 
 * @description
 * @author
 * @Date LiangZiChao Update By 2014-9-1下午4:20:12
 * @Package com.xiaobai.xbtrip.view.tabs
 */
public class TitleIndicator extends LinearLayout implements View.OnClickListener, OnFocusChangeListener {

	private static final float FLAG_LINE_HEIGHT = 4.0f;

	private static final int FLAG_COLOR = 0xFFFFC445;

	private static final float FLAG_TRIANGLE_HEIGHT = 10;

	private final static int TOP = 0;
	private final static int BOTTOM = 1;
	private final static int LEFT = 2;
	private final static int RIGHT = 3;

	private final static int FOCUS = -1000;
	private final static int UNFOCUS = -2000;
	private final static int MATRIXFOCUS = -3000;
	private final static int MATRIXUNFOCUS = -4000;

	private int mCurrentScroll = 0;

	// 标识位置
	private int flagOrientation;

	// drawableLeft
	private int compoundOrientation;

	// 选项卡列表
	private List<TabInfo> mTabs;

	// 选项卡所依赖的viewpager
	private ViewPager mViewPager;

	// 选项卡普通状态下的字体颜色
	private ColorStateList mTextColor;

	// 普通状态和选中状态下的字体大小
	private float mTextSizeNormal;
	private float mTextSizeSelected;

	private Path mPath = new Path();

	private Paint mPaintFooterLine;

	private Paint mPaintFooterTriangle;

	private float mFlagTriangleHeight;

	// 滚动条的高度
	private float mFlagLineHeight;

	// 当前选项卡的下标，从0开始
	private int mSelectedTab = 0;

	private Context mContext;

	private final int BSSEEID = 0xffff00;;

	private boolean mChangeOnClick = true;

	private int mCurrID = 0;

	// 单个选项卡的宽度
	private int mPerItemWidth = 0;

	// 表示选项卡总共有几个
	private int mTotal = 0;

	private LayoutInflater mInflater;

	private int bound;

	private SparseBooleanArray matrixSparse; // 存储图片方向
	private SparseArray<CustomTextView> textViewSparseArray;
	private SparseArray<Drawable> drawableSparseArray;

	private Resources resource;

	private OnTabChangeListener mOnChangeListener;

	/**
	 * Default constructor
	 */
	public TitleIndicator(Context context) {
		super(context);
		initDraw(FLAG_LINE_HEIGHT, FLAG_COLOR);
	}

	/**
	 * The contructor used with an inflater
	 * 
	 * @param context
	 * @param attrs
	 */
	public TitleIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFocusable(true);
		setOnFocusChangeListener(this);
		mContext = context;
		// Retrieve styles attributs
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleIndicator);
		// Retrieve the colors to be used for this view and apply them.
		int flagColor = a.getColor(R.styleable.TitleIndicator_flagLineColor, FLAG_COLOR);
		mTextColor = a.getColorStateList(R.styleable.TitleIndicator_textColor);
		mTextSizeNormal = a.getDimension(R.styleable.TitleIndicator_textSizeNormal, 0);
		mTextSizeSelected = a.getDimension(R.styleable.TitleIndicator_textSizeSelected, mTextSizeNormal);
		mFlagLineHeight = a.getDimension(R.styleable.TitleIndicator_flagLineHeight, FLAG_LINE_HEIGHT);
		mFlagTriangleHeight = a.getDimension(R.styleable.TitleIndicator_flagLineTriangleHeight, FLAG_TRIANGLE_HEIGHT);
		flagOrientation = a.getInt(R.styleable.TitleIndicator_flagOrientation, BOTTOM);
		compoundOrientation = a.getInt(R.styleable.TitleIndicator_compoundOrientation, LEFT);

		resource = getResources();
		bound = (int) a.getDimension(R.styleable.TitleIndicator_compundBound, resource.getDimension(R.dimen.icon_arrow_width));
		initDraw(mFlagLineHeight, flagColor);
		a.recycle();
	}

	/**
	 * Initialize draw objects
	 */
	private void initDraw(float flagLineHeight, int flagColor) {
		mPaintFooterLine = new Paint();
		mPaintFooterLine.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterLine.setStrokeWidth(flagLineHeight);
		mPaintFooterLine.setColor(flagColor);
		mPaintFooterTriangle = new Paint();
		mPaintFooterTriangle.setStyle(Paint.Style.FILL_AND_STROKE);
		mPaintFooterTriangle.setColor(flagColor);
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	/*
	 * @这个是核心函数，选项卡是用canvas画出来的。所有的invalidate方法均会触发onDraw
	 * 大意是这样的：当页面滚动的时候，会有一个滚动距离，然后onDraw被触发后， 就会在新位置重新画上滚动条（其实就是画线）
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// 下面是计算本次滑动的距离
		float scroll_x = 0;
		if (mSelectedTab >= 0) {
			if (mTotal != 0) {
				mPerItemWidth = getWidth() / mTotal;
				int tabID = mSelectedTab;
				if (mViewPager != null)
					scroll_x = (mCurrentScroll - ((tabID) * (getWidth() + mViewPager.getPageMargin()))) / mTotal;
			} else {
				mPerItemWidth = getWidth();
				scroll_x = mCurrentScroll;
			}
			// 下面就是如何画线了
			Path path = mPath;
			path.rewind();
			float offset = 0;
			float left_x = mSelectedTab * mPerItemWidth + offset + scroll_x;
			float right_x = (mSelectedTab + 1) * mPerItemWidth - offset + scroll_x;
			float top_y = flagOrientation == TOP ? (mFlagLineHeight - mFlagTriangleHeight) : (getHeight() - mFlagLineHeight - mFlagTriangleHeight);
			float bottom_y = flagOrientation == TOP ? mFlagLineHeight : (getHeight() - mFlagLineHeight);

			path.moveTo(left_x, top_y + 1f);
			path.lineTo(right_x, top_y + 1f);
			path.lineTo(right_x, bottom_y + 1f);
			path.lineTo(left_x, bottom_y + 1f);
			path.close();
			canvas.drawPath(path, mPaintFooterTriangle);
		}
	}

	// 当页面滚动的时候，重新绘制滚动条
	public void onScrolled(int h) {
		mCurrentScroll = h;
		invalidate();
	}

	// 当页面切换的时候，重新绘制滚动条
	public synchronized void onSwitched(int position) {
		if (mSelectedTab == position) {
			return;
		}
		setCurrentTab(position);
		invalidate();
	}

	// 初始化选项卡
	public void init(int startPos, List<TabInfo> tabs, ViewPager mViewPager) {
		removeAllViews();
		this.mViewPager = mViewPager;
		this.mTabs = tabs;
		this.mTotal = tabs.size();
		if (startPos <= mTotal) {
			mSelectedTab = startPos;
		}
		for (int i = 0; i < mTotal; i++) {
			add(i, tabs.get(i));
		}
		if (startPos >= 0 && startPos <= mTotal)
			setCurrentTab(startPos);
		invalidate();
	}

	public void updateChildTips(int postion, boolean showTips) {
		View child = getChildAt(postion);
		final ImageView tips = (ImageView) child.findViewById(R.id.tab_title_tips);
		if (showTips) {
			tips.setVisibility(View.VISIBLE);
		} else {
			tips.setVisibility(View.GONE);
		}
	}

	protected void add(int index, TabInfo tabInfo) {
		View tabIndicator;
		if (index < 2) {
			tabIndicator = mInflater.inflate(R.layout.title_flow_indicator, this, false);
		} else {
			tabIndicator = mInflater.inflate(R.layout.title_flow_indicator_v2, this, false);
		}
		final CustomTextView tv = (CustomTextView) tabIndicator.findViewById(R.id.tab_title);
		final ImageView tips = (ImageView) tabIndicator.findViewById(R.id.tab_title_tips);

		if (textViewSparseArray == null)
			textViewSparseArray = new SparseArray<CustomTextView>(mTotal);
		textViewSparseArray.put(index, tv);
		if (matrixSparse == null)
			matrixSparse = new SparseBooleanArray(mTotal);
		matrixSparse.put(index, false);

		if (mTextColor != null) {
			tv.setTextColor(mTextColor);
		}
		if (mTextSizeNormal > 0) {
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeNormal);
		}
		tv.setText(tabInfo.name);
		boolean flag = mSelectedTab == index ? (tabInfo.focusIcon > 0 || tabInfo.focus != null) : (tabInfo.unFocusIcon > 0 || tabInfo.unFocus != null);
		flag = !flag ? tabInfo.selectorIcon > 0 : flag;
		if (flag) {
			Drawable drawable = null;
			if (drawableSparseArray == null)
				drawableSparseArray = new SparseArray<Drawable>(mTotal);
			if (mSelectedTab == index) {
				if (tabInfo.focusIcon > 0) {
					drawable = getBoundDrawable(tabInfo.focusIcon);
				} else if (tabInfo.focus != null) {
					drawable = getBoundDrawable(tabInfo.id, FOCUS, tabInfo.focus);
				} else if (tabInfo.selectorIcon > 0)
					drawable = getBoundDrawable(tabInfo.selectorIcon);
				else if (tabInfo.selectorDrawable != null)
					drawable = getBounds(tabInfo.selectorDrawable);
				tabIndicator.setSelected(true);
			} else {
				if (tabInfo.unFocusIcon > 0)
					drawable = getBoundDrawable(tabInfo.unFocusIcon);
				else if (tabInfo.unFocus != null)
					drawable = getBoundDrawable(tabInfo.id, UNFOCUS, tabInfo.unFocus);
				else if (tabInfo.selectorIcon > 0)
					drawable = getBoundDrawable(tabInfo.selectorIcon);
				else if (tabInfo.selectorDrawable != null)
					drawable = getBounds(tabInfo.selectorDrawable);
			}
			setCompoundDrawable(tv, drawable);
			matrixSparse.put(index, mSelectedTab == index);
		}
		if (tabInfo.hasTips) {
			tips.setVisibility(View.VISIBLE);
		} else {
			tips.setVisibility(View.GONE);
		}
		tabIndicator.setId(BSSEEID + (mCurrID++));
		tabIndicator.setOnClickListener(this);
		LinearLayout.LayoutParams lP = (LinearLayout.LayoutParams) tabIndicator.getLayoutParams();
		lP.gravity = Gravity.CENTER_VERTICAL;
		addView(tabIndicator);
	}

	/**
	 * 
	 * @param tv
	 * @param drawable
	 */
	private void setCompoundDrawable(CustomTextView tv, Drawable drawable) {
		if (drawable == null)
			return;
		switch (compoundOrientation) {
		case TOP:
			tv.setCompoundDrawables(null, drawable, null, null);
			break;
		case BOTTOM:
			tv.setCompoundDrawables(null, null, null, drawable);
			break;
		case LEFT:
			tv.setCompoundDrawables(drawable, null, null, null);
			break;
		case RIGHT:
			tv.setCompoundDrawables(null, null, drawable, null);
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * @param _resId
	 *            标识ID
	 */
	private Drawable getBounds(Drawable drawable) {
		if (drawable != null) {
			drawable.setBounds(0, 0, bound, bound);
		}
		return drawable;
	}

	/**
	 * 通过标识ID获取图片
	 * 
	 * @param _resId
	 *            标识ID
	 */
	private Drawable getBoundDrawable(int _drawableId) {
		try {
			if (drawableSparseArray == null)
				drawableSparseArray = new SparseArray<Drawable>(mTotal);
			Drawable drawable = drawableSparseArray.get(_drawableId);
			if (drawable == null) {
				drawable = resource.getDrawable(_drawableId);
				drawable.setBounds(0, 0, bound, bound);
				drawableSparseArray.put(_drawableId, drawable);
			}
			return drawable;
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 通过标识ID获取图片
	 * 
	 * @param _resId
	 *            标识ID
	 */
	private Drawable getBoundDrawable(int index, int _drawableId, Drawable drawable) {
		try {
			if (drawableSparseArray == null)
				drawableSparseArray = new SparseArray<Drawable>(mTotal);
			int key = _drawableId - index;
			Drawable tempDrawable = drawableSparseArray.get(key);
			if (tempDrawable != null)
				drawable = tempDrawable;
			else if (drawable != null) {
				drawable.setBounds(0, 0, bound, bound);
				drawableSparseArray.put(key, drawable);
			}
			return drawable;
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.e(e.getMessage(), e);
			return null;
		}
	}

	public void setOnExtraListener(OnTabChangeListener mOnChangeListener) {
		this.mOnChangeListener = mOnChangeListener;
	}

	public void setDisplayedPage(int index) {
		mSelectedTab = index;
	}

	public void setChangeOnClick(boolean changeOnClick) {
		mChangeOnClick = changeOnClick;
	}

	public boolean getChangeOnClick() {
		return mChangeOnClick;
	}

	@Override
	public void onClick(View v) {
		int position = v.getId() - BSSEEID;
		if (textViewSparseArray != null && textViewSparseArray.size() == mTotal) {
			if (mSelectedTab != position)
				for (int i = 0; i < mTotal; i++) {
					CustomTextView boundTextView = textViewSparseArray.get(i);
					TabInfo tabInfo = mTabs.get(i);
					boolean matrix = i == mSelectedTab ? !matrixSparse.get(i) : matrixSparse.get(i);
					if (i != position && (tabInfo.unFocusIcon > 0 || tabInfo.unFocus != null)) {
						Drawable drawable = null;
						if (matrix && (tabInfo.matrixUnFocusIcon > 0 || tabInfo.matrixUnFocus != null)) {// 反转
							if (tabInfo.matrixUnFocusIcon > 0)
								drawable = getBoundDrawable(tabInfo.matrixUnFocusIcon);
							else if (tabInfo.matrixUnFocus != null)
								drawable = getBoundDrawable(tabInfo.id, MATRIXUNFOCUS, tabInfo.matrixUnFocus);
						} else if (tabInfo.unFocusIcon > 0)
							drawable = getBoundDrawable(tabInfo.unFocusIcon);
						else if (tabInfo.unFocus != null)
							drawable = getBoundDrawable(tabInfo.id, UNFOCUS, tabInfo.unFocus);
						setCompoundDrawable(boundTextView, drawable);
					}
				}
			boolean matrix = matrixSparse.get(position);
			CustomTextView boundTextView = textViewSparseArray.get(position);
			TabInfo tabInfo = mTabs.get(position);
			Drawable drawable = null;
			if (matrix && (tabInfo.matrixFocusIcon > 0 || tabInfo.matrixFocus != null)) {
				if (tabInfo.matrixFocusIcon > 0)
					drawable = getBoundDrawable(tabInfo.matrixFocusIcon);
				else if (tabInfo.matrixFocus != null)
					drawable = getBoundDrawable(tabInfo.id, MATRIXFOCUS, tabInfo.matrixFocus);
			} else if (tabInfo.focusIcon > 0)
				drawable = getBoundDrawable(tabInfo.focusIcon);
			else if (tabInfo.focus != null)
				drawable = getBoundDrawable(tabInfo.id, FOCUS, tabInfo.focus);
			setCompoundDrawable(boundTextView, drawable);
			matrixSparse.put(position, !matrix);
			if (mOnChangeListener != null)
				mOnChangeListener.onChange(textViewSparseArray.get(position), position, matrix ? State.MATRIX : State.FOCUS);
			setCurrentTab(position);
		}
	}

	public int getTabCount() {
		int children = getChildCount();
		return children;
	}

	// 设置当前选项卡
	public synchronized void setCurrentTab(int index) {
		if (index < 0 || index >= getTabCount()) {
			return;
		}
		if (mSelectedTab >= 0) {
			View oldTab = getChildAt(mSelectedTab);
			oldTab.setSelected(false);
			setTabTextSize(oldTab, false);
		}

		mSelectedTab = index;
		View newTab = getChildAt(mSelectedTab);
		newTab.setSelected(true);
		setTabTextSize(newTab, true);
		newTab.findViewById(R.id.tab_title_tips).setVisibility(View.GONE);

		if (mViewPager != null)
			mViewPager.setCurrentItem(mSelectedTab);
		invalidate();
	}

	private void setTabTextSize(View tab, boolean selected) {
		TextView tv = (TextView) tab.findViewById(R.id.tab_title);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, selected ? mTextSizeSelected : mTextSizeNormal);
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (v == this && hasFocus && getTabCount() > 0) {
			getChildAt(mSelectedTab).requestFocus();
			return;
		}

		if (hasFocus) {
			int i = 0;
			int numTabs = getTabCount();
			while (i < numTabs) {
				if (getChildAt(i) == v) {
					setCurrentTab(i);
					break;
				}
				i++;
			}
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mCurrentScroll == 0 && mSelectedTab != 0) {
			if (mViewPager != null)
				mCurrentScroll = (getWidth() + mViewPager.getPageMargin()) * mSelectedTab;
		}
	}

	public interface OnTabChangeListener {
		public void onChange(View view, int position, State state);
	}
}
