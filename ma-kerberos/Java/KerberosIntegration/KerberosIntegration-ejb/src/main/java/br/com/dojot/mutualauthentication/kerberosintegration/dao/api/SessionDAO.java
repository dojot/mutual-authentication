package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface SessionDAO {
	
	void add(String componentId, String json);
	
	void add(String componentId, String json, Long lifespan);
	
	void remove(String componentId);
	
	String get(String componentId);
}
