/*
 * Copyright (c) 2014. xbtrip(深圳小白领先科技有限公司)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import org.xutils.common.util.LogUtil;

import java.security.MessageDigest;

/**
 * MD5工具类
 *
 * @author LiangZiChao
 *         created on 2014-11-4下午5:53:56
 */
@SuppressLint("DefaultLocale")
public class MD5 {

    /**
     * MD5(16位)
     *
     * @param bytes
     * @return
     */
    public static String getEncryptDigit16(byte[] bytes) {
        return getEncryptDigit(bytes, 16);
    }

    /**
     * MD5(16位)
     *
     * @param mData
     * @return
     */
    public static String getEncryptDigit16(String mData) {
        return TextUtils.isEmpty(mData) ? "" : getEncryptDigit(mData.getBytes(), 16);
    }

    /**
     * MD5(32位)
     *
     * @param bytes
     * @return
     */
    public static String getEncryptDigit32(byte[] bytes) {
        return getEncryptDigit(bytes, 32);
    }

    /**
     * MD5(32位)
     *
     * @param mData
     * @return
     */
    public static String getEncryptDigit32(String mData) {
        return TextUtils.isEmpty(mData) ? "" : getEncryptDigit(mData.getBytes(), 32);
    }

    /**
     * MD5加密
     *
     * @param bytes
     * @return
     */
    private static String getEncryptDigit(byte[] bytes, int encryptDigit) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(bytes);
        } catch (Exception e) {
            LogUtil.e(e.getMessage(), e);
        }

        if (messageDigest == null)
            return null;
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        // 16位加密，从第9位到25位
        return encryptDigit == 16 ? md5StrBuff.substring(8, 24).toString().toUpperCase() : md5StrBuff.toString().toUpperCase();
    }
}
