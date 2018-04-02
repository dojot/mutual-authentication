package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class RequestAP implements Encoder, Cloneable, SecureWipe {
	public static final byte REQUEST_AP_CODE = 0x0e;
	
	private Ticket ticket;
	private EncryptedData encAuth;

	private byte[] encoded;

	public RequestAP(Ticket ticket, EncryptedData EncryptedData) {
		if (ticket == null || EncryptedData == null) {
			throw new NullPointerException();
		}

		this.ticket = (Ticket) ticket.clone();
		this.encAuth = (EncryptedData) EncryptedData.clone();

		encode();
	}

	public RequestAP(byte[] encoded) {
		if (encoded == null) {
			throw new NullPointerException();
		}
		
		this.encoded = encoded.clone();
		
		if (this.encoded.length <= 0 || this.encoded[0] != REQUEST_AP_CODE) {
			throw new IllegalArgumentException("Wrong message code");
		}
		decode();
	}

	private void encode() {
		int offset = 0;
		byte[] encodedTicket = ticket.getEncoded();
		byte[] encodedEncryptedData = encAuth.getEncoded();

		this.encoded = new byte[MESSAGE_CODE_LENGTH + encodedTicket.length + encodedEncryptedData.length];
		
		this.encoded[offset] = REQUEST_AP_CODE;
		offset += MESSAGE_CODE_LENGTH;
		System.arraycopy(encodedTicket, 0, this.encoded, offset, encodedTicket.length);
		offset += encodedTicket.length;
		System.arraycopy(encodedEncryptedData, 0, this.encoded, offset, encodedEncryptedData.length);
		offset += encodedEncryptedData.length;
	}

	private void decode() {
		int offset = 0;
		byte[] encodedTicket;

		offset += MESSAGE_CODE_LENGTH;
		encodedTicket = SecureUtil.copyOf(this.encoded, offset, this.encoded.length);
		this.ticket = new Ticket(encodedTicket);
		offset += this.ticket.getEncodedLength();

		byte[] encodedEncryptedData = SecureUtil.copyOf(this.encoded, offset, this.encoded.length);
		this.encAuth = new EncryptedData(encodedEncryptedData);
		offset += this.encAuth.getEncodedLength();
		SecureUtil.erase(encodedTicket);
		SecureUtil.erase(encodedEncryptedData);

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

		decoded.put(TICKET, this.ticket);
		decoded.put(ENCRYPTED_DATA, this.encAuth);

		return decoded;
	}

	public Object clone() {
		RequestAP requestAP;
		try {
			requestAP = (RequestAP) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}
		return requestAP;
	}

	public void erase() {
		ticket.erase();
		encAuth.erase();
		SecureUtil.erase(this.encoded);
	}

}
