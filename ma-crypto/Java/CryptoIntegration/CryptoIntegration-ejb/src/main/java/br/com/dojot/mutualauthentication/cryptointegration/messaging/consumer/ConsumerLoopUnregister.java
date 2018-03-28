package br.com.dojot.mutualauthentication.cryptointegration.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoManagerService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.cryptointegration.util.CryptoIntegrationConstants;

public class ConsumerLoopUnregister extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;

	@EJB
	private CryptoManagerService cryptoManager;
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopUnregister() {
		super(CryptoIntegrationConstants.TOPIC_UNREGISTER, "cryptointegrationgroup", "cryptointegrationunregister");
	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sessionId = jsonObject.getString("sessionId");
			Boolean result = cryptoService.unregisterCC(sessionId);
			cryptoManager.put("unregister." + sessionId, result);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}

}