package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.KerberosResponseManagerDAO;

@Singleton
public class KerberosResponseManagerDAOImpl extends GenericRedisDAOImpl implements KerberosResponseManagerDAO {
	private String PREFFIX_KERBEROS_MANAGER_CACHE = "kerberosmanager.";

	@Override
	public void add(String key, String value) {
		super.set(PREFFIX_KERBEROS_MANAGER_CACHE + key, value);
	}

	@Override
	public void remove(String key) {
		super.del(PREFFIX_KERBEROS_MANAGER_CACHE + key);
	}

}
