package br.com.dojot.mutualauthentication.cryptointegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.cryptointegration.dao.api.CryptoChannelStateDAO;

@Singleton
public class CryptoChannelStateDAOImpl extends GenericRedisDAOImpl implements CryptoChannelStateDAO {
	private String PREFFIX_CRYPTO_CHANNEL_CACHE = "cryptochannel.";

	@Override
	public void add(String sessionId, String json) {
		super.set(PREFFIX_CRYPTO_CHANNEL_CACHE + sessionId, json);
	}

	@Override
	public void add(String sessionId, String json, Long lifespan) {
		super.set(PREFFIX_CRYPTO_CHANNEL_CACHE + sessionId, json, lifespan.intValue() / 1000);
	}

	@Override
	public void remove(String sessionId) {
		super.del(PREFFIX_CRYPTO_CHANNEL_CACHE + sessionId);
	}

	@Override
	public String get(String sessionId) {
		return super.get(PREFFIX_CRYPTO_CHANNEL_CACHE + sessionId);
	}

}
