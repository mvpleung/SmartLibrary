/*
 * Copyright (C) 2015 Quinn Chen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smart.library.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import org.smart.library.R;
import org.smart.library.tools.ImageUtils;
import org.smart.library.control.L;

/**
 * 
 * @author Quinn Chen
 * @version LiangZiChao Updated . 增加绘制图片背景，绘制文本
 *         created on 2015年7月3日
 */
@SuppressLint({ "ClickableViewAccessibility", "HandlerLeak" })
public class SlideSwitch extends View {

	private static final int[] ATTRS = new int[] { android.R.attr.layout_width, android.R.attr.layout_height };

	public static final int SHAPE_RECT = 1;
	public static final int SHAPE_CIRCLE = 2;
	private static final int RIM_SIZE = 6;
	private static final int DEFAULT_COLOR_THEME = Color.parseColor("#ff00ee00");
	// 3 attributes
	private int color_on_theme, color_off_theme;
	private boolean isOpen;
	private int shape;
	private int circle_on_color, circle_off_color;
	private Bitmap bitmap_on_theme, bitmap_off_theme, circle_on_bitmap, circle_off_bitmap;

	// varials of drawing
	private Paint paint;
	private Rect backRect;
	private Rect frontRect;
	private RectF frontCircleRect;
	private RectF backCircleRect;
	private int alpha;
	private int max_left;
	private int min_left;
	private int frontRect_left;
	private int frontRect_left_begin = RIM_SIZE;
	private int eventStartX;
	private int eventLastX;
	private int diffX = 0;
	private int space; // 滑块间隙
	private boolean measureSlide; // 测量滑块
	private boolean slideable = true;
	/** 绘制文本 */
	private String mOnText = "", mOffText = "";
	private int mOnTextColor = Color.WHITE, mOffTextColor = Color.WHITE;
	private int mTextSize = 20;
	private SlideListener listener;

	public interface SlideListener {
		public void onSlide(View view, boolean isOpen);
	}

	public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		listener = null;
		paint = new Paint();
		paint.setAntiAlias(true);
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		int dstWidth = a.getDimensionPixelSize(0, 0);
		int dstHeight = a.getDimensionPixelSize(1, 0);
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.slideswitch);
		/** bitmap */
		int onThemeImage = a.getResourceId(R.styleable.slideswitch_onThemeImage, 0);
		if (onThemeImage != 0) {
			bitmap_on_theme = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), onThemeImage), dstWidth, dstHeight, false);
		}
		int offThemeImage = a.getResourceId(R.styleable.slideswitch_offThemeImage, 0);
		if (offThemeImage != 0) {
			bitmap_off_theme = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), offThemeImage), dstWidth, dstHeight, false);
		}
		int onCircleImage = a.getResourceId(R.styleable.slideswitch_onCircleImage, 0);
		if (onCircleImage != 0) {
			circle_on_bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), onCircleImage), dstWidth, dstHeight, false);
		}
		int offCircleImage = a.getResourceId(R.styleable.slideswitch_offCircleImage, 0);
		if (offCircleImage != 0) {
			circle_off_bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), offCircleImage), dstWidth, dstHeight, false);
		}

		color_on_theme = a.getColor(R.styleable.slideswitch_onThemeColor, DEFAULT_COLOR_THEME);
		color_off_theme = a.getColor(R.styleable.slideswitch_offThemeColor, Color.GRAY);
		circle_on_color = a.getColor(R.styleable.slideswitch_onCircleColor, Color.WHITE);
		circle_off_color = a.getColor(R.styleable.slideswitch_offCircleColor, Color.parseColor("#BBB9B7"));
		slideable = a.getBoolean(R.styleable.slideswitch_slideable, slideable);
		isOpen = a.getBoolean(R.styleable.slideswitch_isOpen, false);
		shape = a.getInt(R.styleable.slideswitch_shape, SHAPE_CIRCLE);
		a.recycle();
	}

	public SlideSwitch(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideSwitch(Context context) {
		this(context, null);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureDimension(280, widthMeasureSpec);
		int height = measureDimension(140, heightMeasureSpec);
		if (shape == SHAPE_CIRCLE) {
			if (width < height)
				width = height * 2;
		}
		setMeasuredDimension(width, height);
		initDrawingVal();
	}

	public void initDrawingVal() {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		backCircleRect = new RectF();
		frontCircleRect = new RectF();
		frontRect = new Rect();
		backRect = new Rect(0, 0, width, height);
		min_left = RIM_SIZE;
		if (shape == SHAPE_RECT)
			max_left = width / 2;
		else
			max_left = width - (height - 2 * RIM_SIZE) - RIM_SIZE;
		if (isOpen) {
			frontRect_left = max_left;
			alpha = 255;
		} else {
			frontRect_left = RIM_SIZE;
			alpha = 0;
		}
		frontRect_left_begin = frontRect_left;

		int mCircleSize = Math.round(height * 0.8f);
		space = (height - mCircleSize) / 4;
		if (!measureSlide && mCircleSize > 0) {
			/** 计算滑动块缩放比例 */
			if (circle_off_bitmap != null) {
				// int circleHeight = circle_off_bitmap.getHeight();
				// float mScale = circleHeight > mCircleSize ? circleHeight *
				// 1.0f / mCircleSize : 1;
				// circleHeight = Math.round(circleHeight * mScale);
				// int mOffWidth = Math.round(circle_off_bitmap.getWidth() *
				// mScale);
				// if (mOffWidth > 0) {
				// circle_off_bitmap = ImageUtils.zoomImage(circle_off_bitmap,
				// mOffWidth, height);
				// measureSlide = !measureSlide;
				// }
				circle_off_bitmap = ImageUtils.zoomImage(circle_off_bitmap, mCircleSize, mCircleSize);
				measureSlide = !measureSlide;
			}
			if (circle_on_bitmap != null) {
				// float mScale = circle_on_bitmap.getHeight() / mCircleSize;
				// int mOnWidth = Math.round(circle_on_bitmap.getWidth() *
				// mScale);
				// if (mOnWidth > 0) {
				// circle_on_bitmap = ImageUtils.zoomImage(circle_on_bitmap,
				// mOnWidth, height);
				// measureSlide = !measureSlide;
				// }
				circle_on_bitmap = ImageUtils.zoomImage(circle_on_bitmap, mCircleSize, mCircleSize);
				measureSlide = !measureSlide;
			}
		}
	}

	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int radius = backRect.height() / 2 - RIM_SIZE;
		if (shape == SHAPE_RECT) {
			paint.setAlpha(255 - alpha);
			if (bitmap_off_theme != null) {
				canvas.drawBitmap(bitmap_off_theme, backRect.left, backRect.top, paint);
			} else {
				paint.setColor(color_off_theme);
				canvas.drawRect(backRect, paint);
			}
			paint.setColor(color_on_theme);
			paint.setAlpha(alpha);
			if (bitmap_on_theme != null) {
				canvas.drawBitmap(bitmap_on_theme, backRect.left, backRect.top, paint);
			} else
				canvas.drawRect(backRect, paint);

			drawText(canvas, radius);
			frontRect.set(frontRect_left, RIM_SIZE, frontRect_left + getMeasuredWidth() / 2 - RIM_SIZE, getMeasuredHeight() - RIM_SIZE);
			paint.setColor(isOpen ? circle_on_color : circle_off_color);
			if (isOpen && circle_on_bitmap != null)
				canvas.drawBitmap(circle_on_bitmap, frontRect.left + space, frontRect.top + space, paint);
			else if (!isOpen && circle_off_bitmap != null)
				canvas.drawBitmap(circle_off_bitmap, frontRect.left + space, frontRect.top + space, paint);
			else
				canvas.drawRect(frontRect, paint);
		} else {
			// draw circle
			backCircleRect.set(backRect);
			paint.setAlpha(255 - alpha);
			if (bitmap_off_theme != null) {
				canvas.drawBitmap(bitmap_off_theme, backCircleRect.left, backCircleRect.top, paint);
			} else {
				paint.setColor(color_off_theme);
				canvas.drawRoundRect(backCircleRect, radius, radius, paint);
			}

			paint.setColor(color_on_theme);
			paint.setAlpha(alpha);
			if (bitmap_on_theme != null) {
				canvas.drawBitmap(bitmap_on_theme, backCircleRect.left, backCircleRect.top, paint);
			} else {
				canvas.drawRoundRect(backCircleRect, radius, radius, paint);
			}

			drawText(canvas, radius);

			frontRect.set(frontRect_left, RIM_SIZE, frontRect_left + backRect.height() - 2 * RIM_SIZE, backRect.height() - RIM_SIZE);
			frontCircleRect.set(frontRect);
			paint.setColor(isOpen ? circle_on_color : circle_off_color);
			if (isOpen && circle_on_bitmap != null)
				canvas.drawBitmap(circle_on_bitmap, frontCircleRect.left + space, frontCircleRect.top + space, paint);
			else if (!isOpen && circle_off_bitmap != null)
				canvas.drawBitmap(circle_off_bitmap, frontCircleRect.left + space, frontCircleRect.top + space, paint);
			else
				canvas.drawRoundRect(frontCircleRect, radius, radius, paint);
		}
	}

	/**
	 * 画不可用状态
	 * 
	 * @param canvas
	 */
	protected void onDrawDisenable(Canvas canvas) {
		int radius = backRect.height() / 2 - RIM_SIZE;
		if (shape == SHAPE_RECT) {
			if (bitmap_off_theme != null) {
				canvas.drawBitmap(bitmap_off_theme, backRect.left, backRect.top, paint);
			} else {
				paint.setColor(ImageUtils.colorBurn(color_off_theme, 0.1f));
				canvas.drawRect(backRect, paint);
			}
			paint.setColor(ImageUtils.colorBurn(color_on_theme, 0.1f));
			paint.setAlpha(alpha);
			if (bitmap_on_theme != null) {
				canvas.drawBitmap(bitmap_on_theme, backRect.left, backRect.top, paint);
			} else
				canvas.drawRect(backRect, paint);

			drawText(canvas, radius);
			frontRect.set(frontRect_left, RIM_SIZE, frontRect_left + getMeasuredWidth() / 2 - RIM_SIZE, getMeasuredHeight() - RIM_SIZE);
			paint.setColor(ImageUtils.colorBurn(isOpen ? circle_on_color : circle_off_color, 0.1f));
			if (isOpen && circle_on_bitmap != null)
				canvas.drawBitmap(circle_on_bitmap, frontRect.left + space, frontRect.top + space, paint);
			else if (!isOpen && circle_off_bitmap != null)
				canvas.drawBitmap(circle_off_bitmap, frontRect.left + space, frontRect.top + space, paint);
			else
				canvas.drawRect(frontRect, paint);
		} else {
			// draw circle
			backCircleRect.set(backRect);
			if (bitmap_off_theme != null) {
				canvas.drawBitmap(bitmap_off_theme, backCircleRect.left, backCircleRect.top, paint);
			} else {
				paint.setColor(color_off_theme);
				canvas.drawRoundRect(backCircleRect, radius, radius, paint);
			}

			paint.setColor(ImageUtils.colorBurn(color_on_theme, 0.1f));
			paint.setAlpha(alpha);
			if (bitmap_on_theme != null) {
				canvas.drawBitmap(bitmap_on_theme, backCircleRect.left, backCircleRect.top, paint);
			} else {
				canvas.drawRoundRect(backCircleRect, radius, radius, paint);
			}

			drawText(canvas, radius);

			frontRect.set(frontRect_left, RIM_SIZE, frontRect_left + backRect.height() - 2 * RIM_SIZE, backRect.height() - RIM_SIZE);
			frontCircleRect.set(frontRect);
			paint.setColor(ImageUtils.colorBurn(isOpen ? circle_on_color : circle_off_color, 0.1f));
			if (isOpen && circle_on_bitmap != null)
				canvas.drawBitmap(circle_on_bitmap, frontCircleRect.left + space, frontCircleRect.top + space, paint);
			else if (!isOpen && circle_off_bitmap != null)
				canvas.drawBitmap(circle_off_bitmap, frontCircleRect.left + space, frontCircleRect.top + space, paint);
			else
				canvas.drawRoundRect(frontCircleRect, radius, radius, paint);
		}
	}

	private void drawText(Canvas canvas, int radius) {
		if (TextUtils.isEmpty(mOnText) || TextUtils.isEmpty(mOffText)) {
			// 绘制文本
			paint.setColor(isOpen ? mOnTextColor : mOffTextColor);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(mTextSize);
			canvas.drawText(isOpen ? mOnText : mOffText, isOpen ? radius : backRect.width() * 3 / 4, backRect.height() / 2 + RIM_SIZE, paint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (slideable == false)
			return super.onTouchEvent(event);
		L.i("onTouchEvent");
		getParent().requestDisallowInterceptTouchEvent(true);
		int action = MotionEventCompat.getActionMasked(event);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			eventStartX = (int) event.getRawX();
			break;
		case MotionEvent.ACTION_MOVE:
			eventLastX = (int) event.getRawX();
			diffX = eventLastX - eventStartX;
			int tempX = diffX + frontRect_left_begin;
			tempX = (tempX > max_left ? max_left : tempX);
			tempX = (tempX < min_left ? min_left : tempX);
			if (tempX >= min_left && tempX <= max_left) {
				frontRect_left = tempX;
				alpha = (int) (255 * (float) tempX / (float) max_left);
				invalidateView();
			}
			break;
		case MotionEvent.ACTION_UP:
			int wholeX = (int) (event.getRawX() - eventStartX);
			frontRect_left_begin = frontRect_left;
			boolean toRight = (frontRect_left_begin > max_left / 2 ? true : false);
			if (Math.abs(wholeX) < 3) {
				toRight = !toRight;
			}
			moveToDest(toRight);
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * draw again
	 */
	private void invalidateView() {
		if (Looper.getMainLooper() == Looper.myLooper()) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	public void setSlideListener(SlideListener listener) {
		this.listener = listener;
	}

	public SlideListener getSlideListener() {
		return listener;
	}

	public void moveToDest(final boolean toRight) {
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (listener != null)
					listener.onSlide(SlideSwitch.this, msg.what == 1);
			}

		};

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (toRight) {
					while (frontRect_left <= max_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left += 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							L.e(e.getMessage(), e);
						}
					}
					alpha = 255;
					frontRect_left = max_left;
					if (!isOpen) {
						isOpen = true;
						if (listener != null)
							handler.sendEmptyMessage(1);
					}
					frontRect_left_begin = max_left;
				} else {
					while (frontRect_left >= min_left) {
						alpha = (int) (255 * (float) frontRect_left / (float) max_left);
						invalidateView();
						frontRect_left -= 3;
						try {
							Thread.sleep(3);
						} catch (InterruptedException e) {
							L.e(e.getMessage(), e);
						}
					}
					alpha = 0;
					frontRect_left = min_left;
					if (isOpen) {
						isOpen = false;
						if (listener != null)
							handler.sendEmptyMessage(0);
					}
					frontRect_left_begin = min_left;
				}
			}
		}).start();
	}

	public void setState(boolean isOpen) {
		this.isOpen = isOpen;
		initDrawingVal();
		invalidateView();
		if (listener != null)
			listener.onSlide(this, isOpen);
	}

	public void initState(boolean isOpen) {
		this.isOpen = isOpen;
		initDrawingVal();
		invalidateView();
	}

	/**
	 * 设置开关上面的文本
	 * 
	 * @param onText
	 *            控件打开时要显示的文本
	 * @param offText
	 *            控件关闭时要显示的文本
	 * @param textSize
	 *            文本字号
	 * @param onTextColor
	 *            打开时文本颜色
	 * @param offTextColor
	 *            关闭时文本颜色
	 */
	public void setText(final String onText, final String offText, final int textSize, final int onTextColor, final int offTextColor) {
		mOnText = onText;
		mOffText = offText;
		if (textSize > 0)
			mTextSize = textSize;
		if (onTextColor > 0)
			mOnTextColor = onTextColor;
		if (offTextColor > 0)
			mOffTextColor = offTextColor;
		invalidateView();
	}

	public void setShapeType(int shapeType) {
		this.shape = shapeType;
	}

	public void setSlideable(boolean slideable) {
		this.slideable = slideable;
	}

	public boolean isOpen() {
		return isOpen;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			this.isOpen = bundle.getBoolean("isOpen");
			state = bundle.getParcelable("instanceState");
		}
		super.onRestoreInstanceState(state);
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putParcelable("instanceState", super.onSaveInstanceState());
		bundle.putBoolean("isOpen", this.isOpen);
		return bundle;
	}
}
