package org.smart.library.tools;

import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.smart.library.R;

import java.io.File;

/**
 * Glide 加载辅助类
 *
 * @author LiangZiChao
 *         created on 2016/1/6 16:09
 */
public class GlideHelper {

    public static void load(RequestManager mRequestManager, String url, ImageView view) {
        mRequestManager.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).into(view);
    }

    public static void load(RequestManager mRequestManager, String url, GlideDrawableImageViewTarget target) {
        mRequestManager.load(url).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).into(target);
    }

    public static void load(RequestManager mRequestManager, File file, ImageView view) {
        mRequestManager.load(file).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).into(view);
    }

    public static void load(RequestManager mRequestManager, File file, GlideDrawableImageViewTarget target) {
        mRequestManager.load(file).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.ic_picture_loading).error(R.drawable.ic_picture_loading).into(target);
    }
}
