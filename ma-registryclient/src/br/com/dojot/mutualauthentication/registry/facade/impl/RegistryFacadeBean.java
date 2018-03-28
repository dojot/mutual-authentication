package br.com.dojot.mutualauthentication.registry.facade.impl;

import java.util.LinkedHashMap;

import br.com.dojot.mutualauthentication.registry.facade.api.RegistryFacade;
import br.com.dojot.mutualauthentication.registry.service.api.RegistryService;
import br.com.dojot.mutualauthentication.registry.service.impl.RegistryServiceImpl;

public class RegistryFacadeBean implements RegistryFacade {

	@Override
	public boolean register(String target, String path, LinkedHashMap<String, Object> params) {
		RegistryService registryService = new RegistryServiceImpl();
		return registryService.register(target, path, params);
	}

	@Override
	public boolean unregister(String target, String path, LinkedHashMap<String, Object> params) {
		RegistryService registryService = new RegistryServiceImpl();
		return registryService.unregister(target, path, params);
	}
}
