package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ReplayDAO;

@Singleton
public class ReplayDAOImpl extends GenericRedisDAOImpl implements ReplayDAO {

	private String PREFFIX_KERBEROS_REPLAY_CACHE = "kerberos.replay.";

    @Override
	public void add(String cnameAuth, String authTime) {
    	super.set(PREFFIX_KERBEROS_REPLAY_CACHE + cnameAuth, authTime);
	}

	@Override
	public void remove(String cnameAuth) {
		super.del(PREFFIX_KERBEROS_REPLAY_CACHE + cnameAuth);
	}

	@Override
	public String get(String cnameAuth) {
		return super.get(PREFFIX_KERBEROS_REPLAY_CACHE + cnameAuth);
	}

}
