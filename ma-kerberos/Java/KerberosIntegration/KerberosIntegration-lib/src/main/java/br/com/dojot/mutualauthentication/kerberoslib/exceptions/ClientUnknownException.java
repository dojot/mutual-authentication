package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown when client is not found in Kerberos database. 
 * Kerberos code is KDC_ERR_C_PRINCIPAL_UNKNOWN.
 */
public class ClientUnknownException extends Exception {
	public ClientUnknownException() {
	}
	
	public ClientUnknownException(String message) {
		super(message);
	}
	
	public ClientUnknownException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
