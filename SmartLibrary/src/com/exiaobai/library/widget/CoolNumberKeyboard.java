package com.exiaobai.library.widget;

import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.exiaobai.library.R;
import com.exiaobai.library.listener.OnChoiceListener;
import com.exiaobai.library.tools.UITools;

/**
 * 车辆号码键盘
 *
 * @version 1.1 2015-09-14 替换中文为ASCII码值
 * @update Liangzichao
 * @author yoojia.chen@gmail.com
 * @version version 2015-04-24
 * @since 1.0
 */
public class CoolNumberKeyboard {

	private static final int NUMBER_LEN = 7;

	private final Context mContext;
	private final Dialog mDialog;
	private final KeyboardView mKeyboardView;
	private final TextView[] mNumber = new TextView[NUMBER_LEN];

	private OnKeyboardActionListener mKeyboardActionListener = new OnKeyboardActionListener() {

		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			mSelectedTextView.setText(Character.toString((char) primaryCode));
			autoNextNumber();
		}
	};

	private final View.OnClickListener mNumberSelectedHandler = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			// if (mSelectedTextView != null){
			// mSelectedTextView.setActivated(false);
			// }
			mSelectedTextView = (TextView) view;
			// mSelectedTextView.setActivated(true);
			int id = view.getId();
			if (id == R.id.keyboard_number_0) {
				if (mShowKeyboard != R.xml.keyboard_province) {
					mShowKeyboard = R.xml.keyboard_province;
					mKeyboardView.setKeyboard(mProvinceKeyboard);
				}
			} else if (id == R.id.keyboard_number_1) {
				if (mShowKeyboard != R.xml.keyboard_city_code) {
					mShowKeyboard = R.xml.keyboard_city_code;
					mKeyboardView.setKeyboard(mCityCodeKeyboard);
				}
			} else if (id == R.id.keyboard_number_6) {
				if (mShowKeyboard != R.xml.keyboard_number_extra) {
					mShowKeyboard = R.xml.keyboard_number_extra;
					mKeyboardView.setKeyboard(mNumberExtraKeyboard);
				}
			} else {
				if (mShowKeyboard != R.xml.keyboard_number) {
					mShowKeyboard = R.xml.keyboard_number;
					mKeyboardView.setKeyboard(mNumberKeyboard);
				}
			}
			changeBackground(id);
			// mKeyboardView.invalidateAllKeys();
			// mKeyboardView.invalidate();
		}
	};

	/**
	 * 改变提示背景
	 * 
	 * @param index
	 */
	private void changeBackground(int id) {
		for (TextView view : mNumber) {
			view.setBackgroundResource(view.getId() == id ? R.mipmap.check_gray : R.drawable.keyboard_bg_number);
		}
	}

	private final View.OnClickListener mCommitClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			StringBuilder number = new StringBuilder(NUMBER_LEN);
			for (TextView i : mNumber) {
				if (!" ".equals(i.getText())) {
					number.append(i.getText());
				}
			}
			if (number.length() == NUMBER_LEN) {
				mOnChoiceListener.onChoice(number.toString());
				dismiss();
			}
		}
	};

	private final boolean mAutoCommit, mAutoAlpha;
	private View mCommitButton;
	private int mShowKeyboard = 0;
	private TextView mSelectedTextView;
	private OnChoiceListener<String> mOnChoiceListener;

	private Keyboard mProvinceKeyboard;
	private Keyboard mCityCodeKeyboard;
	private Keyboard mNumberKeyboard;
	private Keyboard mNumberExtraKeyboard;

	public CoolNumberKeyboard(Context context, OnChoiceListener<String> mOnChoiceListener) {
		this(context, false, mOnChoiceListener);
	}

	public CoolNumberKeyboard(Context context, boolean autoAlpha, OnChoiceListener<String> mOnChoiceListener) {
		this(context, autoAlpha, false, mOnChoiceListener);
	}

	public CoolNumberKeyboard(Context context, boolean autoAlpha, boolean autoCommit, OnChoiceListener<String> mOnChoiceListener) {
		mContext = context;
		mAutoCommit = autoCommit;
		mAutoAlpha = autoAlpha;
		this.mOnChoiceListener = mOnChoiceListener;
		final View contentView = View.inflate(context, R.layout.view_vehicle_keyboard, null);

		mNumber[0] = (TextView) contentView.findViewById(R.id.keyboard_number_0);
		mNumber[1] = (TextView) contentView.findViewById(R.id.keyboard_number_1);
		mNumber[2] = (TextView) contentView.findViewById(R.id.keyboard_number_2);
		mNumber[3] = (TextView) contentView.findViewById(R.id.keyboard_number_3);
		mNumber[4] = (TextView) contentView.findViewById(R.id.keyboard_number_4);
		mNumber[5] = (TextView) contentView.findViewById(R.id.keyboard_number_5);
		mNumber[6] = (TextView) contentView.findViewById(R.id.keyboard_number_6);

		for (TextView m : mNumber)
			m.setOnClickListener(mNumberSelectedHandler);

		mProvinceKeyboard = new Keyboard(mContext, R.xml.keyboard_province);
		mCityCodeKeyboard = new Keyboard(mContext, R.xml.keyboard_city_code);
		mNumberKeyboard = new Keyboard(mContext, R.xml.keyboard_number);
		mNumberExtraKeyboard = new Keyboard(mContext, R.xml.keyboard_number_extra);

		mKeyboardView = (KeyboardView) contentView.findViewById(R.id.keyboard_view);
		mKeyboardView.setOnKeyboardActionListener(mKeyboardActionListener);
		mKeyboardView.setPreviewEnabled(false);// !!! Must be false

		mDialog = UITools.createCustomDialog(mContext, contentView, mAutoAlpha ? R.style.DialogStyle : R.style.DialogStyleBgDisEnable, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
		Window mDialogWindow = mDialog.getWindow();
		mDialogWindow.setWindowAnimations(R.style.animPopupWindowPushBottom);

		mCommitButton = contentView.findViewById(R.id.keyboard_commit);
		mCommitButton.setOnClickListener(mCommitClickListener);
	}

	/**
	 * 显示车牌输入法
	 * 
	 * @param activity
	 *            Activity
	 */
	public void showKeyBoard(View mView, String givenNumber) {
		show(mView);
		if (TextUtils.isEmpty(givenNumber) || NUMBER_LEN != givenNumber.length()) {
			for (TextView i : mNumber)
				i.setText(" ");
		} else if (NUMBER_LEN != givenNumber.length()) {
			// throw new
			// IllegalArgumentException("Illegal vehicle number length");
		} else {
			char[] numbers = givenNumber.toUpperCase(Locale.getDefault()).toCharArray();
			for (int i = 0; i < NUMBER_LEN; i++) {
				mNumber[i].setText(Character.toString(numbers[i]));
			}
		}
	}

	public void dismiss() {
		mDialog.dismiss();
	}

	/**
	 * 自动跳转到下一个输入框
	 */
	private void autoNextNumber() {
		int numberId = mSelectedTextView.getId();
		if (numberId == R.id.keyboard_number_0) {
			mNumber[1].performClick();

		} else if (numberId == R.id.keyboard_number_1) {
			mNumber[2].performClick();

		} else if (numberId == R.id.keyboard_number_2) {
			mNumber[3].performClick();

		} else if (numberId == R.id.keyboard_number_3) {
			mNumber[4].performClick();

		} else if (numberId == R.id.keyboard_number_4) {
			mNumber[5].performClick();

		} else if (numberId == R.id.keyboard_number_5) {
			mNumber[6].performClick();

		} else if (numberId == R.id.keyboard_number_6 && mAutoCommit) {
			mCommitButton.performClick();
		}
	}

	private void show(View anchorView) {
		mDialog.show();
		mNumber[0].performClick();
	}
}
