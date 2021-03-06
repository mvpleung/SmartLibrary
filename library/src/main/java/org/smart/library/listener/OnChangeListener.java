package org.smart.library.listener;

import android.view.View;

/**
 * 状态改变监听
 * 
 * @author LiangZiChao
 *         created on 2015年7月1日
 */
public abstract class OnChangeListener {

	public boolean onBefore(View view, boolean isToggle) {
		return true;
	};

	public abstract void onChange(View view, boolean isToggle);
}
