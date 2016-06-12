package org.smart.library.ui.photo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.AbstractPagerAdapter;
import com.joanzapata.android.BaseAdapterHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.control.AppManager;
import org.smart.library.tools.ImageLoader;
import org.smart.library.tools.JudgmentLegal;
import org.smart.library.tools.UITools;

import java.util.ArrayList;

import uk.co.senab.PhotoView;
import uk.co.senab.PhotoViewAttacher.OnViewTapListener;
import uk.co.senab.ViewPagerFixed;

/**
 * 图片预览
 *
 * @author LiangZiChao created on 2015年7月9日
 */
public class PhotoPreviewActivity extends Activity implements OnClickListener, OnPageChangeListener {

	/**
	 * 图片集合
	 */
	public final static String PHOTOS = "mPhotos";
	/**
	 * 返回图片集合
	 */
	public final static String RESULTS = "results";

	public final static String EDIT_MODE = "editMode";

	public final static String POSITION = "position";

	/**
	 * 是否是文件路径
	 */
	public final static String PHOTO_FILE_PATH = "photoFilePath";

	private View mToolBar;
	private ViewPagerFixed mViewPager;
	private ImageView mImageDelete;
	private TextView mToolBarTitle;

	private SystemBarTintManager mTintManager;

	private ArrayList<String> mPhotos;
	protected int mCurrent;

	private AbstractPagerAdapter<String> mPagerAdapter;

	boolean mIsFilePath;// 是否本地文件路径false为本地路径
	boolean mIsEditMode;

	LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManager.getAppManager().addActivity(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setTheme(android.R.style.Theme_Light_NoTitleBar);
		initUI();
		initData();
	}

	private void initUI() {
		setContentView(R.layout.activity_photopreview);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			mTintManager = new SystemBarTintManager(this);
			mTintManager.setStatusBarTintEnabled(true);
			mTintManager.setNavigationBarTintEnabled(true);
			mTintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
		}
		findViewById(R.id.im_title_back).setOnClickListener(this);
		mToolBar = findViewById(R.id.title_layout);
		mViewPager = (ViewPagerFixed) findViewById(R.id.vp_base_app);
		mImageDelete = (ImageView) findViewById(R.id.im_title_right);
		mToolBarTitle = (TextView) findViewById(R.id.tv_title);
		mImageDelete.setOnClickListener(this);
		mViewPager.setOnPageChangeListener(this);
//		mViewPager.setOffscreenPageLimit(1);
		mViewPager.setPageMargin(getResources().getDimensionPixelSize(R.dimen.padding_5));
		findViewById(R.id.im_title_back).setOnClickListener(this);

		overridePendingTransition(R.anim.activity_alpha_action_in, 0); // 渐入效果
		mInflater = getLayoutInflater();
	}

	private void initData() {
		Bundle extras = getIntent().getExtras();
		if (extras == null)
			return;

		mPhotos = extras.getStringArrayList(PHOTOS);
		if (JudgmentLegal.isListFull(mPhotos)) { // 预览图片
			if (mIsEditMode = extras.getBoolean(EDIT_MODE)) {
				mImageDelete.setVisibility(View.VISIBLE);
				mImageDelete.setImageResource(R.drawable.delete_icon);
			}
			mIsFilePath = extras.getBoolean(PHOTO_FILE_PATH, true);
			mCurrent = extras.getInt(POSITION, 0);
			updatePercent();
			mViewPager.setAdapter(mPagerAdapter = new AbstractPagerAdapter<String>(this, R.layout.adapter_photopreview, mPhotos) {

				@Override
				protected void convert(BaseAdapterHelper helper, String item) {
					PhotoView photoView = helper.getView(R.id.iv_image);
					ImageLoader.load((mIsFilePath ? "file://" : "") + item, photoView);
					photoView.setOnViewTapListener(onPhotoViewTapListener);
				}
			});
			mViewPager.setCurrentItem(mCurrent);
		} else {
			UITools.showSingleConfirmDialog(this, null, getString(R.string.photo_preview_empty), new OnClickListener() {

				@Override
				public void onClick(View v) {
					PhotoPicker.getInstance().initialise();
					finish();
				}
			});
		}
	}

	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.im_title_back) {
			onBackPressed();
		} else if (id == R.id.im_title_right) {
			if (mPhotos != null && mPhotos.size() > 0) {
				int mCurrentItem = mViewPager.getCurrentItem();
				mPhotos.remove(mCurrentItem);
				mPagerAdapter.remove(mCurrentItem);
				if (mPhotos.size() == 0) {
					onBackPressed();
				} else {
					updatePercent();
					mPagerAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = getIntent();
		if (mIsEditMode) {
			intent.putStringArrayListExtra(RESULTS, mPhotos);
			if (intent.hasExtra(PhotoPicker.HANDLE_PICKER))
				PhotoPicker.handleActivityResult(RESULT_OK, intent);
			else
				setResult(RESULT_OK, intent);
		} else {
			PhotoPicker.getInstance().initialise();
		}
		finish();
	}

	protected boolean isUp;

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mCurrent = arg0;
		updatePercent();
	}

	protected void updatePercent() {
		mToolBarTitle.setText((mCurrent + 1) + "/" + mPhotos.size());
	}

	private OnViewTapListener onPhotoViewTapListener = new OnViewTapListener() {

		@Override
		public void onViewTap(View view, float x, float y) {
			if (!isUp) {
				mToolBar.setVisibility(View.GONE);
				isUp = true;
			} else {
				mToolBar.setVisibility(View.VISIBLE);
				isUp = false;
			}
			full(isUp);
		}
	};

	private void full(boolean enable) {
		if (enable) {
			WindowManager.LayoutParams lp = getWindow().getAttributes();
			lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(lp);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attr = getWindow().getAttributes();
			attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attr);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
		if (mTintManager != null)
			mTintManager.setStatusBarTintResource(enable ? R.drawable.transparent : R.color.colorPrimaryDark);
	}
}
