package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.CryptoManagerDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.CryptoManagerService;

@Stateless
public class CryptoManagerServiceImpl implements CryptoManagerService {
	
	@EJB
	private CryptoManagerDAO cryptoManagerDAO;

	@Override
	public void remove(String sessionId) {
		cryptoManagerDAO.remove(sessionId);
	}

	@Override
	public Boolean findBooleanResultBySessionId(String sessionId) {
		Boolean out = null;
		String json = cryptoManagerDAO.get(sessionId);
		if(json != null) {
			JSONObject jsonObject = new JSONObject(json);
			out = jsonObject.getBoolean("resultsOperation");
		}
		return out;
	}

	@Override
	public Object[] findTextPlainResultsBySessionId(String sessionId) {
		Object[] out = null;
		String json = cryptoManagerDAO.get(sessionId);
		if(json != null) {
			JSONObject jsonObject = new JSONObject(json);
			out = new Object[2];
			out[0] = jsonObject.getString("plainText");
			out[1] = jsonObject.getString("results");
		}
		return out;
	}
	
	@Override
	public Object[] findTextCypherResultsBySessionId(String sessionId) {
		Object[] out = null;
		String json = cryptoManagerDAO.get(sessionId);
		if(json != null) {
			JSONObject jsonObject = new JSONObject(json);
			out = new Object[2];
			out[0] = jsonObject.getString("cipherText");
			out[1] = jsonObject.getString("results");
		}
		return out;
	}
	
}
