package br.com.dojot.mutualauthentication.kerberosintegration.beans.dto;

public class BatchCryptoDTO {
	private String sessionId;
	private String data;
	private String key;
	private String iv;
	private int tagLen;
	
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
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getIv() {
		return iv;
	}
	public void setIv(String iv) {
		this.iv = iv;
	}
	public int getTagLen() {
		return tagLen;
	}
	public void setTagLen(int tagLen) {
		this.tagLen = tagLen;
	}
}
