package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ai.android.picker.DatePicker;
import org.smart.library.R;
import org.smart.library.tools.PxUtil;
import org.smart.library.widget.wheel.WheelView;

/**
 * 日期/周滑动选择框
 * 
 *         created on 2015年7月14日
 *         In the org.smart.smartlibrary.widget
 */
@SuppressLint("SimpleDateFormat")
public class DateWeekDialog extends Dialog {

	Button mButtonCancel;
	TextView mTextTitle;
	DateWeekCallback dateCallback;
	DatePicker mDatePicker;
	View mDateView;
	WheelView mDatewheelView, mTimewheelView;
	View.OnClickListener mSureClickListener;

	String mTitle;
	boolean cancelVisible;

	public DateWeekDialog(Context context) {
		super(context);
	}

	public DateWeekDialog(Context context, int theme) {
		super(context, theme);
	}

	public DateWeekDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public DateWeekDialog(Context context, View mView, View.OnClickListener mSureClickListener) {
		super(context, R.style.custom_dialog);
		this.mDateView = mView;
		this.mSureClickListener = mSureClickListener;
	}

	public DateWeekDialog(Context context, DateWeekCallback dateWeekCallback) {
		super(context, R.style.custom_dialog);
		this.dateCallback = dateWeekCallback;
		initDateView();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_nubber_layout);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		int screenWidth = PxUtil.getScreenWidth(getContext());
		lp.width = (int) (screenWidth == 720 ? (screenWidth * 4 / 5) : (screenWidth * 85 / 100)); // 设置宽度
		getWindow().setAttributes(lp);
		if (mDateView != null) {
			LinearLayout linear = (LinearLayout) findViewById(R.id.view_content);// 加载布局
			linear.addView(mDateView, new LinearLayout.LayoutParams(screenWidth * 4 / 5, LinearLayout.LayoutParams.WRAP_CONTENT));
		}
		mTextTitle = (TextView) findViewById(R.id.dialog_ensure_text_title);
		mTextTitle.setText(mTitle);
		mButtonCancel = (Button) findViewById(R.id.dialog_ensure_button_cancel);
		mButtonCancel.setVisibility(cancelVisible ? View.VISIBLE : View.GONE);
		mButtonCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		Button mButtonSure = (Button) findViewById(R.id.dialog_ensure_button_sure);
		mButtonSure.setOnClickListener(mSureClickListener != null ? mSureClickListener : new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (dateCallback != null && mDatewheelView != null && mTimewheelView != null) {
					dateCallback.onChoice(mDatewheelView.getCurrentItemValue(), mTimewheelView.getCurrentItemValue());
				}
			}
		});
	}

	private void initDateView() {
		mDateView = View.inflate(getContext(), R.layout.twowheel, null);
		mDatewheelView = (WheelView) mDateView.findViewById(R.id.passw_1);
		mTimewheelView = (WheelView) mDateView.findViewById(R.id.passw_2);
	}

	public void setCancelVisible(boolean cancelVisible) {
		this.cancelVisible = cancelVisible;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public WheelView getDatewheelView() {
		return mDatewheelView;
	}

	public WheelView getTimewheelView() {
		return mTimewheelView;
	}

	/**
	 * 是否有数据
	 * 
	 * @return
	 */
	public boolean hasData() {
		int dateCount = mDatewheelView != null ? mDatewheelView.getCount() : 0;
		int timeCount = mTimewheelView != null ? mTimewheelView.getCount() : 0;
		return dateCount + timeCount > 0;
	}

	public interface DateWeekCallback {
		public void onChoice(String dateValue, String timeValue);
	}
}
