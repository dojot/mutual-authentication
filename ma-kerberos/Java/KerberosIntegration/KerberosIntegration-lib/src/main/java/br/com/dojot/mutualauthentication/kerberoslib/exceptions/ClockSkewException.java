package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown when clock skew is too great.
 * Kerberos code is KRB_AP_ERR_SKEW.
 */
public class ClockSkewException extends Exception {
	public ClockSkewException() {
	}
	
	public ClockSkewException(String message) {
		super(message);
	}
	
	public ClockSkewException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
