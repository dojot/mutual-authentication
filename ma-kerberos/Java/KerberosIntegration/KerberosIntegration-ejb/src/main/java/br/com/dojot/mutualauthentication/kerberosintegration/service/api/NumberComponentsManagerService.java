package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface NumberComponentsManagerService {

	String findResultsByTransactionId(String transactionId);

	void remove(String transactionId);
}