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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * 扩展Adapter
 * 
 * @author LiangZiChao
 * @Data 2015年6月18日
 * @Package com.joanzapata.android
 */
public abstract class BaseExpandAdapter<T, H extends BaseAdapterHelper, K> extends BaseExpandableListAdapter {

	protected final Context context;

	protected int groupLayoutResId, childLayoutResId;

	protected final List<T> groupData;

	protected final Map<T, List<K>> dataMap;

	protected boolean displayIndeterminateProgress = false;

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 */
	public BaseExpandAdapter(Context context) {
		this(context, 0, 0, null);
	}

	/**
	 * Create a QuickAdapter.
	 * 
	 * @param context
	 *            The context.
	 * @param groupLayoutResId
	 *            The layout resource id of each groupItem.
	 * @param childLayoutResId
	 *            The layout resource id of each childItem.
	 */
	public BaseExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId) {
		this(context, groupLayoutResId, childLayoutResId, null);
	}

	/**
	 * Same as QuickAdapter#QuickAdapter(Context,int) but with some
	 * initialization data.
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
	public BaseExpandAdapter(Context context, int groupLayoutResId, int childLayoutResId, Map<T, List<K>> dataMap) {
		this.dataMap = dataMap == null ? new HashMap<T, List<K>>() : new HashMap<T, List<K>>(dataMap);
		this.groupData = dataMap != null ? new ArrayList<T>(dataMap.keySet()) : new ArrayList<T>();
		this.context = context;
		this.groupLayoutResId = groupLayoutResId;
		this.childLayoutResId = childLayoutResId;
	}

	@Override
	public int getGroupCount() {
		int extra = displayIndeterminateProgress ? 1 : 0;
		return groupData.size() + extra;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int extra = displayIndeterminateProgress ? 1 : 0;
		return getChildListSize(groupPosition) + extra;
	}

	public List<K> getChildList(int groupPosition) {
		return dataMap.get(getGroup(groupPosition));
	}

	public int getChildListSize(int groupPosition) {
		List<K> mChildList = getChildList(groupPosition);
		return mChildList != null ? mChildList.size() : 0;
	}

	@Override
	public T getGroup(int groupPosition) {
		if (groupPosition >= groupData.size())
			return null;
		return groupData.get(groupPosition);
	}

	@Override
	public K getChild(int groupPosition, int childPosition) {
		List<K> mChildList = getChildList(groupPosition);
		int size = mChildList != null ? mChildList.size() : 0;
		if (childPosition >= size)
			return null;
		return size > 0 ? mChildList.get(childPosition) : null;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildTypeCount() {
		return 2;
	}

	@Override
	public int getChildType(int groupPosition, int childPosition) {
		return showIndeterminateProgress() ? childPosition >= getChildListSize(groupPosition) ? 1 : 0 : 0;
	}

	@Override
	public int getGroupTypeCount() {
		return 2;
	}

	@Override
	public int getGroupType(int groupPosition) {
		return showIndeterminateProgress() ? groupPosition >= groupData.size() ? 1 : 0 : 0;
	}

	/**
	 * 是否显示加载更多进度条
	 * 
	 * @return
	 */
	public boolean showIndeterminateProgress() {
		return false;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		if (getGroupType(groupPosition) == 0) {
			return createConvertView(groupPosition, convertView, parent, isExpanded);
		}
		return createIndeterminateProgressView(convertView, parent);
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		if (getChildType(groupPosition, childPosition) == 0) {
			return createConvertView(groupPosition, childPosition, convertView, parent);
		}

		return createIndeterminateProgressView(convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public View createConvertView(int position, View convertView, ViewGroup parent, boolean isExpanded) {
		final H helper = getAdapterHelper(position, convertView, parent);
		T item = getGroup(position);
		helper.setAssociatedObject(item);
		convertGroup(helper, item, isExpanded);
		return helper.getView();
	}

	public View createConvertView(int groupPosition, int childPosition, View convertView, ViewGroup parent) {
		final H helper = getAdapterHelper(groupPosition, childPosition, convertView, parent);
		K item = getChild(groupPosition, childPosition);
		helper.setAssociatedObject(item);
		convertChild(helper, item);
		return helper.getView();
	}

	private View createIndeterminateProgressView(View convertView, ViewGroup parent) {
		if (convertView == null) {
			FrameLayout container = new FrameLayout(context);
			container.setForegroundGravity(Gravity.CENTER);
			ProgressBar progress = new ProgressBar(context);
			container.addView(progress);
			convertView = container;
		}
		return convertView;
	}

	public Map<T, List<K>> getData() {
		return dataMap;
	}

	public void add(T elem, List<K> child) {
		if (elem != null && child != null) {
			groupData.add(elem);
			dataMap.put(elem, child);
			notifyDataSetChanged();
		}
	}

	public void addAll(HashMap<T, List<K>> elem) {
		if (elem != null) {
			dataMap.putAll(elem);
			notifyDataSetChanged();
		}
	}

	public void set(T elem, List<K> child) {
		add(elem, child);
	}

	public void remove(T elem) {
		if (elem != null) {
			groupData.remove(elem);
			dataMap.remove(elem);
			notifyDataSetChanged();
		}
	}

	public void remove(int index) {
		T elem = groupData.remove(index);
		dataMap.remove(elem);
		notifyDataSetChanged();
	}

	public void replaceAll(HashMap<T, List<K>> elem) {
		groupData.clear();
		dataMap.clear();
		if (elem != null) {
			dataMap.putAll(elem);
			groupData.addAll(dataMap.keySet());
		}
		notifyDataSetChanged();
	}

	public boolean contains(T elem) {
		return dataMap.containsKey(elem);
	}

	/** Clear data list */
	public void clear() {
		groupData.clear();
		dataMap.clear();
	}

	/** Clear data list */
	public void clearNotify() {
		clear();
		notifyDataSetChanged();
	}

	public void showIndeterminateProgress(boolean display) {
		if (display == displayIndeterminateProgress)
			return;
		displayIndeterminateProgress = display;
		notifyDataSetChanged();
	}

	/**
	 * Group扩展
	 * 
	 * @param helper
	 *            A fully initialized helper.
	 * @param item
	 *            The item that needs to be displayed.
	 */
	protected abstract void convertGroup(H helper, T item, boolean isExpanded);

	/**
	 * Child扩展
	 * 
	 * @param helper
	 *            A fully initialized helper.
	 * @param item
	 *            The item that needs to be displayed.
	 */
	protected abstract void convertChild(H helper, K item);

	/**
	 * You can override this method to use a custom BaseAdapterHelper in order
	 * to fit your needs
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to convert this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
	 *            ).
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return An instance of BaseAdapterHelper
	 */
	protected abstract H getAdapterHelper(int position, View convertView, ViewGroup parent);

	/**
	 * 扩展列表
	 * 
	 * @param groupPosition
	 * @param childPosition
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract H getAdapterHelper(int groupPosition, int childPosition, View convertView, ViewGroup parent);
}
