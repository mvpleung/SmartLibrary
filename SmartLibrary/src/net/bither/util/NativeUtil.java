/*
 * Copyright 2014 http://Bither.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.bither.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.exiaobai.library.tools.ImageUtils;
import com.lidroid.xutils.util.LogUtils;

/**
 * JNI调用压缩
 * 
 * @Data 2015年7月31日
 * @Package net.bither.util
 */
public class NativeUtil {
	private static int DEFAULT_QUALITY = 80;

	public static void compressBitmap(Bitmap bit, String fileName, boolean optimize) throws Throwable {
		compressBitmap(bit, DEFAULT_QUALITY, fileName, optimize);
	}

	public static void compressBitmap(String srcPath, String fileName, boolean optimize) throws Throwable {
		compressBitmap(srcPath, DEFAULT_QUALITY, fileName, optimize);
	}

	public static void compressBitmap(Bitmap bit, int quality, String fileName, boolean optimize) throws Throwable {
		LogUtils.d("native:compress of native");

		if (bit != null) {
			if (bit.getConfig() != Config.ARGB_8888) {
				Bitmap result = null;

				result = Bitmap.createBitmap(bit.getWidth(), bit.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(result);
				Rect rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());// original
				rect = new Rect(0, 0, bit.getWidth(), bit.getHeight());
				canvas.drawBitmap(bit, null, rect, null);
				saveBitmap(result, quality, fileName, optimize);
				result.recycle();
			} else {
				saveBitmap(bit, quality, fileName, optimize);
			}
			if (bit != null)
				bit.recycle();
		} else {
			LogUtils.e("native:bit may be empty");
		}
	}

	public static void compressBitmap(String srcPath, int quality, String fileName, boolean optimize) throws Throwable {
		int degree = ImageUtils.readPictureDegree(srcPath);
		Bitmap bit = ImageUtils.rotate(ImageUtils.getScaledBitmap(srcPath), degree);
		if (bit != null) {
			compressBitmap(bit, fileName, optimize);
			if (bit != null) {
				bit.recycle();
				System.gc();
			}
		}
	}

	private static void saveBitmap(Bitmap bit, int quality, String fileName, boolean optimize) {
		compressBitmap(bit, bit.getWidth(), bit.getHeight(), quality, fileName.getBytes(), optimize);
	}

	private static native String compressBitmap(Bitmap bit, int w, int h, int quality, byte[] fileNameBytes, boolean optimize);

	static {
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");
	}

}
