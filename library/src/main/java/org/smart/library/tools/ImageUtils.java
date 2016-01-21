package org.smart.library.tools;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import net.bither.util.NativeUtil;

import org.xutils.common.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Hashtable;

/**
 * 图片处理工具类
 * 
 * @author LiangZiChao
 *         created on 2014-7-30上午11:31:55
 */
@SuppressWarnings("deprecation")
public class ImageUtils {
	/**
	 * 缩图大小
	 * 
	 * @param srcDrawable
	 * @return
	 */
	public static Drawable createWaterMarkDrawable(Drawable srcDrawable, Drawable watermarkDrawable) {
		if (srcDrawable == null) {
			return null;
		}

		Bitmap src = drawable2Bitmap(srcDrawable);
		srcDrawable.setFilterBitmap(true);

		Bitmap watermark = drawable2Bitmap(watermarkDrawable);
		int w = src.getWidth() + 40;
		int h = src.getHeight() + 40;
		int ww = watermark.getWidth();
		// int wh = watermark.getHeight();

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);
		// 对于批量购 图片进行平移
		cv.drawBitmap(watermark, w - ww - 14, -3, null);// 在src的右上角画入水印
		cv.save(Canvas.ALL_SAVE_FLAG); // 保存所有的标志
		cv.restore(); // 恢复
		return bitmap2Drawable(newb);
	}

	/**
	 * 水印
	 * 
	 * @param srcDrawable
	 * @return
	 */
	public static Drawable createWaterMarkDrawableS(Drawable srcDrawable) {
		if (srcDrawable == null) {
			return null;
		}
		Bitmap src = drawable2Bitmap(srcDrawable);
		srcDrawable.setFilterBitmap(true);

		// Bitmap watermark = drawable2Bitmap(watermarkDrawable);
		int w = src.getWidth() + 160;
		int h = src.getHeight() + 160;
		// int ww = watermark.getWidth();
		// int wh = watermark.getHeight();

		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		Canvas cv = new Canvas(newb);
		cv.drawBitmap(src, 0, 0, null);
		// 对于批量购 图片进行平移
		// cv.drawBitmap(srcDrawable, -6, null);// 在src的右上角画入水印
		cv.drawBitmap(src, 10, -6, null);
		cv.save(Canvas.ALL_SAVE_FLAG); // 保存所有的标志
		cv.restore(); // 恢复
		return bitmap2Drawable(newb);
	}

	/**
	 * create the bitmap from a byte array
	 * 
	 * @param src
	 *            the bitmap object you want proecss
	 * @param watermark
	 *            the water mark above the src
	 * @return return a bitmap object ,if paramter's length is 0,return null
	 */
	public static Bitmap createBitmap(Bitmap src, Bitmap watermark) {
		String tag = "createBitmap";
		Log.d(tag, "create a new bitmap");
		if (src == null) {
			return null;
		}

		watermark = small(watermark);

		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		// int wh = watermark.getHeight();
		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		// cv.drawBitmap( watermark, w - ww + 5, h - wh + 5, null
		// );//在src的右下角画入水印
		cv.drawBitmap(watermark, w - ww + 0, 0, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储
		return newb;
	}

	/**
	 * 设置水印
	 * 
	 * @param bitmap
	 * @return
	 */
	private static Bitmap small(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		matrix.postScale(0.8f, 0.8f); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	/**
	 * Bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * Drawable 转 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth() * 2, drawable.getIntrinsicHeight() * 2, drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 图片旋转
	 * 
	 * @param drawable
	 * @param degrees
	 *            角度，负数为逆时针，正数为顺时针
	 * @return
	 */
	public static Drawable rotate(Drawable drawable, float degrees) {
		Bitmap img = drawable2Bitmap(drawable);
		Matrix matrix = new Matrix();
		matrix.postRotate(degrees); /* 翻转degrees度 */
		int widthOrig = img.getWidth();
		int heightOrig = img.getHeight();
		Bitmap resizeBitmap = Bitmap.createBitmap(img, 0, 0, widthOrig, heightOrig, matrix, true);
		return new BitmapDrawable(resizeBitmap);
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param drawable
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static BitmapDrawable toRoundCorner(Drawable drawable, int pixels) {
		return new BitmapDrawable(toRoundCorner(drawable2Bitmap(drawable), pixels));
	}

	/**
	 * 获取圆角位图的方法
	 * 
	 * @param bitmap
	 *            需要转化成圆角的位图
	 * @param pixels
	 *            圆角的度数，数值越大，圆角越大
	 * @return 处理后的圆角位图
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * 根据原图和变长绘制圆形图片
	 * 
	 * @param source
	 * @param min
	 * @return
	 */
	public static Bitmap createCircleImage(Bitmap source, int min) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(min, min, Config.ARGB_8888);
		/**
		 * 产生一个同样大小的画布
		 */
		Canvas canvas = new Canvas(target);
		/**
		 * 首先绘制圆形
		 */
		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		/**
		 * 使用SRC_IN，参考上面的说明
		 */
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		/**
		 * 绘制图片
		 */
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createRoundConerImage(Bitmap source) {
		return createRoundConerImage(source.getWidth(), source.getHeight(), source);
	}

	/**
	 * 根据原图添加圆角
	 * 
	 * @param source
	 * @return
	 */
	public static Bitmap createRoundConerImage(int width, int height, Bitmap source) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		Bitmap target = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(target);
		RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
		canvas.drawRoundRect(rect, 50f, 50f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	/**
	 * 将View或者Viewgroup转化为Bitmap
	 * 
	 * @param addViewContent
	 * @return
	 */
	public static Bitmap getViewBitmap(View addViewContent) {

		addViewContent.setDrawingCacheEnabled(true);

		addViewContent.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		addViewContent.layout(0, 0, addViewContent.getMeasuredWidth(), addViewContent.getMeasuredHeight());

		addViewContent.buildDrawingCache();
		Bitmap cacheBitmap = addViewContent.getDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		return bitmap;
	}

	/**
	 * 图片转成string
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String convertBitmapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
		bitmap.compress(CompressFormat.PNG, 100, baos);
		byte[] appicon = baos.toByteArray();// 转为byte数组
		return Base64.encodeToString(appicon, Base64.DEFAULT);

	}

	/**
	 * string转成bitmap
	 * 
	 * @param st
	 */
	public static Bitmap convertStringToBitmap(String st) {
		// OutputStream out;
		Bitmap bitmap = null;
		try {
			// out = new FileOutputStream("/sdcard/aa.jpg");
			byte[] bitmapArray = Base64.decode(st, Base64.DEFAULT);
			// bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
			// bitmapArray.length);
			bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(bitmapArray));
			// bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			return bitmap;
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * bytes字符串转换为Byte值
	 * 
	 * @param src
	 *            src Byte字符串，每个Byte之间没有分隔符
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src) {
		int m = 0, n = 0;
		int l = src.length() / 2;
		byte[] ret = new byte[l];
		for (int i = 0; i < l; i++) {
			m = i * 2 + 1;
			n = m + 1;
			ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
		}
		return ret;
	}

	/**
	 * 生成二维码
	 * 
	 * @param qrcode
	 * @return
	 */
	public static Bitmap createQRImage(String qrcode, int qrWidth, int qrHeight) {
		try {
			// 判断URL合法性
			if (TextUtils.isEmpty(qrcode)) {
				return null;
			}
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			hints.put(EncodeHintType.MARGIN, 0);
			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(qrcode, BarcodeFormat.QR_CODE, qrWidth, qrHeight, hints);
			int[] pixels = new int[qrWidth * qrHeight];
			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < qrHeight; y++) {
				for (int x = 0; x < qrWidth; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * qrWidth + x] = 0xff000000;
					} else {
						pixels[y * qrWidth + x] = 0xffffffff;
					}
				}
			}
			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(qrWidth, qrHeight, Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, qrWidth, 0, 0, qrWidth, qrHeight);
			return bitmap;
		} catch (WriterException e) {
			LogUtil.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 保存指定VIew截图
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap); // 创建画布
		view.draw(canvas);
		return bitmap;
	}

	/**
	 * 颜色加深处理
	 * 
	 * @param RGBValues
	 *            RGB的值，由alpha（透明度）、red（红）、green（绿）、blue（蓝）构成，
	 *            Android中我们一般使用它的16进制，
	 *            例如："#FFAABBCC",最左边到最右每两个字母就是代表alpha（透明度）、
	 *            red（红）、green（绿）、blue（蓝）。每种颜色值占一个字节(8位)，值域0~255
	 *            所以下面使用移位的方法可以得到每种颜色的值，然后每种颜色值减小一下，在合成RGB颜色，颜色就会看起来深一些了
	 * @param burn
	 *            正数代表变浅，复数代表加深
	 * @return
	 */
	public static int colorBurn(int RGBValues, float burn) {
		// int alpha = RGBValues >> 24;
		int red = RGBValues >> 16 & 0xFF;
		int green = RGBValues >> 8 & 0xFF;
		int blue = RGBValues & 0xFF;
		red = (int) Math.floor(red * (1 + burn));
		green = (int) Math.floor(green * (1 + burn));
		blue = (int) Math.floor(blue * (1 + burn));
		return Color.rgb(red, green, blue);
	}

	/**
	 * 压缩图片到制定路径
	 */
	public static void compress(String srcPath, String fPath) {
		int degree = readPictureDegree(srcPath);
		Bitmap mBitmap = rotate(getSmallCompressBitmap(srcPath), degree);
		FileTools.saveSourcePhoto(mBitmap, fPath);
	}

	/**
	 * 压缩图片到制定路径
	 */
	public static String compressNative(String srcPath, String fPath) {
		try {
			NativeUtil.compressBitmap(srcPath, fPath, true);
			return fPath;
		} catch (Throwable e) {
			LogUtil.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 压缩图片到制定路径
	 * 
	 * @param percent
	 *            压缩比例
	 */
	public static void compress(String srcPath, String fPath, int percent) {
		int degree = readPictureDegree(srcPath);
		Bitmap mBitmap = rotate(getSmallCompressBitmap(srcPath, percent), degree);
		FileTools.saveSourcePhoto(mBitmap, fPath);
	}

	/**
	 * 旋转获取的bitmap
	 */
	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees == 0 || b == null) {
			return b;
		}
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth(), (float) b.getHeight());
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();
					b = b2;
				}
			} catch (Exception ex) {
				// 建议如果出现了内存不足异常，最好return 原始的bitmap对象。.
				LogUtil.e(ex.getMessage(), ex);
			}
		}
		return b;
	}

	/**
	 * 获取图片选装的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			LogUtil.e(e.getMessage(), e);
		}
		return degree;
	}

	/**
	 * 获取小图
	 */
	public static Bitmap getSmallBitmap(String filePath) {
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			// 调用上面定义的方法计算inSampleSize值
			options.inSampleSize = calculateInSampleSize(options, 640f, 960f);
			options.inDither = false;
			// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
			options.inPreferredConfig = Config.RGB_565;
			// 使用获取到的inSampleSize值再次解析图片
			options.inJustDecodeBounds = false;

			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);
			return BitmapFactory.decodeStream(bs, null, options);
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
		} finally {
			try {
				if (bs != null)
					bs.close();
				if (fs != null)
					fs.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 比例大小压缩
	 */
	public static Bitmap getSmallCompressBitmap(String filePath) {
		Bitmap bitmap = getSmallBitmap(filePath);
		if (bitmap != null) {
			bitmap = compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
		}
		return bitmap;
	}

	/**
	 * 比例大小压缩
	 * 
	 * @param filePath
	 *            原路径
	 * @param percent
	 *            压缩比例（长宽比）
	 * @return
	 */
	public static Bitmap getSmallCompressBitmap(String filePath, int percent) {
		FileInputStream fs = null;
		BufferedInputStream bs = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			options.inSampleSize = calculateInSampleSize(options, percent);// 设置缩放比例
			options.inDither = false;
			options.inJustDecodeBounds = false;
			// 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
			options.inPreferredConfig = Config.RGB_565;
			// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			fs = new FileInputStream(filePath);
			bs = new BufferedInputStream(fs);
			return compressImage(BitmapFactory.decodeStream(bs, null, options));// 压缩好比例大小后再进行质量压缩
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
		} finally {
			try {
				if (bs != null)
					bs.close();
				if (fs != null)
					fs.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 获取缩放的Bitmap
	 * 
	 * @param filePath
	 * @return
	 */
	public static Bitmap getScaledBitmap(String filePath) {
		return getScaledBitmap(filePath, 640, 960);
	}

	/**
	 * 获取缩放的bitmap
	 * 
	 * @param filePath
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static Bitmap getScaledBitmap(String filePath, int maxWidth, int maxHeight) {
		try {
			BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
			Bitmap bitmap = null;
			// If we have to resize this image, first get the natural bounds.
			decodeOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, decodeOptions);
			int actualWidth = decodeOptions.outWidth;
			int actualHeight = decodeOptions.outHeight;

			// Then compute the dimensions we would ideally like to decode to.
			int desiredWidth = getResizedDimension(maxWidth, maxHeight, actualWidth, actualHeight);
			int desiredHeight = getResizedDimension(maxHeight, maxWidth, actualHeight, actualWidth);

			// Decode to the nearest power of two scaling factor.
			decodeOptions.inJustDecodeBounds = false;
			// TODO(ficus): Do we need this or is it okay since API 8 doesn't
			// support it?
			// decodeOptions.inPreferQualityOverSpeed =
			// PREFER_QUALITY_OVER_SPEED;
			decodeOptions.inSampleSize = findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);
			Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
			// If necessary, scale down to the maximal acceptable size.
			if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth || tempBitmap.getHeight() > desiredHeight)) {
				bitmap = Bitmap.createScaledBitmap(tempBitmap, desiredWidth, desiredHeight, true);
				tempBitmap.recycle();
			} else {
				bitmap = tempBitmap;
			}
			return bitmap;
		} catch (Exception e) {
			LogUtil.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Returns the largest power-of-two divisor for use in downscaling a bitmap
	 * that will not result in the scaling past the desired dimensions.
	 *
	 * @param actualWidth
	 *            Actual width of the bitmap
	 * @param actualHeight
	 *            Actual height of the bitmap
	 * @param desiredWidth
	 *            Desired width of the bitmap
	 * @param desiredHeight
	 *            Desired height of the bitmap
	 */
	// Visible for testing.
	public static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
		double wr = (double) actualWidth / desiredWidth;
		double hr = (double) actualHeight / desiredHeight;
		double ratio = Math.min(wr, hr);
		float n = 1.0f;
		while ((n * 2) <= ratio) {
			n *= 2;
		}

		return (int) n;
	}

	/**
	 * Scales one side of a rectangle to fit aspect ratio.
	 *
	 * @param maxPrimary
	 *            Maximum size of the primary dimension (i.e. width for max
	 *            width), or zero to maintain aspect ratio with secondary
	 *            dimension
	 * @param maxSecondary
	 *            Maximum size of the secondary dimension, or zero to maintain
	 *            aspect ratio with primary dimension
	 * @param actualPrimary
	 *            Actual size of the primary dimension
	 * @param actualSecondary
	 *            Actual size of the secondary dimension
	 */
	public static int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary, int actualSecondary) {
		// If no dominant value at all, just return the actual.
		if (maxPrimary == 0 && maxSecondary == 0) {
			return actualPrimary;
		}

		// If primary is unspecified, scale primary to match secondary's scaling
		// ratio.
		if (maxPrimary == 0) {
			double ratio = (double) maxSecondary / (double) actualSecondary;
			return (int) (actualPrimary * ratio);
		}

		if (maxSecondary == 0) {
			return maxPrimary;
		}

		double ratio = (double) actualSecondary / (double) actualPrimary;
		int resized = maxPrimary;
		if (resized * ratio > maxSecondary) {
			resized = (int) (maxSecondary / ratio);
		}
		return resized;
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, float reqWidth, float reqHeight) {
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to the
			// requested height and width.
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		// if (height > reqHeight || width > reqWidth) {
		//
		// final int halfHeight = height / 2;
		// final int halfWidth = width / 2;
		//
		// // 在保证解析出的bitmap宽高分别大于目标尺寸宽高的前提下，取可能的inSampleSize的最大值
		// while ((halfHeight / inSampleSize) > reqHeight && (halfWidth /
		// inSampleSize) > reqWidth) {
		// inSampleSize *= 2;
		// }
		// }
		return inSampleSize;
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param percent
	 *            长宽压缩比
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, float percent) {
		// 源图片的宽度
		float width = options.outWidth;
		float height = options.outHeight;
		int inSampleSize = 1;

		float reqWidth = width * percent;
		float reqHeight = height * percent;

		if (width > reqWidth && height > reqHeight) {
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round(width / reqWidth);
			int heightRatio = Math.round(height / reqHeight);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 质量压缩
	 */
	public static Bitmap compressImage(Bitmap image) {
		if (image == null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ByteArrayInputStream isBm = null;
		try {
			image.compress(CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			int options = 100;
			while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				image.compress(CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 10;// 每次都减少10
			}
			if (image != null) {
				image.recycle();
				image = null;
			}
			isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
			return bitmap;
		} catch (Throwable e) {
			LogUtil.e(e.getMessage(), e);
		} finally {
			try {
				if (isBm != null)
					isBm.close();
				baos.close();
			} catch (Exception e2) {
			}
		}
		return null;
	}

	/***
	 * 图片的缩放方法
	 *
	 * 
	 * @param bgimage
	 *            ：源图片资源
	 * 
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 * 
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {

		// 获取这个图片的宽和高

		int width = bgimage.getWidth();
		int height = bgimage.getHeight();

		// 创建操作图片用的matrix对象

		Matrix matrix = new Matrix();

		// 计算缩放率，新尺寸除原始尺寸

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 缩放图片动作

		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, width, height, matrix, true);
		return bitmap;

	}
}
