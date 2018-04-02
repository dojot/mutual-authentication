package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;

@Stateless
public class ConfigServiceImpl implements ConfigService {
	
	@EJB
	private ConfigDAO configDAO;

	@Override
	public String findConfigurationByKey(String key) {
		return configDAO.findConfigurationByKey(key);
	}

}
