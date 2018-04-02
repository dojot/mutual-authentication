package br.com.dojot.mutualauthentication.kerberoslib.encode;

import java.util.HashMap;

public interface Encoder {
	public static final int PRINCIPAL_NAME_LENGTH = 16;
	public static final int PRINCIPAL_NAME_FIXED_LENGTH = PRINCIPAL_NAME_LENGTH;
	public static final int PRINCIPAL_NAME_RANDOM_LENGTH = 0;
	public static final int NONCE_LENGTH = 4;
	public static final int KEY_LENGTH = 16;
	public static final int IV_LENGTH = 12;
	public static final int TIME_LENGTH = 8;
	public static final int SEC_LENGTH = 4;
	public static final int MESSAGE_CODE_LENGTH = 1;
	public static final int ERROR_CODE_LENGTH = 1;

	public static final String AUTHENTICATOR = "authenticator";
	public static final String CNAME = "cname";
	public static final String CUSEC = "cusec";
	public static final String CTIME = "ctime";
	public static final String AUTHTIME = "authtime";
	public static final String ENDTIME = "endtime";
	public static final String TICKET = "ticket";
	public static final String ENCRYPTED_DATA = "encryptedData";
	public static final String SNAME = "sname";
	public static final String NONCE = "nonce";
	public static final String IV = "iv";
	public static final String CIPHERTEXT = "ciphertext";
	public static final String SESSION_KEY = "sessionKey";
	public static final String KEY_CS = "keyCS";
	public static final String KEY_SC = "keySC";
	public static final String IV_CS = "ivCS";
	public static final String IV_SC = "ivSC";
	public static final String ERROR_CODE = "error";

	public byte[] getEncoded();

	public int getEncodedLength();

	public HashMap<String, Object> getDecoded();
}
