package org.smart.library.control;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import org.smart.library.tools.FileTools;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.List;


/**
 * 自定义Application
 * 
 * @author LiangZiChao
 *         created on 2015年6月11日
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
		x.Ext.init(this);
		x.Ext.setDebug(AppConfig.DEBUG_MODEL); // 是否输出debug日志
		DisplayMetrics mDisplayMetrices = getResources().getDisplayMetrics();
		screenWidth = mDisplayMetrices.widthPixels;
		screenHeight = mDisplayMetrices.heightPixels;
		// 注册App异常崩溃处理器
//		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
		if (AppConfig.DEBUG_MODEL)
			startService(new Intent(this, LogService.class));
	}

	public static Application getApplication() {
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
						LogUtil.i(String.format("Background App:%s", appProcess.processName));
						return true;
					} else {
						LogUtil.i(String.format("Foreground App:%s", appProcess.processName));
						return false;
					}
				}
			}
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
		}
		return false;
	}
}
