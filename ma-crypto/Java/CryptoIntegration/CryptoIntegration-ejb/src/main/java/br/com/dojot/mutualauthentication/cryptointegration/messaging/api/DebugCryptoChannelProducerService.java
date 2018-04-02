package br.com.dojot.mutualauthentication.cryptointegration.messaging.api;

import br.com.dojot.mutualauthentication.cryptointegration.beans.dto.DebugCryptoChannelDTO;

public interface DebugCryptoChannelProducerService {
	
	void produce(DebugCryptoChannelDTO dto);
}
