package br.com.dojot.jcrypto;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import br.com.dojot.jcrypto.jni.ErrorCode;
import br.com.dojot.jcrypto.jni.JCrypto;
import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.jcrypto.util.SecureUtil;

public class AES_GCM extends AES {

	/* Indicates if the cipher is prepared to process data */
	private boolean isInitialized;
	/* Indicates if the cipher is prepared to encrypt or decrypt data */
	private boolean isUpdating;

	private Key key;
	private int tagLenBits;
	private GCMParameterSpec gcmParams;

	/* AAD data not processed yet */
	private byte[] aad = new byte[BLOCK_LENGTH];
	private int aadOffset;

	public AES_GCM() {
		this.ctx = ByteBuffer.allocateDirect(JCrypto.aes_gcm_size());
		this.mode = GCM_MODE;
		this.padding = NO_PADDING;
	}

	public void setState(byte[] ctx, byte[] key, byte[] iv, int opmode, int tagLenBits) {
		AlgorithmParameterSpec gcmParam = new GCMParameterSpec(tagLenBits, iv, 0, iv.length);
		this.key = new SecretKeySpec(key, "AES");
		this.gcmParams = ((GCMParameterSpec) gcmParam);
		this.opmode = opmode;
		this.aadOffset = 0;
		this.blockOffset = 0;
		this.totalInputLen = 0;
		this.iv = gcmParams.getIV();
		this.tagLenBits = gcmParams.getTLen();
		this.ctx = ByteBuffer.allocateDirect(JCrypto.aes_gcm_size());
		this.ctx.put(ctx);

		this.isInitialized = true;
		this.isUpdating = false;
	}
	
	public Map<String, Object> getState() {
		Map<String, Object> cipherState = new HashMap<String, Object>();
		
		cipherState.put("key", key.getEncoded());
		cipherState.put("opmode", opmode);
		cipherState.put("iv" , iv);
		cipherState.put("tagLenBits", tagLenBits);
		cipherState.put("ctx", ctx);
		
		return cipherState;
	}

	@Override
	protected void engineInit(int opmode, Key key, AlgorithmParameterSpec params, SecureRandom random)
			throws InvalidKeyException, InvalidAlgorithmParameterException {
		if (iv == null) {
			iv = new byte[engineGetBlockSize()];
			random.nextBytes(iv);
		}
		if (params == null || !(params instanceof GCMParameterSpec)) {
			throw new InvalidAlgorithmParameterException();
		}
		if (!(key instanceof SecretKey) || !key.getAlgorithm().equals("AES") || key.getEncoded() == null) {
			throw new InvalidKeyException();
		}

		int keylength = key.getEncoded().length;
		if (!(keylength == 128 / 8 || keylength == 192 / 8 || keylength == 256 / 8)) {
			throw new InvalidKeyException();
		}

		this.key = key;
		this.gcmParams = ((GCMParameterSpec) params);
		this.opmode = opmode;
		init();
	}

	private void init() {
		int result;
		this.aadOffset = 0;
		this.blockOffset = 0;
		this.totalInputLen = 0;
		this.iv = gcmParams.getIV();
		this.tagLenBits = gcmParams.getTLen();

		result = JCrypto.aes_gcm_key_exp(ctx, key.getEncoded(), opmode);
		checkIfErrorOccurred(result);

		result = JCrypto.aes_gcm_init(ctx, iv, this.tagLenBits, GCM_MODE);
		checkIfErrorOccurred(result);

		this.isInitialized = true;
		this.isUpdating = false;
	}

	protected int update(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
			throws ShortBufferException {
		int remainingBytes, fullBytes;
		int result = 0;

		checkIfErrorOccurred();

		doFinalAAD();

		remainingBytes = CryptoUtil.calculateRemainingBytes(inputLen, inputOffset, blockOffset, BLOCK_LENGTH);
		fullBytes = CryptoUtil.calculateFullBytes(inputLen, inputOffset, blockOffset, remainingBytes);

		if (fullBytes == 0) {
			System.arraycopy(input, inputOffset, block, blockOffset, inputLen - inputOffset);
			blockOffset += inputLen - inputOffset;
		} else {
			byte[] blocks = new byte[fullBytes];
			System.arraycopy(block, 0, blocks, 0, blockOffset);
			if (inputLen > 0) {
				System.arraycopy(input, inputOffset, blocks, blockOffset, SecureUtil.sub_s(fullBytes, blockOffset));
			}

			result = JCrypto.aes_gcm_enc(ctx, output, outputOffset, SecureUtil.sub_s(output.length, outputOffset),
					blocks, 0, blocks.length, INTERMEDIATE_BLOCK);
			checkIfErrorOccurred(result);

			System.arraycopy(input, SecureUtil.sub_s(inputLen, remainingBytes), block, 0, remainingBytes);
			blockOffset = remainingBytes;
		}
		return fullBytes;
	}

	@Override
	protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
			throws IllegalBlockSizeException, BadPaddingException {
		byte[] buffer = new byte[this.engineGetOutputSize(inputLen)];
		int processed = 0;
		try {
			processed = doFinal(input, inputOffset, inputLen, buffer, 0);
		} catch (ShortBufferException e) {
			/*
			 * This exception shouldn't be thrown, as this indicates the buffer
			 * size was incorrectly determined.
			 */
			throw new IllegalStateException("Incorrect buffer size");
		}
		byte[] output = new byte[processed];
		System.arraycopy(buffer, 0, output, 0, processed);
		Arrays.fill(buffer, (byte) 0x00);
		return output;
	}

	@Override
	protected int engineGetOutputSize(int inputLen) {
		int fullBytes = super.engineGetOutputSize(inputLen);
		fullBytes = SecureUtil.add_s(fullBytes, this.tagLenBits >> 3);

		return fullBytes;
	}

	protected int doFinal(byte[] input, int inputOffset, int inputLen, byte[] output, int outputOffset)
			throws AEADBadTagException, ShortBufferException {
		checkIfErrorOccurred();
		doFinalAAD();

		int processedBytes = 0, result = 0;
		int bufferSize;
		bufferSize = SecureUtil.sub_s(inputLen, inputOffset);
		bufferSize = SecureUtil.add_s(bufferSize, blockOffset);

		byte[] buffer = new byte[bufferSize];
		System.arraycopy(block, 0, buffer, 0, blockOffset);
		if (inputLen > 0) {
			System.arraycopy(input, inputOffset, buffer, blockOffset, (inputLen - inputOffset));
		}

		result = JCrypto.aes_gcm_enc(ctx, output, outputOffset, output.length - outputOffset, buffer, 0, buffer.length,
				LAST_BLOCK);
		processedBytes += bufferSize;

		Arrays.fill(buffer, (byte) 0x00);
		Arrays.fill(block, (byte) 0x00);

		switch (ErrorCode.fromInt(result)) {
		case INVALID_TAG:
			throw new AEADBadTagException();
		case INVALID_OUTPUT_SIZE:
			throw new ShortBufferException();
		default:
			checkIfErrorOccurred(result);
		}

		if (this.opmode == Cipher.ENCRYPT_MODE) {
			processedBytes += (tagLenBits >> 3);
		} else {
			processedBytes -= (tagLenBits >> 3);
		}
		isInitialized = false;
		init();
		return processedBytes;
	}

	@Override
	protected void engineUpdateAAD(byte[] src, int offset, int len) {
		doEngineUpdateAAD(src, offset, len);
	}

	@Override
	protected void engineUpdateAAD(ByteBuffer src) {
		byte[] aad = src.array();
		doEngineUpdateAAD(aad, 0, aad.length);
	}

	private void doEngineUpdateAAD(byte[] src, int offset, int len) {
		int result;
		checkIfErrorOccurred();

		int remainingBytes = CryptoUtil.calculateRemainingBytes(src.length, offset, aadOffset, BLOCK_LENGTH);
		int fullBytes = CryptoUtil.calculateFullBytes(src.length, offset, aadOffset, remainingBytes);

		if (fullBytes == 0) {
			/* There is no complete block to calculate AAD */
			System.arraycopy(src, offset, aad, aadOffset, SecureUtil.sub_s(src.length, offset));
			aadOffset = SecureUtil.add_s(aadOffset, src.length);
			aadOffset = SecureUtil.sub_s(aadOffset, offset);
		} else {
			byte[] partialAAD = new byte[fullBytes];
			System.arraycopy(aad, 0, partialAAD, 0, aadOffset);
			System.arraycopy(src, offset, partialAAD, aadOffset, fullBytes - aadOffset);

			/* Updates the value of the hash */
			result = JCrypto.aes_gcm_aad(ctx, partialAAD);
			Arrays.fill(aad, (byte) 0x00);
			checkIfErrorOccurred(result);

			/* Copy AAD data which was not processed */
			System.arraycopy(src, SecureUtil.sub_s(src.length, remainingBytes), aad, 0, remainingBytes);
			aadOffset = remainingBytes;
		}
	}

	private void doFinalAAD() {
		int result;
		checkIfErrorOccurred();

		if (aadOffset > 0) {
			if (isUpdating) {
				throw new IllegalStateException("AAD added after update operation");
			}
			byte[] aadLast = new byte[aadOffset];
			System.arraycopy(aad, 0, aadLast, 0, aadOffset);

			/* Updates the value of the hash */
			result = JCrypto.aes_gcm_aad(ctx, aadLast);
			checkIfErrorOccurred(result);

			/* Erases all the data */
			aadOffset = 0;
			Arrays.fill(aad, (byte) 0x00);
			Arrays.fill(aadLast, (byte) 0x00);
		}
		isUpdating = true;
	}

	@Override
	protected void checkIfErrorOccurred() {
		super.checkIfErrorOccurred();
		if (!isInitialized) {
			throw new IllegalStateException("Cipher not initialized");
		}
	}
}
