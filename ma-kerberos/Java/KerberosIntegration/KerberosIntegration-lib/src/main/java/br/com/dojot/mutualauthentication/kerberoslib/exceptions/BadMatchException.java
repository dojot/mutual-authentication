package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown if ticket and authenticator don't match.
 * Kerberos code is KRB_AP_ERR_BADMATCH.
 */
public class BadMatchException extends Exception {
	public BadMatchException() {
	}
	
	public BadMatchException(String message) {
		super(message);
	}
	
	public BadMatchException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
