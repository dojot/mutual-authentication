package br.com.dojot.mutualauthentication.communication.facade.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import br.com.dojot.mutualauthentication.communication.exceptions.InvalidResponseException;

public interface CommunicationFacade {
	
	Set<String> requestRedisSentinelsHosts();
	
	LinkedHashMap<String, Object> requestNodeConfigs();
	
	String get(String server, String path) throws InvalidResponseException;
	
	String get(String target, String find, LinkedHashMap<String, Object> microservice) throws InvalidResponseException;
	
	String post(String server, String path, LinkedHashMap<String, Object> parameters) throws InvalidResponseException;

	String post(String target, String find, LinkedHashMap<String, Object> microservice, LinkedHashMap<String, Object> parameters) throws InvalidResponseException;

	String requestKafkaBrokers();
}
