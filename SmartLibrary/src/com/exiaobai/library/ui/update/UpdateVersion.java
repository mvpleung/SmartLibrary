package com.exiaobai.library.ui.update;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.baoyz.pg.PG;
import com.exiaobai.library.R;
import com.exiaobai.library.control.AppException;
import com.exiaobai.library.control.Version;
import com.exiaobai.library.model.AppVersionBean;
import com.exiaobai.library.tools.JudgmentLegal;
import com.exiaobai.library.tools.UITools;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.util.LogUtils;

/**
 * 
 * @description 版本更新
 * @author LiangZiChao
 * @Date 2014-9-4下午1:54:56
 * @Package com.xiaobai.xbtrip.update
 */
@SuppressLint({ "HandlerLeak", "DefaultLocale" })
public class UpdateVersion {

	private static final Lock LOCK = new ReentrantLock();

	private Context context;
	private HttpUtils mHttpUtils;
	private boolean isUpdateDialog;
	private AppVersionBean mVersionBean;
	private RequestParams mParams;
	private String mUpdateUrl;
	private String mVersionCode;
	private UpdateListener mUpdateListener;
	private VersionParseListener mParseListener;

	private UpdateVersion() {
	}

	private static class UpdateVersionHolder {
		static UpdateVersion updateVersion = new UpdateVersion();
	}

	public static UpdateVersion getInstance(Context mContext) {
		try {
			LOCK.lock();
			UpdateVersion updateVersion = UpdateVersionHolder.updateVersion;
			if (updateVersion == null) {
				throw new IllegalStateException("AnyVersion NOT init !");
			}
			if (mContext == null) {
				throw new NullPointerException("Application Context CANNOT be null !");
			}
			updateVersion.setContext(mContext);
			return updateVersion;
		} finally {
			LOCK.unlock();
		}
	}

	/**
	 * 
	 * @param mContext
	 * @param mUpdateUrl
	 *            更新URL
	 */
	public static void init(String mUrl, VersionParseListener mParseListener) {
		UpdateVersion mUpdateVersion = UpdateVersionHolder.updateVersion;
		if (mParseListener == null) {
			throw new NullPointerException("Parser CANNOT be null !");
		}
		if (TextUtils.isEmpty(mUrl))
			throw new NullPointerException("URL CANNOT be null !");
		mUpdateVersion.mUpdateUrl = mUrl;
		if (mUpdateVersion.mParseListener == null)
			mUpdateVersion.mParseListener = mParseListener;
		mUpdateVersion.mHttpUtils = new HttpUtils();
		mUpdateVersion.mHttpUtils.configRequestRetryCount(3);
	}

	public void setContext(Context mContext) {
		this.context = mContext;
	}

	/**
	 * 获取最新版本信息（不弹窗提示更新）
	 * 
	 * @param silently
	 *            是否是静默检测
	 * @param isUpdateDialog
	 *            true：弹窗更新 false：仅获取信息
	 */
	public void checkVersion(boolean silently, boolean isUpdateDialog) {
		this.isUpdateDialog = isUpdateDialog;
		getNewVersion(silently);
	}

	/**
	 * 获取最新版本信息（不弹窗提示更新）
	 * 
	 * @param silently
	 *            是否是静默检测
	 * @param isUpdateDialog
	 *            true：弹窗更新 false：仅获取信息
	 */
	public void checkVersion(boolean silently, boolean isUpdateDialog, UpdateListener mUpdateListener) {
		this.isUpdateDialog = isUpdateDialog;
		this.mUpdateListener = mUpdateListener;
		getNewVersion(silently);
	}

	/**
	 * 获取版本信息
	 * 
	 * @param silently
	 */
	public void getNewVersion(final boolean silently) {
		if (mVersionBean != null && isUpdateDialog)
			updateVersion(!silently);
		else if (mVersionBean == null) {
			if (mParams == null && mParseListener != null)
				mParams = mParseListener.getRequestParams();
			if (mParams != null)
				LogUtils.i("RequestParams:" + mParams.toString());
			mHttpUtils.send(HttpMethod.POST, mUpdateUrl, mParams, new RequestCallBack<String>() {

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					LogUtils.e(arg0.getMessage(), arg0);
					UITools.dismissLoading();
					if (!silently)
						UITools.showToastShortDuration(context, AppException.convertException(arg0.getCause()).getMessage(context.getString(R.string.error_version_getNew_fail)));
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					UITools.dismissLoading();
					if (arg0 != null && arg0.result != null) {
						LogUtils.i("Result:" + arg0.result);
						if (mParseListener != null)
							try {
								mVersionBean = mParseListener.parseVersion(arg0.result);
								if (mVersionBean != null) {
									mVersionCode = mVersionBean.versionCode;
									boolean isNeedUpdate = isNeedUpdate();
									if (isUpdateDialog) {
										updateVersion(!silently);
									}
									if (mUpdateListener != null)
										mUpdateListener.onUpdate(mVersionBean, isNeedUpdate);
								} else if (mUpdateListener != null)
									mUpdateListener.onUpdate(mVersionBean, false);
							} catch (Exception e) {
								LogUtils.e(e.getMessage(), e);
								if (!silently)
									UITools.showToastShortDuration(context, R.string.error_version_getNew_fail);
							}
					} else {
						mVersionCode = String.valueOf(Version.getAppVersionCode(context));
					}
				}

				@Override
				public void onStart() {
					if (!silently)
						UITools.showDialogLoading(context);
				}

			});
		}
	}

	/**
	 * 提示版本更新
	 * 
	 * @param appVersionBean
	 * @param isShowToast
	 */
	public boolean updateVersion(boolean isShowToast) {
		boolean isNeedUpdate = isNeedUpdate();
		if (isNeedUpdate) {
			if (mVersionBean != null && context != null) {
				Parcelable mPar = PG.convertParcelable(mVersionBean);
				if (mPar != null)
					UITools.startToNextActivity(context, UpdateActivity.class, UpdateActivity.VERSION_BEAN, PG.convertParcelable(mVersionBean));
			}
		} else {
			if (isShowToast)
				UITools.showToastShortDuration(context, R.string.version_isNew);
		}
		return isNeedUpdate;
	}

	/**
	 * 是否需要升级
	 * 
	 * @param versionName
	 * @return
	 */
	public boolean isNeedUpdate() {
		return isNeedUpdate(mVersionCode);
	}

	/**
	 * 是否需要升级
	 * 
	 * @param versionName
	 * @return
	 */
	public boolean isNeedUpdate(String versionCode) {
		int localVersionCode = Version.getAppVersionCode(context);
		int webVersionCode = JudgmentLegal.isNumeric(versionCode) ? Integer.parseInt(versionCode) : 0;
		return !TextUtils.isEmpty(versionCode) ? localVersionCode < webVersionCode : false;
	}

	public AppVersionBean getVersionBean() {
		return mVersionBean;
	}

	public void setVersionBean(AppVersionBean mVersionBean) {
		this.mVersionBean = mVersionBean;
	}

	public interface VersionParseListener {

		public RequestParams getRequestParams();

		public AppVersionBean parseVersion(String mResult) throws Exception;
	}

	public interface UpdateListener {

		/**
		 * 
		 * @param isNeedUpdate
		 *            是否需要升级
		 */
		public void onUpdate(AppVersionBean mVersion, boolean isNeedUpdate);
	}
}
