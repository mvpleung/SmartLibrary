package com.exiaobai.library.control;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import com.exiaobai.library.tools.FileTools;
import com.lidroid.xutils.util.LogUtils;

/**
 * 自定义Application
 * 
 * @author LiangZiChao
 * @Data 2015年6月11日
 */
public class AppContext extends Application {

	/** 屏幕宽度 */
	public static int screenWidth;
	/** 屏幕高度 */
	public static int screenHeight;

	@Override
	public void onCreate() {
		super.onCreate();
		FileTools.init(this);
		LogUtils.allowAll = AppConfig.DEBUG_MODEL;
		DisplayMetrics mDisplayMetrices = getResources().getDisplayMetrics();
		screenWidth = mDisplayMetrices.widthPixels;
		screenHeight = mDisplayMetrices.heightPixels;
		// 注册App异常崩溃处理器
		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
		if (AppConfig.DEBUG_MODEL)
			startService(new Intent(this, LogService.class));
	}

	public static AppContext getApplication() {
		return AppManager.getAppManager().getApplication();
	}

	/**
	 * 应用是否运行到后台
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBackground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		try {
			for (RunningAppProcessInfo appProcess : appProcesses) {
				if (appProcess.processName.equals(context.getPackageName())) {
					if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
						LogUtils.i(String.format("Background App:%s", appProcess.processName));
						return true;
					} else {
						LogUtils.i(String.format("Foreground App:%s", appProcess.processName));
						return false;
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			LogUtils.e(e.getMessage(), e);
		}
		return false;
	}
}
