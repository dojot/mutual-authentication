package br.com.dojot.mutualauthentication.cryptointegration.dao.impl;

import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.dojot.mutualauthentication.cryptointegration.dao.api.ConfigDAO;

@Startup
@Singleton
public class ConfigDAOImpl extends GenericRedisDAOImpl implements ConfigDAO {

	@Override
	public String findParameterByKey(String key) {
		return super.get(key);
	}
	
}
