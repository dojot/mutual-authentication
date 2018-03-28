package br.com.dojot.mutualauthentication.loggingapi.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.loggingapi.service.api.LoggingService;
import br.com.dojot.mutualauthentication.loggingapi.service.api.LoggingService.Level;
import br.com.dojot.mutualauthentication.loggingapi.utils.LoggingConstants;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class GenericRedisDAOImpl {
	private JedisSentinelPool pool = null;

	private Jedis jedis = null;
	
	private String version = null;
	
	@EJB
    private LoggingService loggingService;
	
	@PostConstruct
	public void init() {
    	CommunicationFacade communicationFacade = new CommunicationFacadeBean();
		Set<String> sentinels = communicationFacade.requestRedisSentinelsHosts();
    	version = (String) communicationFacade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION);
		JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(1);
        pool = new JedisSentinelPool(LoggingConstants.REDIS_CLUSTER_MASTER_NAME, sentinels, poolConfig);
        jedis = pool.getResource();
    }
	
	@PreDestroy
	public void destroy() {
		if (jedis != null) {
			jedis.close();
		}
		pool.close();
		pool.destroy();
	}

	private void getNewConnection() {
		if(jedis != null) {
			jedis.close();
			jedis = null;
		}
		
		try {
			jedis = pool.getResource();
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", "Falha na obtencao de uma conexao com o redis-node master");
		}
    }
	
	public String get(String key) {
		String content = null;
		try {
            content = jedis.get(key + "." + version);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
		return content;
	}
	
	public void set(String key, String value) {
		try {
            jedis.set(key + "." + version, value);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
	}
	
	public void set(String key, String value, int lifespan) {
		try {
            jedis.setex(key + "." + version, lifespan, value);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
	}
	
	public void del(String key) {
		try {
			jedis.del(key + "." + version);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
	}
	
	public List<String> lrange(String key) {
		List<String> values = null;
		try {
			values = jedis.lrange(key + "." + version, 0, -1);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
		return values;
	}

	public void hmset(String key, Map<String, String> value) {
		try {
			jedis.hmset(key + "." + version, value);
        } catch (Exception e) {
        	loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
	}

	public String hmget(String key, String field) {
		String value = null;
		try {
			List<String> list = jedis.hmget(key + "." + version, field);	
			value = list.get(0);
		} catch (Exception e) {
			loggingService.saveLogging(Level.ERROR, "LOGGINGAPI", "SISTEMA", getClass().getName() + ":" + e.getMessage());
			getNewConnection();
		}
		return value;
	}
}
