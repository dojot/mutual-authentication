package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.SecureEraseException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class EncryptedData implements Encoder, Cloneable, SecureWipe {
	/* Fields of encrypted data type */
	/*
	 * Length fields must be carefully manipulated because of byte signedness
	 * representation
	 */
	private byte ivLength;
	private byte ciphertextLength;
	private byte[] iv;
	private byte[] ciphertext;
	/* Encoded version of encrypted data type */
	private byte[] encoded;

	public EncryptedData(byte[] iv, byte[] ciphertext) {
		/* Check for null parameters */
		if (iv == null || ciphertext == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		/* Check if IV has adequate size and also if ciphertext is not empty */
		if (iv.length > IV_LENGTH || ciphertext.length == 0) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize the encrypted data part */
		this.iv = iv.clone();
		this.ciphertext = ciphertext.clone();
		this.ivLength = (byte) iv.length;
		this.ciphertextLength = (byte) ciphertext.length;

		encode();
	}

	public EncryptedData(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/* Check if fields have correct sizes */
		if ( (this.encoded.length < 2) || 
		     (this.encoded.length < (2 + (this.encoded[0] & 0xff)
						+ (this.encoded[1] & 0xff)))) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		decode();
	}

	private void encode() {
		/* Generates encoded version */
		int offset = 0;
		this.encoded = new byte[2 + ciphertext.length + iv.length];
		this.encoded[0] = ivLength;
		this.encoded[1] = ciphertextLength;
		offset += 2;
		System.arraycopy(iv, 0, this.encoded, offset, iv.length);
		offset += iv.length;
		System.arraycopy(ciphertext, 0, this.encoded, offset, ciphertext.length);
		offset += ciphertext.length;
	}

	private void decode() {
		int offset;
		/* Initialize the encrypted data part */
		this.ivLength = this.encoded[0];
		this.ciphertextLength = this.encoded[1];
		offset = 2;
		
		this.iv = new byte[this.encoded[0] & 0xff];
		System.arraycopy(this.encoded, offset, this.iv, 0, encoded[0] & 0xff);
		offset += this.encoded[0] & 0xff;

		this.ciphertext = new byte[this.encoded[1] & 0xff];
		System.arraycopy(this.encoded, offset, this.ciphertext, 0,
				encoded[1] & 0xff);
		offset += this.encoded[1] & 0xff;

		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(IV, this.iv.clone());
		decoded.put(CIPHERTEXT, this.ciphertext.clone());

		return decoded;
	}

	public byte[] getEncoded() {
		return this.encoded;
	}

	public int getEncodedLength() {
		return this.encoded.length;
	}

	public Object clone() {
		EncryptedData encData;
		try {
			encData = (EncryptedData) super.clone();
			encData.iv = iv.clone();
			encData.ciphertext = ciphertext.clone();
			encData.encoded = encoded.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}

		return encData;
	}

	public void erase() {
		ivLength = 0x00;
		ciphertextLength = 0x00;
		if (ivLength != 0x00 || ciphertextLength != 0x00) {
			throw new SecureEraseException();
		}
		SecureUtil.erase(iv);
		SecureUtil.erase(ciphertext);
		SecureUtil.erase(encoded);
	}
}
