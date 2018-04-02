package br.com.dojot.mutualauthentication.cryptointegration.messaging.producer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import br.com.dojot.mutualauthentication.communication.constants.CommunicationKeysConstants;
import br.com.dojot.mutualauthentication.communication.facade.api.CommunicationFacade;
import br.com.dojot.mutualauthentication.communication.facade.impl.CommunicationFacadeBean;
import br.com.dojot.mutualauthentication.cryptointegration.beans.dto.DebugCryptoChannelDTO;
import br.com.dojot.mutualauthentication.cryptointegration.dao.api.ConfigDAO;
import br.com.dojot.mutualauthentication.cryptointegration.messaging.api.DebugCryptoChannelProducerService;
import br.com.dojot.mutualauthentication.cryptointegration.util.CryptoIntegrationConstants;

@Startup
@Singleton
public class DebugCryptoChannelProducerServiceImpl extends Thread implements DebugCryptoChannelProducerService {
	private ProducerServiceImpl producer;
	
	@EJB
	private ConfigDAO configDAO;
    
	@PostConstruct
	public void init() {
		CommunicationFacade facade = new CommunicationFacadeBean();
		
		producer = new ProducerServiceImpl(configDAO.findParameterByKey(CryptoIntegrationConstants.PARAM_KAFKA_BOOTSTRAP_SERVERS),
				CryptoIntegrationConstants.TOPIC_DEBUG_CRYPTO_CHANNEL,
				(String) facade.requestNodeConfigs().get(CommunicationKeysConstants.KEY_VERSION), "cry.debugcryptochannel.");
	}
	
	@PreDestroy
	public void close() {
		producer.close();
	}

	@Override
	public void produce(DebugCryptoChannelDTO dto) {
		producer.produce(dto);
	}

}