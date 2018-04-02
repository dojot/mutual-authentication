package br.com.dojot.mutualauthentication.kerberosintegration.beans.dto;

import java.io.Serializable;

public class BatchCryptoWithCCDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1579317452658839787L;

	private String sessionId;
    
    private String data;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
}