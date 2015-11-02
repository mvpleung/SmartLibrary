package com.exiaobai.library.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;

import com.exiaobai.library.tools.BitmapHelper;
import com.exiaobai.library.widget.EcoGallery;
import com.exiaobai.library.widget.ZoomImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 可缩放的图片展示
 * 
 * @author LiangZC
 * @create 2014-4-10
 */
public class ZoomImageAdapter extends BaseAdapter {
	private Context context;
	private List<String> imgUrls;
	private BitmapHelper bitmapHelper;

	public ZoomImageAdapter(Context context, List<String> imgUrls) {
		init(context, imgUrls);
	}

	public ZoomImageAdapter(Context context, String imgUrl) {
		this.imgUrls = new ArrayList<String>();
		this.imgUrls.add(imgUrl);
		init(context, imgUrls);
	}

	private void init(Context context, List<String> imgUrls) {
		this.context = context;
		this.imgUrls = imgUrls == null ? new ArrayList<String>() : imgUrls;
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
		ZoomImageView view = null;
		if (convertView == null) {
			view = new ZoomImageView(context);
			EcoGallery.LayoutParams lp = new EcoGallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			view.setLayoutParams(lp);
			view.setScaleType(ScaleType.CENTER);
			// view.setBackgroundColor(context.getResources().getColor(R.color.white));
			convertView = view;
		} else {
			view = (ZoomImageView) convertView;
		}
		bitmapHelper.displayImage(convertImgUrl(imgUrls.get(position)), view, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {

			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				((ZoomImageView) view).setScaleType(ScaleType.MATRIX);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {

			}
		});
		return convertView;
	}

	public void replaceImgUrl(String mImageUrl) {
		if (!TextUtils.isEmpty(mImageUrl)) {
			imgUrls.clear();
			imgUrls.add(mImageUrl);
		}
	}

	public void replaceAll(List<String> mImageUrl) {
		if (mImageUrl != null) {
			imgUrls.clear();
			imgUrls.addAll(mImageUrl);
		}
	}

	public String convertImgUrl(String url) {
		return url;
	}
}
