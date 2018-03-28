package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import javax.ejb.Local;

import br.com.dojot.mutualauthentication.kerberosintegration.beans.cache.ComponentCache;
import br.com.dojot.mutualauthentication.kerberosintegration.beans.entity.ComponentVO;

@Local
public interface ComponentService {
	
	void save(ComponentVO vo);
	
	void remove(String componentId);
	
	ComponentCache get(String componentId);
}