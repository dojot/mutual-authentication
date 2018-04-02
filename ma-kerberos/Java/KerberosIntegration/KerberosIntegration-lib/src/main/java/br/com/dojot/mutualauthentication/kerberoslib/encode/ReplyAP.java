package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class ReplyAP implements Encoder, Cloneable, SecureWipe {
	public static final byte REPLY_AP_CODE = 0x0f;
	
	private EncryptedData encData;
	private byte[] encoded;

	public ReplyAP(EncryptedData encData) {
		if (encData == null) {
			throw new NullPointerException();
		}

		this.encData = (EncryptedData) encData.clone();
		encode();
	}

	public ReplyAP(byte[] encoded) {
		if (encoded == null) {
			throw new NullPointerException();
		}
		
		this.encoded = encoded.clone();
		
		if(this.encoded.length <= 0 || this.encoded[0] != REPLY_AP_CODE) {
			throw new IllegalArgumentException("Wrong message code");
		}
		decode();
	}

	private void encode() {
		byte[] encodedEncData;
		int offset = 0;
		
		encodedEncData = encData.getEncoded();
		this.encoded = new byte[MESSAGE_CODE_LENGTH + encodedEncData.length];
		this.encoded[offset] = REPLY_AP_CODE;
		offset += MESSAGE_CODE_LENGTH;
		
		System.arraycopy(encodedEncData, 0, this.encoded, offset, encodedEncData.length);
		SecureUtil.erase(encodedEncData);		
	}

	@Override
	public byte[] getEncoded() {
		return this.encoded.clone();
	}

	@Override
	public int getEncodedLength() {
		return this.encoded.length;
	}

	private void decode() {
		int offset = 0;
		
		offset += MESSAGE_CODE_LENGTH;
		byte[] encodedEncData = SecureUtil.copyOf(this.encoded, offset, this.encoded.length);

		this.encData = new EncryptedData(encodedEncData);
		offset += this.encData.getEncodedLength();
		SecureUtil.erase(encodedEncData);

		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(ENCRYPTED_DATA, this.encData.clone());

		return decoded;
	}

	public Object clone() throws CloneNotSupportedException {
		ReplyAP replyAP = (ReplyAP) super.clone();
		replyAP.encData = (EncryptedData) this.encData.clone();
		return replyAP;
	}

	public void erase() {
		this.encData.erase();
	}

}
