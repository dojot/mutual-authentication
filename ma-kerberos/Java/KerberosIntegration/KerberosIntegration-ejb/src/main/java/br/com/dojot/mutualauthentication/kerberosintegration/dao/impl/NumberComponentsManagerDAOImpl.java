package br.com.dojot.mutualauthentication.kerberosintegration.dao.impl;

import javax.ejb.Singleton;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.NumberComponentsManagerDAO;

@Singleton
public class NumberComponentsManagerDAOImpl extends GenericRedisDAOImpl implements NumberComponentsManagerDAO {

	private String PREFFIX_COMPONENTS_COUNTER_MANAGER_CACHE = "numbercomponentsmanager.";
	
	@Override
	public String get(String transactionId) {
		return super.get(PREFFIX_COMPONENTS_COUNTER_MANAGER_CACHE + transactionId);
	}

	@Override
	public void remove(String transactionId) {
		super.del(PREFFIX_COMPONENTS_COUNTER_MANAGER_CACHE + transactionId);
	}

}
