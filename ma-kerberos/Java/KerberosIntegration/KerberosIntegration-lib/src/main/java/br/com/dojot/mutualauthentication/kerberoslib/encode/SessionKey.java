package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class SessionKey implements Encoder, Cloneable, SecureWipe {
	private byte ivLength;
	private byte keyLength;
	private byte[] keyCS;
	private byte[] ivCS;
	private byte[] keySC;
	private byte[] ivSC;

	private byte[] encoded;

	public SessionKey(byte[] keyCS, byte[] ivCS, byte[] keySC, byte[] ivSC) {
		/* Check for null parameters */
		if (keyCS == null || ivCS == null || keySC == null || ivSC == null) {
			throw new IllegalArgumentException("Invalid null parameter");
		}

		/* Check parameter size */
		if (keyCS.length == 0 || ivCS.length == 0 || keySC.length == 0
				|| ivSC.length == 0 || ivCS.length != ivSC.length
				|| keyCS.length != keySC.length) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize the session key */
		this.ivLength = (byte) ivCS.length;
		this.keyLength = (byte) keyCS.length;
		this.keyCS = keyCS.clone();
		this.keySC = keySC.clone();
		this.ivCS = ivCS.clone();
		this.ivSC = ivSC.clone();

		encode();
	}

	public SessionKey(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new IllegalArgumentException("Invalid null parameter");
		}
		this.encoded = encoded.clone();
		/* Check if fields have correct sizes */
		if (this.encoded.length < 2
				|| (2 * (this.encoded[0] & 0xff) + 2 * (this.encoded[1] & 0xff)) > this.encoded.length) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		decode();
	}

	public void decode() {
		/* Initialize the session keys */
		int offset = 0;
		keyLength = this.encoded[0];
		ivLength = this.encoded[1];
		offset += 2;
		this.keyCS = new byte[keyLength & 0xff];
		System.arraycopy(this.encoded, offset, keyCS, 0, keyLength & 0xff);
		offset += keyLength & 0xff;
		
		this.ivCS = new byte[ivLength & 0xff];
		System.arraycopy(this.encoded, offset, ivCS, 0, ivLength & 0xff);
		offset += ivLength & 0xff;
		
		this.keySC = new byte[keyLength & 0xff];
		System.arraycopy(this.encoded, offset, keySC, 0, keyLength & 0xff);
		offset += keyLength & 0xff;
		
		this.ivSC = new byte[ivLength & 0xff];
		System.arraycopy(this.encoded, offset, ivSC, 0, ivLength & 0xff);
		offset += ivLength & 0xff;
		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	public byte[] encode() {
		int offset = 0;
		this.encoded = new byte[2 + 2 * (ivLength & 0xff) + 2
				* (keyLength & 0xff)];

		this.encoded[0] = keyLength;
		this.encoded[1] = ivLength;
		offset += 2;
		System.arraycopy(this.keyCS, 0, this.encoded, offset, keyLength & 0xff);
		offset += keyLength & 0xff;
		System.arraycopy(this.ivCS, 0, this.encoded, offset, ivLength & 0xff);
		offset += ivLength & 0xff;
		System.arraycopy(this.keySC, 0, this.encoded, offset, keyLength & 0xff);
		offset += keyLength & 0xff;
		System.arraycopy(this.ivSC, 0, this.encoded, offset, ivLength & 0xff);
		offset += ivLength & 0xff;

		return this.encoded;
	}

	public byte[] getEncoded() {
		return this.encoded.clone();
	}

	@Override
	public int getEncodedLength() {
		return this.encoded.length;
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(KEY_CS, keyCS.clone());
		decoded.put(KEY_SC, keySC.clone());
		decoded.put(IV_CS, ivCS.clone());
		decoded.put(IV_SC, ivSC.clone());

		return decoded;
	}

	public Object clone() {
		SessionKey sk;
		try {
			sk = (SessionKey) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}
		sk.keyCS = this.keyCS.clone();
		sk.ivCS = this.ivCS.clone();
		sk.keySC = this.keySC.clone();
		sk.ivSC = this.ivSC.clone();

		return new SessionKey(keyCS, ivCS, keySC, ivSC);
	}

	@Override
	public void erase() {
		SecureUtil.erase(keyCS);
		SecureUtil.erase(ivCS);
		SecureUtil.erase(keySC);
		SecureUtil.erase(ivSC);
	}

}
