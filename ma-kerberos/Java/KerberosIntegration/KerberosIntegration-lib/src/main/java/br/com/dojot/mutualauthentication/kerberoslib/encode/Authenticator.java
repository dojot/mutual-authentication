package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class Authenticator implements Encoder, Cloneable, SecureWipe {
	/* Authenticator fields */
	private byte[] cname;
	private byte[] ctime;
	/* Encoded message */
	private byte[] encoded;

	public Authenticator(byte[] cname, byte[] ctime) {
		/* Check for null parameters */
		if (cname == null || ctime == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		/* Check parameter size */
		if (cname.length != PRINCIPAL_NAME_LENGTH || ctime.length != TIME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize variables */
		this.cname = cname.clone();
		this.ctime = ctime.clone();

		encode();
	}

	public Authenticator(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/* Check parameter size */
		if (this.encoded.length != PRINCIPAL_NAME_LENGTH + TIME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		decode();
	}

	private void encode() {
		int offset = 0;

		this.encoded = new byte[PRINCIPAL_NAME_LENGTH + TIME_LENGTH];

		System.arraycopy(this.cname, 0, this.encoded, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		
		System.arraycopy(this.ctime, 0, this.encoded, offset, TIME_LENGTH);
		offset += TIME_LENGTH;
	}

	private void decode() {
		/* Initialize variables */
		int offset = 0;
		this.cname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, 0, this.cname, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		
		this.ctime = new byte[TIME_LENGTH];
		System.arraycopy(this.encoded, offset, this.ctime, 0, TIME_LENGTH);
		offset += TIME_LENGTH;

		/* Encoded input may contain extra bytes */
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

		decoded.put(CNAME, cname.clone());
		decoded.put(CTIME, ctime.clone());

		return decoded;
	}

	public Object clone() {
		Authenticator authenticator;
		try {
			authenticator = (Authenticator) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}

		authenticator.cname = this.cname.clone();
		authenticator.ctime = this.ctime.clone();
		authenticator.encoded = this.encoded.clone();
		return authenticator;
	}

	public void erase() {
		SecureUtil.erase(cname);
		SecureUtil.erase(ctime);
		SecureUtil.erase(encoded);
	}
}
