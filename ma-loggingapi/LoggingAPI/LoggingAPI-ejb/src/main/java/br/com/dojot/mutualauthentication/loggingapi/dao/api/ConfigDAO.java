package br.com.dojot.mutualauthentication.loggingapi.dao.api;

import java.util.List;

public interface ConfigDAO {
	
	String findConfigurationByKey(String key);
	
	List<String> searchCassandraContactPoints();
	
	List<String> searchElasticSearchNodes();
}
