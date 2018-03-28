package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface CryptoManagerDAO {
	
	void add(String sessionId, String json);
	
	void remove(String sessionId);
	
	String get(String sessionId);
}
