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

import java.util.List;
import java.util.Map;

import android.content.Context;

/**
 * 增强版扩展填充
 * 
 * @author LiangZiChao
 * @Data 2015年6月18日
 * @Package com.joanzapata.android
 */
public abstract class EnhancedExpandAdapter<T, K> extends ExpandAdapter<T, K> {

	/**
	 * Create a EnhancedExpandAdapter.
	 * 
	 * @param context
	 *            The context.
	 */
	public EnhancedExpandAdapter(Context context) {
		super(context, 0, 0);
	}

	/**
	 * Create a EnhancedExpandAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param groupLayoutResId
	 *            The layout resource id of each groupItem.
	 * @param childLayoutResId
	 *            The layout resource id of each childItem.
	 */
	public EnhancedExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId) {
		super(context, groupLayoutResId, childLayoutResId);
	}

	/**
	 * Same as EnhancedExpandAdapter#EnhancedExpandAdapter(Context,int) but with
	 * some initialization data.
	 * 
	 * @param context
	 *            The context.
	 * @param groupLayoutResId
	 *            The layout resource id of each groupItem.
	 * @param childLayoutResId
	 *            The layout resource id of each childItem.
	 * @param data
	 *            A new list is created out of this one to avoid mutable list
	 */
	public EnhancedExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId, Map<T, List<K>> dataMap) {
		super(context, groupLayoutResId, childLayoutResId, dataMap);
	}

	@Override
	protected final void convertGroup(BaseAdapterHelper helper, T item, boolean isExpanded) {
		boolean itemChanged = helper.associatedObject == null || !helper.associatedObject.equals(item);
		helper.associatedObject = item;
		convertGroup(helper, item, isExpanded, itemChanged);
	}

	@Override
	protected final void convertChild(BaseAdapterHelper helper, K item) {
		boolean itemChanged = helper.associatedObject == null || !helper.associatedObject.equals(item);
		helper.associatedObject = item;
		convertChild(helper, item, itemChanged);
	}

	/**
	 * @param helper
	 *            The helper to use to adapt the view.
	 * @param item
	 *            The item you should adapt the view to.
	 * @param itemChanged
	 *            Whether or not the helper was bound to another object before.
	 */
	protected abstract void convertGroup(BaseAdapterHelper helper, T item, boolean isExpanded, boolean itemChanged);

	/**
	 * @param helper
	 *            The helper to use to adapt the view.
	 * @param item
	 *            The item you should adapt the view to.
	 * @param itemChanged
	 *            Whether or not the helper was bound to another object before.
	 */
	protected abstract void convertChild(BaseAdapterHelper helper, K item, boolean itemChanged);
}
