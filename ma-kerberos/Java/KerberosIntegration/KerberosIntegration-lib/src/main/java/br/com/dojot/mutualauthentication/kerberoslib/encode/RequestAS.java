package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class RequestAS implements Encoder, Cloneable, SecureWipe {
	public static final byte REQUEST_AS_CODE = 0x0a;
	/* Fields which exists in RequestAS */
	private byte[] cname;
	private byte[] sname;
	private byte[] nonce;
	/* Encoded version of RequestAS fields */
	private byte[] encoded;

	public RequestAS(byte[] cname, byte[] sname, byte[] nonce) {
		/* Check for null parameters */
		if (cname == null || sname == null || nonce == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		/* Check parameter size */
		if (cname.length != PRINCIPAL_NAME_LENGTH
				|| sname.length != PRINCIPAL_NAME_LENGTH
				|| nonce.length != NONCE_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize variables */
		this.cname = cname.clone();
		this.sname = sname.clone();
		this.nonce = nonce.clone();

		encode();
	}

	public RequestAS(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/* Check parameter size */
		if (this.encoded.length != MESSAGE_CODE_LENGTH + 2 * PRINCIPAL_NAME_LENGTH + NONCE_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}
		/* Check message code */
		if (this.encoded[0] != REQUEST_AS_CODE) {
			throw new IllegalArgumentException("Wrong message code");
		}
		decode();
	}

	private void encode() {
		/* Generates encoded version */
		int offset = 0;
		this.encoded = new byte[MESSAGE_CODE_LENGTH + 2 * PRINCIPAL_NAME_LENGTH + NONCE_LENGTH];
		
		this.encoded[offset] = REQUEST_AS_CODE;
		offset += MESSAGE_CODE_LENGTH;
		System.arraycopy(this.cname, 0, this.encoded, offset, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		System.arraycopy(this.sname, 0, this.encoded, offset, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		System.arraycopy(this.nonce, 0, this.encoded, offset, NONCE_LENGTH);
		offset += NONCE_LENGTH;
	}

	private void decode() {
		/* Initialize variables */
		int offset = 0;
		
		offset += MESSAGE_CODE_LENGTH;
		this.cname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, this.cname, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		this.sname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, this.sname, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		this.nonce = new byte[NONCE_LENGTH];
		System.arraycopy(this.encoded, offset, this.nonce, 0, NONCE_LENGTH);
		offset += NONCE_LENGTH;

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

		decoded.put(CNAME, this.cname.clone());
		decoded.put(SNAME, this.sname.clone());
		decoded.put(NONCE, this.nonce.clone());

		return decoded;
	}

	public Object clone() {
		RequestAS requestAs;
		try {
			requestAs = (RequestAS) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}
		requestAs.cname = cname.clone();
		requestAs.sname = sname.clone();
		requestAs.nonce = nonce.clone();
		requestAs.encoded = encoded.clone();

		return requestAs;
	}

	public void erase() {
		SecureUtil.erase(cname);
		SecureUtil.erase(sname);
		SecureUtil.erase(nonce);
		SecureUtil.erase(encoded);
	}

}
