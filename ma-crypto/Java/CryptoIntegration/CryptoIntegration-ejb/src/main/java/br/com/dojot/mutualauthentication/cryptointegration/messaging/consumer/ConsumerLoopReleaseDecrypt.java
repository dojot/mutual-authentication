package br.com.dojot.mutualauthentication.cryptointegration.messaging.consumer;

import javax.ejb.EJB;

import org.apache.commons.lang3.exception.ExceptionUtils;

import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.cryptointegration.util.CryptoIntegrationConstants;

public class ConsumerLoopReleaseDecrypt extends ConsumerLoopGeneric {
	@EJB
	private CryptoService cryptoService;  
	
	@EJB
	private LoggingService loggingService;

	public ConsumerLoopReleaseDecrypt() {
		super(CryptoIntegrationConstants.TOPIC_CRYPTO_RELEASE_DECRYPT_CC, "cryptointegrationgroup", "cryptointegrationrelease");
	}
	
	@Override
	public void process(String json) {
		try {
			cryptoService.releaseDecryptCCVolatile(json);
		} catch (Exception ex) {
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", ex.getMessage() + ".\n" + ExceptionUtils.getStackTrace(ex));
		}
	}
	
}