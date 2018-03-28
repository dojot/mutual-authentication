package br.com.dojot.mutualauthentication.kerberosintegration.dao.api;

import javax.ejb.Local;

@Local
public interface NumberComponentsManagerDAO {
	
	String get(String transactionId);

	void remove(String transactionId);
}
