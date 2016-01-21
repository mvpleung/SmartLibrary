/*
 * @Project: xbtrip
 * @File: OnExtraEventListener.java
 *         created on: 2014年7月28日
 * @Copyright: 2014 www.exiaobai.com Inc. All Rights Reserved.
 */
package org.smart.library.listener;

import android.view.View;

/**
 * 数据改变监听
 * 
 * @author LiangZiChao
 * @param <T>
 *         created on 2014-7-28上午11:31:30
 */
public abstract class OnExtraEventListener<T> {

	public void onExtraEventBefore(int position, T t) {
	};

	/**
	 * 视图点击监听
	 * 
	 * @param view
	 * @param position
	 * @param t
	 */
	public abstract void onExtraEvent(View view, int position, T t);
}
