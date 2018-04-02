package br.com.dojot.jcrypto.exception;

/** Exception thrown when a service provided by the own cryptography provider is not found. */
public class IllegalProviderException extends RuntimeException {

	private static final long serialVersionUID = -6947359667826294025L;
	
	public IllegalProviderException() {

	}
	
	public IllegalProviderException(String message) {
		super(message);		
	}

	public IllegalProviderException(Throwable cause) {
		super(cause);
	}

	public IllegalProviderException(String message, Throwable cause) {
		super(message, cause);		
	}
}
