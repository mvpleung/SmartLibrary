package org.smart.library.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.smart.library.tools.ImageLoader;
import org.smart.library.widget.EcoGallery;
import org.smart.library.widget.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 可缩放的图片展示
 *
 * @author LiangZC
 *         created on 2014-4-10
 */
public class ZoomImageAdapter extends BaseAdapter {
    private Context context;
    private List<String> imgUrls;

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
        ImageLoader.load(convertImgUrl(imgUrls.get(position)), new GlideDrawableImageViewTarget(view) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                ((ZoomImageView) getView()).setScaleType(ScaleType.MATRIX);
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
