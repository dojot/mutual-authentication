package br.com.dojot.kerberos.base;

public class ServerCache implements Cache {
	private static final ServerCache cache = new ServerCache();
	
	private ServerCache() { }
	
	public static synchronized ServerCache getInstance() {   
	    return cache;
	}
	
	@Override
	public void addPrincipal(byte[] id, PrincipalEntry principalEntry) {
		throw new UnsupportedOperationException("Use the command line tool for add new services");
	}

	@Override
	public void removePrincipal(byte[] id) {
		throw new UnsupportedOperationException("Use the command line tool for removing services");
	}

	@Override
	public boolean isValidPrincipal(byte[] id) {
		/* Default implementation, it must be changed */
		return true;
	}

	@Override
	public PrincipalEntry getPrincipal(byte[] id) {
		/* Default implementation, it must be changed */
		byte[] key = new byte[32];
		PrincipalEntry entry = new PrincipalEntry(key, 0);
		return entry;
	}

}
