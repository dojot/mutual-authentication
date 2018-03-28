package br.com.dojot.jcrypto.parameter;

import java.security.spec.AlgorithmParameterSpec;

public class HmacParameter implements AlgorithmParameterSpec {
	private int outputLen;
	
	public HmacParameter(int outputLength)
	{
		this.outputLen = outputLength;
	}
	
	public int getOutputLength() {
		return outputLen;
	}
}
