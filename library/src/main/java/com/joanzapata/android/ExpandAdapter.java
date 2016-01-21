/**
 * Copyright 2015 LiangZiChao
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
import java.util.Map;

import static com.joanzapata.android.BaseAdapterHelper.get;

/**
 * 扩展列表
 * 
 * @author LiangZiChao
 * @param <K>
 *         created on 2015年6月18日
 *
 */
public abstract class ExpandAdapter<T, K> extends BaseExpandAdapter<T, BaseAdapterHelper, K> {

	/**
	 * Create a ExpandableAdapter.
	 * 
	 * @param context
	 *            The context.
	 */
	public ExpandAdapter(Context context) {
		super(context, 0, 0);
	}

	/**
	 * Create a ExpandableAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param groupLayoutResId
	 *            The layout resource id of each groupItem.
	 * @param childLayoutResId
	 *            The layout resource id of each childItem.
	 */
	public ExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId) {
		super(context, groupLayoutResId, childLayoutResId);
	}

	/**
	 * Same as ExpandableAdapter#ExpandableAdapter(Context,int,int) but with
	 * some initialization data.
	 * 
	 * @param context
	 *            The context.
	 * @param groupLayoutResId
	 *            The layout resource id of each groupItem.
	 * @param childLayoutResId
	 *            The layout resource id of each childItem.
	 * @param dataMap
	 *            A new list is created out of this one to avoid mutable list
	 */
	public ExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId, Map<T, List<K>> dataMap) {
		super(context, groupLayoutResId, childLayoutResId, dataMap);
	}

	protected BaseAdapterHelper getAdapterHelper(int position, View convertView, ViewGroup parent) {
		return get(context, convertView, parent, groupLayoutResId, position);
	}

	@Override
	protected BaseAdapterHelper getAdapterHelper(int groupPosition, int childPosition, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return get(context, convertView, parent, childLayoutResId, childPosition);
	}
}
