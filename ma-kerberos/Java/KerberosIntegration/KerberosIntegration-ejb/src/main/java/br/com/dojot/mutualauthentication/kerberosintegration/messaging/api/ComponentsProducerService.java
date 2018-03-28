package br.com.dojot.mutualauthentication.kerberosintegration.messaging.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.dto.BatchNumberComponentsDTO;

@Local
public interface ComponentsProducerService {

	void produce(BatchNumberComponentsDTO dto);
}
