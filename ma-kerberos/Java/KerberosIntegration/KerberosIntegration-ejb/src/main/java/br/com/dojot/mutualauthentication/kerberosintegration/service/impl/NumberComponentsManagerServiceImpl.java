package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.NumberComponentsManagerDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.NumberComponentsManagerService;

@Stateless
public class NumberComponentsManagerServiceImpl implements NumberComponentsManagerService {
	
	@EJB
	private NumberComponentsManagerDAO numberComponentsManagerDAO;

	@Override
	public String findResultsByTransactionId(String transactionId) {
		String components = numberComponentsManagerDAO.get(transactionId);
		return components;
	}

	@Override
	public void remove(String transactionId) {
		 numberComponentsManagerDAO.remove(transactionId);
	}
	
}
