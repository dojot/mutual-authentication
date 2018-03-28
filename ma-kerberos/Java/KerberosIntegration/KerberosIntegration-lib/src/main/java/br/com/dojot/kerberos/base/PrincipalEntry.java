package br.com.dojot.kerberos.base;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.InvalidEventException;
import br.com.dojot.mutualauthentication.kerberoslib.util.*;

public final class PrincipalEntry implements Cloneable {
	private byte[] key;
	private double validityPeriod;
	
	/**
	 * Creates an component entry
	 * @param key Cryptographic key to be shared with Kerberos
	 * @param validityPeriod Period of time that a ticket will be valid for this entry
	 */
	public PrincipalEntry(byte[] key, double validityPeriod) 
	{
		if(key == null || validityPeriod < 0) {
			throw new IllegalArgumentException();
		}
		
		this.key = key.clone();
		this.validityPeriod = validityPeriod;
	}

	public byte[] getKey() {
		return key.clone();
	}

	/**
	 * Used to calculate ticket endtime
	 * @return period of time
	 */
	public double getValidityPeriod() {
		return validityPeriod;
	}
	
	public void erase() 
	{
		this.validityPeriod = 0;
		SecureUtil.erase(this.key);
	}
	
	public PrincipalEntry clone() {
		PrincipalEntry principalEntry;
		try {
			principalEntry = (PrincipalEntry) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InvalidEventException();
		}
		return principalEntry;
	}
}
