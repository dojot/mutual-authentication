package br.com.dojot.mutualauthentication.cryptointegration.messaging.api;

import javax.ejb.Local;

@Local
public interface ReleaseDecryptConsumerService {
	
	void init();
	
	void start();
}
