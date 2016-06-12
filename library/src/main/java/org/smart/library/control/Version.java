package org.smart.library.control;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/**
 * 获取版本信息
 *
 * @author LiangZiChao
 *         created on 2013-4-22 下午02:01:21
 */
public class Version {

    private static String VERSION_NAME;
    private static int VERSION_CODE = -1;

    /**
     * 获取版本号
     *
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (VERSION_CODE == -1)
            readPackageInfo(context);
        return VERSION_CODE;
    }

    /**
     * 获取当前程序版本
     *
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (TextUtils.isEmpty(VERSION_NAME))
            readPackageInfo(context);
        return VERSION_NAME;
    }

    /**
     * 获取APP版本信息
     *
     * @return
     */
    public static String getAppVersionInfo(Context context) {
        if (TextUtils.isEmpty(VERSION_NAME) || VERSION_CODE == -1) {
            readPackageInfo(context);
        }
        return VERSION_NAME + "(" + VERSION_CODE + ")";
    }

    /**
     * 读取PackageInfo
     *
     * @param context
     */
    private static void readPackageInfo(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            VERSION_NAME = info.versionName;
            VERSION_CODE = info.versionCode;
        } catch (NameNotFoundException e) {
            L.e(e.getMessage(), e);
        }
    }

    /**
     * 获取Android版本型号
     *
     * @return
     */
    public static String getAndroidVersionInfo() {
        String model = android.os.Build.MODEL;
        L.i("Model:" + model);
        if (!TextUtils.isEmpty(model)) {
            int index = model.indexOf("-");
            if (index > 0) {
                model = model.substring(0, index).trim();
            }
        }
        return model;
    }
}
