package br.com.dojot.kerberos.base;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DummyCache should  only be used for test purposes.
 * Kerberos entries that refer solely to the flash components.
 */
public final class DummyCache implements Cache {
	private static final DummyCache cache = new DummyCache();
	private ConcurrentHashMap<ByteBuffer, PrincipalEntry> app;
	
	private DummyCache()
	{
		this.app = new ConcurrentHashMap<ByteBuffer, PrincipalEntry>();
	}
	
	public static synchronized DummyCache getInstance() {   
	    return cache;
	}
	
	public void addPrincipal(byte[] id, PrincipalEntry principalEntry)
	{
		ByteBuffer bid;
		if(id == null || principalEntry == null) {
			throw new NullPointerException();
		}
		
		bid = ByteBuffer.wrap(id.clone());
		if(app.containsKey(bid)) {
			app.get(bid).erase();
		}
		app.put(bid, principalEntry.clone());
	}
	
	public void removePrincipal(byte[] id)
	{
		ByteBuffer bid;
		
		if(id == null) {
			throw new NullPointerException();
		}
		
		bid = ByteBuffer.wrap(id.clone());
		if(app.containsKey(bid)) {
			app.get(bid).erase();
		}
	}
	
	
	public boolean isValidPrincipal(byte[] id) 
	{
		return true;
	}
	
	
	public PrincipalEntry getPrincipal(byte[] id) 
	{
		byte[] key = new byte[32];
		Arrays.fill(key, (byte)0x01);
		return new PrincipalEntry(key, 700000000);
	}
}
