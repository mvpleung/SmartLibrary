/**
 * Copyright 2013 LiangZiChao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joanzapata.android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import static com.joanzapata.android.BaseAdapterHelper.get;

/**
 * PagerAdapter
 * 
 * @author LiangZiChao
 * @Data 2015-9-2日
 * @Package com.joanzapata.android
 */
public abstract class AbstractPagerAdapter<T> extends BasePagerAdapter<T, BaseAdapterHelper> {

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 */
	public AbstractPagerAdapter(Context context) {
		super(context, 0);
	}

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 */
	public AbstractPagerAdapter(Context context, int layoutResId) {
		super(context, layoutResId);
	}

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
	 * 
	 * @param context
	 *            The context.
	 * @param layoutResId
	 *            The layout resource id of each item.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public AbstractPagerAdapter(Context context, int layoutResId, List<T> data) {
		super(context, layoutResId, data);
	}

	protected BaseAdapterHelper getAdapterHelper(int position, View convertView, ViewGroup parent) {
		return get(context, convertView, parent, layoutResId, position);
	}

}
