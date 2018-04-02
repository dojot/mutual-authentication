package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class Error implements Encoder, Cloneable, SecureWipe {
	public static final byte ERROR_CODE = 0x1e;
	/* Error code */
	private byte code;
	/* Encoded version of Error fields */
	private byte[] encoded;
	/* Error description. */
	private String description;

	public Error(ErrorCode error) {
		/* Check for null parameters */
		
		if (error == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		/* Initialize the error code variable */
		code = error.getCode();
		/* Get the error description. */
		description = error.getDescription();
		
		encode();
	}

	public Error(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/* Check parameter size */
		if (this.encoded.length != MESSAGE_CODE_LENGTH + ERROR_CODE_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}
		/* Check message code */
		if (this.encoded[0] != ERROR_CODE) {
			throw new IllegalArgumentException("Wrong message code");
		}
		decode();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private void encode() {
		/* Generates encoded version */
		int offset = 0;
		this.encoded = new byte[MESSAGE_CODE_LENGTH + ERROR_CODE_LENGTH];
		
		this.encoded[offset] = ERROR_CODE;
		offset += MESSAGE_CODE_LENGTH;
		this.encoded[offset] = code;
		offset += ERROR_CODE_LENGTH;
	}

	private void decode() {
		/* Initialize variables */
		int offset = 0;
		
		offset += MESSAGE_CODE_LENGTH;
		this.code = this.encoded[offset];
		offset += ERROR_CODE_LENGTH;
		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	public byte[] getEncoded() {
		return encoded.clone();
	}

	public int getEncodedLength() {
		return this.encoded.length;
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(Encoder.ERROR_CODE, new Byte(this.code));

		return decoded;
	}

	public Object clone() {
		ErrorCode error;
		try {
			error = (ErrorCode) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}

		return error;
	}

	public void erase() {
		this.code = 0x00;
		SecureUtil.erase(encoded);
	}

}
