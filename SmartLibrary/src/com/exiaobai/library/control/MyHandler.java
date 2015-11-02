/*
 * @Project: xbtrip
 * @File: MyHandler.java
 * @Date: 2014年7月24日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package com.exiaobai.library.control;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.ComponentName;
import android.os.Handler;
import android.os.Message;

/**
 * 弱引用Handler
 * 
 * @author LiangZiChao
 * @Data 2014-7-24下午6:38:15
 * @Package com.xiaobai.mobile.network
 */
public class MyHandler extends Handler {
	WeakReference<Activity> mActivityReference;

	public MyHandler(Activity activity) {
		setWeakReference(activity);
	}
	
	public void setWeakReference(Activity activity){
		mActivityReference = new WeakReference<Activity>(activity);
	}
	
	/**
	 * 获取当前Activity引用
	 * 
	 * @return
	 */
	public Activity getActivity() {
		return mActivityReference.get();
	}

	/**
	 * 引用是否为空
	 * 
	 * @return
	 */
	public boolean isActivityNull() {
		return getActivity() == null;
	}

	/**
	 * 当前Activity是否在运行
	 * 
	 * @return
	 */
	public boolean isCurrentRunning() {
		ComponentName component = AppManager.getAppManager().getRunningComponentName();
		return getActivity() == null ? false : component != null ? component.getClassName().equals(getActivity().getClass().getName()) : true;
	}
	
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
	}
	
}
