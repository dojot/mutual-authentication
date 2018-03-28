package br.com.dojot.mutualauthentication.kerberoslib.encode;

public enum ErrorCode {
	KDC_ERR_C_PRINCIPAL_UNKNOWN(6, "CS-CCS-008"), KDC_ERR_S_PRINCIPAL_UNKNOWN(7, "CS-CCS-001"), INVALID_MESSAGE(90, "CS-CCS-005"),
	KRB_AP_ERR_BAD_INTEGRITY(31, "CS-CCS-003"), KRB_AP_ERR_TKT_EXPIRED(32, "CS-CCS-004"), KRB_AP_ERR_REPEAT(34, "CS-CCS-006"), 
	KRB_AP_ERR_BADMATCH(36, "CS-CCS-007"), KRB_AP_ERR_SKEW(37, "CS-CCS-009"), KRB_ERR_GENERIC(60, "CS-CCS-002"), KDC_ERR_NONE(0, "CS-CCS-000"),
	CC_AUTH_FAIL(91, "CS-CCS-010"), UNKNOWN_ERROR(92, "CS-CCS-011");
	
	private byte code;
	
	private String description;
	
	ErrorCode(int code, String description) {
		this.code = (byte)code;
		this.description = description;
	}
	
	public byte getCode() {
		return this.code;
	}
	
	public String getDescription() {
		return this.description;
	}
}
