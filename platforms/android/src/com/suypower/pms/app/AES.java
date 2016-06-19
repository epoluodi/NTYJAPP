package com.suypower.pms.app;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


import android.util.Base64;
import android.util.Log;

public class AES  {

	private static String KEY_WORD = "M2cl3lBi9hK46NGaxv3CR5LPOkQRoEsX";
	public static final String VIPARA = "0102030405060708";
	public static final String bm = "GBK";


//	public static String encrypt(String dataPassword, String cleartext)
//			throws Exception {
//		IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
//		SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
//		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
//		byte[] encryptedData = cipher.doFinal(cleartext.getBytes("utf-8"));
//
//		return Base64.encodeToString(encryptedData, Base64.DEFAULT);
//	}
//
//	public static String decrypt(String dataPassword, String encrypted)
//			throws Exception {
//		byte[] byteMi = Base64.decode(encrypted,Base64.DEFAULT);
//		IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes());
//		SecretKeySpec key = new SecretKeySpec(dataPassword.getBytes(), "AES");
//		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
//		byte[] decryptedData = cipher.doFinal(byteMi);
//
//		return new String(decryptedData,"utf-8");
//	}


//	public static String encrypt(String seed, String cleartext) throws Exception {
//		byte[] rawKey = getRawKey(seed.getBytes());
//		byte[] result = encrypt(rawKey, cleartext.getBytes());
//		return toHex(result);
//	}
//
//	public static String decrypt(String seed, String encrypted) throws Exception {
//		byte[] rawKey = getRawKey(seed.getBytes());
//		byte[] enc = toByte(encrypted);
//		byte[] result = decrypt(rawKey, enc);
//		return new String(result);
//	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(128, sr); // 192 and 256 bits may not be available      
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}


//	private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
//		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//		Cipher cipher = Cipher.getInstance("AES");
//		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//		byte[] encrypted = cipher.doFinal(clear);
//		return encrypted;
//	}
//
//	private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
//		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//		Cipher cipher = Cipher.getInstance("AES");
//		cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//		byte[] decrypted = cipher.doFinal(encrypted);
//		return decrypted;
//	}

	public static String toHex(String txt) {
		return toHex(txt.getBytes());
	}
	public static String fromHex(String hex) {

		return new String(toByte(hex));
	}

	public static byte[] toByte(String hexString) {
		int len = hexString.length()/2;
		byte[] result = new byte[len];
		for (int i = 0; i < len; i++)
			result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
		return result;
	}

	public static String toHex(byte[] buf) {
		if (buf == null)
			return "";
		StringBuffer result = new StringBuffer(2*buf.length);
		for (int i = 0; i < buf.length; i++) {
			appendHex(result, buf[i]);
		}
		return result.toString();
	}
	private final static String HEX = "0123456789ABCDEF";
	private static void appendHex(StringBuffer sb, byte b) {
		sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
	}
	/** Called when the activity is first created. */


	/**
	 * Encodes a String in AES-256 with a given key
	 *
	 * @param context
	 * @param password
	 * @param text
	 * @return String Base64 and AES encoded String
	 */
	public static String encode(String keyString, String stringToEncode) throws NullPointerException {
		if (keyString.length() == 0 || keyString == null) {
			throw new NullPointerException("Please give Password");
		}

		if (stringToEncode.length() == 0 || stringToEncode == null) {
			throw new NullPointerException("Please give text");
		}

		try {
			SecretKeySpec skeySpec = getKey(keyString);
			byte[] clearText = stringToEncode.getBytes("UTF8");

			// IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
			final byte[] iv = new byte[16];
			Arrays.fill(iv, (byte) 0x00);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			// Cipher is not thread safe
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

			String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
			Log.d("jacek", "Encrypted: " + stringToEncode + " -> " + encrypedValue);
			return encrypedValue;

		}
		catch (Exception e){e.printStackTrace();}
		return "";
	}

	/**
	 * Decodes a String using AES-256 and Base64
	 *
	 * @param context
	 * @param password
	 * @param text
	 * @return desoded String
	 */
	public static String decode(String password, String text) throws NullPointerException {

		if (password.length() == 0 || password == null) {
			throw new NullPointerException("Please give Password");
		}

		if (text.length() == 0 || text == null) {
			throw new NullPointerException("Please give text");
		}

		try {
			SecretKey key = getKey(password);

			// IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
			final byte[] iv = new byte[16];
			Arrays.fill(iv, (byte) 0x00);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

			byte[] encrypedPwdBytes = Base64.decode(text, Base64.DEFAULT);
			// cipher is not thread safe
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
			byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

			String decrypedValue = new String(decrypedValueBytes);

			return decrypedValue;

		}
		catch (Exception e)
		{e.printStackTrace();}
		return "";
	}

	/**
	 * Generates a SecretKeySpec for given password
	 *
	 * @param password
	 * @return SecretKeySpec
	 * @throws UnsupportedEncodingException
	 */
	private static SecretKeySpec getKey(String password) throws Exception {

		// You can change it to 128 if you wish
		int keyLength = 256;
		byte[] keyBytes = new byte[keyLength / 8];
		// explicitly fill with zeros
		Arrays.fill(keyBytes, (byte) 0x0);

		// if password is shorter then key length, it will be zero-padded
		// to key length
		byte[] passwordBytes = password.getBytes("UTF-8");
		int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
		System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
		SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
		return key;
	}




	public static void main()
	{

		String originalText = "test";

		try {
			String encryptingCode = encode(KEY_WORD, originalText);
//          System.out.println("加密结果为 " + encryptingCode);
			Log.i("加密结果为 ",encryptingCode);
			String decryptingCode = decode(KEY_WORD, "VFi7YFCH4R/Sz+SwNsrTPQ==");
			System.out.println("解密结果为 " + decryptingCode);
			Log.i("解密结果",decryptingCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
