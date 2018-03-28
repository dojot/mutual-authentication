package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.exception.KerberosIntegrationTimeoutException;

@Local
public interface ComponentsService {
	
	long getComponentExpirationInterval(String transactionId) throws KerberosIntegrationTimeoutException;
}