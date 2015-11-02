/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.exiaobai.library.tools;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * 可以中途修改倒计时
 * 
 * @author LiangZiChao
 * @Data 2015年9月14日
 * @Package com.exiaobai.library.tools
 */
@SuppressLint("HandlerLeak")
public abstract class CustomDownTimer {

	/**
	 * Millis since epoch when alarm should stop.
	 */
	private final long mMillisInFuture;

	/**
	 * The interval in millis that the user receives callbacks
	 */
	private final long mCountdownInterval;

	private long mStopTimeInFuture;

	/**
	 * @param millisInFuture
	 *            The number of millis in the future from the call to
	 *            {@link #start()} until the countdown is done and
	 *            {@link #onFinish()} is called.
	 * @param countDownInterval
	 *            The interval along the way to receive {@link #onTick(long)}
	 *            callbacks.
	 */
	public CustomDownTimer(long millisInFuture, long countDownInterval) {
		mMillisInFuture = millisInFuture;
		mCountdownInterval = countDownInterval;
	}

	/**
	 * Cancel the countdown.
	 */
	public final void cancel() {
		mHandler.removeMessages(MSG);
	}

	/**
	 * Start the countdown.
	 */
	public synchronized final CustomDownTimer start() {
		if (mMillisInFuture <= 0) {
			onFinish();
			return this;
		}
		mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
		mHandler.sendMessage(mHandler.obtainMessage(MSG));
		return this;
	}

	/**
	 * 累加
	 * 
	 * @param timeInFuture
	 */
	public synchronized void cumulative(long timeInFuture) {
		if (timeInFuture > 0)
			mStopTimeInFuture += timeInFuture;
	}

	/**
	 * Callback fired on regular interval.
	 * 
	 * @param millisUntilFinished
	 *            The amount of time until finished.
	 */
	public abstract void onTick(long millisUntilFinished);

	/**
	 * Callback fired when the time is up.
	 */
	public abstract void onFinish();

	private static final int MSG = 1;

	// handles counting down
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			synchronized (CustomDownTimer.this) {
				final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

				if (millisLeft <= 0) {
					onFinish();
				} else if (millisLeft < mCountdownInterval) {
					// no tick, just delay until done
					sendMessageDelayed(obtainMessage(MSG), millisLeft);
				} else {
					long lastTickStart = SystemClock.elapsedRealtime();
					onTick(millisLeft);

					// take into account user's onTick taking time to execute
					long delay = lastTickStart + mCountdownInterval - SystemClock.elapsedRealtime();

					// special case: user's onTick took more than interval to
					// complete, skip to next interval
					while (delay < 0)
						delay += mCountdownInterval;

					sendMessageDelayed(obtainMessage(MSG), delay);
				}
			}
		}
	};
}
