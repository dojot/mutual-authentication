package br.com.dojot.mutualauthentication.kerberosintegration.messaging.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchSaveCryptoChannelDTO;

@Local
public interface SaveCryptoChannelProducerService {

	void produce(BatchSaveCryptoChannelDTO dto);
}
