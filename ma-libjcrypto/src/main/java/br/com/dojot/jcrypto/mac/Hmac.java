package br.com.dojot.jcrypto.mac;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.MacSpi;

import br.com.dojot.jcrypto.parameter.HmacParameter;
import br.com.dojot.jcrypto.util.CryptoUtil;

public class Hmac extends MacSpi{
	protected MessageDigest md;
	protected int macLen;
	protected int B; 			/** Digest input block size. */
	private byte[] internal;	/** Ko xor ipad */
	private byte[] external;	/** Ko xor opad */
	private int L;				/** Truncated output size requested. */
	
	public Hmac(String algorithm, String provider, int B) throws NoSuchAlgorithmException, NoSuchProviderException {
		this.md = MessageDigest.getInstance(algorithm, provider);
		this.B = B;
		this.L = md.getDigestLength();
	}
	
	@Override
	protected byte[] engineDoFinal() {
		byte[] buffer, mac;
		
		mac = new byte[L];
		/** Steps 8 and 9. */
		buffer = md.digest();
		md.update(external);
		md.update(buffer);
		System.arraycopy(md.digest(), 0, mac, 0, mac.length);
		
		engineReset();
		return mac;
	}

	@Override
	protected int engineGetMacLength() {
		return macLen;
	}

	@Override
	protected void engineInit(Key key, AlgorithmParameterSpec params)
			throws InvalidKeyException, InvalidAlgorithmParameterException {
		byte[] Ko, K;
		ByteBuffer bb;
		
		md.reset();
		macLen = md.getDigestLength();
		
		if(params != null){
			if(!(params instanceof HmacParameter)) {
				throw new InvalidAlgorithmParameterException();
			}
			else{
				L = ((HmacParameter)params).getOutputLength();
				if(L > md.getDigestLength()) {
					throw new InvalidAlgorithmParameterException();
				}
			}
		} else {
			macLen = L = md.getDigestLength();
		}
		
		/** Steps 1, 2 and 3. */
		Ko = new byte[B];
		K = key.getEncoded();
		bb = ByteBuffer.wrap(Ko);
		if(K.length <= B) {
			bb.put(K);
		} else if(K.length > B) {
			bb.put(md.digest(K));
		}
		
		/** Step 4. */
		byte[] ipad = new byte[B];
		Arrays.fill(ipad, (byte) 0x36);
		internal = CryptoUtil.xor(Ko, ipad);
		
		/** Step 6. */
		md.update(internal);
		
		/** Step 7. */
		byte[] opad = new byte[B];
		Arrays.fill(opad, (byte) 0x5c);
		external = CryptoUtil.xor(Ko, opad);
	}

	@Override
	protected void engineReset() {
		md.reset();
		md.update(internal);
	}

	@Override
	protected void engineUpdate(byte input) {
		/** Step 6. */
		md.update(input);
	}

	@Override
	protected void engineUpdate(byte[] input, int offset, int len) {
		/** Step 6. */
		md.update(input, offset, len);
	}

}
