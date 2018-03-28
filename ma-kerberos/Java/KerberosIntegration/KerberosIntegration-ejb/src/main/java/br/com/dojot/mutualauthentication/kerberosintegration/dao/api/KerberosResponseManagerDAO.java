package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface KerberosResponseManagerDAO {
	
	void add(String key, String value);
	
	void remove(String key);
}
