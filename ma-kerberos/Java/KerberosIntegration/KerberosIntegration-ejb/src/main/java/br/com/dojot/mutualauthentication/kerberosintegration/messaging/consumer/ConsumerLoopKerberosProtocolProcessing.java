package br.com.dojot.mutualauthentication.kerberosintegration.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.KerberosResponseManagerDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosProtocolService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.Utils;

public class ConsumerLoopKerberosProtocolProcessing extends ConsumerLoopGeneric {
	@EJB
	private KerberosResponseManagerDAO kerberosResponseManagerDAO;
	
    @EJB
    private KerberosProtocolService kerberosProtocolService;
    
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopKerberosProtocolProcessing() {
		super(KerberosIntegrationConstants.TOPIC_KERBEROS_PROTOCOL, "kerberosgroup", "kerberosprotocol");
	}
	
	@Override
	public void process(String json) {
		try {
			String reply = kerberosProtocolService.process(json);
			JSONObject jsonObject = new JSONObject(json);
			String uniqueId = Utils.generateKerberosSessionId(jsonObject.getString("sessionId"), jsonObject.getString("transactionId"));
			Integer step = Utils.get(jsonObject.get("step"));
			switch(step) {
				case KerberosIntegrationConstants.KERBEROS_REQUEST_AS:
					kerberosResponseManagerDAO.add("requestas." + uniqueId, reply);
					break;
				case KerberosIntegrationConstants.KERBEROS_REQUEST_AP:
					kerberosResponseManagerDAO.add("requestap." + uniqueId, reply);
					break;
			}
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
		
	}
	
}