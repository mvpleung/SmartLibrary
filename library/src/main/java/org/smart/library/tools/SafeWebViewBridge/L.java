package org.smart.library.tools.SafeWebViewBridge;

import android.util.Log;

import org.xutils.x;

/**
 * 日志工具类
 *
 * @author Liangzc
 *         created on 2016/4/20 11:29
 */
public class L {

    public static void d(String tag, String message) {
        if (x.isDebug())
            Log.d(tag, message);
    }

    public static void e(String tag, String message) {
        if (x.isDebug())
            Log.e(tag, message);
    }

    public static void w(String tag, String message) {
        if (x.isDebug())
            Log.w(tag, message);
    }
}
