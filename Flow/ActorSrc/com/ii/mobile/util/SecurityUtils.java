package com.ii.mobile.util;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import com.ii.mobile.flow.authenticate.Login;

public class SecurityUtils {

	private static final byte[] salt = { (byte) 0xA4, (byte) 0x0B, (byte) 0xC8,
			(byte) 0x34, (byte) 0xD6, (byte) 0x95, (byte) 0xF3, (byte) 0x13 };

	private static int BLOCKS = 128;

	public static String encryptAES(String seed, String clearText)
	{
		seed = Login.cookie;
		// L.out("seed: " + seed.length());
		try {
			if (seed.length() > BLOCKS / 8)
				seed = seed.substring(0, BLOCKS / 8 - 1);
			// L.out("seed: " + seed.length());
			byte[] padded = pad(seed.getBytes("UTF-8"));
			// L.out("padded: " + padded + " " + padded.length);
			// debug(padded);
			byte[] rawKey = getRawKey(padded);
			// L.out("rawKey: " + rawKey + " " + rawKey.length);
			// debug(rawKey);
			// byte[] rawKey = getRawKey(seed.getBytes("UTF8"));
			SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] compressed = cipher.doFinal(clearText.getBytes("UTF-8"));
			String encoded = byteArrayToHexString(compressed);
			return encoded;
		} catch (Exception e) {
			L.out("encryption error: " + e + L.p());
			L.out("clearText: " + clearText);
		}
		return null;
	}

	public static String decryptAES(String seed, String encoded) {
		try {
			seed = Login.cookie;
			// L.out("seed: " + seed.length());
			if (seed.length() > BLOCKS / 8)
				seed = seed.substring(0, BLOCKS / 8 - 1);
			// L.out("seed: " + seed.length());
			byte[] padded = pad(seed.getBytes("UTF-8"));
			// L.out("padded: " + padded + " " + padded.length);
			// debug(padded);
			byte[] rawKey = getRawKey(padded);
			// L.out("rawKey: " + rawKey + " " + rawKey.length);
			// debug(rawKey);
			SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] uncompressed = cipher.doFinal(hexStringToByteArray(encoded));
			return new String(uncompressed, "UTF-8");
		} catch (Exception e) {
			L.out("encryption error: " + e + L.p());
			L.out("data: " + encoded.toString());
		}
		return null;
	}

	public static String byteArrayToHexString(byte[] buf) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Long.toString(buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	private static byte[] getRawKey(byte[] seed) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
		// SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(seed);
		kgen.init(BLOCKS, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		byte[] raw = skey.getEncoded();
		return raw;
	}

	private static void debug(byte[] out) {
		String temp = "";
		for (int i = 0; i < out.length; i++) {
			temp += " " + out[i];
		}
		L.out("debug: " + temp);
	}

	private static byte[] pad(byte[] seed) {
		byte[] nseed = new byte[BLOCKS / 8];
		for (int i = 0; i < BLOCKS / 8; i++)
			nseed[i] = 0;
		for (int i = 0; i < seed.length; i++)
			nseed[i] = seed[i];

		return nseed;
	}

	public static byte[] encryptPBE(String password, String cleartext)
			throws Exception {
		SecretKeyFactory factory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 1024, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");

		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		return cipher.doFinal(cleartext.getBytes("UTF-8"));
	}

	public static String decryptPBE(SecretKey secret, String ciphertext,
			byte[] iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));
		return new String(cipher.doFinal(ciphertext.getBytes()), "UTF-8");
	}

}