package com.exiaobai.library.ui;

import java.io.Serializable;

import android.support.v4.app.Fragment;

import com.lidroid.xutils.util.LogUtils;

/**
 * Fragment基类
 * 
 * @author LiangZiChao
 * @Data 2015年6月11日
 */
public class BaseFragment extends Fragment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5822180054871773357L;

	protected String mTitle;

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public <T> void onEvent(T t) {

	}

	public <T> void onEvent(int position, T t) {

	}

	public static <T> T createFragment(Class<T> cls) {
		try {
			return cls.getConstructor(new Class[0]).newInstance(new Object[0]);
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
			return null;
		}
	}
}
