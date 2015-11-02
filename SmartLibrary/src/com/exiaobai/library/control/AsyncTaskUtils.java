package com.exiaobai.library.control;

import android.os.Handler;
import android.os.Message;

import com.exiaobai.library.control.AppException;
import com.exiaobai.library.control.ThreadPoolUtils;
import com.exiaobai.library.tools.UITools;
import com.lidroid.xutils.util.LogUtils;

/**
 * 异步任务
 * 
 * @author LiangZiChao
 * @Data 2015年7月21日
 * @Package net.gemeite.smartcommunity.network
 */
public abstract class AsyncTaskUtils<Result, Params> {

	private final static int SUCCESS = 0;
	private final static int FAIL = -1;

	private InternalHandler sHandler;

	/**
	 * 异步任务
	 * 
	 * @author LiangZiChao
	 * @Data 2015年7月21日
	 * @Package net.gemeite.smartcommunity.network
	 */
	public AsyncTaskUtils() {
		sHandler = new InternalHandler(this);
	}

	public abstract Result doInBackground(Params mParams) throws Exception;

	public void onStart() {
	};

	public abstract void onFail(AppException e);

	public abstract void onSuccess(Result mParam) throws Exception;

	public void onFinish() {
		UITools.dismissLoading();
	};

	public void execute() {
		execute(null);
	}

	public void execute(Params mParams) {
		onStart();
		final Params params = mParams;
		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				Message msg = sHandler.obtainMessage();
				try {
					msg.what = SUCCESS;
					msg.obj = doInBackground(params);
					LogUtils.i("Response:" + msg.obj);
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);
					msg.what = FAIL;
					if (e instanceof AppException) {
						msg.obj = (AppException) e;
					} else
						msg.obj = AppException.convertException(e);
				}
				sHandler.sendMessage(msg);
			}
		});
	}

	@SuppressWarnings("rawtypes")
	private static class InternalHandler extends Handler {

		private AsyncTaskUtils mAsync;

		public InternalHandler(AsyncTaskUtils mAsync) {
			this.mAsync = mAsync;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			mAsync.onFinish();
			switch (msg.what) {
			case SUCCESS:
				try {
					mAsync.onSuccess(msg.obj);
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);
					mAsync.onFail(AppException.convertException(e));
				}
				break;
			case FAIL:
				if (msg.obj != null && msg.obj instanceof AppException) {
					mAsync.onFail((AppException) msg.obj);
				}
				break;
			}
		}
	}
}
