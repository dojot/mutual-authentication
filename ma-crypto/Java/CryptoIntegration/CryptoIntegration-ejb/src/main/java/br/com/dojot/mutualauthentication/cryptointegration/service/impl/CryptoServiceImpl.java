package br.com.dojot.mutualauthentication.cryptointegration.service.impl;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.dojot.jcrypto.exception.ParseException;
import br.com.dojot.jcrypto.util.CryptoChannel;
import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.mutualauthentication.cryptointegration.beans.cache.CryptoChannelStateCache;
import br.com.dojot.mutualauthentication.cryptointegration.beans.dto.DebugCryptoChannelDTO;
import br.com.dojot.mutualauthentication.cryptointegration.messaging.api.DebugCryptoChannelProducerService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.AESConfigService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoChannelService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoManagerService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.cryptointegration.util.Constants;
import br.com.dojot.mutualauthentication.cryptointegration.util.Crypto;

@Stateless
public class CryptoServiceImpl implements CryptoService {
	
	@EJB
	private LoggingService loggingService;

	@EJB
	private CryptoManagerService cryptoManager;

	@EJB
	private DebugCryptoChannelProducerService debugCryptoChannelProducerService;
	
	@EJB
	private CryptoChannelService cryptoChannelService;
	
	@EJB
	private AESConfigService AesConfigService;

	@Override
	public Map<String, String> encrypt(String key, String iv, int tagSize, String data) {
		Map<String, String> reply = new HashMap<String, String>();
		try {
			byte[] encrypted = Crypto.encrypt(tagSize, CryptoUtil.HexStrToByteArray(key),
					CryptoUtil.HexStrToByteArray(iv), null, CryptoUtil.HexStrToByteArray(data), AesConfigService.getLibrary());
			reply.put(Constants.CIPHERTEXT, CryptoUtil.ByteArrayToHexStr(encrypted));
			reply.put(Constants.OPERATION_RESULT, Constants.SUCCESS);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Cryptographic exception during encryption");
		} catch (ParseException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Parse exception");
		} catch (IllegalStateException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			String parameters = Crypto.getConfig(tagSize, key, iv, AesConfigService.getLibrary());
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Incorrect parameter during encryption without crypto channel. " + parameters + "\n" + e.getMessage());
		}
		return reply;
	}

	@Override
	public Map<String, String> decrypt(String key, String iv, int tagSize, String data) {
		Map<String, String> reply = new HashMap<String, String>();
		try {
			byte[] decrypted = Crypto.decrypt(tagSize, CryptoUtil.HexStrToByteArray(key),
					CryptoUtil.HexStrToByteArray(iv), null, CryptoUtil.HexStrToByteArray(data), AesConfigService.getLibrary());
			reply.put(Constants.PLAINTEXT, CryptoUtil.ByteArrayToHexStr(decrypted));
			reply.put(Constants.OPERATION_RESULT, Constants.SUCCESS);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Cryptographic exception during decryption");
		} catch (ParseException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Parse exception");
		} catch (IllegalStateException e) {
			reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
			String parameters = Crypto.getConfig(tagSize, key, iv, AesConfigService.getLibrary());
			loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Incorrect parameter during decryption without crypto channel." + parameters + "\n" + e.getMessage());
		}
		return reply;
	}

	@Override
	public Map<String, String> encryptWithCC(String sessionId, String data) {
		Map<String, String> reply = new HashMap<String, String>();
		
		CryptoChannelStateCache cache = cryptoChannelService.findBySessionId(sessionId);

		if (cache != null) {
			try {
				CryptoChannel cc = new CryptoChannel(cache.getKeyServerToComponent(), cache.getKeyComponentToServer(),
						cache.getIvServerToComponent(), cache.getIvComponentToServer(), cache.getTagLen(), cache.getProvider());
				byte[] encrypted = cc.encrypt(null, CryptoUtil.HexStrToByteArray(data));
				reply.put(Constants.CIPHERTEXT, CryptoUtil.ByteArrayToHexStr(encrypted));
				reply.put(Constants.OPERATION_RESULT, Constants.SUCCESS);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Cryptographic exception during encryption");
			} catch (ParseException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Parse exception");
			} catch (IllegalStateException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Incorrect parameter during encryption with crypto channel. " + cache.toString() + "\n" + e.getMessage());
			}
		}
		return reply;
	}

	@Override
	public Map<String, String> decryptWithCC(String sessionId, String data) {
		Map<String, String> reply = new HashMap<String, String>();
		CryptoChannelStateCache cache = cryptoChannelService.findBySessionId(sessionId);
		if (cache != null) {
			try {
				CryptoChannel cc = new CryptoChannel(cache.getKeyServerToComponent(), cache.getKeyComponentToServer(),
						cache.getIvServerToComponent(), cache.getIvComponentToServer(), cache.getTagLen(), cache.getProvider());
				byte[] decrypted = cc.decrypt(null, CryptoUtil.HexStrToByteArray(data));
				reply.put(Constants.PLAINTEXT, CryptoUtil.ByteArrayToHexStr(decrypted));
				reply.put(Constants.OPERATION_RESULT, Constants.SUCCESS);
			} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException
					| InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Cryptographic exception during decryption");
			} catch (ParseException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Parse exception");
			} catch (IllegalStateException e) {
				reply.put(Constants.OPERATION_RESULT, Constants.FAILURE);
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Incorrect parameter during decryption with crypto channel. " + cache.toString() + "\n" + e.getMessage());
			}
		}
		return reply;
	}

	@Override
	public boolean registerCC(String sessionId, String transactionId, String provider, int tagLen, String keyServerToComponent,
			String keyComponentToServer, String ivServerToComponent, String ivComponentToServer, long lifespan) {
		boolean result = false;
		if (cryptoChannelService.findBySessionId(sessionId) == null) {
			try {
				CryptoChannelStateCache cache = new CryptoChannelStateCache();
				cache.setIvComponentToServer(CryptoUtil.HexStrToByteArray(ivComponentToServer));
				cache.setIvServerToComponent(CryptoUtil.HexStrToByteArray(ivServerToComponent));
				cache.setProvider(AesConfigService.getLibrary());
				cache.setTagLen(tagLen);
				cache.setKeyComponentToServer(CryptoUtil.HexStrToByteArray(keyComponentToServer));
				cache.setKeyServerToComponent(CryptoUtil.HexStrToByteArray(keyServerToComponent));
				cryptoChannelService.save(sessionId, cache, lifespan);
				result = true;
			} catch (ParseException e) {
				loggingService.saveLogging(Level.ERROR, "CRYPTO_INTEGRATION", "SISTEMA", "Parse exception during crypto channel registration");
			}
		}
		return result;
	}

	@Override
	public boolean unregisterCC(String sessionId) {
		boolean result = false;
		if (cryptoChannelService.findBySessionId(sessionId) != null) {
			cryptoChannelService.remove(sessionId);
			result = true;
		}
		return result;
	}

	@Override
	public void releaseDecryptCCVolatile(String sessionId) {
		cryptoManager.remove(sessionId);
	}

	@Override
	public boolean saveCryptoChannel(String sessionId, String transactionId) {
		boolean result = false;
		CryptoChannelStateCache cryptoChannelCache = cryptoChannelService.findBySessionId(sessionId);
		if (cryptoChannelCache != null) {
			DebugCryptoChannelDTO dto = new DebugCryptoChannelDTO();
			dto.setIvComponentToServer(CryptoUtil.ByteArrayToHexStr(cryptoChannelCache.getIvComponentToServer()));
			dto.setIvServerToComponent(CryptoUtil.ByteArrayToHexStr(cryptoChannelCache.getIvServerToComponent()));
			dto.setProvider(cryptoChannelCache.getProvider());
			dto.setTagLen(cryptoChannelCache.getTagLen());
			dto.setKeyComponentToServer(CryptoUtil.ByteArrayToHexStr(cryptoChannelCache.getKeyComponentToServer()));
			dto.setKeyServerToComponent(CryptoUtil.ByteArrayToHexStr(cryptoChannelCache.getKeyServerToComponent()));
			dto.setSessionId(sessionId);
			dto.setTransactionId(transactionId);
			debugCryptoChannelProducerService.produce(dto);
			result = true;
		}
		return result;
	}
	
}
