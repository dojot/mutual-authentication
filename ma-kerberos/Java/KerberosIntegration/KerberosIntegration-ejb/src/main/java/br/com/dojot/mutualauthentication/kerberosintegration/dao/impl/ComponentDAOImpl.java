package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ComponentDAO;

@Singleton
public class ComponentDAOImpl extends GenericRedisDAOImpl implements ComponentDAO {
	private String PREFFIX_COMPONENT_CACHE = "kerberos.";

	@Override
	public void add(String componentId, String json) {
		super.set(PREFFIX_COMPONENT_CACHE + componentId, json);
	}

	@Override
	public void remove(String componentId) {
		super.del(PREFFIX_COMPONENT_CACHE + componentId);
	}

	@Override
	public String get(String componentId) {
		return super.get(PREFFIX_COMPONENT_CACHE + componentId);
	}

}
