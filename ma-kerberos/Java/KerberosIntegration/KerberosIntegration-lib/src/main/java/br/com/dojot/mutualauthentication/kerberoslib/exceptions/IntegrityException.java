package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown if integrity check on decrypted field failed.
 * Kerberos code is KRB_AP_ERR_BAD_INTEGRITY.
 */
public class IntegrityException extends Exception {
	public IntegrityException() {
	}
	
	public IntegrityException(String message) {
		super(message);
	}
	
	public IntegrityException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
