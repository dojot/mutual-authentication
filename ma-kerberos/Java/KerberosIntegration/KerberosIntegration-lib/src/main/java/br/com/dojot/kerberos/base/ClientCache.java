package br.com.dojot.kerberos.base;

import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientCache provides functionality to add, remove and query
 * Kerberos entries that refer solely to the flash components.
 */
public final class ClientCache implements Cache {
	private static final ClientCache cache = new ClientCache();
	private ConcurrentHashMap<ByteBuffer, PrincipalEntry> app;
	
	private ClientCache()
	{
		this.app = new ConcurrentHashMap<ByteBuffer, PrincipalEntry>();
	}
	
	public static synchronized ClientCache getInstance() {   
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
		boolean isValid;
		ByteBuffer bid;
		
		if(id == null) {
			throw new NullPointerException();
		}
		
		isValid = false;
		bid = ByteBuffer.wrap(id.clone());
		if(app.containsKey(bid)) {
			isValid = true;
		}
		
		return isValid;
	}
	
	
	public PrincipalEntry getPrincipal(byte[] id) 
	{
		ByteBuffer bid;
		
		if(id == null) {
			throw new NullPointerException();
		}
		
		bid = ByteBuffer.wrap(id.clone());
		return app.get(bid);
	}
}
