package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown if ticket is already expired.
 * Kerberos code is KRB_AP_ERR_TKT_EXPIRED.
 */
public class ExpiredTicketException extends Exception {
	public ExpiredTicketException() {
	}
	
	public ExpiredTicketException(String message) {
		super(message);
	}
	
	public ExpiredTicketException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
