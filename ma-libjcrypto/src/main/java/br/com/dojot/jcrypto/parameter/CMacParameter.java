package br.com.dojot.jcrypto.parameter;

import java.security.spec.AlgorithmParameterSpec;

public class CMacParameter implements AlgorithmParameterSpec {
	private int outputLen;
	
	public CMacParameter(int outputLength)
	{
		this.outputLen = outputLength;
	}
	
	public int getOutputLength() {
		return outputLen;
	}
}
