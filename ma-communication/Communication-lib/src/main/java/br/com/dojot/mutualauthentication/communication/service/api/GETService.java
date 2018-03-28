package br.com.dojot.mutualauthentication.communication.service.api;

import br.com.dojot.mutualauthentication.communication.exceptions.InvalidResponseException;

public interface GETService {
	
	String get(String server, String path) throws InvalidResponseException;
}
