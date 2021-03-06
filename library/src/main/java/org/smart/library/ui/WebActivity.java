/*
 * @Project: xbtrip
 * @File: WebActivity.java
 *         created on: 2014年9月25日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package org.smart.library.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.control.AppManager;
import org.smart.library.tools.SafeWebViewBridge.InjectedChromeClient;
import org.smart.library.widget.FastWebView;
import org.smart.library.control.L;

/**
 * web浏览器，会以 “JsBridge” 作为JS桥接对象，用于Java和JS互通
 * 注入类：请传递 injectedCls 参数，或者继承 WebActivity 重写 getJnjectedClz
 *
 * @author LiangZiChao
 *         created on 2014-9-5下午3:28:52
 */
@SuppressLint("SetJavaScriptEnabled")
public class WebActivity extends Activity {

    /**
     * 显示标题栏
     */
    public final static String WEB_TITLE_BAR = "showTitleBar";
    /**
     * Web标题
     */
    public final static String WEB_TITLE = "webTitle";
    /**
     * WebUrl
     */
    public final static String WEB_URL = "webUrl";
    /**
     * WebContent
     */
    public final static String WEB_CONTENT = "webContent";
    /**
     * StatusBarResource
     */
    public final static String STATUSBAR_RESOURCE = "statusBarResource";
    /**
     * StatusBarColor
     */
    public final static String STAUSBAR_COLOR = "statusBarColor";
    /**
     * injectedCls
     */
    public final static String INJECTEDCLZ = "injectedCls";

    private View mTitleView;
    private TextView mTextTitle;
    private FastWebView mWebContent;
    private ProgressBar mProgressBar;

    private String title, url, webContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTheme(android.R.style.Theme_Light_NoTitleBar);
        setContentView(R.layout.activity_web);
        initUI();
        Intent intent = getIntent();
        mTitleView.setVisibility(intent.getBooleanExtra(WEB_TITLE_BAR, true) ? View.VISIBLE : View.GONE);
        title = intent.getStringExtra(WEB_TITLE);
        mTextTitle.setText(title);
        // 路径地址
        url = intent.getStringExtra(WEB_URL);
        webContent = intent.getStringExtra(WEB_CONTENT);
        mWebContent.setWebViewClient(new MyWebViewClient());
        mWebContent.setWebChromeClient(new MyChromeClient("JsBridge", getJnjectedClz()));
        mWebContent.setDownloadListener(new MyWebViewDownLoadListener());
        WebSettings webSettings = mWebContent.getSettings();
        webSettings.setBlockNetworkImage(true);
        // webSettings.setBuiltInZoomControls(true);// 设置显示缩放按钮
        webSettings.setSupportZoom(true); // 是否支持缩放
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        mWebContent.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        mWebContent.requestFocusFromTouch();
        // mWebContent.setInitialScale(10);
        if (TextUtils.isEmpty(url)) {
            url = "file:///android_asset/web_content.html";
        }
        mWebContent.loadUrl(url); // 载入地址
    }

    private void initUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SystemBarTintManager mTintManager = new SystemBarTintManager(this);
            mTintManager.setStatusBarTintEnabled(true);
            mTintManager.setNavigationBarTintEnabled(true);
            int statusBarColor = getIntent().getIntExtra(STAUSBAR_COLOR, -1);
            if (statusBarColor != -1)
                mTintManager.setStatusBarTintColor(statusBarColor);
            else
                mTintManager.setStatusBarTintResource(getIntent().getIntExtra(STATUSBAR_RESOURCE, R.color.colorPrimaryDark));
        }
        mTitleView = findViewById(R.id.title_layout);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar1);
        mWebContent = (FastWebView) findViewById(R.id.wv_banner);
        mTextTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.im_title_back).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    public WebView getWebView() {
        return mWebContent;
    }

    public Class<?> getJnjectedClz() {
        return (Class<?>) getIntent().getSerializableExtra(INJECTEDCLZ);
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (TextUtils.isEmpty(title))
                mTextTitle.setText(view.getTitle());
            view.getSettings().setBlockNetworkImage(false);
            super.onPageFinished(view, url);
            if (!TextUtils.isEmpty(webContent))
                view.loadUrl("javascript:showWebContent('" + webContent + "')");
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            mProgressBar.setVisibility(View.GONE);
            mWebContent.loadDataWithBaseURL(null, getString(R.string.app_socket_timeout), "text/html", "utf-8", null);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();// 接受证书
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            return super.shouldOverrideKeyEvent(view, event);
        }
    }

    private class MyChromeClient extends InjectedChromeClient {

        public MyChromeClient(String injectedName, Class<?> injectedCls) {
            super(injectedName, injectedCls);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            L.i("Progress:" + newProgress);
            mProgressBar.setProgress(newProgress);
            mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
        }

    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            L.i("url=" + url);
            L.i("userAgent=" + userAgent);
            L.i("contentDisposition=" + contentDisposition);
            L.i("mimetype=" + mimetype);
            L.i("contentLength=" + contentLength);
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebContent != null) {
            if (mWebContent.canGoBack())
                mWebContent.goBack();
            else {
                setResult(RESULT_OK);
                finish();
            }
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebContent != null) {
            mWebContent.removeAllViews();
            mWebContent.setVisibility(View.GONE);
            mWebContent.destroy();
        }
        super.onDestroy();
    }
}
