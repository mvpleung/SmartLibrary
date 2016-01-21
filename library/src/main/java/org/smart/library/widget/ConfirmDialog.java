package org.smart.library.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.smart.library.R;
import org.smart.library.tools.PxUtil;

/**
 * 提示确认选择框
 * 
 * @author LiangZiChao
 *         created on 2014-8-21下午5:48:52
 *         In the com.xiaobai.xbtrip.view
 */
public class ConfirmDialog extends Dialog {

	private Context context;
	private View mViewLine;
	private Button btn_left, btn_right;// 底部按钮
	private TextView tv_content, tv_title;// 显示TextView
	private CharSequence showString = "";// 显示的文字
	private String negative = "", positive = "", title = "";// 按钮上的文字
	private boolean isSingle = false, onBackPressed;
	private View.OnClickListener dismissOnClickListener, negativeListener, positiveListener;// 按钮的监听器
	private LinearLayout ll_dialog;
	private View centerView;
	private OnDialogWindowListener onDialogWindowListener;

	public ConfirmDialog(Context context) {
		this(context, null);
	}

	public ConfirmDialog(Context context, int theme) {
		super(context, theme);
		initDialog(context, null, null, null, null, null, null);
	}

	public ConfirmDialog(Context context, int theme, CharSequence showString) {
		super(context, theme);
		initDialog(context, showString, null, null, null, null, null);
	}

	public ConfirmDialog(Context context, CharSequence showString) {
		this(context, showString, null);
	}

	public ConfirmDialog(Context context, CharSequence showString, String positive) {
		this(context, null, showString, null, positive);
	}

	public ConfirmDialog(Context context, View centerView, String title, View.OnClickListener onClickListener, String positive) {
		super(context, R.style.DialogStyle);
		this.centerView = centerView;
		initDialog(context, null, null, onClickListener, null, positive, title);
	}

	public ConfirmDialog(Context context, String title, CharSequence showString, View.OnClickListener onClickListener, String positive) {
		this(context, title, showString, null, null, onClickListener, positive);
	}

	public ConfirmDialog(Context context, String title, CharSequence showString, View.OnClickListener negativeListener, View.OnClickListener positiveListener) {
		this(context, title, showString, negativeListener, null, positiveListener, null);
	}

	public ConfirmDialog(Context context, String title, CharSequence showString, View.OnClickListener negativeListener, String negative, View.OnClickListener positiveListener, String positive) {
		super(context, R.style.DialogStyle);
		initDialog(context, showString, negativeListener, positiveListener, negative, positive, title);
	}

	/**
	 * 初始化Dialog
	 */
	private void initDialog(Context context, CharSequence showString, View.OnClickListener negativeListener, View.OnClickListener positiveListener, String negative, String positive, String title) {
		this.context = context;
		this.showString = showString;
		this.negativeListener = negativeListener;
		this.positiveListener = positiveListener;
		this.negative = negative;
		this.title = title;
		this.positive = positive;
		this.isSingle = isSingle();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_confirm);
		ll_dialog = (LinearLayout) findViewById(R.id.ll_dialog);
		if (centerView != null) {
			ll_dialog.addView(centerView, 2);
		}
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		int screenWidth = PxUtil.getScreenWidth(context);
		lp.width = (int) (screenWidth == 720 ? (screenWidth * 4 / 5) : (screenWidth * 85 / 100)); // 设置宽度
		ll_dialog.setMinimumHeight(lp.width * 618 / 1000);
		getWindow().setAttributes(lp);
		dismissOnClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		};
		mViewLine = findViewById(R.id.view_line);
		btn_left = (Button) findViewById(R.id.btn_left);
		btn_right = (Button) findViewById(R.id.btn_right);

		mViewLine.setVisibility(isSingle ? View.GONE : View.VISIBLE);
		btn_left.setVisibility(isSingle ? View.GONE : View.VISIBLE);
		tv_content = (TextView) findViewById(R.id.tv_content);
		tv_content.setMinHeight(lp.width * 618 / (screenWidth == 720 ? 2000 : (3 * 1000)));
		tv_content.setMaxHeight(PxUtil.getScreenHeight(context) * 2 / 3);
		tv_title = (TextView) findViewById(R.id.tv_title);
		if (!TextUtils.isEmpty(title))
			tv_title.setText(title);

		// 默认，按钮的动作为关闭Dialog
		btn_left.setOnClickListener(negativeListener == null ? dismissOnClickListener : negativeListener);
		btn_right.setOnClickListener(positiveListener == null ? dismissOnClickListener : positiveListener);

		if (!TextUtils.isEmpty(negative))
			btn_left.setText(negative);
		if (!TextUtils.isEmpty(positive))
			btn_right.setText(positive);

		tv_content.setText(showString);
		tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		int lineCount = tv_content.getLineCount();
		tv_content.setGravity(lineCount > 2 ? (Gravity.CENTER_VERTICAL | Gravity.LEFT) : Gravity.CENTER);
		if (onDialogWindowListener != null)
			onDialogWindowListener.onWindowFocusChanged(this);
	}

	// 显示Dialog
	public void showDialog() {
		show();
	}

	/**
	 * 是否单按钮
	 * 
	 */
	public void isSingle(boolean isOneButton) {
		this.isSingle = isOneButton;
	}

	/**
	 * 是否单按钮
	 * 
	 * @return
	 */
	public boolean isSingle() {
		return isSingle;
	}

	public void onBackPressed(boolean onBackPressed) {
		this.onBackPressed = onBackPressed;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (onBackPressed && context instanceof Activity) {
			((Activity) context).onBackPressed();
		}
	}

	public Context getDialogContext() {
		return context;
	}

	public TextView getContentView() {
		return tv_content;
	}

	public void setOnDialogWindowListener(OnDialogWindowListener onDialogWindowListener) {
		this.onDialogWindowListener = onDialogWindowListener;
	}

	public interface OnDialogWindowListener {
		void onWindowFocusChanged(ConfirmDialog dialog);
	}
}
