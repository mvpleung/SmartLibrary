package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.ai.android.picker.DatePicker;
import org.smart.library.R;
import org.smart.library.listener.DateCallback;
import org.smart.library.tools.PxUtil;

/**
 * 日期滑动选择框
 * 
 * @author LiangZiChao
 *         created on 2015年7月14日
 *         In the org.smart.library.widget
 */
@SuppressLint("SimpleDateFormat")
public class DatePickerDialog extends Dialog {

	DateCallback dateCallback;
	DatePicker mDatePicker;

	StringBuffer sb = new StringBuffer();

	public DatePickerDialog(Context context) {
		super(context);
	}

	public DatePickerDialog(Context context, int theme) {
		super(context, theme);
	}

	public DatePickerDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public DatePickerDialog(Context context, DateCallback dateCallback) {
		super(context, R.style.AiTheme_Light);
		this.dateCallback = dateCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_num_datepicker);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		int screenWidth = PxUtil.getScreenWidth(getContext());
		lp.width = (int) (screenWidth == 720 ? (screenWidth * 4 / 5) : (screenWidth * 85 / 100)); // 设置宽度
		getWindow().setAttributes(lp);
		View mView = findViewById(R.id.ll_date);
		mView.setMinimumHeight((int) (lp.width * 0.618));
		mDatePicker = (DatePicker) findViewById(R.id.datePicker);
		View mViewSure = findViewById(R.id.btn_sure);
		mViewSure.setMinimumWidth((int) (lp.width * 0.6));
		mViewSure.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dateCallback != null) {
					dateCallback.onChoice(mDatePicker.getDate());
				}
			}
		});
	}
}
