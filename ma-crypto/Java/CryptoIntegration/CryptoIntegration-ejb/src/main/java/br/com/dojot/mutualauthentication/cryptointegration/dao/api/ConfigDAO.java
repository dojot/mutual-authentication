package br.com.dojot.mutualauthentication.cryptointegration.dao.api;

import javax.ejb.Local;

@Local
public interface ConfigDAO {
	
	String findParameterByKey(String key);
}
