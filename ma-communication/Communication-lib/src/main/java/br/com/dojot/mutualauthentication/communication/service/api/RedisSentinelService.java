package br.com.dojot.mutualauthentication.communication.service.api;

import java.util.List;
import java.util.Set;

public interface RedisSentinelService {
	
	Set<String> requestRedisSentinelsHosts();
}
