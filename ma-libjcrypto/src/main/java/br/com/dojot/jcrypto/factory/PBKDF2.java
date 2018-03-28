package br.com.dojot.jcrypto.factory;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.dojot.jcrypto.exception.IllegalProviderException;
import br.com.dojot.jcrypto.hash.SHA512;
import br.com.dojot.jcrypto.util.CryptoUtil;

public class PBKDF2 extends SecretKeyFactorySpi {

	@Override
	protected SecretKey engineGenerateSecret(KeySpec spec)
			throws InvalidKeySpecException {
		
		if(!(spec instanceof PBEKeySpec)) {
			throw new InvalidKeySpecException();
		}
		
		Mac mac;
		try {
			mac = Mac.getInstance("HMACSHA512", "JCrypto");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalProviderException();
		} catch (NoSuchProviderException e) {
			throw new IllegalProviderException();
		}
		
		byte[] rawkey;
		try {
			rawkey = pbkdf2(mac, ((PBEKeySpec)spec).getPassword(), ((PBEKeySpec)spec).getSalt(), 
					((PBEKeySpec)spec).getIterationCount(), ((PBEKeySpec)spec).getKeyLength());
		} catch (InvalidKeyException e) {
			throw new InvalidKeySpecException();
		}
		return new SecretKeySpec(rawkey, "");
	}

	@Override
	protected KeySpec engineGetKeySpec(SecretKey key, Class keySpec)
			throws InvalidKeySpecException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected SecretKey engineTranslateKey(SecretKey key)
			throws InvalidKeyException {
		throw new UnsupportedOperationException();
	}
	
	private byte[] pbkdf2(Mac mac, char[] password, byte[] salt, int iterationCount, int keyLength) throws InvalidKeySpecException, InvalidKeyException
	{
		byte[] dk;
		int len, r;
		ByteBuffer bb;
		
		if(keyLength > ((long)Integer.MAX_VALUE) * SHA512.DIGEST_LEN) {
			throw new InvalidKeySpecException();
		}
		
		dk = new byte[keyLength];
		bb = ByteBuffer.wrap(dk);
		len = (int) Math.ceil(keyLength/(1.0 * SHA512.DIGEST_LEN));
		r = keyLength - (len-1) * SHA512.DIGEST_LEN;
		for(int i = 0; i < len; i++) {
			byte[] T, U;
			
			T = new byte[SHA512.DIGEST_LEN];
			U = new byte[salt.length + 4];
			ByteBuffer.wrap(U).put(salt).putInt(i + 1);
			
			mac.init(new SecretKeySpec(CryptoUtil.charArrayToByteArray(password), "HmacSHA512"));
			for(int j = 0; j < iterationCount; j++) {
				U = mac.doFinal(U);
				T = CryptoUtil.xor(T, U);
			}
			if(i < len - 1) {
				bb.put(T);
			} else {
				bb.put(T, 0, r);
			}
		}
		
		return dk;
	}
	
	

}
