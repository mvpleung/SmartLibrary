package org.smart.library.ui.update;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
import android.text.TextUtils;

import com.baoyz.pg.PG;

import org.smart.library.R;
import org.smart.library.control.AppException;
import org.smart.library.control.Version;
import org.smart.library.model.AppVersionBean;
import org.smart.library.tools.JudgmentLegal;
import org.smart.library.tools.UITools;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author LiangZiChao
 * 版本更新
 *         created on 2014-9-4下午1:54:56
 *         In the com.xiaobai.xbtrip.update
 */
@SuppressLint({"HandlerLeak", "DefaultLocale"})
public class UpdateVersion {

    private static final Lock LOCK = new ReentrantLock();

    private Context context;
    private boolean isUpdateDialog;
    private AppVersionBean mVersionBean;
    private RequestParams mParams;
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
     * @param mParseListener 解析监听
     */
    public static void init(VersionParseListener mParseListener) {
        UpdateVersion mUpdateVersion = UpdateVersionHolder.updateVersion;
        if (mParseListener == null) {
            throw new NullPointerException("Parser CANNOT be null !");
        }
        if (mUpdateVersion.mParseListener == null)
            mUpdateVersion.mParseListener = mParseListener;
    }

    public void setContext(Context mContext) {
        this.context = mContext;
    }

    /**
     * 获取最新版本信息（不弹窗提示更新）
     *
     * @param silently       是否是静默检测
     * @param isUpdateDialog true：弹窗更新 false：仅获取信息
     */
    public void checkVersion(boolean silently, boolean isUpdateDialog) {
        this.isUpdateDialog = isUpdateDialog;
        getNewVersion(silently);
    }

    /**
     * 获取最新版本信息（不弹窗提示更新）
     *
     * @param silently       是否是静默检测
     * @param isUpdateDialog true：弹窗更新 false：仅获取信息
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
            if (mParams == null || TextUtils.isEmpty(mParams.getUri()))
                throw new NullPointerException("URL CANNOT be null !");
            LogUtil.i("RequestParams:" + mParams.toString());
            if (!silently)
                UITools.showDialogLoading(context);
            x.http().request(mParams.getMethod(), mParams, new Callback.CommonCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    if (!TextUtils.isEmpty(result)) {
                        LogUtil.i("Result:" + result);
                        if (mParseListener != null)
                            try {
                                mVersionBean = mParseListener.parseVersion(result);
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
                                LogUtil.e(e.getMessage(), e);
                                if (!silently)
                                    UITools.showToastShortDuration(context, R.string.error_version_getNew_fail);
                            }
                    } else {
                        mVersionCode = String.valueOf(Version.getAppVersionCode(context));
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    LogUtil.e(ex.getMessage(), ex);
                    if (!silently)
                        UITools.showToastShortDuration(context, AppException.convertException(ex.getCause()).getMessage(context.getString(R.string.error_version_getNew_fail)));
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    UITools.dismissLoading();
                }
            });
        }
    }

    /**
     * 提示版本更新
     *
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
     * @return
     */
    public boolean isNeedUpdate() {
        return isNeedUpdate(mVersionCode);
    }

    /**
     * 是否需要升级
     *
     * @param versionCode
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
         * @param isNeedUpdate 是否需要升级
         */
        public void onUpdate(AppVersionBean mVersion, boolean isNeedUpdate);
    }
}
