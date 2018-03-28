package br.com.dojot.mutualauthentication.cryptointegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import br.com.dojot.mutualauthentication.cryptointegration.beans.cache.CryptoManagerCache;
import br.com.dojot.mutualauthentication.cryptointegration.dao.api.CryptoManagerDAO;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoManagerService;

@Stateless
public class CryptoManagerServiceImpl implements CryptoManagerService {
	
	@EJB
	private CryptoManagerDAO cryptoManagerDAO;

	@Override
	public void put(String sessionId, String plainText, String cypherText, String results) {
		CryptoManagerCache cache = new CryptoManagerCache();
		cache.setCipherText(cypherText);
		cache.setPlainText(plainText);
		cache.setResults(results);
		cache.setResultsOperation(null);
		JSONObject jsonObject = new JSONObject(cache);
		cryptoManagerDAO.add(sessionId, jsonObject.toString());
	}

	@Override
	public void put(String sessionId, Boolean results) {
		CryptoManagerCache cache = new CryptoManagerCache();
		cache.setCipherText("");
		cache.setPlainText("");
		cache.setResults("");
		cache.setResultsOperation(results);
		JSONObject jsonObject = new JSONObject(cache);
		cryptoManagerDAO.add(sessionId, jsonObject.toString());
	}

	@Override
	public void remove(String sessionId) {
		cryptoManagerDAO.remove(sessionId);
	}
	
}
