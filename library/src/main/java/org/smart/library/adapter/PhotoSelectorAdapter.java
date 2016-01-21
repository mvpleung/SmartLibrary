package org.smart.library.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.smart.library.R;
import org.smart.library.model.PhotoModel;
import org.smart.library.tools.GlideHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PhotoSelectorAdapter extends BaseAdapter {

	private Context context;
	private List<PhotoModel> mModels;
	private int itemWidth;
	private int horizentalNum = 3;
	private LayoutParams itemLayoutParams;
	private LayoutInflater mInflater;
	private OnItemCheckedListener mOnItemCheckedListener;

	private RequestManager mRequestManager;

	public PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth) {
		this.context = context;
		setItemWidth(screenWidth);
		this.mModels = models;
		this.mInflater = LayoutInflater.from(context);
		this.mRequestManager = Glide.with(context);
	}

	/** 设置每一个Item的宽高 */
	public void setItemWidth(int screenWidth) {
		int horizentalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
		this.itemWidth = (screenWidth - (horizentalSpace * (horizentalNum - 1))) / horizentalNum;
		this.itemLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mViewHolder = null;
		PhotoModel mPhotoModel = getItem(position);
		if (position == 0 && TextUtils.isEmpty(mPhotoModel.getOriginalPath())) { // 当时第一个时，显示按钮
			if (convertView == null || !(convertView instanceof TextView)) {
				TextView tvCamera = (TextView) mInflater.inflate(R.layout.view_camera, parent, false);
				tvCamera.setHeight(itemWidth);
				tvCamera.setWidth(itemWidth);
				convertView = tvCamera;
			}
		} else { // 显示图片
			if (convertView == null || convertView.getTag() == null) {
				mViewHolder = new ViewHolder(convertView = mInflater.inflate(R.layout.adapter_photoitem, parent, false));
				convertView.setLayoutParams(itemLayoutParams);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			GlideHelper.load(mRequestManager,new File(mPhotoModel.getOriginalPath()),mViewHolder.ivPhoto);
			mViewHolder.cbPhoto.setChecked(mPhotoModel.isChecked());
			final int index = position;
			final ImageView image = mViewHolder.ivPhoto;
			mViewHolder.cbPhoto.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mOnItemCheckedListener != null) {
						CheckBox checkBox = (CheckBox) v;
						PhotoModel mPhotoModel = getItem(index);
						if (mOnItemCheckedListener.onCheckedBefore(mPhotoModel, checkBox))
							checkItem(checkBox, mPhotoModel, image, index);
						else {
							checkBox.setChecked(!checkBox.isChecked());
							image.clearColorFilter();
						}

					}
				}
			});
			if (mViewHolder.cbPhoto.isChecked()) {
				image.setDrawingCacheEnabled(true);
				image.buildDrawingCache();
				image.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
			}
		}
		return convertView;
	}

	public final static class ViewHolder {

		ImageView ivPhoto;
		public CheckBox cbPhoto;

		public ViewHolder(View convertView) {
			ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo_lpsi);
			cbPhoto = (CheckBox) convertView.findViewById(R.id.cb_photo_lpsi);
			convertView.setTag(this);
		}
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mModels != null ? mModels.size() : 0;
	}

	@Override
	public PhotoModel getItem(int position) {
		return mModels != null ? mModels.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void replaceAll(List<PhotoModel> mItems) {
		if (mItems == null)
			return;
		if (this.mModels == null)
			this.mModels = new ArrayList<PhotoModel>();
		else
			this.mModels.clear();
		this.mModels.addAll(mItems);
		notifyDataSetChanged();
	}

	public void setOnItemCheckedListener(OnItemCheckedListener mOnItemCheckedListener) {
		this.mOnItemCheckedListener = mOnItemCheckedListener;
	}

	/** 图片Item选中事件监听器 */
	public static interface OnItemCheckedListener {

		public boolean onCheckedBefore(PhotoModel photoModel, CheckBox checkBox);

		public void onCheckedChanged(PhotoModel photoModel, CheckBox checkBox);
	}

	/**
	 * 选中Item
	 * 
	 * @param image
	 * @param index
	 */
	public void checkItem(CheckBox checkBox, PhotoModel mPhotoModel, ImageView image, int index) {
		mPhotoModel.setChecked(checkBox.isChecked());
		// 让图片变暗或者变亮
		if (checkBox.isChecked()) {
			image.setDrawingCacheEnabled(true);
			image.buildDrawingCache();
			image.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		} else {
			image.clearColorFilter();
		}
		mOnItemCheckedListener.onCheckedChanged(mPhotoModel, checkBox);
	}
}
