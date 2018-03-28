package br.com.dojot.mutualauthentication.kerberosintegration.listener;

import java.util.LinkedHashMap;

import javax.ejb.EJB;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.registry.facade.api.RegistryFacade;
import br.com.dojot.mutualauthentication.registry.facade.impl.RegistryFacadeBean;
import br.com.dojot.mutualauthentication.kerberosintegration.facade.api.KerberosIntegrationFacade;

@WebListener
public class ServiceRegistryMicroserviceListener implements ServletContextListener {

	@EJB
	private KerberosIntegrationFacade kerberosIntegrationFacade;

	private LinkedHashMap<String, Object> params;

	private String targetServiceRegistry;

	private String pathServiceRegistryRegister;

	private String pathServiceRegistryUnregister;

	@Override
	public void contextInitialized(ServletContextEvent arg0) {		
		CommunicationFacade facade = new CommunicationFacadeBean();
		params = facade.requestNodeConfigs();
		params.put("microservice", arg0.getServletContext().getInitParameter("microserviceName"));
		params.put("context", arg0.getServletContext().getContextPath());
		targetServiceRegistry = kerberosIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_TARGET);
		pathServiceRegistryRegister = kerberosIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_PATH_REGISTER);
		pathServiceRegistryUnregister = kerberosIntegrationFacade.findParameterByKey(CommunicationKeysConstants.SERVICE_REGISTRY_PATH_UNREGISTER);
        RegistryFacade registryFacade = new RegistryFacadeBean();
		registryFacade.register(targetServiceRegistry, pathServiceRegistryRegister, params);
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
        RegistryFacade registryFacade = new RegistryFacadeBean();
		registryFacade.unregister(targetServiceRegistry, pathServiceRegistryUnregister, params);
	}

}
