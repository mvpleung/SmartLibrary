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
package org.smart.library.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.smart.library.R;
import org.smart.library.tools.PxUtil;

/**
 * 解决滑动和触摸冲突
 *
 * @author Liangzc
 *         created on 2014-3-20
 */
public class XListView extends ListView {

    private OnTouchListener onTouchListener;
    private TextView mTextEmptyView;

    private boolean enableScroll = true;

    public XListView(Context context) {
        super(context);
    }

    public XListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public XListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        onTouchListener = l;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (onTouchListener != null) {
                onTouchListener.onTouch(this, ev);
            }
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
        }
        return true;
    }

    public void enableScroll(boolean enableScroll) {
        this.enableScroll = enableScroll;
    }

    private void initEmptyView() {
        mTextEmptyView = new TextView(getContext());
        mTextEmptyView.setTextColor(Color.parseColor("#777777"));
        mTextEmptyView.setText(R.string.app_no_data_tips);
        mTextEmptyView.setGravity(Gravity.CENTER);
    }

    /**
     * 开启EmptyView
     *
     * @param enable
     */
    public void setEmptyViewEnable(boolean enable) {
        if (enable) {
            setEmptyViewUI(R.drawable.record, 0);
        }
    }

    public void setEmptyViewText(int mEmptyTextId) {
        setEmptyViewUI(R.drawable.record, mEmptyTextId);
    }

    public void setEmptyViewText(String mEmptyText) {
        setEmptyViewUI(R.drawable.record, mEmptyText);
    }

    /**
     * @param resId DrawableTop ResId
     */
    public void setEmptyViewImage(int resId) {
        setEmptyViewUI(resId, R.string.app_no_data_tips);
    }

    /**
     * @param resId        DrawableTop ResId
     * @param mEmptyTextId
     */
    public void setEmptyViewUI(int resId, int mEmptyTextId) {
        setEmptyViewUI(resId, getResources().getText(mEmptyTextId > 0 ? mEmptyTextId : R.string.app_no_data_tips).toString());
    }

    /**
     * @param resId      DrawableTop ResId
     * @param mEmptyText
     */
    public void setEmptyViewUI(int resId, String mEmptyText) {
        initEmptyView();
        if (mTextEmptyView != null) {
            mTextEmptyView.setText(mEmptyText);
            if (resId > 0) {
                Drawable mDrawable = getResources().getDrawable(resId);
                if (mDrawable != null) {
                    int height = mDrawable.getIntrinsicHeight();
                    mDrawable.setBounds(0, 0, mDrawable.getIntrinsicWidth(), height);
                    mTextEmptyView.setCompoundDrawables(null, mDrawable, null, null);
                    mTextEmptyView.setCompoundDrawablePadding(PxUtil.dip2px(getContext(), 5));
                    mTextEmptyView.setPadding(0, 0, 0, height * 2);
                }
            } else {
                mTextEmptyView.setPadding(0, getResources().getDimensionPixelSize(R.dimen.dimen_15), 0, getResources().getDimensionPixelSize(R.dimen.dimen_24));
            }
            setEmptyView(mTextEmptyView);
        }
    }

    @Override
    public void setEmptyView(View emptyView) {
        ViewGroup mViewGroup = ((ViewGroup) getParent());
        FrameLayout.LayoutParams params;
        if (isDrawableTop()) {
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        } else
            params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        emptyView.setLayoutParams(params);
        mViewGroup.addView(emptyView);
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                if (View.VISIBLE != getVisibility())
                    setVisibility(View.VISIBLE);
            }
        });
        super.setEmptyView(emptyView);
    }

    private boolean isDrawableTop() {
        Drawable[] mDrawables = mTextEmptyView != null ? mTextEmptyView.getCompoundDrawables() : null;
        return mDrawables != null && mDrawables[1] != null;
    }

    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = enableScroll ? heightMeasureSpec : MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
