package br.com.dojot.mutualauthentication.kerberosintegration.beans.cache;

import java.io.Serializable;

public class CryptoManagerCache implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2307005822042260417L;

	private String cipherText;
	
	private String plainText;
	
	private String results;

	private Boolean resultsOperation;

	public String getCipherText() {
		return cipherText;
	}

	public void setCipherText(String cipherText) {
		this.cipherText = cipherText;
	}

	public String getPlainText() {
		return plainText;
	}

	public void setPlainText(String plainText) {
		this.plainText = plainText;
	}

	public String getResults() {
		return results;
	}

	public void setResults(String results) {
		this.results = results;
	}

	public Boolean getResultsOperation() {
		return resultsOperation;
	}

	public void setResultsOperation(Boolean resultsOperation) {
		this.resultsOperation = resultsOperation;
	}
	
}
