package br.com.dojot.mutualauthentication.kerberosintegration.exception;

import java.util.Locale;

@SuppressWarnings("serial")
public class KerberosIntegrationTimeoutException extends GenericException {

	public KerberosIntegrationTimeoutException(String code) {
		super(code);
	}

	@Override
	public String getBundleName() {
		return "exceptions_pt_BR";
	}

	@Override
	public Locale getLocale() {
		return Locale.getDefault();
	}

}
