package br.com.dojot.mutualauthentication.kerberosintegration.service.api;

import java.util.Map;

public interface KerberosProtocolService {
	public String process(String json);
	
	public Map<String, Object> processRequestAS(boolean skipValidation, String sessionId, String transactionId, String encodedInput);

	public Map<String, Object> processRequestAP(boolean skipValidation, String sessionId, String transactionId, String encodedInput);
}
