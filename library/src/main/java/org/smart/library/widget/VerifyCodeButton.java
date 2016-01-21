package org.smart.library.widget;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 验证码倒计时控件（可正常设置OnClickLister）
 * 
 * @author LiangZiChao
 *         created on 2015年6月24日
 *         In the net.gemeite.smartcommunity.widget
 */
public class VerifyCodeButton extends Button {

	String limitformat = "(%s秒)后可重发";
	long millisInFuture = 60000, countDownInterval = 1000;

	CountDownTimer countDownTimer;
	boolean isTimerRun; // 计时器是否正在运行
	CharSequence mText;

	public VerifyCodeButton(Context context) {
		this(context, null, 0);
	}

	public VerifyCodeButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public VerifyCodeButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		mText = getText();
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		// TODO Auto-generated method stub
		if (countDownTimer == null)
			countDownTimer = new CountDownTimer(millisInFuture, countDownInterval) {

				@Override
				public void onTick(long arg0) {
					// TODO Auto-generated method stub
					setText(String.format(limitformat, arg0 / 1000));
				}

				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					setClickable(true);
					setText(mText);
				}
			};
			super.setOnClickListener(l);
	}

	/**
	 * 设置限制提示（必须包含%s）[(%s秒)后可重发]
	 * 
	 * @param limitformat
	 */
	public void setLimitformat(String limitformat) {
		this.limitformat = limitformat;
	}

	/**
	 * 设置倒计时长
	 * 
	 * @param millisInFuture
	 *            时长
	 * @param countDownInterval
	 *            步进值
	 */
	public void setCountTimer(long millisInFuture, long countDownInterval) {
		this.millisInFuture = millisInFuture;
		this.countDownInterval = countDownInterval;
	}

	/**
	 * 是否开启验证码倒计时
	 * 
	 * @return
	 */
	private void isStartTimer(boolean isStart) {
		setClickable(!isStart);
		isTimerRun = isStart;
		if (countDownTimer != null)
			if (isStart)
				countDownTimer.start();
			else {
				countDownTimer.cancel();
				setText(mText);
			}
	}

	/**
	 * 开启倒计时
	 */
	public void startTimer() {
		isStartTimer(true);
	}

	/**
	 * 取消倒计时
	 */
	public void cancelTimer() {
		isStartTimer(false);
	}
}
