package br.com.dojot.mutualauthentication.kerberosintegration.beans.to;

public class KerberosEntityTO {
	private String sessionId;
	private String transactionId;

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
}
