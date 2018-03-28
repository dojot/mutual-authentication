package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface ReplayService {
	
	void save(String cnameAuth, String authTime);
	
	void remove(String cnameAuth);
	
	String get(String cnameAuth);
}