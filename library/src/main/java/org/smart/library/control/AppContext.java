package org.smart.library.control;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;

import org.smart.library.tools.FileTools;
import org.smart.library.tools.PxUtil;
import org.xutils.x;

import java.util.List;


/**
 * 自定义Application
 *
 * @author LiangZiChao
 *         created on 2015年6月11日
 */
public class AppContext extends Application {

    private int mStatuBarHeight;
    /**
     * 屏幕宽度
     */
    public static int screenWidth;
    /**
     * 屏幕高度
     */
    public static int screenHeight;

    @Override
    public void onCreate() {
        super.onCreate();
        initApplication(this);
    }

    /**
     * Init AppContext
     *
     * @param context context
     */
    public static void initApplication(Application context) {
        FileTools.init(context);
        x.Ext.init(context);
        x.Ext.setDebug(AppConfig.DEBUG_MODEL); // 是否输出debug日志
        DisplayMetrics mDisplayMetrices = context.getResources().getDisplayMetrics();
        screenWidth = mDisplayMetrices.widthPixels;
        screenHeight = mDisplayMetrices.heightPixels;
        // 注册App异常崩溃处理器
//		Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        if (AppConfig.DEBUG_MODEL)
            context.startService(new Intent(context, LogService.class));
    }

    public static Application getApplication() {
        return AppManager.getAppManager().getApplication();
    }

    /**
     * 应用是否运行到后台
     *
     * @param context context
     * @return true or false
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        try {
            for (RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                        L.i(String.format("Background App:%s", appProcess.processName));
                        return true;
                    } else {
                        L.i(String.format("Foreground App:%s", appProcess.processName));
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            L.e(e.getMessage(), e);
        }
        return false;
    }

    public int getStatuBarHeight() {
        return mStatuBarHeight != 0 ? mStatuBarHeight : (mStatuBarHeight = PxUtil.getStatusBarHeight(this));
    }
}
