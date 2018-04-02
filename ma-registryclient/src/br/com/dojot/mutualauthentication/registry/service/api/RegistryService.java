package br.com.dojot.mutualauthentication.registry.service.api;

import java.util.LinkedHashMap;
import org.json.JSONObject;

public interface RegistryService {
	boolean register(String target, String path, LinkedHashMap<String, Object> params);
	boolean unregister(String target, String path, LinkedHashMap<String, Object> params);
}
