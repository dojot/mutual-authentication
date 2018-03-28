package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface ConfigService {
	
	String findConfigurationByKey(String key);
}