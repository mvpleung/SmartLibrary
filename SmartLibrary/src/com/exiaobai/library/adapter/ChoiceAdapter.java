package com.exiaobai.library.adapter;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.exiaobai.library.R;
import com.exiaobai.library.tools.JudgmentLegal;
import com.joanzapata.android.BaseAdapterHelper;
import com.joanzapata.android.QuickAdapter;

/**
 * 选择列表Adapter
 * 
 * @author LiangZiChao
 * @Data 2015年6月19日
 */
public class ChoiceAdapter<T> extends QuickAdapter<T> {

	public T[] currentItems;
	public ListView listView;

	public ChoiceAdapter(Context context, List<T> data, T... currents) {
		super(context, R.layout.adapter_singlechoice_item, data);
		this.currentItems = currents;
	}

	public ChoiceAdapter(Context context, int layoutResId, List<T> data, T... currents) {
		super(context, layoutResId, data);
		this.currentItems = currents;
	}

	public ChoiceAdapter(Context context, T[] items, T... currents) {
		this(context, Arrays.asList(items), currents);
	}

	@Override
	protected void convert(BaseAdapterHelper helper, T item) {
		if (item == null)
			return;
		CheckedTextView mCheckedTextView = helper.getView(android.R.id.text1);
		mCheckedTextView = initCheckedView(mCheckedTextView);
		mCheckedTextView.setText((CharSequence) item);
		if (listView != null) {
			int mPosition = helper.getPosition();
			if (JudgmentLegal.isArrayFull(currentItems)) {
				for (Object currentItem : currentItems) {
					boolean isEquals = item.equals(currentItem);
					listView.setItemChecked(mPosition, isEquals);
					if (isEquals)
						break;
				}
			} else {
				listView.setItemChecked(mPosition, false);
			}
		}
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	public CheckedTextView initCheckedView(CheckedTextView mCheckedTextView) {
		return mCheckedTextView;
	}

	public void replaceAll(T[] array, T... currents) {
		currentItems = currents;
		replaceAll(Arrays.asList(array));
	}

	public void replaceAll(List<T> list, T... currents) {
		currentItems = currents;
		replaceAll(list);
	}

	public void setCurrentItems(T... currents) {
		currentItems = currents;
	}

	public T[] getCurrentItems() {
		return currentItems;
	}
}
