package org.smart.library.listener;

import org.xutils.common.Callback;

import java.io.File;

/*package*/ public class DownloadCallback implements
        Callback.CommonCallback<File>,
        Callback.ProgressCallback<File>,
        Callback.Cancelable {

    private boolean cancelled = false;
    private Cancelable cancelable;

    public DownloadCallback() {
    }

    public void setCancelable(Cancelable cancelable) {
        this.cancelable = cancelable;
    }

    @Override
    public void onWaiting() {
    }

    @Override
    public void onStarted() {
    }

    @Override
    public void onLoading(long total, long current, boolean isDownloading) {
    }

    @Override
    public void onSuccess(File result) {
    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {
    }

    @Override
    public void onCancelled(CancelledException cex) {
    }

    @Override
    public void onFinished() {
        cancelled = false;
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (cancelable != null) {
            cancelable.cancel();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
}
