package br.com.dojot.jcrypto.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.dojot.jcrypto.AES_GCM;

public class CryptoChannel {	
	private Cipher readChannel;
	private Cipher writeChannel;
	private SecretKeySpec keyExternal;
	private SecretKeySpec keyLocal;
	private byte[] ivExternal;
	private byte[] ivLocal;
	private int tagLen;
	
	/* Initializes full duplex communication channel */
	public CryptoChannel(byte[] keyLocal, byte[] keyExternal, byte[] ivLocal, byte[] ivExternal, int tagLen, String provider) throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException 
	{
		this.keyExternal = new SecretKeySpec(keyExternal, "AES");
		this.keyLocal = new SecretKeySpec(keyLocal, "AES");
		this.tagLen = tagLen;
		
		readChannel = Cipher.getInstance("AES/GCM/NoPadding", provider);
		writeChannel = Cipher.getInstance("AES/GCM/NoPadding", provider);
		
		this.ivExternal = ivExternal.clone();
		this.ivLocal = ivLocal.clone();

		initChannel(readChannel, Cipher.DECRYPT_MODE, this.keyExternal, this.ivExternal);
		initChannel(writeChannel, Cipher.ENCRYPT_MODE, this.keyLocal, this.ivLocal);
	}	
	
	public byte[] encrypt(byte[] aad, byte[] plaintext) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		byte[] ciphertext;
		
		if(aad != null && aad.length != 0) {
			writeChannel.updateAAD(aad);
		} 
		
		if(plaintext != null && plaintext.length != 0) {
			ciphertext = writeChannel.doFinal(plaintext);
		} else {
			ciphertext = writeChannel.doFinal();
		}

		initChannel(writeChannel, Cipher.ENCRYPT_MODE, keyLocal, ivLocal);
		return ciphertext;
	}
	
	public byte[] decrypt(byte[] aad, byte[] ciphertext) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException
	{
		byte[] plaintext;		
		
		if(aad != null && aad.length != 0) {
			readChannel.updateAAD(aad);
		} 
		
		if(ciphertext != null && ciphertext.length != 0) {
			plaintext = readChannel.doFinal(ciphertext);
		} else {
			plaintext = readChannel.doFinal();
		}
		
		initChannel(readChannel, Cipher.DECRYPT_MODE, keyExternal, ivExternal);
		return plaintext;
	}
	
	private void initChannel(Cipher channel, int opmode, SecretKeySpec key, byte[] iv) throws InvalidKeyException, InvalidAlgorithmParameterException 
	{
		AlgorithmParameterSpec gcmParam = new GCMParameterSpec(this.tagLen, iv, 0, iv.length);
		channel.init(opmode, key, gcmParam);
	}
	
	
	public byte[] getExternalIV() {
		return ivExternal;
	}
	
	public byte[] getLocalIV() {
		return ivLocal;
	}
}
