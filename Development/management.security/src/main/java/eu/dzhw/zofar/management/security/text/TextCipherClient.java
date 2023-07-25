/*START HEADER*/
/* Zofar Survey System
* Copyright (C) 2014 Deutsches Zentrum für Hochschul- und Wissenschaftsforschung
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/*STOP HEADER*/
/*
 * Class to encrypt and decrypt Text
 */
package eu.dzhw.zofar.management.security.text;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * The Class TextCipherClient.
 */
public class TextCipherClient {
	
	/** The instance. */
	private static TextCipherClient INSTANCE;

	/**
	 * Instantiates a new text cipher client.
	 */
	private TextCipherClient() {
		super();
	}

	/**
	 * Gets the single instance of TextCipherClient.
	 * 
	 * @return single instance of TextCipherClient
	 */
	public static TextCipherClient getInstance() {
		if (INSTANCE == null)
			INSTANCE = new TextCipherClient();
		return INSTANCE;
	}
	
	/**
	 * encode String as SHA
	 * @param plain
	 * @return encoded String
	 */
	public String encodeSHA(final String plain){
		return DigestUtils.shaHex(plain);
	}

	
	/**
	 * Cipher Text by 3DES.
	 * 
	 * @param plain
	 *            the plain
	 * @param password
	 *            the password
	 * @return the byte[]
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 */
	public byte[] cipher3DES(final String plain,
			final String password) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest(password.getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}
		final byte[] plainTextBytes = plain.getBytes("utf-8");
		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);

		final byte[] cipherText = cipher.doFinal(plainTextBytes);

		return cipherText;
	}

	/**
	 * DeCipher 3DES encrypted Text.
	 * 
	 * @param cipher
	 *            the cipher
	 * @param password
	 *            the password
	 * @return the string
	 * @throws NoSuchAlgorithmException
	 *             the no such algorithm exception
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 * @throws NoSuchPaddingException
	 *             the no such padding exception
	 * @throws IllegalBlockSizeException
	 *             the illegal block size exception
	 * @throws BadPaddingException
	 *             the bad padding exception
	 * @throws InvalidKeyException
	 *             the invalid key exception
	 * @throws InvalidAlgorithmParameterException
	 *             the invalid algorithm parameter exception
	 */
	public String deCipher3DES(final byte[] cipher,
			final String password) throws NoSuchAlgorithmException,
			UnsupportedEncodingException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException {
		final MessageDigest md = MessageDigest.getInstance("md5");
		final byte[] digestOfPassword = md.digest(password.getBytes("utf-8"));
		final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
		for (int j = 0, k = 16; j < 8;) {
			keyBytes[k++] = keyBytes[j++];
		}

		final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
		final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
		final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		decipher.init(Cipher.DECRYPT_MODE, key, iv);

		final byte[] plainText = decipher.doFinal(cipher);

		return new String(plainText, "UTF-8");
	}
	
	/**
	 * Cipher Text by RSA.
	 * 
	 * @param inpBytes
	 *            the inp bytes
	 * @param key
	 *            the key
	 * @param xform
	 *            the xform
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public byte[] encryptRSA(final byte[] inpBytes,
			final PublicKey key, final String xform) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final Cipher cipher = Cipher.getInstance(xform, "BC");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}

	/**
	 * DeCipher RSA encrypted Text.
	 * 
	 * @param inpBytes
	 *            the inp bytes
	 * @param key
	 *            the key
	 * @param xform
	 *            the xform
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public byte[] decryptRSA(final byte[] inpBytes,
			final PrivateKey key, final String xform) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final Cipher cipher = Cipher.getInstance(xform, "BC");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}

	/**
	 * Cipher Text by DES.
	 * 
	 * @param inpBytes
	 *            the inp bytes
	 * @param key
	 *            the key
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public byte[] encryptDES(final byte[] inpBytes,
			final Key key) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}

	/**
	 * DeCipher DES encrypted Text.
	 * 
	 * @param inpBytes
	 *            the inp bytes
	 * @param key
	 *            the key
	 * @return the byte[]
	 * @throws Exception
	 *             the exception
	 */
	public byte[] decryptDES(final byte[] inpBytes,
			final Key key) throws Exception {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		final Cipher cipher = Cipher.getInstance("DESede");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}
}
