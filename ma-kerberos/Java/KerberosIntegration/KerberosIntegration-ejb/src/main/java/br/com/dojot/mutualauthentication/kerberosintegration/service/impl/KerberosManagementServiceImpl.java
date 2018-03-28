package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.cache.ComponentCache;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.entity.ComponentVO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.KerberosRegisterReplyTO;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ParseException;
import br.com.dojot.mutualauthentication.kerberoslib.util.CryptoUtil;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ComponentService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.CryptoIntegrationService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosManagementService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.SessionService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.Utils;

@Stateless
public class KerberosManagementServiceImpl implements KerberosManagementService {
	
	@EJB
	private CryptoIntegrationService cryptoIntegrationService;
	
	@EJB
	private ComponentService componentService;
	
	@EJB
	private SessionService sessionService;
	
	@EJB
	private LoggingService loggingService;
	
	@EJB
	private ConfigService configService;

	private static final long TICKET_VALIDITY_PERIOD = 5 * 60 * 1000;
	
	@Override
	public String process(String json) {
		JSONObject jsonObject = new JSONObject(json);
		
		String sessionId = Utils.get(jsonObject.get("sessionId"));
		String transactionId = Utils.get(jsonObject.get("transactionId"));
		Integer step = Utils.get(jsonObject.get("step"));
		
		KerberosRegisterReplyTO to = null;
		
		switch(step) {
			case KerberosIntegrationConstants.KERBEROS_REGISTER_USER:
				to = registerUser(sessionId, transactionId);
				break;
			case KerberosIntegrationConstants.KERBEROS_UNREGISTER_USER:
				to = unregisterUser(sessionId, transactionId);
				break;
		}
		
		JSONObject reply = new JSONObject();
		reply.put("result", to.getResult());
		return reply.toString();
	}
	

	@Override
	public KerberosRegisterReplyTO registerComponent(String componentId, String key) {
		KerberosRegisterReplyTO to = new KerberosRegisterReplyTO();
		to.setResult(false);
		try {
			if (componentService.get(componentId) == null) {
				ComponentVO vo = new ComponentVO();
				vo.setComponentId(CryptoUtil.HexStrToByteArray(componentId));
				vo.setKey(CryptoUtil.HexStrToByteArray(key));
				vo.setTicketValidityPeriod(TICKET_VALIDITY_PERIOD);
				ComponentCache cache = new ComponentCache();
				cache.setKey(CryptoUtil.HexStrToByteArray(key));
				cache.setTicketValidityPeriod(TICKET_VALIDITY_PERIOD);
				componentService.save(vo);
				to.setResult(true);
			}
		} catch (ParseException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA",
					"Failed to register component because of failure during decoding!");
		}
		return to;
	}

	@Override
	public KerberosRegisterReplyTO unregisterComponent(String componentId) {
		KerberosRegisterReplyTO to = new KerberosRegisterReplyTO();
		to.setResult(false);
		try {
			if (componentService.get(componentId) != null) {
				componentService.remove(componentId);
				to.setResult(true);
			}
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA",
					"Failed to register component because of failure during decoding!");
		}
		return to;
	}

	@Override
	public KerberosRegisterReplyTO registerUser(String sessionId, String transactionId) {
		KerberosRegisterReplyTO to = new KerberosRegisterReplyTO();
		to.setResult(false);
		String kerberosSessionId = Utils.generateKerberosSessionId(sessionId, transactionId);
		if (sessionService.get(kerberosSessionId) == null) {
			sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_WAIT_REQUEST_AS, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
			to.setResult(true);
		}
		return to;
	}

	@Override
	public KerberosRegisterReplyTO unregisterUser(String sessionId, String transactionId) {
		KerberosRegisterReplyTO to = new KerberosRegisterReplyTO();
		to.setResult(false);
		String kerberosSessionId = Utils.generateKerberosSessionId(sessionId, transactionId);
		if (sessionService.get(kerberosSessionId) != null) {
			sessionService.remove(kerberosSessionId);
			try {
				to.setResult(cryptoIntegrationService.unregisterCryptoChannel(kerberosSessionId));
			} catch (GenericException e) {
				to.setResult(false);
				loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA",
						"Timeout while trying to unregister user cryptochannel");
			}
		}
		return to;
	}
	
	@Override
	public String getUserInfo(String sessionId) {
		return sessionService.get(sessionId);
	}

}
