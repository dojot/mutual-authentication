package br.com.dojot.mutualauthentication.cryptointegration.service.api;

import javax.ejb.Local;

@Local
public interface LoggingService {

	enum Level { INFO, WARNING, ERROR };
	
	void saveLogging(Level level, String component, String username, String details);
}