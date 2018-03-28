package br.com.dojot.mutualauthentication.kerberosintegration.messaging.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchRegisterDTO;

@Local
public interface RegisterProducerService {

	void produce(BatchRegisterDTO dto);
}
