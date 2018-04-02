package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosRegisterReplyTO;

@Local
public interface KerberosManagementService {
	
	String process(String json);
	
	KerberosRegisterReplyTO registerUser(String sessionId, String transactionId);

	KerberosRegisterReplyTO unregisterUser(String sessionId, String transactionId);

	KerberosRegisterReplyTO unregisterComponent(String componentId);

	KerberosRegisterReplyTO registerComponent(String componentId, String key);
	
	String getUserInfo(String userId);
}
