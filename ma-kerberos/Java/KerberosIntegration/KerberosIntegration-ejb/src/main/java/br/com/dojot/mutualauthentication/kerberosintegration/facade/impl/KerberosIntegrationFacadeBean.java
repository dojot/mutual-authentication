package br.com.dojot.mutualauthentication.kerberosintegration.facade.impl;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosRegisterReplyTO;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;
import br.com.dojot.mutualauthentication.kerberosintegration.facade.api.KerberosIntegrationFacade;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosManagementService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosProtocolService;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class KerberosIntegrationFacadeBean implements KerberosIntegrationFacade {
	
	@EJB
	private KerberosManagementService kerberosManagementService;
	
	@EJB
	private KerberosProtocolService kerberosProtocolService;

	@EJB
	private ConfigService configService;

	@Override
	public Map<String, Object> processRequestAS(boolean skipValidation, String sessionId, String transactionId, String request) {
		return kerberosProtocolService.processRequestAS(skipValidation, sessionId, transactionId, request);
	}

	@Override
	public Map<String, Object> processRequestAP(boolean skipValidation, String sessionId, String transactionId, String request) {
		return kerberosProtocolService.processRequestAP(skipValidation, sessionId, transactionId, request);
	}
	
	@Override
	public KerberosRegisterReplyTO registerComponent(String componentId, String key) {
		return  kerberosManagementService.registerComponent(componentId, key);
	}
	
	@Override
	public KerberosRegisterReplyTO unregisterComponent(String componentId) {
		return kerberosManagementService.unregisterComponent(componentId);
	}

	@Override
	public KerberosRegisterReplyTO registerSession(String sessionId, String transactionId) {
		return kerberosManagementService.registerUser(sessionId, transactionId);
	}

	@Override
	public KerberosRegisterReplyTO unregisterSession(String sessionId, String transactionId) throws GenericException {
		return kerberosManagementService.unregisterUser(sessionId, transactionId);
	}

	@Override
	public String findParameterByKey(String key) {
		return configService.findConfigurationByKey(key);
	}
	
	@Override
	public String getSessionInfo(String sessionId) {
		return kerberosManagementService.getUserInfo(sessionId);
	}

}