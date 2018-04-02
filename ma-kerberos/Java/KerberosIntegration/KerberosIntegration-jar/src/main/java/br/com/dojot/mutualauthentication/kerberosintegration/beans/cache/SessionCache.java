package br.com.dojot.mutualauthentication.kerberosintegration.beans.cache;

public class SessionCache {
	public enum State {KERBEROS_WAIT_REQUEST_AS, KERBEROS_PROCESS_REQUEST_AS, KERBEROS_WAIT_REQUEST_AP, KERBEROS_PROCESS_REQUEST_AP, KERBEROS_COMPLETED, KERBEROS_NOT_COMPLETED}; 
	
	private State state;
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
}
