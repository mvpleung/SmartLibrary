package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;

/**
 * 解决webview占用内存过大的问题
 * 
 * @author LiangZiChao
 *         created on 2014-9-5下午3:28:41
 *         In the com.xiaobai.xbtrip.view
 */
@SuppressLint({ "SetJavaScriptEnabled", "MissingSuperCall" })
public class FastWebView extends WebView {
	private boolean is_gone = false;

	public FastWebView(Context context) {
		super(context);
	}

	public FastWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		if (visibility == View.GONE) {
			try {
				WebView.class.getMethod("onPause").invoke(this);// stop flash
			} catch (Exception e) {
			}
			this.pauseTimers();
			this.is_gone = true;
		} else if (visibility == View.VISIBLE) {
			try {
				WebView.class.getMethod("onResume").invoke(this);// resume flash
			} catch (Exception e) {
			}
			this.resumeTimers();
			this.is_gone = false;
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if (this.is_gone) {
			try {
				this.destroy();
			} catch (Exception e) {
			}
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public WebSettings getSettings() {
		WebSettings webSettings = super.getSettings();
		webSettings.setDefaultTextEncodingName("utf-8");// 设置文本编码
		webSettings.setSavePassword(false); // 是否保存密码
		webSettings.setSaveFormData(false); // 是否保存产生的数据
		webSettings.setPluginState(PluginState.ON); // 插件的状态
		webSettings.setJavaScriptEnabled(true); // 是否启用JAVA脚本
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setNeedInitialFocus(true);// 当webview调用requestFocus时为webview设置节点
		webSettings.setRenderPriority(RenderPriority.HIGH);
		webSettings.setAllowFileAccess(true);// 允许访问文件
		webSettings.setAppCacheEnabled(true);
		webSettings.setUseWideViewPort(true);
		return webSettings;
	}

}