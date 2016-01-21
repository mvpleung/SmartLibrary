package org.smart.library.ui.photo;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.joanzapata.android.AbstractPagerAdapter;
import com.joanzapata.android.BaseAdapterHelper;
import com.polites.GestureImageView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import org.smart.library.R;
import org.smart.library.control.AppManager;
import org.smart.library.tools.GlideHelper;
import org.smart.library.tools.JudgmentLegal;

import java.io.File;
import java.util.ArrayList;

/**
 * 图片预览
 *
 * @author LiangZiChao
 *         created on 2015年7月9日
 *         In the net.gemeite.merchant.ui.photo
 */
public class PhotoPreviewActivity extends Activity implements OnClickListener, OnPageChangeListener {

    /**
     * 图片集合
     */
    public final static String PHOTOS = "photos";

    /**
     * 单张图片
     */
    public final static String PHOTO = "photo";

    public final static String EDIT_MODE = "editMode";

    public final static String POSITION = "position";

    /**
     * 是否是文件路径
     */
    public final static String PHOTO_FILE_PATH = "photoFilePath";

    private View mToolBar;
    private ViewPager mViewPager;
    private ImageView mImageDelete;
    private TextView mToolBarTitle;

    private SystemBarTintManager mTintManager;

    private RequestManager mRequestManager;

    private ArrayList<String> photos;
    protected int current;

    private AbstractPagerAdapter<String> mPagerAdapter;

    boolean isFilePath;// 是否本地文件路径false为本地路径

    LayoutInflater mInflater;

    boolean isSingle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTheme(android.R.style.Theme_Light_NoTitleBar);
        mRequestManager = Glide.with(this);
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
        findViewById(R.id.tv_title_back).setOnClickListener(this);
        mToolBar = findViewById(R.id.title_layout);
        mViewPager = (ViewPager) findViewById(R.id.vp_base_app);
        mImageDelete = (ImageView) findViewById(R.id.im_title_right);
        mToolBarTitle = (TextView) findViewById(R.id.tv_title);
        mImageDelete.setOnClickListener(this);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setOnClickListener(photoItemClickListener);
        findViewById(R.id.tv_title_back).setOnClickListener(this);

        overridePendingTransition(R.anim.activity_alpha_action_in, 0); // 渐入效果
        mInflater = getLayoutInflater();
    }

    private void initData() {
        Bundle extras = getIntent().getExtras();
        if (extras == null)
            return;

        if (extras.containsKey(PHOTOS)) {
            photos = extras.getStringArrayList(PHOTOS);
        } else if (extras.containsKey(PHOTO)) {
            isSingle = true;
            photos = new ArrayList<String>();
            photos.add(extras.getString(PHOTO));
        }

        if (photos != null) { // 预览图片
            if (extras.getBoolean(EDIT_MODE)) {
                mImageDelete.setVisibility(View.VISIBLE);
                mImageDelete.setImageResource(R.drawable.delete_icon);
            }
            isFilePath = extras.getBoolean(PHOTO_FILE_PATH, true);
            current = extras.getInt(POSITION, 0);
            updatePercent();
            mViewPager.setAdapter(mPagerAdapter = new AbstractPagerAdapter<String>(this, R.layout.adapter_photopreview, photos) {

                @Override
                protected void convert(BaseAdapterHelper helper, String item) {
                    int position = helper.getPosition();
                    GestureImageView mImageView = helper.getView(R.id.iv_image);
                    if (isFilePath)
                        GlideHelper.load(mRequestManager, new File(getItem(position)), mImageView);
                    else
                        GlideHelper.load(mRequestManager, getItem(position), mImageView);
                    mImageView.setTag(position);
                    mImageView.setOnClickListener(photoItemClickListener);
                }
            });
            mViewPager.setCurrentItem(current);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_title_back) {
            onBackPressed();
        } else if (id == R.id.im_title_right) {
            if (photos != null && photos.size() > 0) {
                int mCurrentItem = mViewPager.getCurrentItem();
                photos.remove(mCurrentItem);
                mPagerAdapter.remove(mCurrentItem);
                if (photos.size() == 0) {
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
        Intent mIntent = getIntent();
        if (isSingle) {
            mIntent.putExtra(PHOTO, JudgmentLegal.isListFull(photos) ? photos.get(0) : null);
        } else {
            mIntent.putStringArrayListExtra(PHOTOS, photos);
        }
        setResult(RESULT_OK, mIntent);
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
        current = arg0;
        updatePercent();
    }

    protected void updatePercent() {
        mToolBarTitle.setText((current + 1) + "/" + photos.size());
    }

    /**
     * 图片点击事件回调
     */
    private OnClickListener photoItemClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
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
