package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.SessionDAO;

@Singleton
public class SessionDAOImpl extends GenericRedisDAOImpl implements SessionDAO {
	private String PREFFIX_KERBEROS_SESSION_CACHE = "kerberos.session.";

	@Override
	public void add(String componentId, String json) {
		super.set(PREFFIX_KERBEROS_SESSION_CACHE + componentId, json);
	}

	@Override
	public void add(String componentId, String json, Long lifespan) {
		super.set(PREFFIX_KERBEROS_SESSION_CACHE + componentId, json, lifespan.intValue()/1000);
	}

	@Override
	public void remove(String componentId) {
		super.del(PREFFIX_KERBEROS_SESSION_CACHE + componentId);
	}

	@Override
	public String get(String componentId) {
		return super.get(PREFFIX_KERBEROS_SESSION_CACHE + componentId);
	}

}
