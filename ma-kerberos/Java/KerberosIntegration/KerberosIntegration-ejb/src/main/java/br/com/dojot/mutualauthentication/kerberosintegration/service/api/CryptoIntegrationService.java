package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.CryptoReplyTO;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;

public interface CryptoIntegrationService {

	boolean registerCryptoChannel(String sessionId, String transactionId, String provider, byte[] keySC, byte[] ivSC,
			byte[] keyCS, byte[] ivCS, int tagLen, long lifespan) throws GenericException ;

	CryptoReplyTO encryptWithCC(String sessionId, byte[] data) throws GenericException;

	CryptoReplyTO decryptWithCC(String sessionId, byte[] data) throws GenericException;

	CryptoReplyTO encrypt(String sessionId, byte[] key, byte[] iv, int tagSize, byte[] data) throws GenericException;

	CryptoReplyTO decrypt(String sessionId, byte[] key, byte[] iv, int tagSize, byte[] data) throws GenericException;

	boolean unregisterCryptoChannel(String sessionId) throws GenericException ;

	boolean saveCryptoChannel(String sessionId, String transactionId) throws GenericException ;
}