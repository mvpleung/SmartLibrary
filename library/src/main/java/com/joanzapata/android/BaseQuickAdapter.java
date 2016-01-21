/**
 * Copyright 2013 Joan Zapata
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.joanzapata.android;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstraction class of a BaseAdapter in which you only need to provide the
 * convert() implementation.<br>
 * Using the provided BaseAdapterHelper, your code is minimalist.
 *
 * @param <T> The type of the items in the list.
 */
public abstract class BaseQuickAdapter<T, H extends BaseAdapterHelper> extends BaseAdapter {

    protected final Context context;

    protected final int layoutResId;

    protected final List<T> data;

    protected boolean displayIndeterminateProgress = false;

    protected ItemTypeSupport<T> mItemTypeSupport;

    /**
     * Create a QuickAdapter.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     */
    public BaseQuickAdapter(Context context, int layoutResId) {
        this(context, layoutResId, null);
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with some
     * initialization data.
     *
     * @param context     The context.
     * @param layoutResId The layout resource id of each item.
     * @param data        A new list is created out of this one to avoid mutable list
     */
    public BaseQuickAdapter(Context context, int layoutResId, List<T> data) {
        this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.context = context;
        this.layoutResId = layoutResId;
    }

    /**
     * Create a QuickAdapter.
     *
     * @param context          The context.
     * @param mItemTypeSupport
     */
    public BaseQuickAdapter(Context context, ItemTypeSupport<T> mItemTypeSupport) {
        this(context, null, mItemTypeSupport);
    }

    /**
     * Create a QuickAdapter.
     *
     * @param context          The context.
     * @param data
     * @param mItemTypeSupport
     */
    public BaseQuickAdapter(Context context, List<T> data, ItemTypeSupport<T> mItemTypeSupport) {
        this.data = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        this.context = context;
        this.layoutResId = -1;
        this.mItemTypeSupport = mItemTypeSupport;
    }

    @Override
    public int getCount() {
        int extra = displayIndeterminateProgress ? 1 : 0;
        return data.size() + extra;
    }

    @Override
    public T getItem(int position) {
        if (position >= data.size())
            return null;
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        if (mItemTypeSupport != null)
            return mItemTypeSupport.getViewTypeCount() + 1;
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (showIndeterminateProgress()) {
            if (mItemTypeSupport != null)
                return position >= data.size() ? 0 : mItemTypeSupport.getItemViewType(position, getItem(position));
        } else {
            if (mItemTypeSupport != null)
                return mItemTypeSupport.getItemViewType(position, getItem(position));
        }
        int size = data.size();
        return size == 0 ? 1 : position >= size ? 0 : 1;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (getItemViewType(position) == 0) {
            return createIndeterminateProgressView(convertView, parent);
        }

        return createConvertView(position, convertView, parent);
    }

    public View createConvertView(int position, View convertView, ViewGroup parent) {
        final H helper = getAdapterHelper(position, convertView, parent);
        T item = getItem(position);
        convert(helper, item);
        helper.setAssociatedObject(item);
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

    @Override
    public boolean isEnabled(int position) {
        int size = data.size();
        return size == 0 ? true : position < size;
    }

    public List<T> getData() {
        return data;
    }

    public void add(T elem) {
        if (elem != null) {
            data.add(elem);
            notifyDataSetChanged();
        }
    }

    public void add(int index, T elem) {
        if (elem != null) {
            data.add(index, elem);
            notifyDataSetChanged();
        }
    }

    public void addAll(List<T> elem) {
        if (elem != null) {
            data.addAll(elem);
            notifyDataSetChanged();
        }
    }

    public void addAll(int index, List<T> elem) {
        if (elem != null) {
            data.addAll(index, elem);
            notifyDataSetChanged();
        }
    }

    public void set(T oldElem, T newElem) {
        set(data.indexOf(oldElem), newElem);
    }

    public void set(int index, T elem) {
        if (elem != null) {
            data.set(index, elem);
            notifyDataSetChanged();
        }
    }

    public void remove(T elem) {
        if (elem != null) {
            data.remove(elem);
            notifyDataSetChanged();
        }
    }

    public void remove(int index) {
        data.remove(index);
        notifyDataSetChanged();
    }

    public void replaceAll(List<T> elem) {
        data.clear();
        if (elem != null) {
            data.addAll(elem);
        }
        notifyDataSetChanged();
    }

    public boolean contains(T elem) {
        return data.contains(elem);
    }

    /**
     * Clear data list
     */
    public void clear() {
        data.clear();
    }

    /**
     * Clear data list
     */
    public void clearNotify() {
        data.clear();
        notifyDataSetChanged();
    }

    public void showIndeterminateProgress(boolean display) {
        if (display == displayIndeterminateProgress)
            return;
        displayIndeterminateProgress = display;
        notifyDataSetChanged();
    }

    /**
     * Implement this method and use the helper to adapt the view to the given
     * item.
     *
     * @param helper A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    protected abstract void convert(H helper, T item);

    /**
     * You can override this method to use a custom BaseAdapterHelper in order
     * to fit your needs
     *
     * @param position    The position of the item within the adapter's data set of the
     *                    item whose view we want.
     * @param convertView The old view to reuse, if possible. Note: You should check
     *                    that this view is non-null and of an appropriate type before
     *                    using. If it is not possible to convert this view to display
     *                    the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so
     *                    that this View is always of the right type (see
     *                    {@link #getViewTypeCount()} and {@link #getItemViewType(int)}
     *                    ).
     * @param parent      The parent that this view will eventually be attached to
     * @return An instance of BaseAdapterHelper
     */
    protected abstract H getAdapterHelper(int position, View convertView, ViewGroup parent);

}
