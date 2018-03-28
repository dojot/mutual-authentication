package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface CryptoManagerService {

	Boolean findBooleanResultBySessionId(String sessionId);
	
	Object[] findTextCypherResultsBySessionId(String sessionId);
	
	Object[] findTextPlainResultsBySessionId(String sessionId);
	
	void remove(String sessionId);
}