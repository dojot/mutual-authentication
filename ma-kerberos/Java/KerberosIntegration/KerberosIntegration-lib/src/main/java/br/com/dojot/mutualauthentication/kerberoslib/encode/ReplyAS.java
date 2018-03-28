package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class ReplyAS implements Encoder, Cloneable, SecureWipe {
	public final static byte REPLY_AS_CODE = 0x0b;
	/* Fields of ReplyAS message */
	private byte[] cname;
	private Ticket ticket;
	private EncryptedData encData;
	/* Encoded ReplyAS message */
	private byte[] encoded;

	public ReplyAS(byte[] cname, Ticket ticket, EncryptedData encData) {
		if (cname == null || ticket == null || encData == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		if (cname.length != PRINCIPAL_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		this.cname = cname.clone();
		this.ticket = (Ticket) ticket.clone();
		this.encData = (EncryptedData) encData.clone();
		
		encode();
	}

	public ReplyAS(byte[] encoded) {
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/*
		 * Check if encodedInput has at least enough bytes to encoded 2
		 * principal names
		 */
		if (this.encoded.length < MESSAGE_CODE_LENGTH + 2 * PRINCIPAL_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}
		
		if (this.encoded[0] != REPLY_AS_CODE) {
			throw new IllegalArgumentException("Wrong message code");
		}

		decode();
	}

	private void encode() {
		int offset;
		byte[] encodedTicket, encodedEncData;

		encodedTicket = ticket.getEncoded();
		encodedEncData = encData.getEncoded();
		
		this.encoded = new byte[MESSAGE_CODE_LENGTH + PRINCIPAL_NAME_LENGTH + encodedTicket.length
				+ encodedEncData.length];

		offset = 0;
		this.encoded[offset] = REPLY_AS_CODE;
		offset += MESSAGE_CODE_LENGTH;
		System.arraycopy(this.cname, 0, this.encoded, offset, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		System.arraycopy(encodedTicket, 0, this.encoded, offset, encodedTicket.length);
		offset += encodedTicket.length;
		System.arraycopy(encodedEncData, 0, this.encoded, offset, encodedEncData.length);
		offset += encodedEncData.length;

		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
	}

	private void decode() {
		int offset = 0;

		offset += MESSAGE_CODE_LENGTH;
		this.cname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, this.cname, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		/* Copy encoded fields that weren't processed yet */
		byte[] remainingEncoded = new byte[this.encoded.length - offset];
		System.arraycopy(this.encoded, offset, remainingEncoded, 0,	this.encoded.length - offset);
		this.ticket = new Ticket(remainingEncoded);
		offset += this.ticket.getEncodedLength();
		SecureUtil.erase(remainingEncoded);

		remainingEncoded = new byte[this.encoded.length - offset];
		System.arraycopy(this.encoded, offset, remainingEncoded, 0, this.encoded.length - offset);
		this.encData = new EncryptedData(remainingEncoded);
		offset += this.encoded.length - offset;
		SecureUtil.erase(remainingEncoded);
	}

	public byte[] getEncoded() {
		return this.encoded.clone();
	}

	public int getEncodedLength() {
		return this.encoded.length;
	}

	public HashMap<String, Object> getDecoded() {
		HashMap<String, Object> decoded = new HashMap<String, Object>();

		decoded.put(CNAME, this.cname.clone());
		decoded.put(TICKET, this.ticket.clone());
		decoded.put(ENCRYPTED_DATA, this.encData.clone());

		return decoded;
	}

	public Object clone() throws CloneNotSupportedException {
		ReplyAS replyAs = (ReplyAS) super.clone();
		replyAs.cname = this.cname.clone();
		replyAs.ticket = (Ticket) this.ticket.clone();
		replyAs.encData = (EncryptedData) this.encData.clone();

		return replyAs;
	}

	public void erase() {
		SecureUtil.erase(this.cname);
		this.ticket.erase();
		this.encData.erase();
	}
}
