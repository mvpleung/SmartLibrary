package org.smart.library.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.smart.library.control.L;

import java.io.Serializable;

/**
 * Fragment基类
 * 
 * @author LiangZiChao
 *         created on 2015年6月11日
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

	public BaseFragment setTitle(String mTitle) {
		this.mTitle = mTitle;
		return this;
	}

	public <T> void onEvent(T t) {

	}

	public <T> void onEvent(int position, T t) {

	}

	public static <T> T createFragment(Class<T> cls) {
		try {
			return cls.newInstance();
		} catch (Exception e) {
			L.e(e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// for fix a known issue in support library
		// https://code.google.com/p/android/issues/detail?id=19917
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}
}
