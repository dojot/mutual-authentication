package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/*
 * This exception should be throw if secure erase didn't work
 */
public class SecureEraseException extends RuntimeException {
	public SecureEraseException() {
	}
	
	public SecureEraseException(String message) {
		super(message);
	}
	
	public SecureEraseException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
