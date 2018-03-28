package br.com.dojot.mutualauthentication.kerberosintegration.messaging.producer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchRegisterDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.UnregisterProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;

@Startup
@Singleton
public class UnregisterProducerServiceImpl extends Thread implements UnregisterProducerService {
	private ProducerServiceImpl producer;
	
	@EJB
	private ConfigDAO configDAO;
    
	@PostConstruct
	public void init() {
		CommunicationFacade facade = new CommunicationFacadeBean();
		producer = new ProducerServiceImpl(configDAO.findConfigurationByKey(KerberosIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS),
				KerberosIntegrationConstants.TOPIC_UNREGISTER,
				(String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION), "ker.unregister.");
	}
	
	@PreDestroy
	public void close() {
		producer.close();
	}

	@Override
	public void produce(BatchRegisterDTO dto) {
		producer.produce(dto);
	}

}