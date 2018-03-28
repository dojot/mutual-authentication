package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchNumberComponentsDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.KerberosIntegrationTimeoutException;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.ComponentsProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ComponentsService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.NumberComponentsManagerService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationExceptionConstants;

@Stateless
public class ComponentsServiceImpl implements ComponentsService {
	
	@EJB
	private ConfigService configService;

	@EJB
	private ComponentsProducerService componentsProducerService;

	@EJB
	private NumberComponentsManagerService numberComponentsManagerService;

	@EJB
	private LoggingService loggingService;

	@Override
	public long getComponentExpirationInterval(String transactionId) throws KerberosIntegrationTimeoutException {
    	long expirationInterval = KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL;
    	BatchNumberComponentsDTO dto = new BatchNumberComponentsDTO();
    	dto.setTransactionId(transactionId);
		componentsProducerService.produce(dto);
		String tmp = null;
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_NUMBER_COMPONENTS_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		while (tmp==null) {
			tmp = numberComponentsManagerService.findResultsByTransactionId(transactionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to obtain component's current validity for transaction " + transactionId);
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_NUMBER_COMPONENTS_TIMEOUT);
			}
		}    	
		numberComponentsManagerService.remove(transactionId);
		expirationInterval = Long.valueOf(tmp);
		return expirationInterval;
	}
	
}
