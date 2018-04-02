package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchCryptoDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchCryptoWithCCDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchRegisterDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchSaveCryptoChannelDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.CryptoReplyTO;
import br.com.dojot.mutualauthentication.kerberoslib.util.CryptoUtil;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.KerberosIntegrationTimeoutException;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.DecryptProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.DecryptWithCCProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.EncryptProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.EncryptWithCCProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.RegisterProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.SaveCryptoChannelProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.UnregisterProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.CryptoIntegrationService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.CryptoManagerService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationExceptionConstants;

@Stateless
public class CryptoIntegrationServiceImpl implements CryptoIntegrationService {

	@EJB
	private CryptoManagerService cryptoManager;
	
	@EJB
	private EncryptProducerService encryptProducerService;
	
	@EJB
	private DecryptProducerService decryptProducerService;
	
	@EJB
	private EncryptWithCCProducerService encryptWithCCProducerService;
	
	@EJB
	private DecryptWithCCProducerService decryptWithCCProducerService;
	
	@EJB
	private UnregisterProducerService unregisterProducerService;
	
	@EJB
	private RegisterProducerService registerProducerService;
	
	@EJB
	private SaveCryptoChannelProducerService saveCryptoChannelProducerService;

	@EJB
	private ConfigService configService;
	
	@EJB
	private LoggingService loggingService;
	
	@Override
	public boolean registerCryptoChannel(String sessionId, String transactionId, String provider,
			byte[] keyServerToComponent, byte[] ivServerToComponent, byte[] keyComponentToServer,
			byte[] ivComponentToServer, int tagLen, long lifespan) throws GenericException {
		BatchRegisterDTO dto = new BatchRegisterDTO();
		dto.setTransactionId(transactionId);
		dto.setSessionId(sessionId);
		dto.setProvider(provider);
		dto.setTagLen(tagLen);
		dto.setKeyServerToComponent(CryptoUtil.ByteArrayToHexStr(keyServerToComponent));
		dto.setIvServerToComponent(CryptoUtil.ByteArrayToHexStr(ivServerToComponent));
		dto.setKeyComponentToServer(CryptoUtil.ByteArrayToHexStr(keyComponentToServer));
		dto.setIvComponentToServer(CryptoUtil.ByteArrayToHexStr(ivComponentToServer));		
		dto.setLifespan(lifespan);
		registerProducerService.produce(dto);
		Boolean tmp = null;
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_REGISTER_CRYPTO_CHANNEL_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		while (tmp == null) {
			tmp = cryptoManager.findBooleanResultBySessionId("register." + sessionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA", "Timeout to register crypto channel");
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_REGISTER_CRYPTO_CHANNEL_TIMEOUT);
			}
		}
		cryptoManager.remove("register." + sessionId);
		return tmp;
	}
	
	@Override
	public boolean unregisterCryptoChannel(String sessionId) throws GenericException {
			BatchRegisterDTO dto = new BatchRegisterDTO();
			dto.setSessionId(sessionId);
			unregisterProducerService.produce(dto);
			Boolean tmp = null;
			int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_UNREGISTER_CRYPTO_CHANNEL_SERVICE));
			long endTimeMillis = System.currentTimeMillis() + timeout;
			while (tmp == null) {
				tmp = cryptoManager.findBooleanResultBySessionId("unregister." + sessionId);
				if (System.currentTimeMillis() > endTimeMillis) {
					loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA", "Timeout to unregister crypto channel");
					throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_UNREGISTER_CRYPTO_CHANNEL_TIMEOUT);
				}
			}
			cryptoManager.remove("unregister." + sessionId);
			return tmp;
	}

	@Override
	public CryptoReplyTO encryptWithCC(String sessionId, byte[] data) throws GenericException {
		CryptoReplyTO out = new CryptoReplyTO();
		BatchCryptoWithCCDTO dto = new BatchCryptoWithCCDTO();
		dto.setSessionId(sessionId);
		dto.setData(CryptoUtil.ByteArrayToHexStr(data));
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_ENCRYPT_CC_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		encryptWithCCProducerService.produce(dto);
		Object[] tmp = null;
		while (tmp==null) {
			tmp = cryptoManager.findTextCypherResultsBySessionId("encryptcc." + sessionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to encrypt with crypto channel");
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_ENCRYPT_CC_TIMEOUT);
			}
		}
		cryptoManager.remove("encryptcc." + sessionId);
		out.setData((String)tmp[0]);
		out.setResult((String)tmp[1]);
		return out;
	}

	@Override
	public CryptoReplyTO decryptWithCC(String sessionId, byte[] data) throws GenericException {		
		CryptoReplyTO out = new CryptoReplyTO();
		BatchCryptoWithCCDTO dto = new BatchCryptoWithCCDTO();
		dto.setSessionId(sessionId);
		dto.setData(CryptoUtil.ByteArrayToHexStr(data));
		decryptWithCCProducerService.produce(dto);
		Object[] tmp = null;
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_DECRYPT_CC_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		while (tmp==null) {
			tmp = cryptoManager.findTextPlainResultsBySessionId("decryptcc." + sessionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to decrypt with crypto channel");
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_DECRYPT_CC_TIMEOUT);
			}
		}
		cryptoManager.remove("decryptcc." +sessionId);
		out.setData((String)tmp[0]);
		out.setResult((String)tmp[1]);
		return out;
	}
	
	@Override
	public CryptoReplyTO encrypt(String sessionId, byte[] key, byte[] iv, int tagSize, byte[] data) throws GenericException {
		CryptoReplyTO out = new CryptoReplyTO();
		BatchCryptoDTO dto = new BatchCryptoDTO();
		dto.setSessionId(sessionId);
		dto.setKey(CryptoUtil.ByteArrayToHexStr(key));
		dto.setIv(CryptoUtil.ByteArrayToHexStr(iv));
		dto.setTagLen(tagSize);
		dto.setData(CryptoUtil.ByteArrayToHexStr(data));
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_ENCRYPT_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		encryptProducerService.produce(dto);
		Object[] tmp = null;
		while (tmp==null) {
			tmp = cryptoManager.findTextCypherResultsBySessionId("encrypt." + sessionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to encrypt");
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_ENCRYPT_TIMEOUT);
			}
		}
		cryptoManager.remove("encrypt." + sessionId);
		out.setData((String)tmp[0]);
		out.setResult((String)tmp[1]);
		return out;
	}

	@Override
	public CryptoReplyTO decrypt(String sessionId, byte[] key, byte[] iv, int tagSize, byte[] data) throws GenericException {
		CryptoReplyTO out = new CryptoReplyTO();
		BatchCryptoDTO dto = new BatchCryptoDTO();
		dto.setSessionId(sessionId);
		dto.setKey(CryptoUtil.ByteArrayToHexStr(key));
		dto.setIv(CryptoUtil.ByteArrayToHexStr(iv));
		dto.setTagLen(tagSize);
		dto.setData(CryptoUtil.ByteArrayToHexStr(data));
		int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_DECRYPT_SERVICE));
		long endTimeMillis = System.currentTimeMillis() + timeout;
		decryptProducerService.produce(dto);
		Object[] tmp = null;
		while (tmp == null) {
			tmp = cryptoManager.findTextPlainResultsBySessionId("decrypt." + sessionId);
			if (System.currentTimeMillis() > endTimeMillis) {
				loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to decrypt");
				throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_DECRYPT_TIMEOUT);
			}
		}
		cryptoManager.remove("decrypt." + sessionId);
		out.setData((String)tmp[0]);
		out.setResult((String)tmp[1]);
		return out;
	}
	
	@Override
	public boolean saveCryptoChannel(String sessionId, String transactionId) throws GenericException {
		Boolean tmp = false;
		String isEnabled = configService.findConfigurationByKey(KerberosIntegrationConstants.CONFIGURATION_PARAM_DEBUG_IS_ENABLED);
		if (KerberosIntegrationConstants.DEBUG_ENABLED.equals(isEnabled)) {
			BatchSaveCryptoChannelDTO dto = new BatchSaveCryptoChannelDTO();
			dto.setSessionId(sessionId);
			dto.setTransactionId(transactionId);
			saveCryptoChannelProducerService.produce(dto);
			
			int timeout = new Integer(configService.findConfigurationByKey(KerberosIntegrationConstants.TIMEOUT_SAVE_CRYPTO_CHANNEL_SERVICE));
			long endTimeMillis = System.currentTimeMillis() + timeout;
			while (tmp == null) {
				tmp = cryptoManager.findBooleanResultBySessionId(sessionId);
				if (System.currentTimeMillis() > endTimeMillis) {
					loggingService.saveLogging(Level.ERROR, "KERBEROSINTEGRATION", "SISTEMA","Timeout to save crypto channel");
					throw new KerberosIntegrationTimeoutException(KerberosIntegrationExceptionConstants.KERBEROS_INTEGRATION_SAVE_CRYPTO_CHANNEL_TIMEOUT);
				}
			}
			cryptoManager.remove(sessionId);
		}
		return tmp;
	}

}
