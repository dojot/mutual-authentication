package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

@Local
public interface LoggingService {
	
	public enum Level { INFO, WARNING, ERROR };
	
	void saveLogging(Level level, String component, String username, String details);
}
