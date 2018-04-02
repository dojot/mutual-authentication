package br.com.dojot.jcrypto.jni;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCode {
	SUCCESSFULL_OPERATION(0, "Successfull Operation"),
	INVALID_OUTPUT_SIZE(1, "Invalid Output Size"),
	INVALID_INPUT_SIZE(2, "Invalid Input Size"),
	INVALID_PADDING(4, "Invalid Padding"),
	INVALID_TAG(8, "Invalid Tag"),
	INVALID_PARAMETER(16, "Invalid Parameter"),
	INVALID_STATE(32, "Invalid state"),
	UNKNOWN_ERROR(64, "Unknown error happened"),
	INVALID_MEMSET(128, "Invalid memory operation");
	
	private static final Map<Integer, ErrorCode> intToEnum = new HashMap<Integer, ErrorCode>();
	
	private int errorCode;
	private String errorMessage;
	
	ErrorCode(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorMessage() {
		return this.errorMessage;
	}
	
	public int getErrorCode() {
		return this.errorCode;
	}
	
	static {
		for (ErrorCode error : values()) {
			intToEnum.put(error.getErrorCode(), error);
		}
	}
	
	/* Return errorCode from int code */
	public static ErrorCode fromInt(int errorCode) {
		if(intToEnum.containsKey(new Integer(errorCode))) {
			return intToEnum.get(errorCode);
		}
		return ErrorCode.UNKNOWN_ERROR;
	}
	
}
