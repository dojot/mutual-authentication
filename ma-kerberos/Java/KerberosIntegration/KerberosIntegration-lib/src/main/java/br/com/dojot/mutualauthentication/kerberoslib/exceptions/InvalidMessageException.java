package br.com.dojot.mutualauthentication.kerberoslib.exceptions;

public class InvalidMessageException extends Exception {
	public InvalidMessageException() {
	}
	
	public InvalidMessageException(String message) {
		super(message);
	}
	
	public InvalidMessageException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
