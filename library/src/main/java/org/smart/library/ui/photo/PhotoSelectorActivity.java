package org.smart.library.ui.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.adapter.AlbumAdapter;
import org.smart.library.adapter.PhotoSelectorAdapter;
import org.smart.library.adapter.PhotoSelectorAdapter.OnItemCheckedListener;
import org.smart.library.control.AlbumController;
import org.smart.library.control.AlbumController.OnLocalListener;
import org.smart.library.control.AppConfig.AppRequestCode;
import org.smart.library.control.AppManager;
import org.smart.library.model.AlbumModel;
import org.smart.library.model.PhotoModel;
import org.smart.library.tools.PxUtil;
import org.smart.library.tools.Toolkit;
import org.smart.library.tools.UITools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 照片选择
 * 
 * @author LiangZiChao
 *         created on 2015年7月9日
 */
@SuppressLint("InflateParams")
public class PhotoSelectorActivity extends Activity implements OnLocalListener, OnClickListener {

	/** 多张图 */
	public final static String PHOTOS = "photos";
	/** 单张图 */
	public final static String PHOTO = "photo";

	/** 选择模式 */
	public final static String CHOICE_MODE = "choiceMode";

	/** 最大选择数量 */
	public final static String CHOICE_MAX_LIMIT = "choiceMaxLimit";

	public static final String RECCENT_PHOTO = "最近照片";
	public final static String PHOTO_PATH_MODE = "photoPath";

	private TextView mToolBarTitle, mRightText;
	private GridView gvPhotos;
	private TextView tvAlbum, tvPreview;
	private PopupWindow mImageDirPopup;
	private View mBottomBar;

	private AlbumController mAlbumController;
	private PhotoSelectorAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private HashSet<String> selected;
	private int choiceMode, choiceMaxCount = 5;

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
		mRightText.setText("OK");
		mRightText.setOnClickListener(this);
		gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
		tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
		tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
		mBottomBar = findViewById(R.id.layout_toolbar_ar);
		findViewById(R.id.tv_title_back).setOnClickListener(this);
	}

	public void initData() {
		choiceMode = getIntent().getIntExtra(CHOICE_MODE, ListView.CHOICE_MODE_MULTIPLE);
		choiceMaxCount = getIntent().getIntExtra(CHOICE_MAX_LIMIT, choiceMaxCount);
		tvAlbum.setOnClickListener(this);
		tvPreview.setOnClickListener(this);
		mAlbumController = new AlbumController(this, this);

		if (choiceMode == ListView.CHOICE_MODE_SINGLE) {
			mRightText.setVisibility(View.GONE);
			tvPreview.setVisibility(View.GONE);
		}

		selected = new HashSet<String>();

		photoAdapter = new PhotoSelectorAdapter(getApplicationContext(), new ArrayList<PhotoModel>(), PxUtil.getScreenWidth(this));
		photoAdapter.setOnItemCheckedListener(new OnItemCheckedListener() {

			@Override
			public void onCheckedChanged(PhotoModel photoModel, CheckBox checkBox) {
				if (checkBox.isChecked()) {
					selected.add(photoModel.getOriginalPath());
					tvPreview.setEnabled(true);
				} else {
					selected.remove(photoModel.getOriginalPath());
				}
				tvPreview.setText(getString(R.string.photo_preview_count_format, selected.size())); // 修改预览数量

				if (selected.isEmpty()) {
					tvPreview.setEnabled(false);
					tvPreview.setText(R.string.photo_preview);
				}
				if (choiceMode == ListView.CHOICE_MODE_SINGLE)
					selectComplete();
			}

			@Override
			public boolean onCheckedBefore(PhotoModel photoModel, CheckBox checkBox) {
				if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
					if (checkBox.isChecked()) {
						boolean canSelected = selected.size() < choiceMaxCount;
						if (!canSelected) {
							UITools.showToastShortDuration(PhotoSelectorActivity.this, getString(R.string.photo_limit_tips, choiceMaxCount));
						}
						return canSelected;
					}
				}
				return true;
			}
		});
		gvPhotos.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == 0 && view.getId() == R.id.tv_camera_vc) {// 照相机
					catchPicture();
				}
			}
		});
		gvPhotos.setAdapter(photoAdapter);

		mAlbumController.getCurrentPhotos();// 更新最近照片
		mAlbumController.getAlbums();// 跟新相册信息

		initListDirPopupWindw();
	}

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		View centerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.pop_list_dir, null);
		ListView mListDir = (ListView) centerView.findViewById(R.id.id_list_dir);
		albumAdapter = new AlbumAdapter(getApplicationContext(), new ArrayList<AlbumModel>());
		mListDir.setAdapter(albumAdapter);
		mListDir.setOnItemClickListener(new OnItemClickListener() {

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
				albumAdapter.notifyDataSetChanged();
				tvAlbum.setText(current.getName());
				mToolBarTitle.setText(current.getName());

				// 更新照片列表
				if (current.getName().equals(RECCENT_PHOTO))
					mAlbumController.getCurrentPhotos();
				else
					mAlbumController.getAlbum(current.getName());// 获取选中相册的照片
				mImageDirPopup.dismiss();
			}
		});
		mImageDirPopup = UITools.createCustomPopupWindow(centerView, LayoutParams.MATCH_PARENT, (int) (PxUtil.getScreenHeight(this) * 0.7));
		mImageDirPopup.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_album_ar) {
			popAlbum();
		} else if (id == R.id.tv_preview_ar) {
			priview();
		} else if (id == R.id.tv_title_right) {
			selectComplete(); // 选完照片
		} else if (id == R.id.tv_title_back) {
			finish();
		}
	}

	private String picFileFullName;// 照片路径

	/** 拍照 */
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
			selected.clear();
			selected.add(path);
			selectComplete();
			break;
		}
	}

	/** 完成 */
	private void selectComplete() {
		if (selected != null)
			if (selected.isEmpty()) {
				setResult(RESULT_CANCELED);
			} else {
				Intent data = new Intent();
				Bundle bundle = new Bundle();
				bundle.putStringArrayList(PHOTOS, new ArrayList<String>(selected));
				bundle.putString(PHOTO, selected.iterator().next());
				data.putExtras(bundle);
				setResult(RESULT_OK, data);
			}
		finish();
	}

	/** 预览照片 */
	private void priview() {
		Bundle bundle = new Bundle();
		bundle.putStringArrayList(PhotoPreviewActivity.PHOTOS, new ArrayList<String>(selected));
		Intent mIntent = new Intent(this, PhotoPreviewActivity.class);
		mIntent.putExtras(bundle);
		startActivityForResult(mIntent, AppRequestCode.PHOTO_PREVIEW);
	}

	/** 弹出相册列表 */
	private void popAlbum() {
		if (mImageDirPopup != null && !mImageDirPopup.isShowing()) {
			mImageDirPopup.showAsDropDown(mBottomBar, 0, 0);
			// 设置背景颜色变暗
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.alpha = .3f;
			getWindow().setAttributes(lp);
		} else if (mImageDirPopup != null)
			mImageDirPopup.dismiss();
	}

	/** 清空选中的图片 */
	private void reset() {
		selected.clear();
		tvPreview.setText("预览");
		tvPreview.setEnabled(false);
	}

	@Override
	public void onPhotoComplete(List<PhotoModel> mPhotoModels) {
		if (tvAlbum.getText().equals(RECCENT_PHOTO))
			mPhotoModels.add(0, new PhotoModel());
		photoAdapter.replaceAll(mPhotoModels);
		gvPhotos.smoothScrollToPosition(0); // 滚动到顶端
		reset();
	}

	@Override
	public void onAlbumComplete(List<AlbumModel> mAlbums) {
		albumAdapter.replaceAll(mAlbums);
	}
}
