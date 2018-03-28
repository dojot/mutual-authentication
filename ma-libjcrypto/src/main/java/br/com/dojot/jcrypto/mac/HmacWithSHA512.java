package br.com.dojot.jcrypto.mac;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import br.com.dojot.jcrypto.hash.SHA512;

public class HmacWithSHA512 extends Hmac {
	public HmacWithSHA512() throws NoSuchAlgorithmException, NoSuchProviderException {
		super("SHA-512", "JCrypto", SHA512.BYTES_BLOCK_LEN);
	}
}
