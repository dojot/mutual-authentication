package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface SessionService {
	
	void save(String sessionId, String state, Long lifespan);
	
	void remove(String sessionId);
	
	String get(String sessionId);
}