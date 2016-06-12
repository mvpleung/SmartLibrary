package org.smart.library.ui.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.smart.library.R;
import org.smart.library.adapter.AlbumAdapter;
import org.smart.library.adapter.PhotoSelectorAdapter;
import org.smart.library.control.AlbumController;
import org.smart.library.control.AlbumController.OnLocalListener;
import org.smart.library.control.AppConfig.AppRequestCode;
import org.smart.library.control.AppManager;
import org.smart.library.listener.PhotoCallback;
import org.smart.library.model.AlbumModel;
import org.smart.library.model.PhotoModel;
import org.smart.library.tools.PxUtil;
import org.smart.library.tools.Toolkit;
import org.smart.library.tools.UITools;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

/**
 * 照片选择
 *
 * @author LiangZiChao created on 2015年7月9日
 */
@SuppressLint("InflateParams")
public class PhotoSelectorActivity extends Activity implements OnLocalListener, OnClickListener {
	/**
	 * 多张图
	 */
	public final static String RESULTS = "results";

	/**
	 * 选择模式
	 */
	public final static String EXTRA_CHOICE_MODE = "mChoiceMode";

	/**
	 * 最大选择数量
	 */
	public final static String EXTRA_MAX_LIMIT = "choiceMaxLimit";

	/**
	 * 是否显示相机
	 */
	public final static String EXTRA_SHOW_CAMERA = "isSHowCamera";

	public static final String RECCENT_PHOTO = "最近照片";
	public final static String PHOTO_PATH_MODE = "photoPath";

	private TextView mToolBarTitle, mRightText;
	private GridView mPhotosGridView;
	private TextView mAlbumDirText, mPreviewText;
	private ListView mAlbumDirListView;

	private AlbumController mAlbumController;
	private PhotoSelectorAdapter mPhotoAdapter;
	private AlbumAdapter mAlbumAdapter;
	private boolean mIsAlbumShow, mIsInitAlbum;

	/** 是否显示相机 */
	private boolean mIsShowCamera = true;
	private int mChoiceMode, mChoiceMaxCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTheme(android.R.style.Theme_Light_NoTitleBar);
		initUI();
		initData();
	}

	public void initUI() {
		setContentView(R.layout.activity_photoselector);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			SystemBarTintManager mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			mTintManager.setNavigationBarTintEnabled(true);
			mTintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
		}
		mToolBarTitle = (TextView) findViewById(R.id.tv_title);
		mToolBarTitle.setText(R.string.photo_select);
		mRightText = (TextView) findViewById(R.id.tv_title_right);
		mRightText.setText(R.string.app_sure);
		mRightText.setEnabled(false);
		mRightText.setOnClickListener(this);
		mPhotosGridView = (GridView) findViewById(R.id.gv_photos_ar);
		mAlbumDirText = (TextView) findViewById(R.id.tv_album_ar);
		mPreviewText = (TextView) findViewById(R.id.tv_preview_ar);
		findViewById(R.id.im_title_back).setOnClickListener(this);
	}

	public void initData() {
		mAlbumDirText.setOnClickListener(this);
		mPreviewText.setOnClickListener(this);
		mAlbumController = new AlbumController(this, this);

		mPhotoAdapter = new PhotoSelectorAdapter(getApplicationContext(), new ArrayList<PhotoModel>(), PxUtil.getScreenWidth(this));
		mPhotoAdapter.setChoiceMode(mChoiceMode = getIntent().getIntExtra(EXTRA_CHOICE_MODE, ListView.CHOICE_MODE_MULTIPLE));
		mPhotoAdapter.setMaxNum(mChoiceMaxCount = getIntent().getIntExtra(EXTRA_MAX_LIMIT, PhotoPicker.DEFAULT_MAX_NUM));
		mPhotoAdapter.setShowCamera(mIsShowCamera = getIntent().getBooleanExtra(EXTRA_SHOW_CAMERA, mIsShowCamera));

		if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
			mRightText.setVisibility(View.GONE);
			mPreviewText.setVisibility(View.GONE);
		} else {
			mPhotoAdapter.setPhotoCallback(new PhotoCallback<PhotoModel>() {

				@Override
				public void onPhotoCallBack(PhotoModel extra) {
					int selectedCount = mPhotoAdapter.getSelectedSize();
					if (selectedCount > 0) {
						mPreviewText.setEnabled(true);
						mPreviewText.setText(getString(R.string.photo_preview_count_format, selectedCount)); // 修改预览数量
						mRightText.setEnabled(true);
						mRightText.setText(getString(R.string.photo_limit_select_format, selectedCount, mChoiceMaxCount));
					} else {
						reset();
					}
				}
			});
		}
		mPhotosGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && view.getId() == R.id.tv_camera_vc) {// 照相机
					catchPicture();
				} else if (mChoiceMode == ListView.CHOICE_MODE_SINGLE) {
					mPhotoAdapter.addSelect(mPhotoAdapter.getItem(position).getOriginalPath());
					selectComplete();
				}
			}
		});
		mPhotosGridView.setAdapter(mPhotoAdapter);

		mAlbumController.getCurrentPhotos();// 更新最近照片
		mAlbumController.getAlbums();// 跟新相册信息

		mAlbumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
	}

	/**
	 * 初始化展示相册
	 */
	@SuppressLint("ClickableViewAccessibility")
	private void toggleAlbumDir() {
		if (!mIsInitAlbum) {
			ViewStub floderStub = (ViewStub) findViewById(R.id.floder_stub);
			floderStub.inflate();
			mAlbumDirListView = (ListView) findViewById(R.id.id_list_dir);
			FrameLayout.LayoutParams params = (LayoutParams) mAlbumDirListView.getLayoutParams();
			params.height = (int) (PxUtil.getScreenHeight(this) * 0.7);
			params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
			mAlbumDirListView.setLayoutParams(params);
			mAlbumDirListView.setAdapter(mAlbumAdapter);
			mAlbumDirListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
					for (int i = 0; i < parent.getCount(); i++) {
						AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
						if (i == position)
							album.setCheck(true);
						else
							album.setCheck(false);
					}
					mAlbumAdapter.notifyDataSetChanged();
					mAlbumDirText.setText(current.getName());
					mToolBarTitle.setText(current.getName());

					// 更新照片列表
					if (current.getName().equals(RECCENT_PHOTO))
						mAlbumController.getCurrentPhotos();
					else
						mAlbumController.getAlbum(current.getName());// 获取选中相册的照片
					toggle();
				}
			});
			View dimissView = findViewById(R.id.dimiss_view);
			dimissView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (mIsAlbumShow) {
						toggle();
						return true;
					} else {
						return false;
					}
				}
			});
			initAnimation(dimissView);
			mIsInitAlbum = !mIsInitAlbum;
		}
		toggle();
	}

	/**
	 * 弹出相册列表
	 */
	private void toggle() {
		if (mIsAlbumShow) {
			outAnimatorSet.start();
			mIsAlbumShow = false;
		} else {
			inAnimatorSet.start();
			mIsAlbumShow = true;
		}
	}

	AnimatorSet inAnimatorSet = new AnimatorSet();
	AnimatorSet outAnimatorSet = new AnimatorSet();

	private void initAnimation(View dimissView) {
		ObjectAnimator alphaInAnimator, alphaOutAnimator, transInAnimator, transOutAnimator;
		// 获取actionBar的高
//		TypedValue tv = new TypedValue();
//		int actionBarHeight = 0;
//		if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
//			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//		}
		/**
		 * 这里的高度是，屏幕高度减去上、下tab栏，并且上面留有一个tab栏的高度 所以这里减去3个actionBarHeight的高度
		 */
		int height = (int) (PxUtil.getScreenHeight(this) * 0.7);
		alphaInAnimator = ObjectAnimator.ofFloat(dimissView, "alpha", 0f, 0.5f);
		alphaOutAnimator = ObjectAnimator.ofFloat(dimissView, "alpha", 0.5f, 0f);
		transInAnimator = ObjectAnimator.ofFloat(mAlbumDirListView, "translationY", height, 0);
		transOutAnimator = ObjectAnimator.ofFloat(mAlbumDirListView, "translationY", 0, height);

		LinearInterpolator linearInterpolator = new LinearInterpolator();

		inAnimatorSet.play(transInAnimator).with(alphaInAnimator);
		inAnimatorSet.setDuration(300);
		inAnimatorSet.setInterpolator(linearInterpolator);
		outAnimatorSet.play(transOutAnimator).with(alphaOutAnimator);
		outAnimatorSet.setDuration(300);
		outAnimatorSet.setInterpolator(linearInterpolator);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_album_ar) {
			toggleAlbumDir();
		} else if (id == R.id.tv_preview_ar) {
			priview();
		} else if (id == R.id.tv_title_right) {
			selectComplete(); // 选完照片
		} else if (id == R.id.im_title_back) {
			onBackPressed();
		}
	}

	private String picFileFullName;// 照片路径

	/**
	 * 拍照
	 */
	private void catchPicture() {
		String state = Environment.getExternalStorageState(); // 获取SD卡插入状态
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			if (!outDir.exists()) {
				outDir.mkdirs();
			}
			File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
			picFileFullName = outFile.getAbsolutePath();
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile)); // 设置照片路径
			startActivityForResult(intent, AppRequestCode.REQUEST_CAMERA);
		} else {
			UITools.showToastShortDuration(this, "请插入SD卡");
		}
	}

	/**
	 * 完成
	 */
	private void selectComplete() {
		HashSet<String> mSelectedSet = mPhotoAdapter.getSelected();
		if (mSelectedSet != null)
			if (mSelectedSet.isEmpty()) {
				setResult(RESULT_CANCELED);
			} else {
				Intent data = getIntent();
				data.putStringArrayListExtra(RESULTS, new ArrayList<String>(mSelectedSet));
				if (data.hasExtra(PhotoPicker.HANDLE_PICKER))
					PhotoPicker.handleActivityResult(RESULT_OK, data);
				else
					setResult(RESULT_OK, data);
			}
		finish();
	}

	/**
	 * 预览照片
	 */
	private void priview() {
		Intent mIntent = new Intent(this, PhotoPreviewActivity.class);
		mIntent.putStringArrayListExtra(PhotoPreviewActivity.PHOTOS, new ArrayList<String>(mPhotoAdapter.getSelected()));
		startActivityForResult(mIntent, AppRequestCode.PHOTO_PREVIEW);
	}

	/**
	 * 清空选中的图片
	 */
	private void reset() {
		mPhotoAdapter.clearSelected();
		mPreviewText.setEnabled(false);
		mPreviewText.setText(R.string.photo_preview);
		mRightText.setEnabled(false);
		mRightText.setText(R.string.app_sure);
	}

	@Override
	public void onBackPressed() {
		PhotoPicker.getInstance().initialise();
		super.onBackPressed();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode) {
		case AppRequestCode.REQUEST_CAMERA:
			String path = null;
			if (data == null && picFileFullName != null) {
				path = picFileFullName;
				// 通过picFileFullName赋值给path
			} else if (data != null) {
				path = Toolkit.queryImageUrl(getApplicationContext(), data.getData());
				// 通过data.getData()返回的uri 获取图片路径 path
			}
			reset();
			mPhotoAdapter.addSelect(path);
			selectComplete();
			break;
		}
	}

	@Override
	public void onPhotoComplete(List<PhotoModel> mPhotoModels) {
		if (mIsShowCamera && mAlbumDirText.getText().equals(RECCENT_PHOTO))
			mPhotoModels.add(0, new PhotoModel());
		mPhotoAdapter.replaceAll(mPhotoModels);
		mPhotosGridView.smoothScrollToPosition(0); // 滚动到顶端
		reset();
	}

	@Override
	public void onAlbumComplete(List<AlbumModel> mAlbums) {
		mAlbumAdapter.replaceAll(mAlbums);
	}
}
