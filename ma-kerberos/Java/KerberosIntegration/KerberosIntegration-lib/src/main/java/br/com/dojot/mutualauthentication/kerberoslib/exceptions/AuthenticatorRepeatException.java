package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown if request is a replay;
 * Kerberos code is KRB_AP_ERR_REPEAT.
 */
public class AuthenticatorRepeatException extends Exception {
	public AuthenticatorRepeatException() {
	}
	
	public AuthenticatorRepeatException(String message) {
		super(message);
	}
	
	public AuthenticatorRepeatException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
