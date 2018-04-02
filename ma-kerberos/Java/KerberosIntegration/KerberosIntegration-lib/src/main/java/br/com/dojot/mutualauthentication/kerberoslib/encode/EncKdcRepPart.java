package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.encode.Encoder;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class EncKdcRepPart implements Encoder, Cloneable, SecureWipe {
	private SessionKey sk;
	private byte[] sname;
	private byte[] nonce;
	private byte[] authtime;
	private byte[] endtime;
	private byte[] encoded;

	public EncKdcRepPart(SessionKey sk, byte[] sname, byte[] nonce,
			byte[] authtime, byte[] endtime) {

		/* Check for null parameters */
		if (sk == null || sname == null || nonce == null || authtime == null
				|| endtime == null) {
			throw new IllegalArgumentException("Invalid null parameter");
		}

		/* Check parameter size */
		if (sname.length != PRINCIPAL_NAME_LENGTH
				|| nonce.length != NONCE_LENGTH
				|| authtime.length != TIME_LENGTH
				|| endtime.length != TIME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize variables */
		this.sk = (SessionKey) sk.clone();
		this.sname = sname.clone();
		this.nonce = nonce.clone();
		this.authtime = authtime.clone();
		this.endtime = endtime.clone();
		encode();
	}

	public EncKdcRepPart(byte[] encoded) {

		/* Check for null parameter */
		if (encoded == null) {
			throw new IllegalArgumentException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/*
		 * Check if encoded data has at least enough size to store the sname,
		 * times and lengths of session keys
		 */
		if (encoded.length < PRINCIPAL_NAME_LENGTH + NONCE_LENGTH + 2
				* TIME_LENGTH + 2) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		decode();
	}

	private void encode() {
		int offset;
		byte[] encodedSessionKey;

		this.encoded = new byte[sk.getEncodedLength() + PRINCIPAL_NAME_LENGTH
				+ NONCE_LENGTH + 2 * TIME_LENGTH];
		encodedSessionKey = sk.getEncoded();

		offset = 0;
		System.arraycopy(encodedSessionKey, 0, this.encoded, offset,
				encodedSessionKey.length);
		offset += encodedSessionKey.length;
		System.arraycopy(this.sname, 0, this.encoded, offset, this.sname.length);
		offset += sname.length;
		System.arraycopy(this.nonce, 0, this.encoded, offset, this.nonce.length);
		offset += nonce.length;
		System.arraycopy(this.authtime, 0, this.encoded, offset,
				this.authtime.length);
		offset += authtime.length;
		System.arraycopy(this.endtime, 0, this.encoded, offset,
				this.endtime.length);
		offset += endtime.length;
	}

	private void decode() {
		int offset = 0;

		this.sk = new SessionKey(this.encoded);
		offset += this.sk.getEncodedLength();

		this.sname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, this.sname, 0,
				PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		this.nonce = new byte[NONCE_LENGTH];
		System.arraycopy(this.encoded, offset, this.nonce, 0, NONCE_LENGTH);
		offset += NONCE_LENGTH;

		this.authtime = new byte[TIME_LENGTH];
		System.arraycopy(this.encoded, offset, this.authtime, 0,
				TIME_LENGTH);
		offset += TIME_LENGTH;

		this.endtime = new byte[TIME_LENGTH];
		System.arraycopy(this.encoded, offset, this.endtime, 0,
				TIME_LENGTH);
		offset += TIME_LENGTH;

		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	public byte[] getEncoded() {
		return this.encoded.clone();
	}

	public int getEncodedLength() {
		return this.encoded.length;
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(SNAME, this.sname.clone());
		decoded.put(SESSION_KEY, this.sk.clone());
		decoded.put(NONCE, this.nonce.clone());
		decoded.put(AUTHTIME, this.authtime.clone());
		decoded.put(ENDTIME, this.endtime.clone());

		return decoded;
	}

	public Object clone() throws CloneNotSupportedException {
		EncKdcRepPart encKdcRepPart = (EncKdcRepPart) super.clone();

		encKdcRepPart.sname = sname.clone();
		encKdcRepPart.sk = (SessionKey) sk.clone();
		encKdcRepPart.nonce = nonce.clone();
		encKdcRepPart.authtime = authtime.clone();
		encKdcRepPart.endtime = endtime.clone();

		return encKdcRepPart;
	}

	public void erase() {
		SecureUtil.erase(sname);
		sk.erase();
		SecureUtil.erase(nonce);
		SecureUtil.erase(authtime);
		SecureUtil.erase(endtime);
	}
}
