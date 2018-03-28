package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ConfigDAO;

@Singleton
public class ConfigDAOImpl extends GenericRedisDAOImpl implements ConfigDAO {

	@Override
	public String findConfigurationByKey(String key) {
		return super.get(key);
	}
	
}
