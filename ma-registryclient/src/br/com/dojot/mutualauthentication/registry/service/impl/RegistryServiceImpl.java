package br.com.dojot.mutualauthentication.registry.service.impl;

import java.util.LinkedHashMap;

import br.com.dojot.mutualauthentication.communication.exceptions.InvalidResponseException;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.registry.service.api.RegistryService;

public class RegistryServiceImpl implements RegistryService {

	@Override
	public boolean register(String target, String path, LinkedHashMap<String, Object> params) {
		boolean registered = false;
		CommunicationFacade facade = new CommunicationFacadeBean();
		do {
			try {
				Thread.sleep(10000);
				facade.post(target, path, params);
				registered = true;
				System.out.println("Registro do microservico " + params.get("microservice") + " efetuado com sucesso.");
			} catch (InvalidResponseException e) {
				System.out.println("Registro do microservico " + params.get("microservice") + " nao efetuado.");
			} catch (InterruptedException e) {
				System.out.println("Erro durante espera para remocao do registro do microservico.");
			}
		} while (!registered);
		return registered;
	}

	@Override
	public boolean unregister(String target, String path, LinkedHashMap<String, Object> params) {
		boolean unregistered = false;
		CommunicationFacade facade = new CommunicationFacadeBean();
		do {
			try {
				Thread.sleep(10000);
				facade.post(target, path, params);
				unregistered = true;
				System.out.println("Remocao do registro do microservico " + params.get("microservice") + " efetuada com sucesso.");
			} catch (InvalidResponseException e) {
				System.out.println("Remocao do registro do microservico " + params.get("microservice") + " nao efetuado.");
			} catch (InterruptedException e) {
				System.out.println("Erro durante espera para remocao do registro do microservico.");
			}
		} while (!unregistered);
		return unregistered;
	}
}
