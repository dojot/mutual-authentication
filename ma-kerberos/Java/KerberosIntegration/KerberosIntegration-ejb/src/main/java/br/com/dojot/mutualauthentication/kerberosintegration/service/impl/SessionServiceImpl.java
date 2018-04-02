package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.SessionDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.SessionService;

@Stateless
public class SessionServiceImpl implements SessionService {

	@Inject
	private SessionDAO sessionDAO;

	@Override
	public void save(String sessionId, String state, Long lifespan) {
		if (lifespan != null) {
			sessionDAO.add(sessionId, state, lifespan);
		} else {
			sessionDAO.add(sessionId, state);
		}
	}

	@Override
	public void remove(String sessionId) {
		sessionDAO.remove(sessionId);
	}

	@Override
	public String get(String sessionId) {
		return sessionDAO.get(sessionId);
	}

}
