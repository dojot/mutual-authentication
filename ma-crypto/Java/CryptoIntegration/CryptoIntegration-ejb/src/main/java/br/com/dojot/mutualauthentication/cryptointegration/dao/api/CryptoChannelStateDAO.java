package br.com.dojot.mutualauthentication.cryptointegration.dao.api;

import javax.ejb.Local;

@Local
public interface CryptoChannelStateDAO {
	
	void add(String sessionId, String json);
	
	void add(String sessionId, String json, Long lifespan);
	
	void remove(String sessionId);
	
	String get(String sessionId);
}
