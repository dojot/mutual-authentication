package br.com.dojot.mutualauthentication.kerberosintegration.service.impl;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONObject;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.cache.ComponentCache;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.to.CryptoReplyTO;
import br.com.dojot.mutualauthentication.kerberoslib.encode.Authenticator;
import br.com.dojot.mutualauthentication.kerberoslib.encode.EncKdcRepPart;
import br.com.dojot.mutualauthentication.kerberoslib.encode.EncTicketPart;
import br.com.dojot.mutualauthentication.kerberoslib.encode.Encoder;
import br.com.dojot.mutualauthentication.kerberoslib.encode.EncryptedData;
import br.com.dojot.mutualauthentication.kerberoslib.encode.Error;
import br.com.dojot.mutualauthentication.kerberoslib.encode.ErrorCode;
import br.com.dojot.mutualauthentication.kerberoslib.encode.ReplyAP;
import br.com.dojot.mutualauthentication.kerberoslib.encode.ReplyAS;
import br.com.dojot.mutualauthentication.kerberoslib.encode.RequestAP;
import br.com.dojot.mutualauthentication.kerberoslib.encode.RequestAS;
import br.com.dojot.mutualauthentication.kerberoslib.encode.SessionKey;
import br.com.dojot.mutualauthentication.kerberoslib.encode.Ticket;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.AuthenticatorRepeatException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.BadMatchException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ClientUnknownException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ClockSkewException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ExpiredTicketException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.IntegrityException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidMessageException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ParseException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.SecureEraseException;
import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ServerUnknownException;
import br.com.dojot.mutualauthentication.kerberoslib.util.Crypto;
import br.com.dojot.mutualauthentication.kerberoslib.util.CryptoUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.SecureUtil;
import br.com.dojot.mutualauthentication.kerberoslib.util.Time;
import br.com.dojot.mutualauthentication.kerberosintegration.exception.GenericException;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ComponentService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ComponentsService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ConfigService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.CryptoIntegrationService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.KerberosProtocolService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.ReplayService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.SessionService;
import br.com.dojot.mutualauthentication.kerberosintegration.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.Utils;

@Stateless
public class KerberosProtocolServiceImpl implements KerberosProtocolService {

	@EJB
	private LoggingService loggingService;

	@EJB
	private ComponentService componentService;
	
	@EJB
	private ComponentsService componentsService;

	@EJB
	private ReplayService replayService;

	@EJB
	private SessionService sessionService;

	@EJB
	private ConfigService configService;

	@EJB
	private CryptoIntegrationService cryptoIntegrationService;

	public static final String CHANNEL = "channel";
	public static final String REPLY_FOR_EXTERNAL_APPLICATION = "error";
	public static final String REPLY_FOR_CLIENT_KERBEROS = "reply";

	/* Size of the tag in use for all protocol exchange messages */
	private static final int TAG_LEN = 128;
	/* Clock skew in ms */
	public static final int CLOCK_SKEW = 300000;
	
	@Override
	public String process(String json)
	{
		JSONObject jsonObject = new JSONObject(json);
		
		String sessionId = Utils.get(jsonObject.get("sessionId"));
		String transactionId = Utils.get(jsonObject.get("transactionId"));
		String encodedInput = Utils.get(jsonObject.get("request"));
		Boolean skipValidation = Utils.get(jsonObject.get("skipValidation"));
		Integer step = Utils.get(jsonObject.get("step"));
		
		Map<String, Object> reply = new HashMap<>();
		switch(step) {
			case KerberosIntegrationConstants.KERBEROS_REQUEST_AS:
				reply = processRequestAS(skipValidation, sessionId, transactionId, encodedInput);
				break;
			case KerberosIntegrationConstants.KERBEROS_REQUEST_AP:
				reply = processRequestAP(skipValidation, sessionId, transactionId, encodedInput);
				break;
		}
		JSONObject out = new JSONObject();
		for(Map.Entry<String, Object> entry : reply.entrySet()) {
			out.put(entry.getKey(), entry.getValue());
		}
		return out.toString();
	}

	/**
	 * Process the first kerberos message and generates the second message from
	 * the protocol.
	 * 
	 * @param encodedByteInput
	 *            Encoded version of the first Kerberos message
	 * @return Encoded version of the second Kerberos message
	 * @throws InvalidMessageException
	 *             Message was not properly encoded
	 * @throws ClientUnknownException
	 *             Client doesn't exist on the database
	 * @throws ServerUnknownException
	 *             Server doesn't exist on the database
	 */
	public Map<String, Object> processRequestAS(boolean skipValidation, String sessionId, String transactionId, String encodedInput) {
		HashMap<String, Object> reply = new HashMap<>();
		try {
			String kerberosSessionId = Utils.generateKerberosSessionId(sessionId, transactionId);
			byte[] encodedByteInput = CryptoUtil.HexStrToByteArray(encodedInput);
			if (encodedInput == null || encodedByteInput.length == 0) {
				Error error = new Error(ErrorCode.INVALID_MESSAGE);
				reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
				reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
				loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Recebido RequestAS vazio.");
			} else {
				String state = sessionService.get(kerberosSessionId);
				if (state != null && state.equals(KerberosIntegrationConstants.KERBEROS_WAIT_REQUEST_AS)) {
					sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_PROCESS_REQUEST_AS, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
					HashMap<String, Object> decoded = verifyAndDecodeRequestAS(encodedByteInput, reply);
					generateReplyAS(skipValidation, kerberosSessionId, decoded, reply);
					sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_WAIT_REQUEST_AP, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
				} else {
					Error error = new Error(ErrorCode.INVALID_MESSAGE);
					reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
					reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
					sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_NOT_COMPLETED, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
					loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Recebido RequestAS sem sessao ativa.");
				}
			}
		} catch (GenericException | SecureEraseException  e) {
			// These errors is not related to the protocol itself, but something went wrong in the application
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		} catch (InvalidEventException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		} catch (InvalidMessageException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Message has invalid format");
		} catch (ClientUnknownException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Unknown client");
		} catch (ServerUnknownException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Unknown server");
		} catch (ParseException e) {
			Error error = new Error(ErrorCode.INVALID_MESSAGE);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Parse error");
		} catch (NullPointerException e) {
			Error error = new Error(ErrorCode.INVALID_MESSAGE);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		}
		String encodedClientReply = CryptoUtil
				.ByteArrayToHexStr((byte[]) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS));
		reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, encodedClientReply);
		return reply;
	}

	/**
	 * Process the third message from Kerberos protocol and generates the fourth
	 * message.
	 * 
	 * @param encodedByteInput
	 *            Encoded version of the third Kerberos message
	 * @param key
	 *            Shared key between Kerberos service and server application
	 * @return Encoded version of the fourth message of Kerberos protocol
	 * @throws InvalidMessageException
	 *             Message was not properly encoded
	 * @throws ServerUnknownException
	 *             Server doesn't exist on the database
	 * @throws ClockSkewException
	 *             Clock skew is too great
	 * @throws BadMatchException
	 *             Ticket and authenticator don't match
	 * @throws ExpiredTicketException
	 *             Ticket is expired
	 * @throws AuthenticatorRepeatException
	 *             Third message is a replay
	 * @throws IntegrityException
	 *             Message integrity was compromised
	 */
	public Map<String, Object> processRequestAP(boolean skipValidation, String sessionId, String transactionId, String encodedInput) {
		HashMap<String, Object> decoded, reply;
		String kerberosSessionId = Utils.generateKerberosSessionId(sessionId, transactionId);
		reply = new HashMap<>();
		try {
			byte[] encodedByteInput = CryptoUtil.HexStrToByteArray(encodedInput);
			if (encodedByteInput == null || encodedByteInput.length == 0) {
				reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, new Error(ErrorCode.INVALID_MESSAGE));
				loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Recebido RequestAP vazio.");
			} else {
				String state = sessionService.get(kerberosSessionId);
				if (state != null) {
					if (state.equals(KerberosIntegrationConstants.KERBEROS_WAIT_REQUEST_AP)) {
						sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_PROCESS_REQUEST_AP, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
						decoded = verifyAndDecodeRequestAP(encodedByteInput, reply);
						generateReplyAP(skipValidation, kerberosSessionId, transactionId, decoded, reply);
						sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_COMPLETED, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
						cryptoIntegrationService.saveCryptoChannel(kerberosSessionId, transactionId);
					} else {
						Error error = new Error(ErrorCode.INVALID_MESSAGE);
						reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
						reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
						sessionService.save(kerberosSessionId, KerberosIntegrationConstants.KERBEROS_NOT_COMPLETED, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
						loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Recebido RequestAP sem o correto estado da sessao.");
					}
				} else {
					Error error = new Error(ErrorCode.INVALID_MESSAGE);
					reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
					reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
					loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Recebido RequestAP sem sessao ativa.");
				}
			}
		} catch (InvalidMessageException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Message has invalid format");
		} catch (InvalidEventException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		} catch (GenericException | SecureEraseException e) {
			// These errors is not related to the protocol itself, but something went wrong in the application
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		} catch (ServerUnknownException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Unknown server");
		} catch (ClockSkewException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Clock skew error");
		} catch (BadMatchException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Bad match exception");
		} catch (ExpiredTicketException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Expired ticket");
		} catch (AuthenticatorRepeatException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Repeated authenticator");
		} catch (IntegrityException e) {
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Integrity exception");
		} catch (ParseException e) {
			Error error = new Error(ErrorCode.INVALID_MESSAGE);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", "Parse exception");
		} catch (NullPointerException e) {
			Error error = new Error(ErrorCode.INVALID_MESSAGE);
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(KerberosProtocolServiceImpl.REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			loggingService.saveLogging(Level.ERROR, "KERBEROS_INTEGRATION", "SISTEMA", e.getMessage());
		}
		String encodedClientReply = CryptoUtil
				.ByteArrayToHexStr((byte[]) reply.get(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS));
		reply.put(KerberosProtocolServiceImpl.REPLY_FOR_CLIENT_KERBEROS, encodedClientReply);
		return reply;
	}

	private HashMap<String, Object> verifyAndDecodeRequestAS(byte[] request, Map<String, Object> reply)
			throws InvalidMessageException {
		HashMap<String, Object> decoded = null;
		try {
			RequestAS requestAS = new RequestAS(request);
			decoded = requestAS.getDecoded();
		} catch (IllegalArgumentException e) {
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new InvalidMessageException();
		}
		return decoded;
	}

	private HashMap<String, Object> verifyAndDecodeRequestAP(byte[] request, Map<String, Object> reply)
			throws InvalidMessageException {
		HashMap<String, Object> decoded = null;
		try {
			RequestAP requestAP = new RequestAP(request);
			decoded = requestAP.getDecoded();
		} catch (IllegalArgumentException e) {
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new InvalidMessageException();
		}
		return decoded;
	}

	private void generateReplyAS(boolean skipValidation, String sessionId, Map<String, Object> decoded, Map<String, Object> reply)
			throws ClientUnknownException, ServerUnknownException, ParseException, GenericException {
		byte[] cname;
		byte[] sname;
		byte[] nonce;

		/* Get the relevant fields from the RequestAS */
		cname = (byte[]) decoded.get(Encoder.CNAME);
		sname = (byte[]) decoded.get(Encoder.SNAME);
		nonce = (byte[]) decoded.get(Encoder.NONCE);
		/* Get the entry that refers to the client application */
		/* Only PRINCIPAL_NAME_FIXED_LENGTH was inserted in the database */
		byte[] fixedCname = Arrays.copyOf(cname, Encoder.PRINCIPAL_NAME_FIXED_LENGTH);
		ComponentCache client = componentService.get(CryptoUtil.ByteArrayToHexStr(fixedCname));
		if (client == null) {
			Error error = new Error(ErrorCode.KDC_ERR_C_PRINCIPAL_UNKNOWN);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ClientUnknownException();
		}
		/* Get the entry that refers to the server application */
		if (configService.findConfigurationByKey(KerberosIntegrationConstants.SERVER_NAME)
				.contentEquals(CryptoUtil.ByteArrayToHexStr(sname))) {
			Error error = new Error(ErrorCode.KDC_ERR_S_PRINCIPAL_UNKNOWN);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ServerUnknownException();
		}
		/* Ticket validity period */
		double period = skipValidation ? KerberosIntegrationConstants.TEST_EXPIRATION_INTERVAL : client.getTicketValidityPeriod();

		/* Generate random session keys and random IVs */
		SessionKey sk = generateSessionKeys(reply);

		/* Generates the encrypted part of the ticket */
		byte[] authtime = Time.getCurrentUTCTime();
		byte[] endtime = Time.getUTCWithOffset(authtime, period);
		EncTicketPart ticketPart = new EncTicketPart(sk, cname, authtime, endtime);
		
        /* Encrypts the protected part of the ticket */
		byte[] iv, ciphertext;
		iv = Crypto.getRandomBytes(Encoder.IV_LENGTH);
		CryptoReplyTO encTo = cryptoIntegrationService.encrypt(sessionId,
				CryptoUtil.HexStrToByteArray(configService.findConfigurationByKey(KerberosIntegrationConstants.SERVER_KEY)), iv,
				TAG_LEN, ticketPart.getEncoded());
		if (encTo.getResult().equalsIgnoreCase("SUCCESS")) {
			ciphertext = CryptoUtil.HexStrToByteArray(encTo.getData());
		} else {
			Error error = new Error(ErrorCode.UNKNOWN_ERROR);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			/*
			 * These exceptions are being grouped because they should never
			 * occur. If any of them happens for some unknown reason, then throw
			 * a InvalidEventException with the original exception as its cause.
			 */
			throw new InvalidEventException("Failed to encrypt using the kerberos server key.");
		}
		EncryptedData encTicketPart = new EncryptedData(iv, ciphertext);

		/* Generates the ticket */
		Ticket ticket = new Ticket(sname, encTicketPart);

		/* Generates the encrypted part of the reply */
		EncKdcRepPart encKdcRepPart = new EncKdcRepPart(sk, sname, nonce, authtime, endtime);

		/* Encrypts the encrypted part of the reply */
		iv = Crypto.getRandomBytes(Encoder.IV_LENGTH);
		encTo = cryptoIntegrationService.encrypt(sessionId, client.getKey(), iv, TAG_LEN, encKdcRepPart.getEncoded());
		if (encTo.getResult().equalsIgnoreCase("SUCCESS")) {
			ciphertext = CryptoUtil.HexStrToByteArray(encTo.getData());
		} else {
			Error error = new Error(ErrorCode.UNKNOWN_ERROR);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			/*
			 * These exceptions are being grouped because they should never
			 * occur. If any of them happens for some unknown reason, then throw
			 * a InvalidEventException with the original exception as its cause.
			 */
			throw new InvalidEventException("Failed to encrypt using the component key.");
		}
		EncryptedData encData = new EncryptedData(iv, ciphertext);
		/* Generates the reply */
		ReplyAS replyAS = new ReplyAS(cname, ticket, encData);
		Error error = new Error(ErrorCode.KDC_ERR_NONE);
		reply.put(REPLY_FOR_CLIENT_KERBEROS, replyAS.getEncoded());
		reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
	}

	private void generateReplyAP(boolean skipValidation, String sessionId, String transactionId, Map<String, Object> decoded, Map<String, Object> reply)
			throws ServerUnknownException, ClockSkewException, BadMatchException, ExpiredTicketException,
			AuthenticatorRepeatException, InvalidMessageException, IntegrityException, ParseException, GenericException {
		Ticket ticket = (Ticket) decoded.get(Encoder.TICKET);

		EncryptedData encAuth = (EncryptedData) decoded.get(Encoder.ENCRYPTED_DATA);

		/* Decrypts the encrypted part of the ticket */
		HashMap<String, Object> ticketDecoded = ticket.getDecoded();
		EncryptedData encData = (EncryptedData) ticketDecoded.get(Encoder.ENCRYPTED_DATA);

		/* Get the entry that refers to the server application */
		byte[] sname = (byte[]) ticketDecoded.get(Encoder.SNAME);

		/* Get the entry that refers to the server application */
		if (configService.findConfigurationByKey(KerberosIntegrationConstants.SERVER_NAME)
				.contentEquals(CryptoUtil.ByteArrayToHexStr(sname))) {
			Error error = new Error(ErrorCode.KDC_ERR_S_PRINCIPAL_UNKNOWN);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ServerUnknownException();
		}

		HashMap<String, Object> encDataDecoded = encData.getDecoded();

		byte[] iv = (byte[]) encDataDecoded.get(Encoder.IV);
		byte[] ciphertext = (byte[]) encData.getDecoded().get(Encoder.CIPHERTEXT);
		byte[] plaintext = null;

		CryptoReplyTO decTo = cryptoIntegrationService.decrypt(sessionId,
				CryptoUtil.HexStrToByteArray(configService.findConfigurationByKey(KerberosIntegrationConstants.SERVER_KEY)), iv,
				TAG_LEN, ciphertext);
		if (decTo.getResult().equalsIgnoreCase("SUCCESS")) {
			plaintext = CryptoUtil.HexStrToByteArray(decTo.getData());
		} else {
			Error error = new Error(ErrorCode.KRB_AP_ERR_BAD_INTEGRITY);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			/*
			 * These exceptions are being grouped because they should never
			 * occur. If any of them happens for some unknown reason, then throw
			 * a InvalidEventException with the original exception as its cause.
			 */
			throw new InvalidEventException("Could not decrypt the data received from the client using kerberos' key.");
		}

		EncTicketPart encTicketPart = new EncTicketPart(plaintext);
		HashMap<String, Object> encTicketPartDecoded = encTicketPart.getDecoded();

		/* Get all the session parameters */
		SessionKey sk = (SessionKey) encTicketPartDecoded.get(Encoder.SESSION_KEY);
		HashMap<String, Object> decodedSK = sk.getDecoded();
		byte[] keyCS, keySC, ivCS, ivSC;

		keyCS = (byte[]) decodedSK.get(Encoder.KEY_CS);
		keySC = (byte[]) decodedSK.get(Encoder.KEY_SC);
		ivCS = (byte[]) decodedSK.get(Encoder.IV_CS);
		ivSC = (byte[]) decodedSK.get(Encoder.IV_SC);

		boolean result = cryptoIntegrationService.registerCryptoChannel(sessionId, transactionId, "JCrypto", keySC, ivSC, keyCS, ivCS,
				TAG_LEN, KerberosIntegrationConstants.DEFAULT_EXPIRATION_INTERVAL);
		if (!result) {
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new InvalidEventException("Could not register the crypto channel for the specified client.");
		}

		/* Check authenticator timestamp against local time */
		byte[] authDecoded = null;
		ciphertext = (byte[]) encAuth.getDecoded().get(Encoder.CIPHERTEXT);
		decTo = cryptoIntegrationService.decryptWithCC(sessionId, ciphertext);
		if (decTo.getResult().equalsIgnoreCase("SUCCESS")) {
			authDecoded = CryptoUtil.HexStrToByteArray(decTo.getData());
		} else {
			Error error = new Error(ErrorCode.CC_AUTH_FAIL);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			/*
			 * These exceptions are being grouped because they should never
			 * occur. If any of them happens for some unknown reason, then throw
			 * a InvalidEventException with the original exception as its cause.
			 */
			throw new InvalidEventException("Could not decrypt using the crypto channel previously registered.");
		}

		Authenticator authenticator = new Authenticator(authDecoded);
		HashMap<String, Object> decodedAuthenticator = authenticator.getDecoded();

		/*
		 * Protocol doesn't need to keep a replay cache because its state
		 * machine doesn't allow the reception of kerberos messages out of
		 * order.
		 */

		/*
		 * Check if cname in the authenticator is the same present in the ticket
		 */
		byte[] cnameAuth = (byte[]) decodedAuthenticator.get(Encoder.CNAME);
		byte[] cnameTicket = (byte[]) encTicketPartDecoded.get(Encoder.CNAME);
		if (!skipValidation && !CryptoUtil.compareArrayToArrayDiffConstant(cnameAuth, cnameTicket)) {
			/* cnames don't match */
			Error error = new Error(ErrorCode.KRB_AP_ERR_BADMATCH);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new BadMatchException();
		}

		byte[] currentTime = Time.getCurrentUTCTime();
		/* Check if start time is inside clock skew of 5 minutes */
		byte[] authtime = (byte[]) encTicketPartDecoded.get(Encoder.AUTHTIME);
		if (!skipValidation && (Time.order(authtime, currentTime) > CLOCK_SKEW)) {
			/* hosts aren't loosely synchronized */
			Error error = new Error(ErrorCode.KRB_AP_ERR_SKEW);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ClockSkewException();
		}

		/* Check if ticket still in validity period */
		byte[] endtime = (byte[]) encTicketPartDecoded.get(Encoder.ENDTIME);
		if (!skipValidation && (Time.order(currentTime, endtime) > CLOCK_SKEW)) {
			/* Ticket already expired */
			Error error = new Error(ErrorCode.KRB_AP_ERR_TKT_EXPIRED);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ExpiredTicketException();
		}

		/* Check if authenticator time is inside clock skew of 5 minutes */
		if (!skipValidation && (Math.abs(Time.order(currentTime, (byte[]) decodedAuthenticator.get(Encoder.CTIME))) > CLOCK_SKEW)) {
			/* Authenticator time is invalid */
			Error error = new Error(ErrorCode.KRB_AP_ERR_SKEW);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new ClockSkewException();
		}

		/* Check if authenticator is not a replay. */
		String lastAuthTime = replayService.get(CryptoUtil.ByteArrayToHexStr(cnameAuth));
		String authTimeString = new String(authtime);
		if (!skipValidation && lastAuthTime != null && lastAuthTime.equals(authTimeString)) {
			Error error = new Error(ErrorCode.KRB_AP_ERR_REPEAT);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new AuthenticatorRepeatException();
		} else {
			/* If it is not a replay we add a new entry to the reply cache. */
			replayService.save(CryptoUtil.ByteArrayToHexStr(cnameAuth), CryptoUtil.ByteArrayToHexStr(authtime));
		}

		/* Generate a reply with only the authentication timestamp */
		byte[] authtimestamp = (byte[]) decodedAuthenticator.get(Encoder.CTIME);
		byte[] encryptedAuthTime;

		decTo = cryptoIntegrationService.encryptWithCC(sessionId, authtimestamp);
		if (decTo.getResult().equalsIgnoreCase("SUCCESS")) {
			encryptedAuthTime = CryptoUtil.HexStrToByteArray(decTo.getData());
		} else {
			Error error = new Error(ErrorCode.UNKNOWN_ERROR);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			/*
			 * These exceptions are being grouped because they should never
			 * occur. If any of them happens for some unknown reason, then throw
			 * a InvalidEventException with the original exception as its cause.
			 */
			throw new InvalidEventException("Failed to encrypt using the newly created crypto channel.");
		}

		EncryptedData encReplyAP = new EncryptedData(ivSC, encryptedAuthTime);
		ReplyAP replyAP = new ReplyAP(encReplyAP);

		Error error = new Error(ErrorCode.KDC_ERR_NONE);
		reply.put(REPLY_FOR_CLIENT_KERBEROS, replyAP.getEncoded());
		reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
	}

	private SessionKey generateSessionKeys(Map<String, Object> reply) {
		byte[] keyCS, keySC;
		byte[] ivCS, ivSC;

		keyCS = null;
		keySC = null;
		ivCS = new byte[Encoder.IV_LENGTH];
		ivSC = new byte[Encoder.IV_LENGTH];

		KeyGenerator keyGen;
		try {
			keyGen = KeyGenerator.getInstance("AES");
			keyGen.init(Encoder.KEY_LENGTH << 3);
			/* Key creation for both directions */
			SecretKey secretKey;
			secretKey = keyGen.generateKey();
			keyCS = secretKey.getEncoded();
			secretKey = keyGen.generateKey();
			keySC = secretKey.getEncoded();

		} catch (NoSuchAlgorithmException e) {
			Error error = new Error(ErrorCode.KRB_ERR_GENERIC);
			reply.put(REPLY_FOR_CLIENT_KERBEROS, error.getEncoded());
			reply.put(REPLY_FOR_EXTERNAL_APPLICATION, error.getDescription());
			throw new InvalidEventException("Cryptographic algorithm requested not available.", e);
		}

		/* IV creation for both directions */
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(ivCS);
		sr.nextBytes(ivSC);
		SessionKey sk = new SessionKey(keyCS, ivCS, keySC, ivSC);

		SecureUtil.erase(keySC);
		SecureUtil.erase(keyCS);
		SecureUtil.erase(ivSC);
		SecureUtil.erase(ivCS);

		return sk;
	}
}
