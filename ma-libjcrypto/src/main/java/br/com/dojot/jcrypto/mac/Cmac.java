package br.com.dojot.jcrypto.mac;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.MacSpi;
import javax.crypto.NoSuchPaddingException;

import br.com.dojot.jcrypto.exception.IllegalProviderException;
import br.com.dojot.jcrypto.parameter.CMacParameter;
import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.jcrypto.util.SecureUtil;

public class Cmac extends MacSpi{
	private Cipher cipher;	/** Underlying block cipher, it can be AES or TDEA. */
	private int blockSize;	/** The size of the underlying block cipher in bytes. */
	private byte[] k1, k2;	/** Sub-keys used by CMAC. */
	private byte[] block;	/** Current non complete block. */
	private int blockOffset;/** Current non complete block offset. */
	private byte[] Ci;		/** Current state. */
	private int macLen;		/** Length of the MAC in bytes. */		

	public Cmac() {
		try {
			cipher = Cipher.getInstance("AES/ECB/NoPadding", "JCrypto");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalProviderException(e);
		} catch (NoSuchProviderException e) {
			throw new IllegalProviderException(e);
		} catch (NoSuchPaddingException e) {
			throw new IllegalProviderException(e);
		}
	}
	
	@Override
	protected int engineGetMacLength() {
		return cipher.getBlockSize();
	}

	@Override
	protected void engineInit(Key key, AlgorithmParameterSpec params)
			throws InvalidKeyException, InvalidAlgorithmParameterException {
		
		if(!(params instanceof CMacParameter)) {
			throw new InvalidAlgorithmParameterException("Error: expecting CMAC parameters.");
		}
		macLen = ((CMacParameter)params).getOutputLength();
		blockSize = cipher.getBlockSize();
		block = new byte[blockSize];
		Ci = new byte[blockSize];
		subk(key);
	}

	@Override
	protected void engineReset() {
		reset();
	}

	@Override
	protected void engineUpdate(byte input) {
		update(new byte[]{input}, 0, 1);
	}

	@Override
	protected void engineUpdate(byte[] input, int offset, int len) 
	{
		update(input, offset, len);
	}

	@Override
	protected byte[] engineDoFinal() {
		byte[] mac, input;		

		if(blockOffset == blockSize) {
			input = CryptoUtil.xor(block, k1);
		} else {
			input = new byte[blockSize];
			System.arraycopy(block, 0, input, 0, blockOffset);
			Arrays.fill(input, blockOffset + 1, blockSize, (byte) 0x00);
			input[blockOffset] = (byte) 0x80;
			input = CryptoUtil.xor(input, k2);
		}
		input = CryptoUtil.xor(input, Ci);

		try {
			mac = cipher.doFinal(input);
		} catch (BadPaddingException e) {
			/** It should not happen, we are not decrypting the input. */
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			/** It should not happen, we create the input with the expected block size. */
			throw new IllegalStateException(e);
		}
		return Arrays.copyOf(mac, macLen);
	}

	protected void update(byte[] input, int offset, int len)
	{
		int remainingBytes;
		int fullBytes;

		remainingBytes = CryptoUtil.calculateRemainingBytes(input.length, offset, blockOffset, cipher.getBlockSize());
		fullBytes = CryptoUtil.calculateFullBytes(input.length, offset, blockOffset, remainingBytes);

		/** Put last block of input is buffer. */
		if((fullBytes == 0 || (fullBytes == blockSize && remainingBytes == 0)) && input.length > 0) {
			System.arraycopy(input, offset, block, blockOffset, SecureUtil.sub_s(input.length, offset));
			blockOffset = SecureUtil.add_s(blockOffset, input.length);
			blockOffset = SecureUtil.sub_s(blockOffset, offset);
		} else {
			/** Process the rest of the blocks. */
			byte[] intermediaryInput, intermediaryOutput, data;
			int numberOfBlocks;

			/** If there aren't any remaining bytes, we must save the one part of the input. */
			if(remainingBytes == 0) {
				fullBytes = fullBytes - blockSize;
				remainingBytes = (fullBytes == 0)? len : Math.min(len, blockSize - blockOffset);
			}

			data = new byte[fullBytes];
			System.arraycopy(block, 0, data, 0, blockOffset);
			if(input.length > 0) {
				System.arraycopy(input, offset, data, blockOffset, SecureUtil.sub_s(fullBytes, blockOffset));			
			}

			
			numberOfBlocks = fullBytes / blockSize;
			intermediaryOutput = Ci;
			for(int i = 0; i < numberOfBlocks; i++) {
				intermediaryInput = CryptoUtil.xor(data, i * blockSize, Ci, 0, Ci.length);
				intermediaryOutput = cipher.update(intermediaryInput);
				Arrays.fill(Ci, (byte)0x00);
				Ci = intermediaryOutput;
			}
			Arrays.fill(data, (byte)0x00);
			System.arraycopy(input, SecureUtil.sub_s(input.length, remainingBytes), block, blockOffset, remainingBytes);
			blockOffset += remainingBytes;
		}
	}

	/** Performs the sub-key generation step from CMAC. 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException */
	private void subk(Key key) throws InvalidKeyException
	{
		try {
			byte[] rb, L;

			rb = initRb();
			cipher.init(Cipher.ENCRYPT_MODE, key);
			L = cipher.doFinal(new byte[blockSize]);
			if((L[0] & 0x80) == 0){
				k1 = CryptoUtil.shiftLeftOne(L);
			} else {
				k1 = CryptoUtil.xor(CryptoUtil.shiftLeftOne(L), rb);
			}
			if((k1[0] & 0x80) == 0){
				k2 = CryptoUtil.shiftLeftOne(k1);
			} else {
				k2 = CryptoUtil.xor(CryptoUtil.shiftLeftOne(k1), rb);
			}
		} catch (BadPaddingException e) {
			/** It should not happen, we are dealing with encryption not decryption. */
			throw new IllegalStateException(e);
		} catch (IllegalBlockSizeException e) {
			/** It should not happen, we use the exact block size expected. */
			throw new IllegalStateException(e);
		}
	}

	/** Reset the state, but maintain the initial key. */
	private void reset()
	{
		Arrays.fill(block, (byte)(0x00));
		Arrays.fill(Ci, (byte)(0x00));
		this.blockOffset = 0;
	}
	
	/** Init the rb value according to the block size of the cipher. */
	private byte[] initRb()
	{
			byte[] rb = new byte[blockSize];
			
			if(blockSize == 8) {
				rb[blockSize - 1] = (byte) 0x1b;
			} else if(blockSize == 16) {
				rb[blockSize - 1] = (byte) 0x87;
			}
			
			return rb;
	}

}
