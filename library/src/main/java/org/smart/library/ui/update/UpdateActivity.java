package org.smart.library.ui.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.smart.library.R;
import org.smart.library.control.AppConfig;
import org.smart.library.control.AppContext;
import org.smart.library.control.AppException;
import org.smart.library.control.AppManager;
import org.smart.library.listener.DownloadCallback;
import org.smart.library.model.AppVersionBean;
import org.smart.library.tools.NetworkProber;
import org.smart.library.tools.UITools;
import org.smart.library.widget.ConfirmDialog;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

/**
 * APP更新界面
 * @author LiangZiChao
 *         created on 2014-9-8下午09:26:28
 *         In the com.xiaobai.xbtrip.update
 */
@SuppressLint({ "DefaultLocale", "InflateParams" })
public class UpdateActivity extends Activity {

	public final static String VERSION_BEAN = "versionBean";

	private Dialog pd;
	private ProgressBar bar;
	private TextView tv_total;
	private Button btn_right, btn_left;
	private Context context;

	// 下载进度
	private int downloadCount;

	private boolean isBackground;
	private String downloadUrl;
	protected String totalFileSize;

	private Notification updateNotification = null;
	private NotificationManager updateNotificationManager = null;
	private PendingIntent updatePendingIntent = null;

	private AppVersionBean appVersionBean;

	protected Callback.Cancelable cancelable;
	protected boolean isForceUpdate;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i("UpdateActivity.this");
		this.context = UpdateActivity.this;
		AppManager.getAppManager().addActivity(this);
		appVersionBean = getIntent().getParcelableExtra(VERSION_BEAN);
		isForceUpdate = "Y".equals(appVersionBean != null ? appVersionBean.forceUpdateFlag : "");
		ConfirmDialog confirmDialog = null;
		if (!isForceUpdate)
			confirmDialog = UITools.createConfirmDialog(context, getString(R.string.version_new_tips, appVersionBean.versionName), appVersionBean.versionUpdateContent, new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					finish();
				}
			}, getString(R.string.update_laster), updateOnClick, getString(R.string.update_now));
		else {
			if (appVersionBean != null) {
				confirmDialog = UITools.createSingleConfirmDialog(context, getString(R.string.version_new_tips, appVersionBean.versionName), appVersionBean.versionUpdateContent, null, updateOnClick, getString(R.string.update_now), true);
				confirmDialog.setCancelable(false);
				confirmDialog.onBackPressed(true);
			}
		}
		if (confirmDialog != null) {
			confirmDialog.setCanceledOnTouchOutside(false);
			confirmDialog.show();
		}
	}

	/**
	 * 执行更新
	 */
	private void execUpdate() {
		initUpdateUI();
		downloadApk();
	}

	private OnClickListener updateOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (!NetworkProber.isNetworkAvailable(context)) {
				UITools.showToastShortDuration(context, R.string.download_fail_network);
				return;
			}
			if (NetworkProber.isWifi(context)) {
				execUpdate();
			} else {
				ConfirmDialog confirmDialog = UITools.createConfirmDialog(context, null, getString(R.string.download_wifi_tip), new OnClickListener() {
					public void onClick(View view) {
						context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // 进入手机中的wifi网络设置界面
					}
				}, getString(R.string.app_setting), new OnClickListener() {
					public void onClick(View view) {
						execUpdate();
					}
				}, getString(R.string.update_continue));
				confirmDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						arg0.dismiss();
						finish();
					}
				});
				confirmDialog.show();
			}

		}
	};

	/**
	 * 初始化更新View
	 */
	private void initUpdateUI() {
		this.updateNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		this.updateNotification = new Notification();
		updatePendingIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
		// 设置通知栏显示内容
		updateNotification.icon = R.mipmap.ic_launcher;
		updateNotification.flags = Notification.FLAG_NO_CLEAR;
		View view = LayoutInflater.from(context).inflate(R.layout.dialog_download, null);
		if (AppContext.screenWidth == 720) {
			RelativeLayout rl_myDialog = (RelativeLayout) view.findViewById(R.id.rl_downloadDialog);
			ViewGroup.LayoutParams lp = rl_myDialog.getLayoutParams();
			int width = AppContext.screenWidth * 85 / 100;
			lp.width = width;
			if (AppContext.screenWidth == 720) {
				width = AppContext.screenWidth * 4 / 5;
			}
			lp.width = width;
			rl_myDialog.setLayoutParams(lp);
		}
		btn_left = (Button) view.findViewById(R.id.btn_left);
		btn_right = (Button) view.findViewById(R.id.btn_right);
		if (isForceUpdate) {
			view.findViewById(R.id.ll).setVisibility(View.GONE);
		} else {
			btn_left.setText(R.string.app_cancel);
			btn_right.setText(R.string.download_inBackground);
		}
		bar = (ProgressBar) view.findViewById(R.id.pb);
		tv_total = (TextView) view.findViewById(R.id.tv_total);

		btn_left.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelable.cancel();
				pd.cancel();
				finish();
			}
		});
		btn_right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				UITools.showToastShortDuration(context, R.string.downloading_inBackground);
				isBackground = true;
				pd.cancel();
				finish();
				// PackageManager pm = getPackageManager();
				// ResolveInfo homeInfo = pm.resolveActivity(new
				// Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME),
				// 0);
				// ActivityInfo ai = homeInfo.activityInfo;
				// Intent startIntent = new Intent(Intent.ACTION_MAIN);
				// startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				// startIntent.setComponent(new ComponentName(ai.packageName,
				// ai.name));
				// startActivity(startIntent);
			}
		});
		pd = new Dialog(context, R.style.DialogStyle);
		pd.setContentView(view);
		pd.setCancelable(false);
	}

	/**
	 * 下载APK
	 */
	private void downloadApk() {
		pd.show();
		downloadUrl = appVersionBean.versionDownloadUrl;
		StringBuffer sb = new StringBuffer();
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			sb.append(Environment.getExternalStorageDirectory());
			sb.append("/" + AppConfig.APP_NAME + "/download/");
		} else {
			sb.append(context.getFilesDir().getPath());
		}
		if (TextUtils.isEmpty(downloadUrl)) {
			LogUtil.e("downloadUrl may not be Null !");
			onFail(new Exception());
			return;
		}
		sb.append(downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1, downloadUrl.lastIndexOf(".")));
		sb.append(".apk");
		LogUtil.i("DownLoad_APK:" + sb);
		// 显示文件大小格式：2个小数点显示
		final DecimalFormat df = new DecimalFormat("0.00");
		final String downloading = getString(R.string.downloading_tip);
		File file = new File(sb.toString());
		if (file.exists())
			file.delete();
		RequestParams params = new RequestParams(downloadUrl);
		params.setAutoResume(true);
		params.setAutoRename(true);
		params.setSaveFilePath(sb.toString());
		params.setCancelFast(true);
		params.setMaxRetryCount(3);
		Callback.Cancelable cancelable = x.http().get(params, new DownloadCallback(){
			@Override
			public void onSuccess(File result) {
				try {
					installAPK(result);
				} catch (Exception e) {
					LogUtil.e(e.getMessage(), e);
					UITools.showToastShortDuration(context, R.string.error_download_install_fail);
				}
			}

			@Override
			public void onLoading(long total, long current, boolean isDownloading) {
				super.onLoading(total, current, isDownloading);
				bar.setProgress((int) (((float) current / total) * 100));
				if (TextUtils.isEmpty(totalFileSize))
					totalFileSize = df.format((float) total / 1024 / 1024) + "MB";
				tv_total.setText(df.format((float) current / 1024 / 1024) + "MB/" + totalFileSize);
				if (isBackground)
					// 为了防止频繁的通知导致应用吃紧，百分比增加10才通知一次
					if ((downloadCount == 0) || (int) (current * 100 / total) - 10 > downloadCount) {
						downloadCount += 10;
						updateNotification.setLatestEventInfo(context, downloading, (int) current * 100 / total + "%", updatePendingIntent);
						updateNotificationManager.notify(0, updateNotification);
					}
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				LogUtil.e(ex.getMessage(), ex);
				onFail(ex);
			}
		});
	}

	/**
	 * 下载失败
	 */
	private void onFail(Throwable arg0) {
		UITools.showToastShortDuration(context, AppException.convertException(arg0.getCause() != null ? (Exception) arg0.getCause() : null).getMessage(getString(R.string.error_download_fail)));
		updateNotificationManager.cancelAll();
		pd.dismiss();
		finish();
	}

	/**
	 * 安装APP
	 * 
	 * @param updateFile
	 */
	@SuppressWarnings("deprecation")
	private void installAPK(File updateFile) {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			LogUtil.e(e.getMessage(), e);
		} // 等待三秒
		UITools.showToastShortDuration(context, getString(isBackground ? R.string.download_complete_byNotify : R.string.download_complete_install));
		// 安装
		// 添加系统权限
		// [文件夹705:drwx---r-x]
		String[] args1 = { "chmod", "705", updateFile.getParent() };
		exec(args1);
		// [文件604:-rw----r--]
		String[] args2 = { "chmod", "604", updateFile.getPath() };
		exec(args2);
		pd.dismiss();
		Uri uri = Uri.fromFile(updateFile);
		Intent installIntent = new Intent(Intent.ACTION_VIEW);
		installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		installIntent.setDataAndType(uri, getMIMEType(updateFile));
		if (isBackground) {
			updatePendingIntent = PendingIntent.getActivity(context, 0, installIntent, 0);

			updateNotification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
			updateNotification.tickerText = getString(R.string.download_complete_onclick);
			updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
			updateNotification.defaults |= Notification.DEFAULT_VIBRATE;
			updateNotification.setLatestEventInfo(context, getString(R.string.app_name), getString(R.string.download_complete_onclick), updatePendingIntent);
			updateNotificationManager.notify(0, updateNotification);
		} else {
			context.startActivity(installIntent);
			finish();
		}
	}

	/**
	 * 
	 * @param f
	 * @return
	 */
	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
		if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	/** 执行Linux命令，并返回执行结果。 */
	public static String exec(String[] args) {
		String result = "";
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		Process process = null;
		InputStream errIs = null;
		InputStream inIs = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int read = -1;
			process = processBuilder.start();
			errIs = process.getErrorStream();
			while ((read = errIs.read()) != -1) {
				baos.write(read);
			}
			// baos.
			inIs = process.getInputStream();
			while ((read = inIs.read()) != -1) {
				baos.write(read);
			}
			byte[] data = baos.toByteArray();
			result = new String(data);
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
		} finally {
			try {
				if (errIs != null) {
					errIs.close();
				}
				if (inIs != null) {
					inIs.close();
				}
			} catch (IOException e) {
				LogUtil.e(e.getMessage(), e);
			}
			if (process != null) {
				process.destroy();
			}
		}
		return result;
	}

	@Override
	public void onBackPressed() {
		if (isForceUpdate) { // 强制升级
			AppManager.getAppManager().AppExitPrompt(context);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onDestroy() {
		if (pd != null)
			pd.dismiss();
		super.onDestroy();
	}
}
