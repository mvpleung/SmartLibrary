package com.exiaobai.library.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.exiaobai.library.R;
import com.exiaobai.library.control.AppConfig;
import com.exiaobai.library.control.AppManager;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.L;

/**
 * 图片加载辅助类
 * 
 * @description
 * @author LiangZiChao
 * @Date 2014-11-3下午4:40:36
 */
public class BitmapHelper {

	private ImageLoader imageLoader;
	private DisplayImageOptions.Builder mBuilder;
	private DisplayImageOptions mDisplayImageOptions;

	private Activity activity;

	private BitmapHelper() {
		activity = AppManager.getAppManager().getActivityReference();
		imageLoader = ImageLoader.getInstance();
		configUniversal(activity);
		L.writeLogs(AppConfig.DEBUG_MODEL);
		L.writeDebugLogs(false);
		LogUtils.i("init Universal Success");
	}

	private static class BitmapHolder {
		static BitmapHelper bitmapHelper = new BitmapHelper();
	}

	public static BitmapHelper getInstance() {
		return BitmapHolder.bitmapHelper.configDefaultResources(0, 0);
	}

	/**
	 * 
	 * @param resources
	 *            没有默认图片，传0
	 * @param roundPixels
	 * @return
	 */
	public static BitmapHelper getInstance(int resources) {
		return BitmapHolder.bitmapHelper.configDefaultResources(resources, resources);
	}

	/**
	 * 获取加载对象
	 * 
	 * @return
	 */
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	/**
	 * 销毁
	 */
	public static void destory() {
		if (BitmapHolder.bitmapHelper != null && BitmapHolder.bitmapHelper.imageLoader != null) {
			BitmapHolder.bitmapHelper.imageLoader.destroy();
			BitmapHolder.bitmapHelper.imageLoader = null;
			BitmapHolder.bitmapHelper = null;
		}
	}

	/**
	 * universalimageloader 配置
	 */
	private void configUniversal(Context context) {
		/********** 异步下载图片缓存类 初始化 */
		mBuilder = new DisplayImageOptions.Builder().resetViewBeforeLoading(true) // 载入之前重置ImageView
				.considerExifParams(true) // 调整图片方向
				.showImageOnLoading(R.mipmap.ic_picture_loading)// 加载等待时显示的图片
				.showImageForEmptyUri(R.mipmap.ic_picture_loading)// 加载数据为空时显示的图片
				.showImageOnFail(R.mipmap.ic_picture_loading)// 加载失败时显示的图片
				.delayBeforeLoading(0) // 载入之前的延迟时间
				// .displayer(new RoundedBitmapDisplayer(8))
				.bitmapConfig(Bitmap.Config.RGB_565).cacheInMemory(true).cacheOnDisk(true).displayer(new FadeInBitmapDisplayer(100));
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(mBuilder.build()).denyCacheImageMultipleSizesInMemory()
		// .enableLogging() // Not necessary in common
				.memoryCacheSize(4 * 1024 * 1024).diskCacheSize(50 * 1024 * 1024).build();
		// Initialize ImageLoader with configuration.
		imageLoader.init(config);
		// imageLoader.init(ImageLoaderConfiguration.createDefault(context));
	}

	/**
	 * 配置默认图片
	 */
	public BitmapHelper configDefaultResources(int loadingResource, int loadFaileResource) {
		if (loadingResource > 0 && loadFaileResource > 0)
			mDisplayImageOptions = mBuilder.showImageOnLoading(loadingResource)// 加载等待时显示的图片
					.showImageForEmptyUri(loadFaileResource)// 加载数据为空时显示的图片
					.showImageOnFail(loadFaileResource)// 加载失败时显示的图片
					.build();
		else {
			mBuilder.showImageOnLoading(R.mipmap.ic_picture_loading)// 加载等待时显示的图片
					.showImageForEmptyUri(R.mipmap.ic_picture_loading)// 加载数据为空时显示的图片
					.showImageOnFail(R.mipmap.ic_picture_loading);// 加载失败时显示的图片
			mDisplayImageOptions = null;
		}
		return this;
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public void displayImage(String uri, ImageView imageView, BitmapDisplayer bitmapDisplayer, ImageLoadingListener imageLoadingListener) {
		imageLoader.displayImage(uri, imageView, (bitmapDisplayer != null ? mBuilder.displayer(bitmapDisplayer).build() : mDisplayImageOptions), imageLoadingListener);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public void displayImage(String uri, ImageView imageView) {
		displayImage(uri, imageView, 0);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 * @param roundPixels
	 *            圆角弧度
	 */
	public void displayImage(String uri, ImageView imageView, int cornerRadiusPixels) {
		displayImage(uri, imageView, cornerRadiusPixels, null);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener imageLoadingListener) {
		displayImage(uri, imageView, null, imageLoadingListener);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 */
	public void displayImage(String uri, ImageView imageView, int cornerRadiusPixels, ImageLoadingListener imageLoadingListener) {
		displayImage(uri, imageView, cornerRadiusPixels > 0 ? new FadeInRoundBitmapDisplayer(20, cornerRadiusPixels) : null, imageLoadingListener);
	}

	/**
	 * 加载图片
	 * 
	 * @param uri
	 * @param imageView
	 * @param bitmapDisplayer
	 *            加载辅助类（圆角或其他）
	 */
	public void displayImage(String uri, ImageView imageView, BitmapDisplayer bitmapDisplayer) {
		displayImage(uri, imageView, bitmapDisplayer, null);
	}
}
