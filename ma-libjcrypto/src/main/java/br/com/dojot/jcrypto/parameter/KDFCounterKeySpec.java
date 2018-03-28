package br.com.dojot.jcrypto.parameter;

import java.security.spec.KeySpec;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class KDFCounterKeySpec implements KeySpec {
	/**
	 *	A string that identifies the purpose for the derived keying material, which
	 *	is encoded as a binary string. The encoding method for the Label is defined 
	 *  in a larger context, for example, in the protocol that uses a KDF.
	 */
	private byte[] label;
	
	/**
	 * A binary string containing the in formation related to the derived keying
	 * material. It may include identities of parties who are deriving and/or using the
	 * derived keying material and, optionally, a nonce known by the parties who derive
	 * the keys. 
	 */
	private byte[] context;
	
	/**
	 * A key that is used as an input to a key derivation function
	 * (along with other input data) to derive keying material.
	 */
	private byte[] ki;
	
	/**
	 * An integer specifying the length (in bits) of the derived keying material
	 * KO. L is represented as a binary string when it is an input to a key derivation
	 * function. The length of the binary string is specified by the encoding method 
	 * for the input data. 
	 */
	private int l;
	
	/** Name of the algorithm where the key will be used. */
	private String algorithm;
	
	/** Mac to be used within the KDF. */
	private Mac mac;
	
	public byte[] getLabel() {
		return label;
	}

	public void setLabel(byte[] label) {
		this.label = label;
	}

	public byte[] getContext() {
		return context;
	}

	public void setContext(byte[] context) {
		this.context = context;
	}

	public byte[] getKi() {
		return ki;
	}

	public void setKi(byte[] ki) {
		this.ki = ki;
	}

	public int getL() {
		return l;
	}

	public void setL(int l) {
		this.l = l;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public Mac getMac() {
		return mac;
	}

	public void setMac(Mac mac) {
		this.mac = mac;
	}

}
