package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class EncTicketPart implements Encoder, Cloneable, SecureWipe {
	private SessionKey sk;
	private byte[] cname;
	private byte[] authtime;
	private byte[] endtime;

	private byte[] encoded;

	public EncTicketPart(SessionKey sk, byte[] cname, byte[] authtime,
			byte[] endtime) {
		if (sk == null || cname == null || authtime == null || endtime == null) {
			throw new NullPointerException();
		}

		if (cname.length != PRINCIPAL_NAME_LENGTH
				|| authtime.length != TIME_LENGTH
				|| endtime.length != TIME_LENGTH) {
			throw new IllegalArgumentException();
		}

		this.sk = (SessionKey) sk.clone();
		this.cname = cname.clone();
		this.authtime = authtime.clone();
		this.endtime = endtime.clone();

		encode();
	}

	public EncTicketPart(byte[] encoded) {
		if (encoded == null) {
			throw new NullPointerException();
		}
		this.encoded = encoded.clone();

		if (this.encoded.length < PRINCIPAL_NAME_LENGTH + 2 * TIME_LENGTH + 2) {
			throw new IllegalArgumentException();
		}

		decode();
	}

	private void encode() {
		int offset = 0;
		byte[] encodedSessionKey;

		this.encoded = new byte[sk.getEncodedLength() + PRINCIPAL_NAME_LENGTH
				+ 2 * TIME_LENGTH];
		encodedSessionKey = sk.getEncoded();
		System.arraycopy(encodedSessionKey, 0, this.encoded, 0,
				encodedSessionKey.length);
		offset += encodedSessionKey.length;

		System.arraycopy(this.cname, 0, this.encoded, offset,
				PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		System.arraycopy(this.authtime, 0, this.encoded, offset, TIME_LENGTH);
		offset += TIME_LENGTH;

		System.arraycopy(this.endtime, 0, this.encoded, offset, TIME_LENGTH);
		offset += TIME_LENGTH;
	}

	private void decode() {
		int offset = 0;
		byte[] remaining;

		this.sk = new SessionKey(this.encoded);
		remaining = SecureUtil.copyOf(this.encoded, this.sk.getEncodedLength(),
				this.encoded.length);

		this.cname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(remaining, offset, this.cname, 0,
				PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		
		this.authtime = new byte[TIME_LENGTH];
		System.arraycopy(remaining, offset, this.authtime, 0, TIME_LENGTH);
		offset += TIME_LENGTH;
		
		this.endtime = new byte[TIME_LENGTH];
		System.arraycopy(remaining, offset, this.endtime, 0, TIME_LENGTH);
		offset += TIME_LENGTH;

		this.encoded = SecureUtil.resize(this.encoded, 0, sk.getEncodedLength()
				+ PRINCIPAL_NAME_LENGTH + 2 * TIME_LENGTH);
	}

	public byte[] getEncoded() {
		return this.encoded.clone();
	}

	public int getEncodedLength() {
		return this.encoded.length;
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(SESSION_KEY, this.sk.clone());
		decoded.put(CNAME, this.cname.clone());
		decoded.put(AUTHTIME, this.authtime.clone());
		decoded.put(ENDTIME, this.endtime.clone());

		return decoded;
	}

	public Object clone() throws CloneNotSupportedException {
		EncTicketPart encTicketRepPart = (EncTicketPart) super.clone();

		encTicketRepPart.sk = (SessionKey) sk.clone();
		encTicketRepPart.cname = cname.clone();
		encTicketRepPart.authtime = authtime.clone();
		encTicketRepPart.endtime = endtime.clone();
		encTicketRepPart.encoded = encoded.clone();

		return encTicketRepPart;
	}

	public void erase() {
		sk.erase();
		SecureUtil.erase(this.cname);
		SecureUtil.erase(this.authtime);
		SecureUtil.erase(this.endtime);
	}
}
