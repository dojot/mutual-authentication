package br.com.dojot.mutualauthentication.kerberosintegration.beans.cache;

public class ComponentCache {
	private byte[] key;

	private long ticketValidityPeriod;

	public byte[] getKey() {
		return key;
	}

	public void setKey(byte[] key) {
		this.key = key;
	}

	public long getTicketValidityPeriod() {
		return ticketValidityPeriod;
	}

	public void setTicketValidityPeriod(long ticketValidityPeriod) {
		this.ticketValidityPeriod = ticketValidityPeriod;
	}
}
