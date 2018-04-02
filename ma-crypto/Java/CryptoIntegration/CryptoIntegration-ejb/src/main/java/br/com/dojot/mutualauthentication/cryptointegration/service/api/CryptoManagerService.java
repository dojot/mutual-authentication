package br.com.dojot.mutualauthentication.cryptointegration.service.api;

import javax.ejb.Local;

@Local
public interface CryptoManagerService {

	void put(String sessionId, String plainText, String cypherText, String results);
	
	void put(String sessionId, Boolean results);
	
	void remove(String sessionId);
}