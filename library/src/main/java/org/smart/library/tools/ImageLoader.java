package org.smart.library.tools;

import org.smart.library.R;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ViewTarget;

/**
 * 图片加载辅助类
 *
 * @author LiangZiChao created on 2016/1/6 16:09
 */
public class ImageLoader {

	public static void load(String url, ImageView view) {
		Glide.with(view.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).crossFade().into(view);
	}

	public static <T extends View, Z> void load(String url, ViewTarget<T, GlideDrawable> target) {
		Glide.with(target.getView().getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).crossFade().into(target);
	}
}
