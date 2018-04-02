package br.com.dojot.mutualauthentication.kerberosintegration.beans.entity;

import java.io.Serializable;

public class ComponentVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3907237308966350352L;

	private Integer id;

	private byte[] componentId;

	private byte[] key;

	private long ticketValidityPeriod;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getComponentId() {
		return componentId;
	}

	public void setComponentId(byte[] componentId) {
		this.componentId = componentId;
	}

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
