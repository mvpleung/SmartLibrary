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

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 禁止Gridview滑动
 * 
 * @author LiangZC
 * @create 2014-3-25
 */
public class GridViewNoScroll extends GridView {
	public GridViewNoScroll(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public GridViewNoScroll(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewNoScroll(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}