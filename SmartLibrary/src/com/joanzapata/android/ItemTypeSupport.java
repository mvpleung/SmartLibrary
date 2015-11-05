package com.joanzapata.android;

/**
 * 多Item布局支持
 * 
 * @author MVP
 *
 */
public interface ItemTypeSupport<T> {
	
	int getLayoutId(int position, T t);

	int getViewTypeCount();

	int getItemViewType(int position, T t);
}
