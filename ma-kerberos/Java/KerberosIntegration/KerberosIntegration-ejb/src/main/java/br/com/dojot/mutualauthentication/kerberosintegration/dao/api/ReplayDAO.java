package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface ReplayDAO {
	
	void add(String cnameAuth, String authTime);
	
	void remove(String cnameAuth);
	
	String get(String cnameAuth);
}
