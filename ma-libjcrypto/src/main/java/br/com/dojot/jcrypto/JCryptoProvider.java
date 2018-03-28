package br.com.dojot.jcrypto;
import java.security.Provider;


public class JCryptoProvider extends Provider{

	private static final long serialVersionUID = -7586620853705935037L;

	public JCryptoProvider() {
		super("JCrypto", 1.0, "JCrypto Provider");
		/* Algorithms provided by this cryptographic provider */
		put("Cipher.AES/ECB/NoPadding", "br.com.dojot.jcrypto.AES_ECB");
		put("Cipher.AES/GCM/NoPadding", "br.com.dojot.jcrypto.AES_GCM");
		put("Mac.CMAC", "br.com.dojot.jcrypto.mac.Cmac");
		put("SecretKeyFactory.KDFCounter", "br.com.dojot.jcrypto.kdf.KDFCounterMode");
		put("MessageDigest.SHA-512", "br.com.dojot.jcrypto.hash.SHA512");
		put("Mac.HMACSHA512", "br.com.dojot.jcrypto.mac.HmacWithSHA512");
		put("SecretKeyFactory.PBKDF2withHMACSHA512", "br.com.dojot.jcrypto.factory.PBKDF2");
	}

	static {
		/* libjcrypto.so must be at the java.library.path */
		System.load("/home/kerberos/libjcrypto.so");
	}
}
