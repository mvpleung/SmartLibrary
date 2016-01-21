package org.smart.library.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.CompoundButton;

import org.smart.library.R;

/**
 * 自定义RadioButton
 * 
 * @author LiangZiChao
 *         created on 2015年6月15日
 *         In the net.gemeite.smartcommunity.widget
 */
public class CustomRadioButton extends CompoundButton {

	final int Default_TextSize = 15, Default_SubTextSize = 12, Default_Zero = 0;

	/** 图片宽度，图片高度，标题字号，子标题字号 */
	int mBoundWidth, mBoundHeight, mTextSize, mSubTextSize;

	/** 标题颜色，子标题颜色 */
	ColorStateList mTextColor, mSubTextColor;
	/** 标题，子标题 */
	CharSequence mText, mSubText;

	Drawable[] mDrawables;

	public CustomRadioButton(Context context) {
		this(context, null, 0);
	}

	public CustomRadioButton(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.radioButtonStyle);
	}

	public CustomRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomRadioButton);
		mBoundWidth = a.getDimensionPixelSize(R.styleable.CustomRadioButton_crb_boundWidth, getWidth());
		mBoundHeight = a.getDimensionPixelSize(R.styleable.CustomRadioButton_crb_boundHeight, getHeight());
		mTextSize = a.getDimensionPixelSize(R.styleable.CustomRadioButton_android_textSize, Default_TextSize);
		mSubTextSize = a.getDimensionPixelSize(R.styleable.CustomRadioButton_crb_subTextSize, Default_SubTextSize);
		mText = a.getText(R.styleable.CustomRadioButton_android_text);
		mSubText = a.getText(R.styleable.CustomRadioButton_crb_subText);
		mTextColor = a.getColorStateList(R.styleable.CustomRadioButton_android_textColor);
		mSubTextColor = a.getColorStateList(R.styleable.CustomRadioButton_crb_subTextColor);
		setText(mText, mSubText);
		mDrawables = getCompoundDrawables();
		initCompoundDrawable();
		a.recycle();
	}

	public void initCompoundDrawable() {
		if (mDrawables != null && mDrawables.length > 0 && mBoundWidth != Default_Zero && mBoundHeight != Default_Zero) {
			for (Drawable drawable : mDrawables) {
				if (drawable != null)
					drawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
			}
			setCompoundDrawables(mDrawables[0], mDrawables[1], mDrawables[2], mDrawables[3]);
		}
	}

	/**
	 * set Drawalbe
	 * 
	 * @param mDrawables
	 *            [left,top,right,bottom]
	 */
	public void setCompoundDrawables(Drawable[] mDrawables, int mBoundWidth, int mBoundHeight) {
		this.mBoundWidth = mBoundWidth;
		this.mBoundHeight = mBoundHeight;
		this.mDrawables = mDrawables;
		initCompoundDrawable();
	}

	/**
	 * 设置文本
	 * 
	 * @param mText
	 */
	public void setText(CharSequence mText, CharSequence mSubText) {
		if (!TextUtils.isEmpty(mText) && !TextUtils.isEmpty(mSubText)) {
			SpannableString mSpannableString = new SpannableString(mText + "\n" + mSubText);
			if (!TextUtils.isEmpty(mText) && mTextSize > Default_Zero)
				mSpannableString.setSpan(new AbsoluteSizeSpan(mTextSize), 0, mText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (!TextUtils.isEmpty(mSubText) && mSubTextSize > Default_Zero)
				mSpannableString.setSpan(new AbsoluteSizeSpan(mSubTextSize), mText.length(), mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			if (mTextColor != null) {
				mSpannableString.setSpan(new ForegroundColorSpan(mTextColor.getColorForState(getDrawableState(), 0)), 0, mText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			if (mSubTextColor != null) {
				mSpannableString.setSpan(new ForegroundColorSpan(mSubTextColor.getColorForState(getDrawableState(), 0)), mText.length(), mSpannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			setText(mSpannableString);
		}
	}

	/**
	 * CompoundDrawableTop
	 * 
	 * @param mDrawable
	 * @param mBoundHeight
	 */
	public void setCompoundDrawableTop(Drawable mDrawable, int mBoundWidth, int mBoundHeight) {
		if (mDrawables == null)
			mDrawables = getCompoundDrawables();
		this.mBoundWidth = mBoundWidth;
		this.mBoundHeight = mBoundHeight;
		mDrawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
		setCompoundDrawables(mDrawables[0], mDrawable, mDrawables[2], mDrawables[3]);
	}

	/**
	 * CompoundDrawableLeft
	 * 
	 * @param mDrawable
	 * @param mBoundWidth
	 * @param mBoundHeight
	 */
	public void setCompoundDrawableLeft(Drawable mDrawable, int mBoundWidth, int mBoundHeight) {
		if (mDrawables == null)
			mDrawables = getCompoundDrawables();
		this.mBoundWidth = mBoundWidth;
		this.mBoundHeight = mBoundHeight;
		mDrawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
		setCompoundDrawables(mDrawable, mDrawables[1], mDrawables[2], mDrawables[3]);
	}

	/**
	 * CompoundDrawableRight
	 * 
	 * @param mDrawable
	 */
	public void setCompoundDrawableRight(Drawable mDrawable, int mBoundWidth, int mBoundHeight) {
		if (mDrawables == null)
			mDrawables = getCompoundDrawables();
		this.mBoundWidth = mBoundWidth;
		this.mBoundHeight = mBoundHeight;
		mDrawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
		setCompoundDrawables(mDrawables[0], mDrawables[1], mDrawable, mDrawables[3]);
	}

	/**
	 * CompoundDrawableBottom
	 * 
	 * @param mDrawable
	 */
	public void setCompoundDrawableBottom(Drawable mDrawable, int mBoundWidth, int mBoundHeight) {
		if (mDrawables == null)
			mDrawables = getCompoundDrawables();
		this.mBoundWidth = mBoundWidth;
		this.mBoundHeight = mBoundHeight;
		mDrawable.setBounds(0, 0, mBoundWidth, mBoundHeight);
		setCompoundDrawables(mDrawables[0], mDrawables[1], mDrawables[2], mDrawable);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the radio button is already checked, this method will not toggle the
	 * radio button.
	 */
	@Override
	public void toggle() {
		// we override to prevent toggle when the radio is already
		// checked (as opposed to check boxes widgets)
		if (!isChecked()) {
			super.toggle();
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
		super.onInitializeAccessibilityEvent(event);
		event.setClassName(CustomRadioButton.class.getName());
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
		super.onInitializeAccessibilityNodeInfo(info);
		info.setClassName(CustomRadioButton.class.getName());
	}
}
