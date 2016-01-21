package org.smart.library.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.smart.library.tools.GlideHelper;

public class ViewHolder {
    private final SparseArray<View> mViews;
    private int mPosition;
    private Context mContext;
    private View mConvertView;
    private RequestManager mRequestManager;

    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        this.mContext = context;
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        // setTag
        mConvertView.setTag(this);
    }

    /**
     * 拿到一个ViewHolder对象
     *
     * @param context
     * @param convertView
     * @param parent
     * @param layoutId
     * @param position
     * @return
     */
    public static ViewHolder get(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder(context, parent, layoutId, position);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
        }
        return holder;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public void setConvertView(View mConvertView) {
        this.mConvertView = mConvertView;
    }

    /**
     * 通过控件的Id获取对于的控件，如果没有则加入views
     *
     * @param viewId
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 为TextView设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置Hint
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setHint(int viewId, String text) {
        TextView view = getView(viewId);
        view.setHint(text);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param drawableId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int drawableId) {
        ImageView view = getView(viewId);
        view.setImageResource(drawableId);

        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    /**
     * 为ImageView设置图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setImageByUrl(int viewId, String url) {
        if (mRequestManager == null)
            mRequestManager = Glide.with(mContext);
        GlideHelper.load(mRequestManager, url, (ImageView) getView(viewId));
        return this;
    }

    /**
     * 是否可见
     *
     * @param viewId
     * @param visibility true:visiable false:gone
     * @return
     */
    public ViewHolder setVisibility(int viewId, boolean visibility) {
        getView(viewId).setVisibility(visibility ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * 是否选中
     *
     * @param viewId
     * @param checked
     * @return
     */
    public ViewHolder setChecked(int viewId, boolean checked) {
        View view = getView(viewId);
        if (view instanceof CheckBox) {
            CheckBox mCheck = (CheckBox) view;
            mCheck.setChecked(checked);
        } else if (view instanceof CheckedTextView) {
            CheckedTextView mCheck = (CheckedTextView) view;
            mCheck.setChecked(checked);
        }
        return this;
    }

    public int getPosition() {
        return mPosition;
    }

}
