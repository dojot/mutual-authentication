package br.com.dojot.jcrypto.kdf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactorySpi;
import javax.crypto.spec.SecretKeySpec;

import br.com.dojot.jcrypto.parameter.CMacParameter;
import br.com.dojot.jcrypto.parameter.KDFCounterKeySpec;

public class KDFCounterMode extends SecretKeyFactorySpi {
	private Mac mac;
	private SecretKeySpec ki;
	private KDFCounterKeySpec kdfKeySpec;
	private int h; /** The length of the output of the PRF in bits. */
	private static final int r = Integer.SIZE >> 3; /** The length of the binary representation of the counter i. */
	
	@Override
	protected SecretKey engineGenerateSecret(KeySpec keySpec)
			throws InvalidKeySpecException {
		int n;
		SecretKeySpec sks;
		
		if(!(keySpec instanceof KDFCounterKeySpec)) {
			throw new InvalidKeySpecException();
		}
		kdfKeySpec = (KDFCounterKeySpec) keySpec;
		mac = kdfKeySpec.getMac();
		h = mac.getMacLength();
		n = (int) Math.ceil(((double)kdfKeySpec.getL()) / (this.h << 3));
		ki = new SecretKeySpec(kdfKeySpec.getKi(), "AES");
		if(n > Math.pow(2, r) - 1) {
			throw new InvalidKeySpecException();
		}		
		
		try {
			sks = new SecretKeySpec(generateKeyOutput(n), kdfKeySpec.getAlgorithm());
		} catch (InvalidKeyException e) {
			throw new InvalidKeySpecException(e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new InvalidKeySpecException(e);
		}
		
		return sks;
	}
	
	private byte[] generateKeyOutput(int n) throws InvalidKeyException, InvalidAlgorithmParameterException
	{
		byte[] result, k;
		
		result = new byte[n * h];
		mac.init(ki, new CMacParameter(h));
		for(int i = 1; i <= n; i++) {
			byte[] input = generateMacInput(i, kdfKeySpec.getLabel(), kdfKeySpec.getContext(), kdfKeySpec.getL());
			k = mac.doFinal(input);
			System.arraycopy(k, 0, result, h * (i - 1), h);
		}
		
		return Arrays.copyOf(result, kdfKeySpec.getL() >> 3);
	}
	
	/** Encodes the input of the KDF in counter mode for NIST Vectors */
	@SuppressWarnings("unused")
	private byte[] generateMacInputForCAVP(int counter, byte[] label, byte[] context, int L)
	{
		byte[] input;
		ByteBuffer bb;
		
		input = new byte[KDFCounterMode.r + label.length + context.length ];
		bb = ByteBuffer.wrap(input);
		
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(counter);
		bb.put(label);
		
		return input;
	}
	
	/** Encodes the input of the KDF in counter mode. */
	private byte[] generateMacInput(int counter, byte[] label, byte[] context, int L)
	{
		byte[] input;
		ByteBuffer bb;
		
		input = new byte[KDFCounterMode.r + label.length + 1 + context.length + 4];
		bb = ByteBuffer.wrap(input);
		
		bb.order(ByteOrder.BIG_ENDIAN);
		bb.putInt(counter);
		bb.put(label);
		bb.put((byte)0x00);
		bb.putInt(L);
		bb.put(context);
		
		return input;
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

}
