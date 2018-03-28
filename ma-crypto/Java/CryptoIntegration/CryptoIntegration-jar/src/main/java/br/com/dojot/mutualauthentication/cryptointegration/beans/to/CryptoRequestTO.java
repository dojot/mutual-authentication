package br.com.dojot.mutualauthentication.cryptointegration.beans.to;

public class CryptoRequestTO {
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
