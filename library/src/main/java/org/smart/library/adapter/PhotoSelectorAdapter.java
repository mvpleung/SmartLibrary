package org.smart.library.adapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.smart.library.R;
import org.smart.library.listener.PhotoCallback;
import org.smart.library.model.PhotoModel;
import org.smart.library.tools.ImageLoader;
import org.smart.library.tools.UITools;
import org.smart.library.ui.photo.PhotoPicker;
import org.smart.library.ui.photo.PhotoPicker.ChoiceMode;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * created on 2016/4/12 10:40
 */
public class PhotoSelectorAdapter extends BaseAdapter {

	private static final int TYPE_CAMERA = 0;
	private static final int TYPE_PHOTO = 1;

	private Context context;
	private LayoutInflater mInflater;
	private LayoutParams itemLayoutParams;
	private int itemWidth, horizentalNum = 3;
	private PhotoCallback<PhotoModel> mPhotoCallBack;

	private int mChoiceMode;
	// 图片选择数量
	private int mMaxNum = PhotoPicker.DEFAULT_MAX_NUM;
	private boolean mShowCamera;

	private List<PhotoModel> mModels;
	private HashSet<String> mSelectedSet;

	public PhotoSelectorAdapter(Context context, ArrayList<PhotoModel> models, int screenWidth) {
		this.context = context;
		setItemWidth(screenWidth);
		this.mModels = models;
		this.mInflater = LayoutInflater.from(context);
	}

	/**
	 * 设置每一个Item的宽高
	 */
	public void setItemWidth(int screenWidth) {
		int horizentalSpace = context.getResources().getDimensionPixelSize(R.dimen.sticky_item_horizontalSpacing);
		this.itemWidth = (screenWidth - (horizentalSpace * (horizentalNum - 1))) / horizentalNum;
		this.itemLayoutParams = new LayoutParams(itemWidth, itemWidth);
	}

	public void setMaxNum(int mMaxNum) {
		this.mMaxNum = mMaxNum;
	}

	public void setChoiceMode(int choiceMode) {
		this.mChoiceMode = choiceMode;
	}

	public void setShowCamera(boolean mShowCamera) {
		this.mShowCamera = mShowCamera;
	}

	@Override
	public int getCount() {
		return mModels != null ? mModels.size() : 0;
	}

	@Override
	public PhotoModel getItem(int position) {
		return mModels != null ? mModels.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 && mShowCamera ? TYPE_CAMERA : TYPE_PHOTO;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (getItemViewType(position) == TYPE_CAMERA) {
			if (convertView == null || !(convertView instanceof TextView)) {
				TextView tvCamera = (TextView) mInflater.inflate(R.layout.view_camera, parent, false);
				tvCamera.setHeight(itemWidth);
				tvCamera.setWidth(itemWidth);
				convertView = tvCamera;
			}
		} else { // 显示图片
			ViewHolder mViewHolder = null;
			if (convertView == null || convertView.getTag() == null) {
				mViewHolder = new ViewHolder(convertView = mInflater.inflate(R.layout.adapter_photoitem, parent, false));
				convertView.setLayoutParams(itemLayoutParams);
			} else {
				mViewHolder = (ViewHolder) convertView.getTag();
			}
			PhotoModel mPhotoModel = getItem(position);
			ImageLoader.load("file://" + mPhotoModel.getOriginalPath(), mViewHolder.ivPhoto);
			mViewHolder.cbPhoto.setVisibility(mChoiceMode == ChoiceMode.MULTIPLE ? View.VISIBLE : View.GONE);
			mViewHolder.cbPhoto.setChecked(mPhotoModel.isChecked());
			if (mChoiceMode == ListView.CHOICE_MODE_MULTIPLE) {
				mViewHolder.cbPhoto.setVisibility(View.VISIBLE);
				mViewHolder.cbPhoto.setTag(position);
				mViewHolder.cbPhoto.setTag(R.id.iv_photo, mViewHolder.ivPhoto);
				mViewHolder.cbPhoto.setOnClickListener(checkClickListener);
				setFilter(mViewHolder.ivPhoto, mViewHolder.cbPhoto.isChecked());
			} else {
				mViewHolder.cbPhoto.setVisibility(View.GONE);
			}
		}
		return convertView;
	}

	private OnClickListener checkClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (mPhotoCallBack != null) {
				if (mSelectedSet == null)
					mSelectedSet = new HashSet<String>();
				CheckBox checkBox = (CheckBox) v;
				ImageView imageView = (ImageView) v.getTag(R.id.iv_photo);
				PhotoModel photoModel = getItem((Integer) v.getTag());
				if (checkBox.isChecked() && mSelectedSet.size() < mMaxNum) {
					mSelectedSet.add(photoModel.getOriginalPath());
					setFilter(imageView, true);
				} else {
					if (mSelectedSet.size() == mMaxNum) {
						UITools.showToastShortDuration(context, context.getString(R.string.photo_limit_tips, mMaxNum));
						checkBox.setChecked(false);
					}
					mSelectedSet.remove(photoModel.getOriginalPath());
					setFilter(imageView, false);
				}
				photoModel.setChecked(checkBox.isChecked());
				mPhotoCallBack.onPhotoCallBack(photoModel);
			}
		}
	};

	/**
	 * 设置滤镜
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void setFilter(ImageView imageView, boolean isFilter) {
		if (imageView == null)
			return;
		imageView.setColorFilter(isFilter ? Color.parseColor("#60000000") : Color.TRANSPARENT);
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

	public HashSet<String> getSelected() {
		return mSelectedSet;
	}

	public int getSelectedSize() {
		return mSelectedSet != null ? mSelectedSet.size() : 0;
	}

	public void addSelect(String path) {
		if (mSelectedSet == null)
			mSelectedSet = new HashSet<String>();
		mSelectedSet.add(path);
	}

	public void clearSelected() {
		if (mSelectedSet != null)
			mSelectedSet.clear();
	}

	public final static class ViewHolder {

		ImageView ivPhoto;
		public CheckBox cbPhoto;

		public ViewHolder(View convertView) {
			ivPhoto = (ImageView) convertView.findViewById(R.id.iv_photo);
			cbPhoto = (CheckBox) convertView.findViewById(R.id.cb_photo);
			convertView.setTag(this);
		}
	}

	public void setPhotoCallback(PhotoCallback<PhotoModel> mPhotoCallback) {
		this.mPhotoCallBack = mPhotoCallback;
	}
}
