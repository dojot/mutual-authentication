package br.com.dojot.mutualauthentication.kerberosintegration.messaging.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchCryptoDTO;

@Local
public interface EncryptProducerService {

	void produce(BatchCryptoDTO dto);
}
