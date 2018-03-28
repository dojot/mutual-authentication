package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ReplayDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ReplayService;

@Stateless
public class ReplayServiceImpl implements ReplayService {

	@Inject
	private ReplayDAO replayDAO;


	@Override
	public void remove(String componentId) {
		replayDAO.remove(componentId);
	}
	@Override
	public void save(String cnameAuth, String authTime) {
		replayDAO.add(cnameAuth, authTime);
	}

	@Override
	public String get(String cnameAuth) {
		return replayDAO.get(cnameAuth);
	}

}
