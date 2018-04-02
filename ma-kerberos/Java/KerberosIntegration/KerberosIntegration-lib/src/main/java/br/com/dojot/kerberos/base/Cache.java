package br.com.dojot.kerberos.base;

public interface Cache {
	
	/**
	 * Include a principal in the database
	 * @param id Identification of the entity
	 * @param principalEntry Entry that contains the key and the validity period
	 */
	public void addPrincipal(byte[] id, PrincipalEntry principalEntry);
	
	/**
	 * Removes the entry associated with the component which has the id specified
	 * @param id binary component name
	 */
	public void removePrincipal(byte[] id);
	
	/**
	 * Check if component with name id exists in database;
	 * @param id binary component name
	 * @return true if component exists in database, false otherwise.
	 */
	public boolean isValidPrincipal(byte[] id);
	
	/**
	 * Get the key and validity period associated with a specific component.
	 * @param id binary component name
	 * @return key and validity period
	 */
	public PrincipalEntry getPrincipal(byte[] id);
}
