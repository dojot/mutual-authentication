package br.com.dojot.mutualauthentication.kerberosintegration.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.KerberosResponseManagerDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosManagementService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.Utils;

public class ConsumerLoopKerberosManagerProcessing extends ConsumerLoopGeneric {
	@EJB
	private KerberosResponseManagerDAO kerberosResponseManagerDAO;

	@EJB
	private KerberosManagementService kerberosManagementService;

	@EJB
	private LoggingService loggingService;

	public ConsumerLoopKerberosManagerProcessing() {
		super(KerberosIntegrationConstants.TOPIC_KERBEROS_MANAGEMENT, "kerberosgroup", "kerberosmanager");
	}

	@Override
	public void process(String json) {
		try {
			String reply = kerberosManagementService.process(json);
			JSONObject jsonObject = new JSONObject(json);
			String uniqueId = Utils.generateKerberosSessionId(jsonObject.getString("sessionId"), jsonObject.getString("transactionId"));
			Integer step = Utils.get(jsonObject.get("step"));
			switch(step) {
				case KerberosIntegrationConstants.KERBEROS_REGISTER_USER:
					kerberosResponseManagerDAO.add("register." + uniqueId, reply);
					break;
				case KerberosIntegrationConstants.KERBEROS_UNREGISTER_USER:
					kerberosResponseManagerDAO.add("unregister." + uniqueId, reply);
					break;
			}		
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}

}