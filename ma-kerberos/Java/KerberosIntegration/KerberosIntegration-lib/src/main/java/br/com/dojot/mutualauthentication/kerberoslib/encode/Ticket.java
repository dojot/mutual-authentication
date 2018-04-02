package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

import br.com.dojot.mutualauthentication.kerberoslib.encode.Encoder;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureWipe;

public final class Ticket implements Encoder, Cloneable, SecureWipe {
	/* Fields of ticket type */
	private byte[] sname;
	private EncryptedData encData; /* EncTicketPart */
	/* Encoded version of the ticket */
	private byte[] encoded;

	public Ticket(byte[] sname, EncryptedData encData) {
		/* Check for null parameters */
		if (sname == null || encData == null) {
			throw new NullPointerException("Invalid null parameter");
		}

		/* Check parameter size */
		if (sname.length != PRINCIPAL_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		/* Initialize variables */
		this.sname = sname.clone();
		this.encData = (EncryptedData) encData.clone();
		/* Generates encoded version */
		encode();
	}

	public Ticket(byte[] encoded) {
		/* Check for null parameter */
		if (encoded == null) {
			throw new NullPointerException("Invalid null parameter");
		}
		this.encoded = encoded.clone();

		/* Check minimum parameter size */
		if (this.encoded.length <= PRINCIPAL_NAME_LENGTH) {
			throw new IllegalArgumentException("Invalid parameter length");
		}

		decode();
	}

	private void encode() {
		int offset;
		byte[] encodedEncData = encData.getEncoded();
		this.encoded = new byte[encodedEncData.length + PRINCIPAL_NAME_LENGTH];

		offset = 0;
		System.arraycopy(sname, 0, this.encoded, offset, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;
		
		System.arraycopy(encodedEncData, 0, this.encoded, offset, encodedEncData.length);
		offset += encodedEncData.length;
	}

	private void decode() {
		/* Initialize variables */
		int offset = 0;
		this.sname = new byte[PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, this.sname, 0, PRINCIPAL_NAME_LENGTH);
		offset += PRINCIPAL_NAME_LENGTH;

		/* Separate encrypted data encoded input from encoded sname */
		byte[] encodedEncData = new byte[this.encoded.length - PRINCIPAL_NAME_LENGTH];
		System.arraycopy(this.encoded, offset, encodedEncData, 0, this.encoded.length - PRINCIPAL_NAME_LENGTH);
		this.encData = new EncryptedData(encodedEncData);
		offset += this.encData.getEncodedLength();
		
		this.encoded = SecureUtil.resize(this.encoded, 0, offset);
		SecureUtil.erase(encodedEncData);
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
		decoded.put(ENCRYPTED_DATA, this.encData.clone());

		return decoded;
	}

	public void erase() {
		this.encData.erase();
		SecureUtil.erase(this.sname);
		SecureUtil.erase(this.encoded);
	}

	public Object clone() {
		Ticket ticket = null;
		try {
			ticket = (Ticket) super.clone();
			ticket.sname = this.sname.clone();
			ticket.encoded = this.encoded.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}

		return ticket;
	}
}
