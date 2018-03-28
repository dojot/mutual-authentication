package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface ComponentDAO {
	
	void add(String componentId, String json);
	
	void remove(String componentId);
	
	String get(String componentId);
}
