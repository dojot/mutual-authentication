package br.com.dojot.mutualauthentication.cryptointegration.messaging.consumer;

import java.util.Map;
import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoManagerService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.cryptointegration.util.Constants;
import br.com.dojot.mutualauthentication.cryptointegration.util.CryptoIntegrationConstants;

public class ConsumerLoopDecryptWithCC extends ConsumerLoopGeneric {

	@EJB
	private CryptoService cryptoService;

	@EJB
	private CryptoManagerService decryptWithCCManager;

	@EJB
	private LoggingService loggingService;

	public ConsumerLoopDecryptWithCC() {
		super(CryptoIntegrationConstants.TOPIC_DECRYPT_WITH_CC, "cryptointegrationgroup", "cryptointegrationdecryptcc");

	}

	@Override
	public void process(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String sessionId = jsonObject.getString("sessionId");
			String data = jsonObject.getString("data");
			Map<String, String> tmp = cryptoService.decryptWithCC(sessionId, data);
			decryptWithCCManager.put("decryptcc." + sessionId, (String) tmp.getOrDefault(Constants.PLAINTEXT, ""), null,
					(String) tmp.getOrDefault(Constants.OPERATION_RESULT, Constants.FAILURE));
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}

}