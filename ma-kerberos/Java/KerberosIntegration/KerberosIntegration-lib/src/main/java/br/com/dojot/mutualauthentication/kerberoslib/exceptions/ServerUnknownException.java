package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/**
 * Thrown by the protocol when the server ID is unknown.
 * Kerberos code is KDC_ERR_S_PRINCIPAL_UNKNOWN
 */
public class ServerUnknownException extends Exception {
	public ServerUnknownException() {
	}
	
	public ServerUnknownException(String message) {
		super(message);
	}
	
	public ServerUnknownException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
