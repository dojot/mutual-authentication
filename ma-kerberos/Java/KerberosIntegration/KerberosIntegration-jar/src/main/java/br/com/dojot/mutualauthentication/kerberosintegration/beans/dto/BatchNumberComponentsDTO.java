package br.com.dojot.mutualauthentication.kerberosintegration.beans.dto;

import java.io.Serializable;

public class BatchNumberComponentsDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1073394595196308975L;
	
	private String transactionId;

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
}
