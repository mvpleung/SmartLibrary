package com.exiaobai.library.control;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.exiaobai.library.R;
import com.lidroid.xutils.util.LogUtils;

/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 * 
 * @author
 * @Desc LiangZC Update By 2014-8-8
 * @create 2014-3-17
 */
@SuppressLint("HandlerLeak")
public class AppException extends Exception implements UncaughtExceptionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6725174445891438953L;

	/** 定义异常类型 */
	public final static byte EXCEPTION_FAIL = 0x01;
	public final static byte EXCEPTION_NETWORK = 0x04;
	public final static byte EXCEPTION_IO = 0x05;
	public final static byte EXCEPTION_TIMEOUT = 0x06;
	public final static byte EXCEPTION_XMLPULL = 0x07;
	public final static byte EXCEPTION_NULL = 0x08;
	public final static byte EXCEPTION_CAST = 0x09;
	public final static byte EXCEPTION_RUNTIME = 0x10;
	public final static byte EXCEPTION_SOCKET = 0x11;
	public final static byte EXCEPTION_HTTP_CODE = 0x12;
	public final static byte EXCEPTION_HTTP_ERROR = 0x13;
	public final static byte EXCEPTION_RUN = 0x14;
	public final static byte EXCEPTION_XML = 0x15;
	public final static byte EXCEPTION_JSON = 0x16;

	private byte code;
	private String exceptionDesc;// 异常描述

	/** 系统默认的UncaughtException处理类 */
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private AppException() {
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	public AppException(byte code, Throwable throwable) {
		super(throwable);
		this.code = code;
	}

	public AppException(byte code, String exceptionDesc, Throwable throwable) {
		super(throwable);
		this.code = code;
		this.exceptionDesc = exceptionDesc;
	}

	public byte getCode() {
		return this.code;
	}

	public void setCode(byte code) {
		this.code = code;
	}

	public void setExceptionDesc(String exceptionDesc) {
		this.exceptionDesc = exceptionDesc;
	}

	public void setExceptionType(byte code) {
		this.code = code;
	}

	/**
	 * 返回异常描述
	 * 
	 * @return
	 */
	public String getDetailsMessage() {
		return !TextUtils.isEmpty(exceptionDesc) ? exceptionDesc : convertExceptionDesc(code, null);
	}

	/**
	 * 获取异常描述
	 * 
	 * @param defaultMsg
	 * @return
	 */
	public String getMessage(String defaultMsg) {
		String throwMsg = getCause() != null ? getCause().getMessage() : null;
		defaultMsg = !TextUtils.isEmpty(throwMsg) ? throwMsg : defaultMsg;
		return convertExceptionDesc(code, defaultMsg);
	}

	/**
	 * 转换异常描述
	 * 
	 * @return
	 */
	public static String convertExceptionDesc(byte code, String defaultMsg) {
		String outputString = defaultMsg;
		Activity activity = AppManager.getAppManager().getActivityReference();
		if (activity == null)
			return outputString;
		switch (code) {
		case EXCEPTION_NULL:
		case EXCEPTION_CAST:
		case EXCEPTION_IO:
		case EXCEPTION_RUNTIME:
		case EXCEPTION_XMLPULL:
		case EXCEPTION_JSON:
		case EXCEPTION_FAIL:
			if (TextUtils.isEmpty(outputString) && activity != null)
				outputString = activity.getString(R.string.app_handle_fail);
			break;
		case EXCEPTION_NETWORK:
		case EXCEPTION_TIMEOUT:
			if (activity != null)
				outputString = activity.getString(R.string.app_socket_timeout);
			break;
		}
		return outputString;
	}

	/**
	 * 
	 * 转换异常描述
	 * 
	 * @param throwable
	 * @return
	 */
	public static byte convertExceptionType(Throwable throwable) {
		byte exType = EXCEPTION_FAIL;
		if (throwable instanceof UnknownHostException || throwable instanceof ConnectException) {

			exType = EXCEPTION_NETWORK;

		} else if (throwable instanceof SocketTimeoutException || throwable instanceof ConnectTimeoutException) {

			exType = EXCEPTION_TIMEOUT;

		} else if (throwable instanceof IOException) {

			exType = EXCEPTION_IO;

		} else if (throwable instanceof NullPointerException) {

			exType = EXCEPTION_NULL;

		} else if (throwable instanceof ClassCastException) {

			exType = EXCEPTION_CAST;

		} else if (throwable instanceof RuntimeException) {

			exType = EXCEPTION_RUNTIME;

		} else if (throwable instanceof XmlPullParserException || throwable instanceof IllegalAccessException || throwable instanceof InstantiationException || throwable instanceof IllegalArgumentException) {

			exType = EXCEPTION_XMLPULL;

		} else if (throwable instanceof JSONException) {

			exType = EXCEPTION_JSON;

		}
		LogUtils.e(throwable != null ? throwable.getMessage() : throwable + "", throwable);
		return exType;
	}

	/**
	 * 获取异常类型处理对象
	 * 
	 * @param throwable
	 * @return
	 */
	public static AppException convertException(Throwable throwable) {
		byte exceptionType = convertExceptionType(throwable);
		return new AppException(exceptionType, convertExceptionDesc(exceptionType, null), throwable);
	}

	/**
	 * 转换异常
	 * 
	 * @param exceptionType
	 *            异常类型
	 * @param errorMsg
	 *            异常信息
	 * @return
	 */
	public static AppException convertException(byte exceptionType, String errorMsg) {
		return new AppException(exceptionType, new Throwable(errorMsg));
	}

	/**
	 * 转换异常
	 * 
	 * @param exceptionType
	 *            异常类型
	 * @param errorMsg
	 *            异常信息
	 * @return
	 */
	public static AppException convertException(String errorMsg) {
		return convertException(EXCEPTION_FAIL, errorMsg);
	}

	/**
	 * 获取APP异常崩溃处理对象
	 * 
	 * @param context
	 * @return
	 */
	public static AppException getAppExceptionHandler() {
		return new AppException();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (ex != null)
			LogUtils.e(ex.getMessage(), ex);
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LogUtils.e(e.getMessage(), e);
			}
			AppManager.getAppManager().AppExit();
		}

	}

	/**
	 * 自定义异常处理
	 * 
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(Throwable ex) {
		if (ex == null) {
			LogUtils.e("handleException  ex == null");
			return false;
		}
		// final String message = ex.getMessage();
		final Context context = AppManager.getAppManager().getActivityReference();
		if (context == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				Toast.makeText(context, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}

		}.start();

		return true;
	}
}