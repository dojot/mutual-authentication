package br.com.dojot.jcrypto;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.AEADBadTagException;
import javax.crypto.BadPaddingException;
import javax.crypto.CipherSpi;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import br.com.dojot.jcrypto.jni.ErrorCode;
import br.com.dojot.jcrypto.jni.JCrypto;
import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.jcrypto.util.SecureUtil;

public class AES extends CipherSpi {
	/* Flag used to decide if TAG should be calculated */
	protected static final int INTERMEDIATE_BLOCK = 0;
	protected static final int LAST_BLOCK = 1;

	protected static final int BLOCK_LENGTH = 16;
	
	/* Supported mode of operations */
	protected static final int ECB_MODE = 0;
	protected static final int GCM_MODE = 1;

	/* Supported padding*/
	protected static final int NO_PADDING = 0;
	
	/* Indicates if the cipher entered an inconsistent state because of JNI code */
	private boolean isInconsistentState;

	/* number of input bytes processed so far */
	protected int totalInputLen;
	
	/* Buffer of data not yet processed */
	protected byte[] block = new byte[BLOCK_LENGTH];
	protected int blockOffset = 0;
	
	protected int mode;
	protected int opmode;
	protected int padding;
	/* Stores the context visible to native code */
	protected ByteBuffer ctx;
	protected byte[] iv;
	protected Key key;

	public AES() {
		ctx = ByteBuffer.allocateDirect(JCrypto.aes_size());
	}
	
//	public AES(boolean state, int mode, int opmode, int padding, byte[] ctx, byte[] iv, Key key) {
//		this.isInconsistentState = state;
//		this.mode = mode;
//		this.opmode = opmode;
//		this.padding = padding;
//		this.ctx = ByteBuffer.allocateDirect(JCrypto.aes_size());
//		this.ctx.put(ctx);
//		this.iv = iv;
//		this.key = key;
//	}

	@Override
	protected byte[] engineDoFinal(byte[] input, int inputOffset, int inputLen)
			throws IllegalBlockSizeException, BadPaddingException {
		int outputSize;
		byte[] buffer = new byte[this.engineGetOutputSize(inputLen)];
		byte[] output = new byte[0];
		// Check if the total input length of the data processed by this cipher is not a multiple of block size
		if (SecureUtil.add_s(totalInputLen, inputLen) % BLOCK_LENGTH != 0) {
			throw new IllegalBlockSizeException("Input length not multiple of 16 bytes");
		}
		try {
			outputSize = doFinal(input, inputOffset, inputLen, buffer, 0);
			output = new byte[outputSize];
			System.arraycopy(buffer, 0, output, 0, outputSize);
			Arrays.fill(buffer, (byte)0x00);
		} catch (ShortBufferException e) {
			/* Assertion: Must not happen, as the output size is determined by the own cipher */
			throw new IllegalStateException();
		}

		return output;
	}



	@Override
	protected int engineDoFinal(byte[] input, int inputOffset, int inputLen, byte[] output,
			int outputOffset) throws ShortBufferException, IllegalBlockSizeException,
			BadPaddingException {		
		// Check if the total input length of the data processed by this cipher is not a multiple of block size
		if (SecureUtil.add_s(totalInputLen, inputLen) % BLOCK_LENGTH != 0) {
			throw new IllegalBlockSizeException("Input length not multiple of 16 bytes");
		}
		if (SecureUtil.sub_s(output.length, outputOffset) < inputLen) {
			throw new ShortBufferException();
		}		
		return doFinal(input, inputOffset, inputLen, output, outputOffset);
	}

	@Override
	protected int engineGetBlockSize() {
		return BLOCK_LENGTH;
	}

	@Override
	protected byte[] engineGetIV() {
		return iv.clone();
	}

	@Override
	protected int engineGetOutputSize(int inputLen) {	
		int remainingBytes = CryptoUtil.calculateRemainingBytes(inputLen, 0, blockOffset, BLOCK_LENGTH);
		int fullBlocks = CryptoUtil.calculateFullBytes(inputLen, 0, blockOffset, remainingBytes);
		int maxOutputSize;

		maxOutputSize = BLOCK_LENGTH;
		maxOutputSize = SecureUtil.add_s(fullBlocks, maxOutputSize);
		return maxOutputSize;
	}

	@Override
	protected AlgorithmParameters engineGetParameters() {
		return null;
	}

	@Override
	protected void engineInit(int opmode, Key key, SecureRandom random)
			throws InvalidKeyException {
		if (iv == null) {
			iv = new byte[engineGetBlockSize()];
			random.nextBytes(iv);
		}
		this.opmode = opmode;
		this.key = key;
		init();
	}

	@Override
	protected void engineInit(int opmode, Key key,
			AlgorithmParameterSpec params, SecureRandom random)
					throws InvalidKeyException, InvalidAlgorithmParameterException {
		if (params instanceof IvParameterSpec) {
			iv = ((IvParameterSpec) params).getIV();
		}
		engineInit(opmode, key, random);
	}

	@Override
	protected void engineInit(int opmode, Key key, AlgorithmParameters params,
			SecureRandom random) throws InvalidKeyException,
			InvalidAlgorithmParameterException {
		if (params != null) {
			throw new InvalidAlgorithmParameterException("params not supported");
		}
		engineInit(opmode, key, random);
	}

	@Override
	protected void engineSetMode(String mode) throws NoSuchAlgorithmException {
		if(mode.equals("ECB"))
			this.mode = ECB_MODE;
		else
			throw new NoSuchAlgorithmException(mode + " mode not implemented");
	}

	@Override
	protected void engineSetPadding(String padding)
			throws NoSuchPaddingException {
		if(padding.equalsIgnoreCase("NoPadding")) {
			this.padding = NO_PADDING;
		} else {
			throw new NoSuchPaddingException(padding + " padding not implemented");
		}
	}

	@Override
	protected byte[] engineUpdate(byte[] input, int inputOffset, int inputLen) {
		byte[] buffer = new byte[engineGetOutputSize(inputLen)];
		int processed = 0;
		try {
			processed = update(input, inputOffset, inputLen, buffer, 0);
		} catch (ShortBufferException e) {
			/* It won't happen because the base update function doesn't throw ShortBufferException. */
			throw new IllegalStateException();
		}
		byte[] output = new byte[processed];
		System.arraycopy(buffer, 0, output, 0, processed);
		Arrays.fill(buffer, (byte)0x00);
		return output;
	}

	@Override
	protected int engineUpdate(byte[] input, int inputOffset, int inputLen,
			byte[] output, int outputOffset) throws ShortBufferException {
		if (SecureUtil.sub_s(output.length, outputOffset) < engineGetOutputSize(inputLen)) {
			throw new ShortBufferException();
		}
		return update(input, inputOffset, inputLen, output, outputOffset);
	}

	/* Process input data */
	protected int update(byte[] input, int inputOffset, int inputLen, byte[] output,
			int outputOffset) throws ShortBufferException {
		int result;
		int remainingBytes = CryptoUtil.calculateRemainingBytes(inputLen, inputOffset, blockOffset, BLOCK_LENGTH);
		int fullBytes = CryptoUtil.calculateFullBytes(inputLen, inputOffset, blockOffset, remainingBytes);
		
		checkIfErrorOccurred();
		
		if(fullBytes == 0 && inputLen > 0) {
			/* There is no enough bytes to be processed, so put it in the buffer */
			System.arraycopy(input, inputOffset, block, blockOffset, SecureUtil.sub_s(inputLen, inputOffset));
			blockOffset = SecureUtil.add_s(blockOffset, inputLen);
			blockOffset = SecureUtil.sub_s(blockOffset, inputOffset);
		} else {
			/* Process */
			byte[] data = new byte[fullBytes];
			System.arraycopy(block, 0, data, 0, blockOffset);
			if(inputLen > 0) {
				System.arraycopy(input, inputOffset, data, blockOffset, SecureUtil.sub_s(fullBytes, blockOffset));
			}
			result = JCrypto.aes_enc(ctx, output, outputOffset, SecureUtil.sub_s(output.length, outputOffset), data, 0, data.length, INTERMEDIATE_BLOCK);
			Arrays.fill(data, (byte)0x00);
			System.arraycopy(input, SecureUtil.sub_s(inputLen, remainingBytes), block, 0, remainingBytes);
			blockOffset = remainingBytes;
			
			checkIfErrorOccurred(result);
		}
		return fullBytes;
	}

	protected int doFinal(byte[] input, int inputOffset, int inputLen,
			byte[] output, int outputOffset) throws AEADBadTagException, ShortBufferException, IllegalBlockSizeException{
		int remainingBytes, fullBytes;
		int result;
		
		checkIfErrorOccurred();
		
		remainingBytes = CryptoUtil.calculateRemainingBytes(inputLen, inputOffset, blockOffset, BLOCK_LENGTH);
		fullBytes = CryptoUtil.calculateFullBytes(inputLen, inputOffset, blockOffset, remainingBytes);

		if(remainingBytes != 0) {
			/* Because it is the final operation, there must be no byte in the buffer */
			throw new IllegalBlockSizeException();
		} else {
			byte[] blocks = new byte[fullBytes];
			System.arraycopy(block, 0, blocks, 0, blockOffset);
			if(inputLen > 0) {
				System.arraycopy(input, inputOffset, blocks, blockOffset, SecureUtil.sub_s(fullBytes, blockOffset));
			}
			result = JCrypto.aes_enc(ctx, output, outputOffset, SecureUtil.sub_s(output.length, outputOffset), blocks, 0, blocks.length, LAST_BLOCK);
			checkIfErrorOccurred(result);
		}
		init();
		return fullBytes;
	}

	private void init() {
		int result;
		this.isInconsistentState = false;
		this.blockOffset = 0;
		this.totalInputLen = 0;
		
		result = JCrypto.aes_key_exp(ctx, key.getEncoded(), opmode);
		checkIfErrorOccurred(result);
		
		result = JCrypto.aes_init(ctx, iv, mode);
		checkIfErrorOccurred(result);
	}
	
	/* 
	 * There are situations in which errors might occur in C code.
	 * These errors cause the cipher to enter an invalid state. 
	 * Care must be taken when directly calling this method as it 
	 * executes an unchecked exception that will cause the program to
	 * terminate.
	 */
	protected void checkIfErrorOccurred(int result) {
		if(result != 0) {
			isInconsistentState = true;
			throw new IllegalStateException("Error: " + ErrorCode.fromInt(result) + "\n");
		}
	}
	
	protected void checkIfErrorOccurred() {
		if(isInconsistentState) {
			throw new IllegalStateException("Previous error occurred in JNI. Cipher must be restarted");
		}
	}
}
