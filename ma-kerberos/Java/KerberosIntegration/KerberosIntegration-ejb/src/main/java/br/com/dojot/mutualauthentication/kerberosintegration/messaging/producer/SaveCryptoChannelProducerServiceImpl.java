package br.com.dojot.mutualauthentication.kerberosintegration.messaging.producer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchSaveCryptoChannelDTO;
import br.com.dojot.mutualauthentication.kerberosintegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.kerberosintegration.messaging.api.SaveCryptoChannelProducerService;
import br.com.dojot.mutualauthentication.kerberosintegration.utils.KerberosIntegrationConstants;

@Startup
@Singleton
public class SaveCryptoChannelProducerServiceImpl extends Thread implements SaveCryptoChannelProducerService {
	private ProducerServiceImpl producer;
	
	@EJB
	private ConfigDAO configDAO;
    
	@PostConstruct
	public void init() {
		CommunicationFacade facade = new CommunicationFacadeBean();
		producer = new ProducerServiceImpl(configDAO.findConfigurationByKey(KerberosIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS),
				KerberosIntegrationConstants.TOPIC_SAVE_CRYPTO_CHANNEL,
				(String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION), "ker.save.");
	}
	
	@PreDestroy
	public void close() {
		producer.close();
	}

	@Override
	public void produce(BatchSaveCryptoChannelDTO dto) {
		producer.produce(dto);
	}

}