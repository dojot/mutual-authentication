package br.com.dojot.mutualauthentication.kerberosintegration.utils;

import org.json.JSONObject;

public class Utils {
	public static String generateKerberosSessionId(String sessionId, String transactionId) {
		return sessionId + " - " + transactionId;
	}
	
	public static String[] extractIdsFromKerberosSessionId(String kerberosSessionId) {
		return kerberosSessionId.split(" - ");
	}
	
	public static <E> E get(Object object) {
		return object != null && !JSONObject.NULL.equals(object) ? (E) object : null;
	}
}
