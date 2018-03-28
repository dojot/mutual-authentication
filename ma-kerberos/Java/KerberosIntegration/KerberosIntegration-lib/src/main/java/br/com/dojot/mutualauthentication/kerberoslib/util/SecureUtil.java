package br.com.dojot.mutualauthentication.kerberoslib.util;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.SecureEraseException;

public final class SecureUtil {
	public static final void erase(byte[] src) {
		byte eraseResult = 0x00;

		for (int i = 0; i < src.length; i++) {
			src[i] = 0x00;
			eraseResult |= src[i];
		}
		if (eraseResult != 0x00) {
			throw new SecureEraseException();
		}
	}

	public static final byte[] copyOf(byte[] src, int start, int end) {
		if (src == null) {
			throw new NullPointerException();
		}

		if (start > end || end > src.length) {
			throw new IllegalArgumentException();
		}

		byte[] copy = new byte[end - start];
		System.arraycopy(src, start, copy, 0, end - start);
		return copy;
	}

	public static final byte[] resize(byte[] src, int start, int end) {
		byte[] copy = copyOf(src, start, end);
		erase(src);
		return copy;
	}
}
