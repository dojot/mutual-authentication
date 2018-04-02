package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

public interface ConfigDAO {
	
	String findConfigurationByKey(String key);
}
