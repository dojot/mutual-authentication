package br.com.dojot.mutualauthentication.kerberosintegration.beans.dto;

public class BatchRegisterDTO {
	
	private String transactionId;

	private String sessionId;
	
	private String provider;
	
	private String keyServerToComponent;
	
	private String ivServerToComponent;
	
	private String keyComponentToServer;
	
	private String ivComponentToServer;
	
	private int tagLen;
	
	private long lifespan;

	public String getSessionId() {
		return sessionId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getKeyServerToComponent() {
		return keyServerToComponent;
	}

	public void setKeyServerToComponent(String keyServerToComponent) {
		this.keyServerToComponent = keyServerToComponent;
	}

	public String getIvServerToComponent() {
		return ivServerToComponent;
	}

	public void setIvServerToComponent(String ivServerToComponent) {
		this.ivServerToComponent = ivServerToComponent;
	}

	public String getKeyComponentToServer() {
		return keyComponentToServer;
	}

	public void setKeyComponentToServer(String keyComponentToServer) {
		this.keyComponentToServer = keyComponentToServer;
	}

	public String getIvComponentToServer() {
		return ivComponentToServer;
	}

	public void setIvComponentToServer(String ivComponentToServer) {
		this.ivComponentToServer = ivComponentToServer;
	}

	public int getTagLen() {
		return tagLen;
	}

	public void setTagLen(int tagLen) {
		this.tagLen = tagLen;
	}

	public long getLifespan() {
		return lifespan;
	}

	public void setLifespan(long lifespan) {
		this.lifespan = lifespan;
	}
}
