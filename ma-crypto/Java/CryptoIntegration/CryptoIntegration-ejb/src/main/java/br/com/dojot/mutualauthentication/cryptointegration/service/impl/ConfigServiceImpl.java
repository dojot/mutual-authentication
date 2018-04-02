package br.com.dojot.mutualauthentication.cryptointegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.mutualauthentication.cryptointegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.ConfigService;

@Stateless
public class ConfigServiceImpl implements ConfigService {

	@EJB 
	private ConfigDAO configDAO;

	@Override
	public String findParameterByKey(String key) {
		return configDAO.findParameterByKey(key);
	}

}
