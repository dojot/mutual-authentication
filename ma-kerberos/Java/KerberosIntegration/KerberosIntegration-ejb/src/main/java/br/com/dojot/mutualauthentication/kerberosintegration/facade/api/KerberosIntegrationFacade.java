package br.com.dojot.mutualauthentication.kerberosintegration.facade.api;

import java.util.Map;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosRegisterReplyTO;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;

@Local
public interface KerberosIntegrationFacade {
	
	KerberosRegisterReplyTO unregisterComponent(String componentId);

	KerberosRegisterReplyTO registerComponent(String componentId, String key);

	KerberosRegisterReplyTO registerSession(String sessionId, String transactionId);

	KerberosRegisterReplyTO unregisterSession(String sessionId, String transactionId) throws GenericException;

	Map<String, Object> processRequestAS(boolean skipValidation, String sessionId, String transactionId, String request);

	Map<String, Object> processRequestAP(boolean skipValidation, String sessionId, String transactionId, String request);
	
	String findParameterByKey(String key);
	
	String getSessionInfo(String sessionId);
}
