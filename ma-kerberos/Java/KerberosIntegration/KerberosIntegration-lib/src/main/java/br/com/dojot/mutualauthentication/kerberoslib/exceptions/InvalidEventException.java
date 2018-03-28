package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

/* 
 * This exception should be thrown in the occurrence of events that 
 * should be impossible to happen.
 */
public class InvalidEventException extends RuntimeException {
	public InvalidEventException() {
	}
	
	public InvalidEventException(String message) {
		super(message);
	}
	
	public InvalidEventException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
