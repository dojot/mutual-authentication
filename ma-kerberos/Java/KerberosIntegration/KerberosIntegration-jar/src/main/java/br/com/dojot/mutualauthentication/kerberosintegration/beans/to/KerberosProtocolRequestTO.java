package br.com.dojot.mutualauthentication.kerberosintegration.beans.to;

public class KerberosProtocolRequestTO {
	private boolean skipValidation;
	
	private String sessionId;
	
	private String transactionId;

	private String request;

	public boolean getSkipValidation() {
		return skipValidation;
	}

	public void setSkipValidation(boolean skipValidation) {
		this.skipValidation = skipValidation;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

}
