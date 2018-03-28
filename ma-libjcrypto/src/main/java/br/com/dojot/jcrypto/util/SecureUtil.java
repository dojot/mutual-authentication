package br.com.dojot.jcrypto.util;

public final class SecureUtil {
	public static final int add_s(int op1, int op2) throws ArithmeticException {
		if (op2 > 0 ? op1 > Integer.MAX_VALUE - op2 : op1 < Integer.MIN_VALUE
				- op2) {
			throw new ArithmeticException("Integer overflow");
		}
		return op1 + op2;
	}

	public static final int sub_s(int op1, int op2) throws ArithmeticException {
		if (op2 > 0 ? op1 < Integer.MIN_VALUE + op2 : op1 > Integer.MAX_VALUE
				+ op2) {
			throw new ArithmeticException("Integer overflow");
		}
		return op1 - op2;
	}

	public static final int mul_s(int op1, int op2) throws ArithmeticException {
		if (op2 > 0 ? op1 > Integer.MAX_VALUE / op2 
				|| op1 < Integer.MIN_VALUE / op2
				: (op2 < -1 ? op1 > Integer.MIN_VALUE / op2
						|| op1 < Integer.MAX_VALUE / op2 : op2 == -1
						&& op1 == Integer.MIN_VALUE)) {
			throw new ArithmeticException("Integer overflow");
		}
		return op1 * op2;
	}

	public static final int div_s(int op1, int op2) throws ArithmeticException {
		if ((op1 == Integer.MIN_VALUE) && (op2 == -1)) {
			throw new ArithmeticException("Integer overflow");
		}
		return op1 / op2;
	}

	public static final int not_s(int op) throws ArithmeticException {
		if (op == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return -op;
	}

	public static final int abs_s(int op) throws ArithmeticException {
		if (op == Integer.MIN_VALUE) {
			throw new ArithmeticException("Integer overflow");
		}
		return Math.abs(op);
	}

}
