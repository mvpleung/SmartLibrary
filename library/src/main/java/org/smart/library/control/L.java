package org.smart.library.control;

import android.text.TextUtils;
import android.util.Log;

/**
 * 日志工具类
 * created on 2016/4/20 11:29
 */
public class L {

    public static String sTagPrefix = "smart";

    private static String generateTag() {
        StackTraceElement caller = new Throwable().getStackTrace()[2];
        String tag = "%s.%s(L:%d)";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        tag = TextUtils.isEmpty(sTagPrefix) ? tag : sTagPrefix + ":" + tag;
        return tag;
    }

    public static void d(String content) {
        d(generateTag(), content);
    }

    public static void d(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.d(tag, content);
    }

    public static void d(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.d(tag, content, tr);
    }

    public static void e(String content) {
        e(generateTag(), content);
    }

    public static void e(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.e(tag, content);
    }

    public static void e(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.e(tag, content, tr);
    }

    public static void i(String content) {
        i(generateTag(), content);
    }

    public static void i(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.i(tag, content);
    }

    public static void i(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.i(tag, content, tr);
    }

    public static void v(String content) {
        v(generateTag(), content);
    }

    public static void v(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.v(tag, content);
    }

    public static void v(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.v(tag, content, tr);
    }

    public static void w(String content) {
        w(generateTag(), content);
    }

    public static void w(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.w(tag, content);
    }

    public static void w(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.w(tag, content, tr);
    }

    public static void w(Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.w(tag, tr);
    }


    public static void wtf(String content) {
        wtf(generateTag(), content);
    }

    public static void wtf(String tag, String content) {
        if (!AppConfig.DEBUG_MODEL) return;

        Log.wtf(tag, content);
    }

    public static void wtf(String content, Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.wtf(tag, content, tr);
    }

    public static void wtf(Throwable tr) {
        if (!AppConfig.DEBUG_MODEL) return;
        String tag = generateTag();

        Log.wtf(tag, tr);
    }
}
