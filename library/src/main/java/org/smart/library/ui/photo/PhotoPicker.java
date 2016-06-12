package org.smart.library.ui.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.smart.library.listener.PhotoCallback;
import org.xutils.common.util.ParameterizedTypeUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * 照片选择器
 * 
 * @Description TODO
 * @author Liangzc
 * @date 2016年6月6日
 */
public class PhotoPicker {

	private static volatile PhotoPicker sInstance;

	public final static String HANDLE_PICKER = "hanldePicker";
	private final static String PICKER = "picker";

	/** default max limit */
	public final static int DEFAULT_MAX_NUM = 9;

	private int mChoiceMode = ChoiceMode.MULTIPLE;

	private int mMaxLimit = DEFAULT_MAX_NUM;

	private boolean mIshowCamera = true;

	private boolean mIsEditMode;

	private PhotoCallback<?> mPhotoCallback;

	public class ChoiceMode {

		/** SINGLE */
		public final static int SINGLE = 1;

		/** MULTIPLE */
		public final static int MULTIPLE = 2;
	}

	private PhotoPicker() {
	}

	public static PhotoPicker getInstance() {
		if (null == sInstance) {
			synchronized (PhotoPicker.class) {
				if (null == sInstance)
					sInstance = new PhotoPicker();
			}
		}
		return sInstance;
	}

	/**
	 * 单选模式（默认多选）
	 * 
	 * @return
	 */
	public PhotoPicker singleChoice() {
		this.mChoiceMode = ChoiceMode.SINGLE;
		return this;
	}

	/**
	 * 最大数量
	 * 
	 * @param maxLimit
	 * @return
	 */
	public PhotoPicker maxLimit(int maxLimit) {
		this.mMaxLimit = maxLimit;
		return this;
	}

	/**
	 * 隐藏相机
	 * 
	 * @return
	 */
	public PhotoPicker hideCamera() {
		this.mIshowCamera = false;
		return this;
	}

	/**
	 * 是否是编辑模式（仅适用于预览模式）
	 * 
	 * @return
	 */
	public PhotoPicker editMode() {
		this.mIsEditMode = true;
		return this;
	}

	/**
	 * 选择照片
	 * 
	 * @param context
	 * @param photoCallback
	 *            ParameterizedType must be String or ArrayList#String
	 */
	public <T> void pick(Context context, PhotoCallback<T> photoCallback) {
		this.mPhotoCallback = photoCallback;
		Intent intent = new Intent(context, PhotoSelectorActivity.class);
		intent.putExtra(PhotoSelectorActivity.EXTRA_CHOICE_MODE, mChoiceMode);
		intent.putExtra(PhotoSelectorActivity.EXTRA_MAX_LIMIT, mMaxLimit);
		intent.putExtra(PhotoSelectorActivity.EXTRA_SHOW_CAMERA, mIshowCamera);
		intent.putExtra(HANDLE_PICKER, HANDLE_PICKER);
		context.startActivity(intent);
	}

	/**
	 * 预览照片
	 * 
	 * @param context
	 * @param photoCallback
	 *            非编辑模式可以为空,ParameterizedType must be String or ArrayList#String
	 */
	public <T> void preview(Context context, List<String> photos, int defaultPosition, PhotoCallback<T> photoCallback) {
		this.mPhotoCallback = photoCallback;
		this.mChoiceMode = ChoiceMode.MULTIPLE;
		Intent intent = new Intent(context, PhotoPreviewActivity.class);
		intent.putStringArrayListExtra(PhotoPreviewActivity.PHOTOS, new ArrayList<String>(photos));
		intent.putExtra(PhotoPreviewActivity.EDIT_MODE, mIsEditMode);
		intent.putExtra(PhotoPreviewActivity.POSITION, defaultPosition);
		intent.putExtra(HANDLE_PICKER, HANDLE_PICKER);
		context.startActivity(intent);
	}

	/**
	 * 预览照片
	 * 
	 * @param context
	 * @param photoCallback
	 *            非编辑模式可以为空,ParameterizedType must be String or ArrayList#String
	 */
	public <T> void preview(Context context, String photo, PhotoCallback<T> photoCallback) {
		singleChoice();
		this.mPhotoCallback = photoCallback;
		Intent intent = new Intent(context, PhotoPreviewActivity.class);
		ArrayList<String> photos = new ArrayList<String>();
		photos.add(photo);
		intent.putStringArrayListExtra(PhotoPreviewActivity.PHOTOS, photos);
		intent.putExtra(PhotoPreviewActivity.EDIT_MODE, mIsEditMode);
		intent.putExtra(HANDLE_PICKER, HANDLE_PICKER);
		context.startActivity(intent);
	}

	public void initialise() {
		mChoiceMode = ChoiceMode.MULTIPLE;
		mMaxLimit = DEFAULT_MAX_NUM;
		mIshowCamera = true;
		mIsEditMode = false;
		mPhotoCallback = null;
	}

	private static Class<?> getParameterizedType(Class<?> clz) {
		Class<?> objectClass = Object.class;
		Type objectType = ParameterizedTypeUtil.getParameterizedType(clz, PhotoCallback.class, 0);
		if (objectType instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) objectType;
			if ((Class<?>) parameterizedType.getActualTypeArguments()[0] != String.class)
				throw new IllegalArgumentException("not support callback type" + objectType.toString());
			objectClass = (Class<?>) parameterizedType.getRawType();
		} else if (objectType instanceof TypeVariable || (objectClass = (Class<?>) objectType) != String.class) {
			throw new IllegalArgumentException("not support callback type" + objectType.toString());
		}
		return objectClass;
	}

	/* PACKAGE */@SuppressWarnings("unchecked")
	static void handleActivityResult(int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK || data == null)
			return;
		PhotoPicker photoPicker = PhotoPicker.getInstance();
		PhotoCallback<?> photoCallback = photoPicker.mPhotoCallback;
		photoPicker.initialise();
		ArrayList<String> mResults = data.getStringArrayListExtra(PhotoSelectorActivity.RESULTS);
		boolean isVali = PICKER.equals(data.getStringExtra(HANDLE_PICKER)) ? mResults != null && mResults.size() > 0 : mResults != null;
		if (isVali) {
			if (photoCallback != null) {
				Class<?> clasz = getParameterizedType(photoCallback.getClass());
				if (clasz == String.class)
					((PhotoCallback<String>) photoCallback).onPhotoCallBack(mResults.get(0));
				else if (clasz == ArrayList.class)
					((PhotoCallback<ArrayList<String>>) photoCallback).onPhotoCallBack(mResults);
			}
		}
	}
}
