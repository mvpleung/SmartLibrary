package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smart.library.R;
import org.smart.library.listener.DateCallback;
import org.smart.library.widget.KCalendar.OnCalendarClickListener;
import org.smart.library.widget.KCalendar.OnCalendarDateChangedListener;

import java.text.SimpleDateFormat;

/**
 * 日期选择框
 * 
 * @author LiangZiChao
 *         created on 2015年7月14日
 *         In the org.smart.smartlibrary.widget
 */
@SuppressLint("SimpleDateFormat")
public class DatePickDialog extends Dialog {

	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	DateCallback dateCallback;

	StringBuffer sb = new StringBuffer();

	public DatePickDialog(Context context) {
		super(context);
	}

	public DatePickDialog(Context context, int theme) {
		super(context, theme);
	}

	public DatePickDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public DatePickDialog(Context context, DateCallback dateCallback) {
		super(context, R.style.DialogStyle);
		this.dateCallback = dateCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_datepicker);
		final TextView popupwindow_calendar_month = (TextView) findViewById(R.id.popupwindow_calendar_month);
		final KCalendar calendar = (KCalendar) findViewById(R.id.popupwindow_calendar);

		sb.append("年");
		sb.append(calendar.getCalendarMonth());
		sb.append("月");
		popupwindow_calendar_month.setText(sb);

		// 监听所选中的日期
		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {

			public void onCalendarClick(int row, int col, String dateFormat) {
				int month = Integer.parseInt(dateFormat.substring(dateFormat.indexOf("-") + 1, dateFormat.lastIndexOf("-")));

				if (calendar.getCalendarMonth() - month == 1// 跨年跳转
						|| calendar.getCalendarMonth() - month == -11) {
					calendar.lastMonth();

				} else if (month - calendar.getCalendarMonth() == 1 // 跨年跳转
						|| month - calendar.getCalendarMonth() == -11) {
					calendar.nextMonth();

				} else {
					calendar.removeAllMarkColor();
					calendar.setCalendarDayBgColor(dateFormat, R.drawable.bg_calendar_seleced);
					if (dateCallback != null)
						dateCallback.onChoice(dateFormat);
				}
			}
		});

		// 监听当前月份
		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			public void onCalendarDateChanged(int year, int month) {
				sb.setLength(0);
				sb.append(year);
				sb.append("年");
				sb.append(month);
				sb.append("月");
				popupwindow_calendar_month.setText(sb);
			}
		});

		// 上月监听按钮	
		RelativeLayout popupwindow_calendar_last_month = (RelativeLayout) findViewById(R.id.popupwindow_calendar_last_month);
		popupwindow_calendar_last_month.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				calendar.lastMonth();
			}

		});

		// 下月监听按钮
		RelativeLayout popupwindow_calendar_next_month = (RelativeLayout) findViewById(R.id.popupwindow_calendar_next_month);
		popupwindow_calendar_next_month.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				calendar.nextMonth();
			}
		});
	}
}
