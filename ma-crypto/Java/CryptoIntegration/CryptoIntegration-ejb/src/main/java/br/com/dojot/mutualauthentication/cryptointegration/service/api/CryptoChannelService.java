package br.com.dojot.mutualauthentication.cryptointegration.service.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.cryptointegration.beans.cache.CryptoChannelStateCache;

@Local
public interface CryptoChannelService {

	void save(String sessionId, CryptoChannelStateCache cache);
	
	void save(String sessionId, CryptoChannelStateCache cache, Long lifespan);
	
	void remove(String sessionId);
	
	CryptoChannelStateCache findBySessionId(String sessionId);
}