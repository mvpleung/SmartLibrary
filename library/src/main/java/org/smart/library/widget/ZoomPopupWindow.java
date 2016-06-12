package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.smart.library.R;
import org.smart.library.adapter.ZoomImageAdapter;
import org.smart.library.tools.UITools;
import org.smart.library.widget.EcoGalleryAdapterView.OnItemClickListener;
import org.smart.library.widget.EcoGalleryAdapterView.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义PopupWindow，选择图片
 *
 * @author LiangZiChao
 *         created on 2013-7-29 下午08:53:18
 */
@SuppressLint("InflateParams")
public class ZoomPopupWindow implements OnClickListener {

    private Context context;
    private PopupWindow zoomPhotoWindow;
    private PicGallery gallery;
    private ZoomImageAdapter mZoomImageAdapter;
    private TextView tv_count;
    private List<String> imgUrls;
    private int position;

    public ZoomPopupWindow() {
        super();
    }

    /**
     * 初始化参数
     */
    private void init() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.popup_zoom, null);
        zoomPhotoWindow = new PopupWindow(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        zoomPhotoWindow.setAnimationStyle(R.style.animPopupWindow);
        // 点击popupWindow之外的地方能让其消失
        zoomPhotoWindow.setOutsideTouchable(true);
        tv_count = new TextView(context);
        tv_count.setTextColor(Color.WHITE);
        gallery = (PicGallery) view.findViewById(R.id.pic_gallery);
        gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
        gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
        gallery.setDetector(new GestureDetector(context, new MySimpleGesture()));
        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(EcoGalleryAdapterView<?> arg0, View arg1, int arg2, long arg3) {
                clossZoomPhotoWindow();
            }

        });
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(EcoGalleryAdapterView<?> arg0, View arg1, int arg2, long arg3) {
                tv_count.setText((arg2 + 1) + "/" + imgUrls.size());
                UITools.showToast(context, tv_count, Toast.LENGTH_SHORT);
            }

            @Override
            public void onNothingSelected(EcoGalleryAdapterView<?> arg0) {

            }
        });
        gallery.setAdapter(mZoomImageAdapter = new ZoomImageAdapter(context, imgUrls) {

            @Override
            public String convertImgUrl(String url) {
                return convertImageUrl(url);
            }
        });
        gallery.setSelection(position);
    }

    protected class MySimpleGesture extends SimpleOnGestureListener {
        // 按两下的第二下Touch down时触发
        public boolean onDoubleTap(MotionEvent e) {

            View view = gallery.getSelectedView();
            if (view instanceof ZoomImageView) {
                ZoomImageView imageView = (ZoomImageView) view;
                if (imageView.getScale() > imageView.getMiniZoom()) {
                    imageView.zoomTo(imageView.getMiniZoom());
                } else {
                    imageView.zoomTo(imageView.getMaxZoom());
                }

            } else {

            }
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return true;
        }
    }

    public ZoomPopupWindow(Context context, List<String> imgUrls) {
        this(context, imgUrls, 0);
    }

    public ZoomPopupWindow(Context context, String imgUrl) {
        this.context = context;
        this.imgUrls = new ArrayList<String>();
        this.imgUrls.add(imgUrl);
        init();
    }

    public ZoomPopupWindow(Context context, List<String> imgUrls, int position) {
        this.context = context;
        this.imgUrls = imgUrls;
        this.position = position;
        init();
    }

    /**
     * popup is showing
     */
    public boolean isShowing() {
        return zoomPhotoWindow.isShowing();
    }

    /**
     * Set mCurrent Position
     *
     * @param position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * 显示缩放照片的窗口
     *
     * @param view the view on which to pin the popup window
     */
    public void showZoomPhotoWindow(View view, String path) {
        if (zoomPhotoWindow != null) {
            if (mZoomImageAdapter == null)
                gallery.setAdapter(new ZoomImageAdapter(context, path) {

                    @Override
                    public String convertImgUrl(String url) {
                        return convertImageUrl(url);
                    }
                });
            else
                mZoomImageAdapter.replaceImgUrl(path);
            zoomPhotoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 显示缩放照片的窗口
     *
     * @param view the view on which to pin the popup window
     */
    public void showZoomPhotoWindow(View view, List<String> imgUrls) {
        if (zoomPhotoWindow != null) {
            if (mZoomImageAdapter == null)
                gallery.setAdapter(new ZoomImageAdapter(context, imgUrls) {

                    @Override
                    public String convertImgUrl(String url) {
                        return convertImageUrl(url);
                    }
                });
            else
                mZoomImageAdapter.replaceAll(imgUrls);
            zoomPhotoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 显示缩放照片的窗口
     *
     * @param view the view on which to pin the popup window
     */
    public void showZoomPhotoWindow(View view) {
        if (zoomPhotoWindow != null) {
            if (gallery != null && gallery.getAdapter() != null)
                gallery.setSelection(position);
            zoomPhotoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 显示缩放照片的窗口
     *
     * @param view the view on which to pin the popup window
     */
    public void showZoomPhotoWindow(View view, int position) {
        if (zoomPhotoWindow != null) {
            setPosition(position);
            if (gallery != null && gallery.getAdapter() != null)
                gallery.setSelection(position);
            zoomPhotoWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * 关闭缩放照片的窗口
     */
    public void clossZoomPhotoWindow() {
        if (zoomPhotoWindow.isShowing()) {
            zoomPhotoWindow.dismiss();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        clossZoomPhotoWindow();
    }

    public String convertImageUrl(String url) {
        return url;
    }
}
