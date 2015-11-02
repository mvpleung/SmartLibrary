package com.exiaobai.library.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.exiaobai.library.tools.BitmapHelper;
import com.exiaobai.library.widget.EcoGallery;

/**
 * Gallery图片展示
 * 
 * @author LiangZC
 * @create 2014-4-10
 */
public class GalleryImageAdapter extends BaseAdapter {

	private Context context;
	private List<String> imgUrls;
	private BitmapHelper bitmapHelper;
	private EcoGallery.LayoutParams mParams;

	public GalleryImageAdapter(Context context, List<String> imgUrls) {
		init(context, imgUrls);
	}

	public GalleryImageAdapter(Context context, String imgUrl) {
		this.imgUrls = new ArrayList<String>();
		this.imgUrls.add(imgUrl);
		init(context, imgUrls);
	}

	private void init(Context context, List<String> imgUrls) {
		this.context = context;
		this.imgUrls = imgUrls;
		this.bitmapHelper = BitmapHelper.getInstance();
	}

	@Override
	public int getCount() {
		return imgUrls != null ? imgUrls.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return imgUrls != null ? imgUrls.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView view = null;
		if (convertView == null) {
			view = new ImageView(context);
			view.setScaleType(ScaleType.CENTER);
			initImageView(view);
			// view.setBackgroundColor(context.getResources().getColor(R.color.white));
			convertView = view;
		} else {
			view = (ImageView) convertView;
		}
		bitmapHelper.displayImage(convertImgUrl(imgUrls.get(position)), view);
		return convertView;
	}

	public String convertImgUrl(String url) {
		return url;
	}

	/**
	 * 
	 * @return
	 */
	public void initImageView(ImageView mImage) {
		if (mParams == null) {
			mParams = new EcoGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		mImage.setLayoutParams(mParams);
	}
}
