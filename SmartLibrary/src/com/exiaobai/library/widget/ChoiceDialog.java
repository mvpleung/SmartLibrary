/*
 * Copyright (c) 2014. xbtrip(深圳小白领先科技有限公司)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exiaobai.library.widget;

import java.util.Arrays;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.exiaobai.library.R;
import com.exiaobai.library.adapter.ChoiceAdapter;
import com.exiaobai.library.listener.OnExtraEventListener;
import com.exiaobai.library.tools.PxUtil;

/**
 * 单选Dialog列表
 * 
 * @author LiangZiChao
 * @param <T>
 * @Data 2014-8-4下午1:46:50
 * @Package com.xiaobai.xbtrip.view
 */
public class ChoiceDialog<T> extends Dialog {

	private Context context;
	private TextView tv_title;
	private ListView lv_list;
	private String title = "请选择";
	private T[] items;
	private T[] defaultValues;
	private OnExtraEventListener<T> onExtraEventListener;
	private ChoiceAdapter<T> mChoiceAdapter;

	public ChoiceDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ChoiceDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		// TODO Auto-generated constructor stub
	}

	public ChoiceDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public ChoiceDialog(Context context, T[] items, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
		super(context, R.style.DialogStyle);
		initDialog(context, null, items, null, onExtraEventListener, defaultValue);
	}

	public ChoiceDialog(Context context, String title, T[] items, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
		super(context, R.style.DialogStyle);
		initDialog(context, title, items, null, onExtraEventListener, defaultValue);
	}

	public ChoiceDialog(Context context, ChoiceAdapter<T> baseAdapter, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
		super(context, R.style.DialogStyle);
		initDialog(context, null, null, baseAdapter, onExtraEventListener, defaultValue);
	}

	public ChoiceDialog(Context context, String title, ChoiceAdapter<T> baseAdapter, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
		super(context, R.style.DialogStyle);
		initDialog(context, title, null, baseAdapter, onExtraEventListener, defaultValue);
	}

	private void initDialog(Context context, String title, T[] items, ChoiceAdapter<T> baseAdapter, OnExtraEventListener<T> onExtraEventListener, T... defaultValue) {
		if (title != null)
			this.title = title;
		this.items = items;
		this.context = context;
		this.mChoiceAdapter = baseAdapter;
		this.defaultValues = defaultValue;
		this.onExtraEventListener = onExtraEventListener;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_single_choice);
		initView();
		tv_title.setText(title);

		initAdapter();

		lv_list.setChoiceMode(defaultValues != null && defaultValues.length > 1 ? ListView.CHOICE_MODE_MULTIPLE : ListView.CHOICE_MODE_SINGLE);

		lv_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				dismiss();
				T t = (T) arg0.getItemAtPosition(arg2);
				mChoiceAdapter.setCurrentItems(t);
				if (onExtraEventListener != null)
					onExtraEventListener.onExtraEvent(arg1, arg2, t);
			}
		});
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		int screenWidth = PxUtil.getScreenWidth(context);
		lp.width = (int) (screenWidth == 720 ? (screenWidth * 4 / 5) : (screenWidth * 80 / 100)); // 设置宽度
		getWindow().setAttributes(lp);
	}

	/**
	 * 初始化UI
	 */
	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		lv_list = (ListView) findViewById(R.id.lv_list);
	}

	/**
	 * 初始化Adapter
	 */
	@SuppressWarnings("unchecked")
	private void initAdapter() {
		if (mChoiceAdapter == null) {
			mChoiceAdapter = new ChoiceAdapter<T>(context, items != null ? Arrays.asList(items) : null) {

				@Override
				public CheckedTextView initCheckedView(CheckedTextView mCheckedTextView) {
					// TODO Auto-generated method stub
					mCheckedTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
					return mCheckedTextView;
				}
			};
		}
		mChoiceAdapter.setCurrentItems(defaultValues);
		mChoiceAdapter.setListView(lv_list);

		lv_list.setAdapter(mChoiceAdapter);
	}

	/**
	 * get ViewGroup
	 * 
	 * @return
	 */
	public ListView getViewGroup() {
		return lv_list;
	}

	/**
	 * 获取填充
	 * 
	 * @return
	 */
	public ChoiceAdapter<T> getAdapter() {
		return mChoiceAdapter;
	}

	public void setAdapter(ChoiceAdapter<T> baseAdapter) {
		this.mChoiceAdapter = baseAdapter;
		initAdapter();
	}

	/**
	 * 设置当前选中的
	 * 
	 * @param <T>
	 */
	public void setCurrentItems(T... currentItem) {
		getAdapter().setCurrentItems(currentItem);
	}
}
