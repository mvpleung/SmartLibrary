package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.smart.library.R;
import org.smart.library.adapter.ChoiceAdapter;
import org.smart.library.control.AppConfig.AppRequestCode;
import org.smart.library.control.AppConfig.AppSharedName;
import org.smart.library.listener.OnExtraEventListener;
import org.smart.library.widget.ChoiceDialog;
import org.smart.library.widget.ConfirmDialog;
import org.smart.library.widget.DateWeekDialog;
import org.smart.library.widget.DateWeekDialog.DateWeekCallback;
import org.smart.library.widget.LoadingDialog;
import org.smart.library.widget.XListView;
import org.xutils.common.util.LogUtil;

import java.io.File;
import java.io.Serializable;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 工具箱
 *
 * @author LiangZiChao
 *         created on 2015年6月11日
 */
@SuppressLint({"SimpleDateFormat", "DefaultLocale", "HandlerLeak"})
public class UITools {

    private static LoadingDialog mProgressDialog;
    @SuppressWarnings("rawtypes")
    private static ChoiceDialog choiceDialog;
    private static ConfirmDialog confirmDialog;
    private static Dialog mDialog;

    private static Toast mToast;

    private static Notification mUpdateNotification = null;
    private static NotificationManager mUpdateNotificationManager = null;

    private static boolean isBtnAutoClose = true; // 是否点击按钮自动关闭（支持返回按钮和dismis）

    /**
     * toast 提示的长度持续时间
     *
     * @param context
     * @param textResId
     */
    public static void showToastLongDuration(Context context, int textResId) {
        showToastLongDuration(context, context.getText(textResId));
    }

    /**
     * toast 提示的长度持续时间
     *
     * @param context
     */
    public static void showToastLongDuration(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_LONG);
    }

    /**
     * toast 提示的短的持续时间
     *
     * @param context
     * @param textResId
     */
    public static void showToastShortDuration(Context context, int textResId) {
        showToast(context, context.getText(textResId), Toast.LENGTH_SHORT);
    }

    /**
     * toast 提示的短的持续时间
     *
     * @param context
     */
    public static void showToastShortDuration(Context context, CharSequence text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    /**
     * toast 提示
     *
     * @param context
     */
    public static void showToastDuration(Context context, CharSequence text, int duration) {
        showToast(context, text, duration);
    }

    /**
     * @param ctx
     * @param msg
     * @param duration
     */
    public static void showToast(Context ctx, int msg, int duration) {
        showToast(ctx, ctx.getText(msg), duration);
    }

    /**
     * @param ctx
     * @param duration
     */
    public static void showToast(Context ctx, View view, int duration) {
        if (mToast == null) {
            mToast = new Toast(ctx);
        }
        mToast.setDuration(duration);
        mToast.setView(view);
        mToast.show();
    }

    /**
     * @param ctx
     * @param msg
     * @param duration
     */
    public static void showToast(final Context ctx, final CharSequence msg, final int duration) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            showOrCreateToast(ctx, msg, duration);
        } else
            ((Activity) ctx).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showOrCreateToast(ctx, msg, duration);
                }
            });
    }

    /**
     * 显示Toast
     *
     * @param ctx
     * @param msg
     * @param duration
     */
    private static void showOrCreateToast(Context ctx, CharSequence msg, int duration) {
        if (mToast == null)
            mToast = Toast.makeText(ctx, msg, duration);
        else {
            View mView = mToast.getView();
            LogUtil.i("Toast.mView :" + mView);
            if (mView == null) {
                mToast = Toast.makeText(ctx, msg, duration);
            } else {
                View mTextView = mView.findViewById(android.R.id.message);
                LogUtil.i("Toast.mTextView :" + mTextView);
                if (mTextView == null) {
                    mToast = Toast.makeText(ctx, msg, duration);
                } else {
                    mToast.setText(msg);
                    mToast.setDuration(duration);
                }
            }
        }
        mToast.show();
    }

    /**
     * 显示通知栏通知
     *
     * @param context
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showNotification(Context context, Intent paramIntent, String title, String tickerText) {
        showNotification(context, 0, paramIntent, title, tickerText);
    }

    /**
     * 显示通知栏通知
     *
     * @param context
     * @param notifyId    通知ID
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showNotification(Context context, int notifyId, Intent paramIntent, String title, String tickerText) {
        paramIntent = paramIntent == null ? new Intent() : paramIntent;
        showNotificationPendingIntent(context, notifyId, PendingIntent.getActivity(context, notifyId, paramIntent, PendingIntent.FLAG_UPDATE_CURRENT), title, tickerText);
    }

    /**
     * 显示通知栏通知
     *
     * @param context
     * @param notifyId    通知ID
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showNotificationBroadcast(Context context, int notifyId, Intent paramIntent, String title, String tickerText) {
        paramIntent = paramIntent == null ? new Intent() : paramIntent;
        showNotificationPendingIntent(context, notifyId, PendingIntent.getBroadcast(context, notifyId, paramIntent, PendingIntent.FLAG_UPDATE_CURRENT), title, tickerText);
    }

    /**
     * 显示通知栏通知（铃声、震动可配置）
     *
     * @param context
     * @param notifyId      通知ID
     * @param pendingIntent 点击通知栏跳转Intent
     * @param tickerText    通知栏文本
     */
    public static void showNotificationPendingIntent(Context context, int notifyId, PendingIntent pendingIntent, String title, String tickerText) {
        RecordPreferences mRecord = RecordPreferences.getInstance(context);
        boolean mSoundRemind = mRecord.getSharedValue(AppSharedName.RECEIVE_AUDIO_REMIND, true);
        boolean mVibrateRemind = mRecord.getSharedValue(AppSharedName.RECEIVE_VIBRATION_REMIND, true);
        int defaults = mSoundRemind && mVibrateRemind ? Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE : mSoundRemind ? Notification.DEFAULT_SOUND : mVibrateRemind ? Notification.DEFAULT_VIBRATE : 0;
        showNotifyPendingIntent(context, notifyId, pendingIntent, title, tickerText, defaults);
    }

    /**
     * 显示默认通知栏通知
     *
     * @param context
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showDefaultNotification(Context context, Intent paramIntent, String title, String tickerText) {
        showDefaultNotification(context, 0, paramIntent, title, tickerText);
    }

    /**
     * 显示默认通知栏通知
     *
     * @param context
     * @param notifyId    通知ID
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showDefaultNotification(Context context, int notifyId, Intent paramIntent, String title, String tickerText) {
        paramIntent = paramIntent == null ? new Intent() : paramIntent;
        showDefaultNotificationPendingIntent(context, notifyId, PendingIntent.getActivity(context, notifyId, paramIntent, PendingIntent.FLAG_UPDATE_CURRENT), title, tickerText);
    }

    /**
     * 显示默认通知栏通知
     *
     * @param context
     * @param notifyId    通知ID
     * @param paramIntent 点击通知栏跳转Intent
     * @param tickerText  通知栏文本
     */
    public static void showDefaultNotificationBroadcast(Context context, int notifyId, Intent paramIntent, String title, String tickerText) {
        paramIntent = paramIntent == null ? new Intent() : paramIntent;
        showDefaultNotificationPendingIntent(context, notifyId, PendingIntent.getBroadcast(context, notifyId, paramIntent, PendingIntent.FLAG_UPDATE_CURRENT), title, tickerText);
    }

    /**
     * 显示默认通知栏通知
     *
     * @param context
     * @param notifyId      通知ID
     * @param pendingIntent 点击通知栏跳转Intent
     * @param tickerText    通知栏文本
     */
    public static void showDefaultNotificationPendingIntent(Context context, int notifyId, PendingIntent pendingIntent, String title, String tickerText) {
        showNotifyPendingIntent(context, notifyId, pendingIntent, title, tickerText, Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
    }

    /**
     * 显示默认通知栏通知
     *
     * @param context
     * @param notifyId      通知ID
     * @param pendingIntent 点击通知栏跳转Intent
     * @param tickerText    通知栏文本
     */
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void showNotifyPendingIntent(Context context, int notifyId, PendingIntent pendingIntent, String title, String tickerText, int defaults) {
        getNotificationManager(context);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            mUpdateNotification = new Notification();
            mUpdateNotification.icon = R.mipmap.ic_launcher;
            mUpdateNotification.defaults = defaults;// 铃声提醒/震动
            mUpdateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
            mUpdateNotification.tickerText = tickerText;
            mUpdateNotification.setLatestEventInfo(context, context.getString(R.string.app_name), tickerText, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            Notification.Builder builder = new Notification.Builder(context).setAutoCancel(true).setContentTitle(title).setContentText(tickerText).setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).setWhen(System.currentTimeMillis()).setDefaults(defaults).setOngoing(true);
            mUpdateNotification = builder.getNotification();
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            mUpdateNotification = new Notification.Builder(context).setAutoCancel(true).setContentTitle(title).setContentText(tickerText).setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher).setWhen(System.currentTimeMillis()).setDefaults(defaults).build();
        }
        mUpdateNotificationManager.notify(notifyId, mUpdateNotification);
    }

    /**
     * 获取通知管理器
     *
     * @param context
     * @return
     */
    public static NotificationManager getNotificationManager(Context context) {
        if (mUpdateNotificationManager == null)
            mUpdateNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        return mUpdateNotificationManager;
    }

    /**
     * 根据ID，清楚通知
     *
     * @param notifyId
     */
    public static void cancelNotification(int notifyId) {
        try {
            if (mUpdateNotificationManager != null)
                mUpdateNotificationManager.cancel(notifyId);
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e(e.getMessage(), e);
        }
    }

    /**
     * 根据ID，清楚通知
     */
    public static void cancelAllNotification() {
        try {
            if (mUpdateNotificationManager != null)
                mUpdateNotificationManager.cancelAll();
        } catch (Exception e) {
            // TODO: handle exception
            LogUtil.e(e.getMessage(), e);
        }
    }

    /**
     * 清楚Toast
     */
    public static void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
        }
    }

    /**
     * 展示加载loading
     *
     * @param context
     */
    public static LoadingDialog showDialogLoading(Context context) {
        return showDialogLoading(context, null, true);
    }

    /**
     * 展示加载loading (默认文本：正在加载中...)
     *
     * @param context
     */
    public static LoadingDialog showDialogLoadingDefault(Context context) {
        return showDialogLoading(context, context.getString(R.string.app_loading), true);
    }

    /**
     * 展示加载loading
     *
     * @param context
     * @param msg
     */
    public static LoadingDialog showDialogLoading(Context context, String msg) {
        return showDialogLoading(context, msg, true);
    }

    /**
     * 展示加载loading
     *
     * @param context
     * @param msg
     */
    public static LoadingDialog showDialogLoading(Context context, int msg) {
        return showDialogLoading(context, context.getString(msg), true);
    }

    /**
     * 展示加载loading
     *
     * @param context
     * @param msg
     */
    public static LoadingDialog createDialogLoading(Context context, String msg, boolean cancelDismiss) {
        dismissLoading();
        mProgressDialog = new LoadingDialog(context, msg);
        mProgressDialog.setCancelable(cancelDismiss);
        mProgressDialog.setCanceledOnTouchOutside(false);
        return mProgressDialog;
    }

    /**
     * 展示加载loading
     *
     * @param context
     * @param msg
     */
    public static LoadingDialog showDialogLoading(Context context, String msg, boolean cancelDismiss) {
        try {
            mProgressDialog = createDialogLoading(context, msg, cancelDismiss);
            mProgressDialog.show();
            return mProgressDialog;
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 单选列表Dialog
     *
     * @param items
     */
    public static ChoiceDialog<String> showChoiceDialog(Context context, String[] items, OnExtraEventListener<String> onExtraEventListener, String... defaultValue) {
        return showChoiceDialog(context, null, items, onExtraEventListener, defaultValue);
    }

    /**
     * 单选列表Dialog
     *
     * @param title
     * @param items
     */
    @SuppressWarnings("unchecked")
    public static ChoiceDialog<String> showChoiceDialog(Context context, String title, String[] items, OnExtraEventListener<String> onExtraEventListener, String... defaultValue) {
        choiceDialog = new ChoiceDialog<String>(context, title, items, onExtraEventListener, defaultValue);
        choiceDialog.setCanceledOnTouchOutside(true);
        choiceDialog.show();
        return choiceDialog;
    }

    /**
     * 单选列表Dialog
     *
     * @param <T>
     * @param title
     */
    @SuppressWarnings("unchecked")
    public static <T> ChoiceDialog<T> createSingleChoiceDialog(Context context, String title, ChoiceAdapter<T> baseAdapter, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
        choiceDialog = new ChoiceDialog<T>(context, title, baseAdapter, onExtraEventListener, defaultValue);
        choiceDialog.setCanceledOnTouchOutside(true);
        return choiceDialog;
    }

    /**
     * 单选列表Dialog
     *
     * @param <T>
     * @param title
     */
    @SuppressWarnings("unchecked")
    public static <T> ChoiceDialog<T> showSingleChoiceDialog(Context context, String title, ChoiceAdapter<T> baseAdapter, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
        choiceDialog = createSingleChoiceDialog(context, title, baseAdapter, onExtraEventListener, defaultValue);
        choiceDialog.show();
        return choiceDialog;
    }

    /** ConfirmDialog Begin */
    /**
     * 带确认按钮的弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick 确定监听
     * @param positive
     * @return
     */
    public static ConfirmDialog createConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive) {
        return createConfirmDialog(context, title, msg, negativeClick, negative, positiveClick, positive, true);
    }

    /**
     * 创建单按钮的弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param negative
     * @param positiveClick 确定监听
     * @param positive
     * @param isAutoDimiss  监听是否自动关闭所有弹窗
     * @return
     */
    public static ConfirmDialog createSingleConfirmDialog(Context context, String title, CharSequence msg, String negative, OnClickListener positiveClick, String positive, boolean isAutoDimiss) {
        isBtnAutoClose = isAutoDimiss;
        confirmDialog = new ConfirmDialog(context, title, msg, null, negative, getOnClickListener(positiveClick), positive) {

            @Override
            public boolean isSingle() {
                // TODO Auto-generated method stub
                return true;
            }
        };
        return confirmDialog;
    }

    /**
     * 带确认按钮的弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick 确定监听
     * @param positive
     * @param isAutoDimiss  监听是否自动关闭所有弹窗
     * @return
     */
    public static ConfirmDialog createConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive, boolean isAutoDimiss) {
        isBtnAutoClose = isAutoDimiss;
        confirmDialog = new ConfirmDialog(context, title, msg, getOnClickListener(negativeClick), negative, getOnClickListener(positiveClick), positive);
        return confirmDialog;
    }

    /**
     * 带确认按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param negativeClick 消极监听
     * @param positiveClick 确定监听
     * @param isAutoDimiss  监听是否自动关闭所有弹窗
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive, boolean isAutoDimiss) {
        confirmDialog = createConfirmDialog(context, title, msg, getOnClickListener(negativeClick), negative, getOnClickListener(positiveClick), positive, isAutoDimiss);
        confirmDialog.show();
        return confirmDialog;
    }

    /**
     * 带确认按钮的弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick 确定监听
     * @param positive
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive) {
        return showConfirmDialog(context, title, msg, negativeClick, negative, positiveClick, positive, true);
    }

    /**
     * 带确认按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param negativeClick 消极监听
     * @param positiveClick 确定监听
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, OnClickListener positiveClick) {
        return showConfirmDialog(context, title, msg, negativeClick, positiveClick, true);
    }

    /**
     * 带确认按钮的弹窗
     *
     * @param context
     * @param title         标题
     * @param msg           消息
     * @param negativeClick 消极监听
     * @param positiveClick 积极监听
     * @param isAutoDimiss  监听是否自动关闭所有弹窗
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, OnClickListener negativeClick, OnClickListener positiveClick, boolean isAutoDimiss) {
        return showConfirmDialog(context, title, msg, negativeClick, null, positiveClick, null, isAutoDimiss);
    }

    /**
     * 带确认按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param onClick
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, OnClickListener onClick, String positive, boolean isAutoDismiss) {
        return showConfirmDialog(context, title, msg, null, null, onClick, positive, isAutoDismiss);
    }

    /**
     * 带一个按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param onClick
     * @return
     */
    public static ConfirmDialog showSingleConfirmDialog(Context context, String title, CharSequence msg, OnClickListener onClick) {
        return showSingleConfirmDialog(context, title, msg, onClick, null, true);
    }

    /**
     * 带一个按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param onClick
     * @return
     */
    public static ConfirmDialog showSingleConfirmDialog(Context context, String title, CharSequence msg, OnClickListener onClick, boolean isAutoDismiss) {
        return showSingleConfirmDialog(context, title, msg, onClick, null, isAutoDismiss);
    }

    /**
     * 带一个按钮的弹窗
     *
     * @param context
     * @param title
     * @param msg
     * @param onClick
     * @return
     */
    public static ConfirmDialog showSingleConfirmDialog(Context context, String title, CharSequence msg, OnClickListener onClick, String positive, boolean isAutoDismiss) {
        confirmDialog = createSingleConfirmDialog(context, title, msg, null, onClick, positive, isAutoDismiss);
        confirmDialog.show();
        return confirmDialog;
    }

    /**
     * 带消息描述的确定弹窗（可配置按钮文字，描述）
     *
     * @param context
     * @param title
     * @param msg
     * @param msgDesc       消息描述
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick
     * @param positive
     * @return
     */
    public static ConfirmDialog createConfirmDialog(Context context, String title, CharSequence msg, CharSequence msgDesc, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive, boolean isAutoDismiss) {
        isBtnAutoClose = isAutoDismiss;
        if (!TextUtils.isEmpty(msgDesc)) {
            Resources resources = context.getResources();
            SpannableString wordtoSpan = new SpannableString(msg + "\n" + msgDesc);
            wordtoSpan.setSpan(new AbsoluteSizeSpan((int) resources.getDimension(R.dimen.font_size_16)), msg.length(), wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.body_font_gray)), msg.length(), wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg = wordtoSpan;
        }
        confirmDialog = new ConfirmDialog(context, title, msg, getOnClickListener(negativeClick), negative, getOnClickListener(positiveClick), positive);
        confirmDialog.show();
        confirmDialog.setCancelable(false);
        confirmDialog.setCanceledOnTouchOutside(false);
        return confirmDialog;
    }

    /**
     * 带消息描述和一个按钮的确定弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param msgDesc       消息描述
     * @param positiveClick
     * @param positive
     * @return
     */
    public static ConfirmDialog createSingleConfirmDialog(Context context, String title, CharSequence msg, CharSequence msgDesc, OnClickListener positiveClick, String positive, boolean isAutoDismiss) {
        isBtnAutoClose = isAutoDismiss;
        if (!TextUtils.isEmpty(msgDesc)) {
            Resources resources = context.getResources();
            SpannableString wordtoSpan = new SpannableString(msg + "\n" + msgDesc);
            wordtoSpan.setSpan(new AbsoluteSizeSpan((int) resources.getDimension(R.dimen.font_size_16)), msg.length(), wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, msg.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new ForegroundColorSpan(resources.getColor(R.color.body_font_gray)), msg.length(), wordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            msg = wordtoSpan;
        }
        confirmDialog = createSingleConfirmDialog(context, title, msg, null, positiveClick, positive, isAutoDismiss);
        confirmDialog.show();
        confirmDialog.setCancelable(false);
        confirmDialog.setCanceledOnTouchOutside(false);
        return confirmDialog;
    }

    /**
     * 带消息描述的确定弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param msgDesc       消息描述
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick
     * @param positive
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, CharSequence msgDesc, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive) {
        return showConfirmDialog(context, title, msg, msgDesc, negativeClick, negative, positiveClick, positive, true);
    }

    /**
     * 带消息描述的确定弹窗（可配置按钮文字）
     *
     * @param context
     * @param title
     * @param msg
     * @param msgDesc       消息描述
     * @param negativeClick 消极监听
     * @param negative
     * @param positiveClick
     * @param positive
     * @return
     */
    public static ConfirmDialog showConfirmDialog(Context context, String title, CharSequence msg, CharSequence msgDesc, OnClickListener negativeClick, String negative, OnClickListener positiveClick, String positive, boolean isAutoDismiss) {
        confirmDialog = createConfirmDialog(context, title, msg, msgDesc, negativeClick, negative, positiveClick, positive, isAutoDismiss);
        confirmDialog.show();
        confirmDialog.setCancelable(false);
        confirmDialog.setCanceledOnTouchOutside(false);
        return confirmDialog;
    }

    /** ConfirmDialog End */

    /**
     * 创建日期滚轮Dialog
     *
     * @param view
     * @param context
     * @param cancelVisible
     * @param titleText
     * @param cancelable
     * @param sureOnClickListener
     * @return
     * @author ZhaoJiShen
     */
    public static DateWeekDialog createWeekDialog(View view, Context context, boolean cancelVisible, String titleText, boolean cancelable, OnClickListener sureOnClickListener) {
        DateWeekDialog mWeekDialog = new DateWeekDialog(context, view, sureOnClickListener);
        mWeekDialog.setCancelable(cancelable);// 不可以用“返回键”取消
        mWeekDialog.setCancelVisible(cancelVisible);
        mDialog = mWeekDialog;
        return mWeekDialog;
    }

    /**
     * 创建日期滚轮Dialog
     *
     * @param context
     * @param cancelVisible
     * @param titleText
     * @param cancelable
     * @return
     * @author ZhaoJiShen
     */
    public static DateWeekDialog createWeekDialog(Context context, boolean cancelVisible, String titleText, boolean cancelable, DateWeekCallback dateWeekCallback) {
        DateWeekDialog mWeekDialog = new DateWeekDialog(context, dateWeekCallback);
        mWeekDialog.setCancelable(cancelable);// 不可以用“返回键”取消
        mWeekDialog.setCancelVisible(cancelVisible);
        mWeekDialog.setTitle(titleText);
        mDialog = mWeekDialog;
        return mWeekDialog;
    }

    /**
     * 是否正在加载
     */
    public static boolean isLoading() {
        if (mProgressDialog != null) {
            return mProgressDialog.isShowing();
        }
        return false;
    }

    /**
     * 消除加载进度
     */
    public static void dismissLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    /**
     * 关闭弹出框
     */
    public static void dismissDialog() {
        if (choiceDialog != null && choiceDialog.isShowing()) {
            choiceDialog.dismiss();
        }
        choiceDialog = null;
        if (mDialog != null && mDialog.isShowing())
            mDialog.dismiss();
        mDialog = null;
        if (confirmDialog != null) {
            confirmDialog.dismiss();
        }
        confirmDialog = null;
    }

    /**
     * 关闭所有弹窗
     */
    public static void dismissAllDialog() {
        dismissLoading();
        dismissDialog();
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param layoutResID
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, int layoutResID, int gravity) {
        return createCustomDialog(context, layoutResID, gravity, R.style.animPopupWindow);
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param layoutResID
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, int layoutResID, int gravity, int animationStyle) {
        Dialog customDialog = new Dialog(context, R.style.DialogStyle);
        customDialog.setContentView(layoutResID);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setOwnerActivity((Activity) context);
        Window dialogWindow = customDialog.getWindow();
        dialogWindow.setWindowAnimations(animationStyle);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = PxUtil.getScreenWidth(context);
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(gravity);
        return customDialog;
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, View cententView, int gravity) {
        return createCustomDialog(context, cententView, PxUtil.getScreenWidth(context), 0, gravity);
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, View cententView, int dialogStyle, int gravity) {
        return createCustomDialog(context, cententView, dialogStyle, PxUtil.getScreenWidth(context), 0, gravity);
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param cententView centerView
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, View cententView, int width, int height, int gravity) {
        return createCustomDialog(context, cententView, R.style.DialogStyle, width, height, gravity);
    }

    /**
     * 创建自定义位置的Dialog
     *
     * @param context
     * @param cententView centerView
     * @param gravity
     * @return
     */
    public static Dialog createCustomDialog(Context context, View cententView, int dialogStyle, int width, int height, int gravity) {
        Dialog customDialog = new Dialog(context, dialogStyle);
        customDialog.setContentView(cententView);
        customDialog.setCanceledOnTouchOutside(true);
        customDialog.setOwnerActivity((Activity) context);
        Window dialogWindow = customDialog.getWindow();
        dialogWindow.setWindowAnimations(R.style.animPopupWindow);
        if (width != 0 || height != 0) {
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            if (width != 0)
                lp.width = width;
            if (height != 0)
                lp.height = height;
            dialogWindow.setAttributes(lp);
        }
        dialogWindow.setGravity(gravity);
        return customDialog;
    }

    /**
     * 创建PopupWindow
     *
     * @param centerView
     * @return
     */
    public static PopupWindow createCustomPopupWindow(View centerView) {
        return createCustomPopupWindow(centerView, R.style.animPopupWindow);
    }

    /**
     * 创建PopupWindow
     *
     * @param centerView
     * @param animationStyle 动画
     * @return
     */
    public static PopupWindow createCustomPopupWindow(View centerView, int animationStyle) {
        return createCustomPopupWindow(centerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, animationStyle);
    }

    /**
     * 创建PopupWindow
     *
     * @param centerView
     * @param width
     * @param height
     * @return
     */
    public static PopupWindow createCustomPopupWindow(View centerView, int width, int height) {
        return createCustomPopupWindow(centerView, width, height, R.style.animPopupWindow);
    }

    /**
     * 创建PopupWindow
     *
     * @param centerView
     * @param width
     * @param height
     * @param animationStyle 动画
     * @return
     */
    @SuppressWarnings("deprecation")
    public static PopupWindow createCustomPopupWindow(View centerView, int width, int height, int animationStyle) {
        PopupWindow popupWindow = new PopupWindow(centerView, width, height);
        popupWindow.setAnimationStyle(animationStyle);
        popupWindow.setFocusable(true);
        // 点击popupWindow之外的地方能让其消失
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        popupWindow.update(); // 更新窗口的状态
        return popupWindow;
    }

    /**
     * 创建ListView
     *
     * @param context 上下文
     * @return
     */
    public static ListView createCustomListView(Context context) {
        return createCustomListView(context, null);
    }

    /**
     * 创建ListView
     *
     * @param context        上下文
     * @param customListener customScrollListener
     * @return
     */
    public static ListView createCustomListView(Context context, OnScrollListener customListener) {
        XListView mListView = new XListView(context);
        mListView.setBackgroundColor(Color.WHITE);
        mListView.setCacheColorHint(Color.TRANSPARENT);
        mListView.setSelector(R.drawable.listview_gray_selector);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setFooterDividersEnabled(false);
        mListView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        return mListView;
    }

    /**
     * Get OnClickListener
     *
     * @return
     */
    private static OnClickListener getOnClickListener(OnClickListener onClickListener) {
        if (onClickListener == null)
            return onClickListener;
        final OnClickListener clickListener = onClickListener;
        return new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isBtnAutoClose) {
                    dismissAllDialog();
                }
                clickListener.onClick(v);
            }
        };
    }

    /**
     * 动态设置listview高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += listItem.getMeasuredHeight();
            LogUtil.i("setListViewHeightBasedOnChildren, totalHeight:" + totalHeight);
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 空白编辑框的错误显示
     *
     * @param context
     * @param editText
     */
    public static void blankEditTextError(Context context, EditText editText, int stringId) {
        // 内容不正确，请检查你的填写内容
        String error = context.getResources().getString(stringId);
        blankEditTextError(context, editText, error);
    }

    /**
     * 空白编辑框的错误显示
     *
     * @param context
     * @param editText
     */
    public static void blankEditTextError(Context context, EditText editText, String message) {
        if (editText.getVisibility() != View.VISIBLE)
            return;
        // 内容不正确，请检查你的填写内容
        SpannableStringBuilder style = new SpannableStringBuilder(message);
        // 错误提醒信息的颜色
        style.setSpan(new ForegroundColorSpan(Color.RED), 0, message.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        editText.setError(style);
        editText.requestFocus();
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivity(Context context, Class<?> nextActivity) {
        Intent intent = new Intent(context, nextActivity);
        context.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, int requestCode) {
        Intent intent = new Intent(activity, nextActivity);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivity(Context context, Class<?> nextActivity, String key, Serializable serializable) {
        Intent intent = new Intent(context, nextActivity);
        intent.putExtra(key, serializable);
        context.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String key, Serializable serializable, int requestCode) {
        Intent intent = new Intent(activity, nextActivity);
        intent.putExtra(key, serializable);
        if (requestCode > 0)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivity(Context context, Class<?> nextActivity, String[] keys, Serializable[] serializables) {
        Intent intent = new Intent(context, nextActivity);
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            intent.putExtra(keys[i], serializables[i]);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String[] keys, Serializable[] serializables, int requestCode) {
        Intent intent = new Intent(activity, nextActivity);
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            intent.putExtra(keys[i], serializables[i]);
        }
        if (requestCode > 0)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivity(Context context, Class<?> nextActivity, String key, Parcelable parcelable) {
        Intent intent = new Intent(context, nextActivity);
        intent.putExtra(key, parcelable);
        context.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String key, Parcelable parcelable, int requestCode) {
        Intent intent = new Intent(activity, nextActivity);
        intent.putExtra(key, parcelable);
        if (requestCode > 0)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivity(Context context, Class<?> nextActivity, String[] keys, Parcelable[] parcelables) {
        Intent intent = new Intent(context, nextActivity);
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            intent.putExtra(keys[i], parcelables[i]);
        }
        context.startActivity(intent);
    }

    /**
     * 跳转到另外一个activity
     *
     * @param nextActivity 下一个Activity
     */
    public static void startToNextActivityForResult(Activity activity, Class<?> nextActivity, String[] keys, Parcelable[] parcelables, int requestCode) {
        Intent intent = new Intent(activity, nextActivity);
        int length = keys.length;
        for (int i = 0; i < length; i++) {
            intent.putExtra(keys[i], parcelables[i]);
        }
        if (requestCode > 0)
            activity.startActivityForResult(intent, requestCode);
        else
            activity.startActivity(intent);
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param telPhone
     * @return
     */
    public static Intent actionCallPhone(Context context, String telPhone) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telPhone));
        context.startActivity(intent);
        return intent;
    }

    /**
     * 获取当前ListView的Adapter
     *
     * @return
     */
    public static ListAdapter getListAdapter(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null && listAdapter instanceof HeaderViewListAdapter) {
            listAdapter = ((HeaderViewListAdapter) listAdapter).getWrappedAdapter();
        }
        return listAdapter;
    }

    /**
     * 随机生成颜色
     *
     * @return 随机生成的十六进制颜色
     */

    public static String RandomColor() {
        return "#" + Integer.toHexString((int) ((Math.random() * 16777216) * -1));
    }

    /**
     * 随机生成RGB颜色
     *
     * @return
     */
    public static int[] RandomRgbColor() {
        Random r = new Random();
        int[] rgbs = new int[3];
        for (int i = 0; i < rgbs.length; i++) {
            rgbs[i] = r.nextInt(255);
        }
        return rgbs;
    }

    /**
     * 随机生成RGB颜色
     *
     * @return
     */
    public static int RandomRgbColorInt() {
        return RandomRgbInt(256);
    }

    /**
     * 随机生成带透明度RGB颜色
     *
     * @return
     */
    public static int RandomRgbColorInt(int alpha) {
        return RandomRgbColorInt(alpha, 256);
    }

    /**
     * 随机生成RGB颜色
     *
     * @param maxRGB 最大RGB值
     * @return
     */
    public static int RandomRgbInt(int maxRGB) {
        Random mRandom = new Random();
        int[] rgbs = new int[3];
        for (int i = 0; i < rgbs.length; i++) {
            rgbs[i] = mRandom.nextInt(maxRGB);
        }
        return Color.rgb(rgbs[0], rgbs[1], rgbs[2]);
    }

    /**
     * 随机生成带透明度RGB颜色
     *
     * @param maxRGB 最大RGB值
     * @return
     */
    public static int RandomRgbColorInt(int alpha, int maxRGB) {
        Random mRandom = new Random();
        int[] rgbs = new int[3];
        for (int i = 0; i < rgbs.length; i++) {
            rgbs[i] = mRandom.nextInt(maxRGB);
        }
        return Color.argb(alpha, rgbs[0], rgbs[1], rgbs[2]);
    }

	/*
     * -----------------------------EditText Input
	 * Begin-----------------------------------
	 */

    static final Object COMPOSING = new NoCopySpan() {
    };

    /**
     * The default implementation performs the deletion around the current
     * selection position of the editable text.
     *
     * @param beforeLength 光标前几位
     * @param afterLength  光标后几位
     */
    public static boolean deleteSurroundingText(EditText mEditText, int beforeLength, int afterLength) {
        final Editable content = mEditText.getText();
        if (content == null)
            return false;

        int a = Selection.getSelectionStart(content);
        int b = Selection.getSelectionEnd(content);

        if (a > b) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        int deleted = 0;

        if (beforeLength > 0) {
            int start = a - beforeLength;
            if (start < 0)
                start = 0;
            content.delete(start, a);
            deleted = a - start;
        }

        if (afterLength > 0) {
            b = b - deleted;

            int end = b + afterLength;
            if (end > content.length())
                end = content.length();

            content.delete(b, end);
        }
        return true;
    }

    /**
     * 输入文本
     *
     * @param mEditText
     * @param text
     * @param newCursorPosition 为1从当前位置插入文本，
     */
    public static void inputText(EditText mEditText, CharSequence text, int newCursorPosition) {
        final Editable content = mEditText.getText();
        if (content == null) {
            return;
        }

        // delete composing text set previously.
        int a = getComposingSpanStart(content);
        int b = getComposingSpanEnd(content);

        if (b < a) {
            int tmp = a;
            a = b;
            b = tmp;
        }

        if (a != -1 && b != -1) {
            removeComposingSpans(content);
        } else {
            a = Selection.getSelectionStart(content);
            b = Selection.getSelectionEnd(content);
            if (a < 0)
                a = 0;
            if (b < 0)
                b = 0;
            if (b < a) {
                int tmp = a;
                a = b;
                b = tmp;
            }
        }

        Spannable sp = null;
        if (!(text instanceof Spannable)) {
            sp = new SpannableStringBuilder(text);
            text = sp;
        } else {
            sp = (Spannable) text;
        }
        setComposingSpans(sp);

        // Position the cursor appropriately, so that after replacing the
        // desired range of text it will be located in the correct spot.
        // This allows us to deal with filters performing edits on the text
        // we are providing here.
        if (newCursorPosition > 0) {
            newCursorPosition += b - 1;
        } else {
            newCursorPosition += a;
        }
        if (newCursorPosition < 0)
            newCursorPosition = 0;
        if (newCursorPosition > content.length())
            newCursorPosition = content.length();
        Selection.setSelection(content, newCursorPosition);
        content.replace(newCursorPosition, b, text);
    }

    public static void setComposingSpans(Spannable text) {
        setComposingSpans(text, 0, text.length());
    }

    /**
     */
    public static void setComposingSpans(Spannable text, int start, int end) {
        final Object[] sps = text.getSpans(start, end, Object.class);
        if (sps != null) {
            for (int i = sps.length - 1; i >= 0; i--) {
                final Object o = sps[i];
                if (o == COMPOSING) {
                    text.removeSpan(o);
                    continue;
                }

                final int fl = text.getSpanFlags(o);
                if ((fl & (Spanned.SPAN_COMPOSING | Spanned.SPAN_POINT_MARK_MASK)) != (Spanned.SPAN_COMPOSING | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)) {
                    text.setSpan(o, text.getSpanStart(o), text.getSpanEnd(o), (fl & ~Spanned.SPAN_POINT_MARK_MASK) | Spanned.SPAN_COMPOSING | Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }

        text.setSpan(COMPOSING, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE | Spanned.SPAN_COMPOSING);
    }

    public static int getComposingSpanStart(Spannable text) {
        return text.getSpanStart(COMPOSING);
    }

    public static int getComposingSpanEnd(Spannable text) {
        return text.getSpanEnd(COMPOSING);
    }

    public static final void removeComposingSpans(Spannable text) {
        text.removeSpan(COMPOSING);
        Object[] sps = text.getSpans(0, text.length(), Object.class);
        if (sps != null) {
            for (int i = sps.length - 1; i >= 0; i--) {
                Object o = sps[i];
                if ((text.getSpanFlags(o) & Spanned.SPAN_COMPOSING) != 0) {
                    text.removeSpan(o);
                }
            }
        }
    }

	/*
     * -----------------------------EditText Input
	 * End-----------------------------------
	 */

    /**
     * 分享图片
     *
     * @param photoUri 路径
     * @param activity
     */
    public static void SharePhoto(Activity activity, String photoUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        File file = new File(photoUri);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/jpeg");
        activity.startActivityForResult(Intent.createChooser(shareIntent, activity.getTitle()), AppRequestCode.SYSTEM_SHARE);
    }

    /**
     * 分享文本
     *
     * @param activity
     */
    public static void ShareText(Activity activity, String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        shareIntent.setType("text/plain");
        activity.startActivityForResult(Intent.createChooser(shareIntent, activity.getTitle()), AppRequestCode.SYSTEM_SHARE);
    }

    /**
     * 跳转WIFI
     *
     * @param activity
     */
    public static void startWifiSetting(Activity activity) {
        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // 进入手机中的wifi网络设置界面
    }

    /**
     * 跳转WIFI
     *
     * @param activity
     */
    public static void startWifiSetting(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), requestCode); // 进入手机中的wifi网络设置界面
    }

    /**
     * 选择联系人
     *
     * @param activity
     * @param requestCode
     */
    public static void startContacts(Activity activity, int requestCode) {
        try {
            Intent it = new Intent();
            it.setAction(Intent.ACTION_PICK);
            it.setData(Contacts.CONTENT_URI);
            // it.setType(Contacts.CONTENT_TYPE);
            activity.startActivityForResult(it, requestCode);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
        }
    }

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * 获取VIewID
     *
     * @return
     */
    public static int getViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range
            // under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF)
                newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
