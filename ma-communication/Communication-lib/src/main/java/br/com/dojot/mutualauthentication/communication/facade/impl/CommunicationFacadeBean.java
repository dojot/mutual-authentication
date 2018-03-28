package br.com.dojot.mutualauthentication.communication.facade.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import br.com.dojot.mutualauthentication.communication.exceptions.InvalidResponseException;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.service.api.GETService;
import br.com.dojot.mutualauthentication.communication.service.api.KafkaBrokersService;
import br.com.dojot.mutualauthentication.communication.service.api.NodeConfigsService;
import br.com.dojot.mutualauthentication.communication.service.api.POSTService;
import br.com.dojot.mutualauthentication.communication.service.api.RedisSentinelService;
import br.com.dojot.mutualauthentication.communication.service.impl.GETServiceBean;
import br.com.dojot.mutualauthentication.communication.service.impl.KafkaBrokersServiceBean;
import br.com.dojot.mutualauthentication.communication.service.impl.NodeConfigsServiceBean;
import br.com.dojot.mutualauthentication.communication.service.impl.POSTServiceBean;
import br.com.dojot.mutualauthentication.communication.service.impl.RedisSentinelServiceBean;

public class CommunicationFacadeBean implements CommunicationFacade {
	@Override
	public String requestKafkaBrokers() {
		KafkaBrokersService kafkaBrokersService = new KafkaBrokersServiceBean();
		return kafkaBrokersService.requestKafkaBrokers();
	}
	
	@Override
	public Set<String> requestRedisSentinelsHosts() {
		RedisSentinelService redisSentinelService = new RedisSentinelServiceBean();
		return redisSentinelService.requestRedisSentinelsHosts();
	}

	@Override
	public LinkedHashMap<String, Object> requestNodeConfigs() {
		NodeConfigsService nodeConfigService = new NodeConfigsServiceBean();
		return nodeConfigService.requestNodeConfigs();
	}

	@Override
	public String get(String server, String path) throws InvalidResponseException {
		GETService getService = new GETServiceBean();
		return getService.get(server, path);
	}
	
	@Override
	public String get(String target, String find, LinkedHashMap<String, Object> microservice) throws InvalidResponseException {
		JSONObject service = new JSONObject(post(target, find, microservice));
		return get(service.getString("target"), service.getString("path"));
	}

	@Override
	public String post(String server, String path, LinkedHashMap<String, Object> parameters) throws InvalidResponseException {
		POSTService postService = new POSTServiceBean();
		return postService.post(server, path, parameters);
	}

	@Override
	public String post(String target, String find, LinkedHashMap<String, Object> microservice, LinkedHashMap<String, Object> parameters) throws InvalidResponseException {
		JSONObject service = new JSONObject(post(target, find, microservice));
		return post(service.getString("target"), service.getString("path"), parameters);
	}
}
