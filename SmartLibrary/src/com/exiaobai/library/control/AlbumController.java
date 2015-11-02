package com.exiaobai.library.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;

import com.exiaobai.library.model.AlbumModel;
import com.exiaobai.library.model.PhotoModel;

/**
 * 获取照片
 * 
 */
public class AlbumController {

	/** 获取最近照片列表 */
	public final static int CURRENT = 0x11;

	/** 获取所有相册列表 */
	public final static int ALBUMS_ALL = 0x12;

	/** 对应相册下的照片 */
	public final static int ALBUM_PHOTOS = 0x13;

	private ContentResolver resolver;

	private OnLocalListener mOnLocalListener;

	public AlbumController(Context context) {
		resolver = context.getContentResolver();
	}

	public AlbumController(Context context, OnLocalListener mOnLocalListener) {
		resolver = context.getContentResolver();
		setOnLocalListener(mOnLocalListener);
	}

	final AlbumHanlder mHanlder = new AlbumHanlder(AlbumController.this) {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (mOnLocalListener != null && msg.obj != null) {
				switch (msg.what) {
				case CURRENT:
				case ALBUM_PHOTOS:
					mOnLocalListener.onPhotoComplete((List<PhotoModel>) msg.obj);
					break;
				/* 相册 */
				case ALBUMS_ALL:
					mOnLocalListener.onAlbumComplete((List<AlbumModel>) msg.obj);
					break;
				}
			}
		}
	};

	/** 获取最近照片列表 */
	public void getCurrentPhotos() {
		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<PhotoModel> photos = new ArrayList<PhotoModel>();
				Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, null, null, ImageColumns.DATE_ADDED);
				if (cursor != null) {
					if (cursor.moveToNext()) {
						cursor.moveToLast();
						do {
							if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
								PhotoModel photoModel = new PhotoModel();
								photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
								photos.add(photoModel);
							}
						} while (cursor.moveToPrevious());
					}
					cursor.close();
				}
				mHanlder.sendMessage(mHanlder.obtainMessage(CURRENT, photos));
			}
		});
	}

	/** 获取所有相册列表 */
	public void getAlbums() {
		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<AlbumModel> albums = new ArrayList<AlbumModel>();
				Map<String, AlbumModel> map = new HashMap<String, AlbumModel>();
				Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.DATA, ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.SIZE }, null, null, null);
				if (cursor != null) {
					if (cursor.moveToNext()) {
						cursor.moveToLast();
						AlbumModel current = new AlbumModel("最近照片", 0, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)), true); // "最近照片"相册
						albums.add(current);
						do {
							if (cursor.getInt(cursor.getColumnIndex(ImageColumns.SIZE)) < 1024 * 10)
								continue;

							current.increaseCount();
							String name = cursor.getString(cursor.getColumnIndex(ImageColumns.BUCKET_DISPLAY_NAME));
							if (map.keySet().contains(name))
								map.get(name).increaseCount();
							else {
								AlbumModel album = new AlbumModel(name, 1, cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
								map.put(name, album);
								albums.add(album);
							}
						} while (cursor.moveToPrevious());

					}
					cursor.close();
				}
				mHanlder.sendMessage(mHanlder.obtainMessage(ALBUMS_ALL, albums));
			}
		});
	}

	/** 获取对应相册下的照片 */
	public void getAlbum(final String name) {
		ThreadPoolUtils.execute(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				List<PhotoModel> photos = new ArrayList<PhotoModel>();
				Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[] { ImageColumns.BUCKET_DISPLAY_NAME, ImageColumns.DATA, ImageColumns.DATE_ADDED, ImageColumns.SIZE }, "bucket_display_name = ?", new String[] { name }, ImageColumns.DATE_ADDED);
				if (cursor != null) {
					if (cursor.moveToNext()) {
						cursor.moveToLast();
						do {
							if (cursor.getLong(cursor.getColumnIndex(ImageColumns.SIZE)) > 1024 * 10) {
								PhotoModel photoModel = new PhotoModel();
								photoModel.setOriginalPath(cursor.getString(cursor.getColumnIndex(ImageColumns.DATA)));
								photos.add(photoModel);
							}
						} while (cursor.moveToPrevious());
					}
					cursor.close();
				}
				mHanlder.sendMessage(mHanlder.obtainMessage(ALBUM_PHOTOS, photos));
			}
		});
	}

	private static class AlbumHanlder extends Handler {

		AlbumController mAlbumController;

		public AlbumHanlder(AlbumController mAlbumController) {
			this.mAlbumController = mAlbumController;
		}

	}

	/**
	 * 设置回调监听
	 */
	public void setOnLocalListener(OnLocalListener mOnLocalListener) {
		this.mOnLocalListener = mOnLocalListener;
	}

	public interface OnLocalListener {

		/** 照片回调 */
		public void onPhotoComplete(List<PhotoModel> mPhotoModels);

		/** 相册回调 */
		public void onAlbumComplete(List<AlbumModel> mAlbums);

	}
}
