package org.smart.library.tools;

import android.annotation.SuppressLint;
import android.util.Base64;

import org.smart.library.control.L;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * AES加密
 * @author
 *         created on LiangZiChao Update By 2014-8-11下午7:56:03
 */
public class AESUtils {
	private static byte[] key = Base64.decode("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4".getBytes(), Base64.DEFAULT);
	private static byte[] keyiv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * ECB加密,不要IV
	 * 
	 * @param data
	 *            明文
	 * @return Base64编码的密文
	 * @throws Exception
	 */
	@SuppressLint("TrulyRandom")
	public static byte[] des3EncodeECB(byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	/**
	 * ECB加密,不要IV
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            明文
	 * @return Base64编码的密文
	 * @throws Exception
	 */
	public static byte[] des3EncodeECB(byte[] key, byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	/**
	 * ECB解密,不要IV
	 * 
	 * @param data
	 *            Base64编码的密文
	 * @return 明文
	 * @throws Exception
	 */
	public static byte[] ees3DecodeECB(byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		L.i("decode init before");
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		L.i("decode init after");
		byte[] bOut = cipher.doFinal(data);
		L.i("decode doFinal after");
		return bOut;

	}

	/**
	 * ECB解密,不要IV
	 * 
	 * @param key
	 *            密钥
	 * @param data
	 *            Base64编码的密文
	 * @return 明文
	 * @throws Exception
	 */
	public static byte[] ees3DecodeECB(byte[] key, byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
		L.i("decode init before");
		cipher.init(Cipher.DECRYPT_MODE, deskey);
		L.i("decode init after");
		byte[] bOut = cipher.doFinal(data);
		L.i("decode doFinal after");
		return bOut;

	}

	/**
	 * CBC加密
	 * 
	 * @param data
	 *            明文
	 * @return Base64编码的密文
	 * @throws Exception
	 */
	public static byte[] des3EncodeCBC(byte[] data) throws Exception {
		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	/**
	 * CBC解密
	 * 
	 * @param data
	 *            Base64编码的密文
	 * @return 明文
	 * @throws Exception
	 */
	public static byte[] des3DecodeCBC(byte[] data) throws Exception {

		Key deskey = null;
		DESedeKeySpec spec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
		deskey = keyfactory.generateSecret(spec);
		Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
		IvParameterSpec ips = new IvParameterSpec(keyiv);
		cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
		byte[] bOut = cipher.doFinal(data);
		return bOut;
	}

	/**
	 * 加密数据
	 * 
	 * @param mData
	 * @return
	 */
	public static String encodeData(String mData) {
		try {
			return new String(Base64.encode(AESUtils.des3EncodeECB(mData.getBytes("UTF-8")), Base64.DEFAULT), "UTF-8");
		} catch (Exception e) {
			L.e(e.getMessage(), e);
			return null;
		}
	}

	/**
	 * 解密数据
	 * 
	 * @param mEncode
	 * @return
	 */
	public static String decodeData(String mEncode) {
		try {
			return new String(AESUtils.ees3DecodeECB(Base64.decode(mEncode.getBytes("UTF-8"), Base64.DEFAULT)), "UTF-8");
		} catch (Exception e) {
			L.e(e.getMessage(), e);
			return null;
		}
	}
}