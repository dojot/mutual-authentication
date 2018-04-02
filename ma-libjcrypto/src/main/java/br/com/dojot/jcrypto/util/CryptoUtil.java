package br.com.dojot.jcrypto.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import br.com.dojot.jcrypto.exception.ParseException;

/**
 * Cryptographic utility functions.
 * 
 * @author Erick Nogueira do Nascimento (erick@cpqd.com.br)
 * 
 */
public final class CryptoUtil {
	/**
	 * Unpack the 32-bit integer 'word' into the byte array 'out', starting at
	 * outOffset, in little endian order.
	 * 
	 * @param word
	 *            32-bit integer
	 * @param out
	 *            output byte array
	 * @param outOffset
	 *            start index
	 */
	public static void unpackWordLittleEndian(int word, byte[] out,
			int outOffset) {
		out[outOffset] = (byte) (word & 0xff);
		out[outOffset + 1] = (byte) (word >>> 8);
		out[outOffset + 2] = (byte) (word >>> 16);
		out[outOffset + 3] = (byte) (word >>> 24);
	}

	/**
	 * Unpack the 32-bit integer 'word' into the byte array 'out', starting at
	 * outOffset, in big endian order.
	 * 
	 * @param word
	 *            32-bit integer
	 * @param out
	 *            output byte array
	 * @param outOffset
	 *            start index
	 */
	public static void unpackWordBigEndian(int word, byte[] out, int outOffset) {
		out[outOffset] = (byte) (word >>> 24);
		out[outOffset + 1] = (byte) (word >>> 16);
		out[outOffset + 2] = (byte) (word >>> 8);
		out[outOffset + 3] = (byte) (word);
	}

	/**
	 * Pack the first 4 elements of 'in', starting at index 'inOffset', into a
	 * 32-bit word and return that word.
	 * 
	 * @param in
	 * @param inOffset
	 * @return
	 */
	public static int packWordBigEndian(byte[] in, int inOffset) {
		return ((in[inOffset + 0] & 0x000000FF) << 24)
				| ((in[inOffset + 1] & 0x000000FF) << 16)
				| ((in[inOffset + 2] & 0x000000FF) << 8)
				| ((in[inOffset + 3] & 0x000000FF));
	}

	/**
	 * left-rotate x by n bits, 0 <= n <= 32
	 * 
	 * @param x
	 *            the word to rotate
	 * @param n
	 *            number of bits to rotate
	 * @return the rotated word
	 */
	public static int rotL(int x, int n) {
		assert (n >= 0) && (n <= 32);
		return (x << n) | (x >>> (32 - n));
	}

	/**
	 * left-rotate x by n bits, 0 <= n <= 32
	 * 
	 * @param x
	 *            the word to rotate
	 * @param n
	 *            number of bits to rotate
	 * @return the rotated word
	 */
	public static int rotR(int x, int n) {
		assert (n >= 0) && (n <= 32);
		return (x >>> n) | (x << (32 - n));
	}

	/**
	 * Xor the first len bytes from array 'a' and 'b', starting at offsetA and
	 * offset B, respectively.
	 * 
	 * @param a
	 *            first operand
	 * @param offsetA
	 *            start index of 'a'
	 * @param b
	 *            second operand
	 * @param offsetB
	 *            start index of 'b'
	 * @param len
	 *            number of bytes to operate
	 * @return the xor'ed array
	 */
	public static byte[] xor(byte[] a, int offsetA, byte[] b, int offsetB,
			int len) {
		// TODO receive 'out' as a parameter
		byte[] out = new byte[len];
		for (int i = 0; i < len; i++)
			out[i] = (byte) (((a[i + offsetA] & 0xFF) ^ (b[i + offsetB] & 0xFF)) & 0xFF);
		return out;
	}

	/**
	 * XOR the array 'a' and 'b'
	 * 
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @return xor'ed array
	 */
	public static byte[] xor(byte[] a, byte[] b) {
		// TODO receive 'out' as a parameter
		return xor(a, 0, b, 0, Math.min(a.length, b.length));
	}
	
	/** Shift byte 'a' one bit to the left.
	 * 
	 * @param a
	 * 				byte to be shifted
	 * @return shifted byte
	 */
	public static byte shiftLeftOne(byte a) {
		return (byte)((a & 0x7F) << 1);
	}
	
	/**
	 * Shift array 'A' one bit to the left
	 * 
	 * @param A
	 *            array to be shifted
	 * @return shifted array
	 */
	public static byte[] shiftLeftOne(byte[] A) {
		assert A.length >= 1;

		byte[] L = new byte[A.length];

		if (A.length == 1) {
			L[0] = shiftLeftOne(A[0]);
		} else {
			int carry = 0;
			for (int i = A.length - 1; i >= 0; i--) {
				L[i] = (byte) (shiftLeftOne(A[i]) | carry);
				carry = (A[i] & 0x80) >> 7;
			}
		}
		return L;
	}

	/**
	 * Shift byte 'a' one bit to the right
	 * 
	 * @param a
	 *            byte to be shifted
	 * @return shifted byte
	 */
	public static byte shiftRightOne(byte a) {
		// The AND mask is necessary because byte is signed,
		// and bitwise operations (in this case the & and >>>) is applied only
		// to 'int' operators
		// So, the 'a' is promoted to 'int' before the AND
		return (byte) ((a & 0xFF) >>> 1);
	}

	/**
	 * Shift array 'A' one bit to the right
	 * 
	 * @param A
	 *            array to be shifted
	 * @return shifted array
	 */
	public static byte[] shiftRightOne(byte[] A) {
		// TODO receive 'R' as a parameter
		assert A.length >= 1;

		byte[] R = new byte[A.length];

		if (A.length == 1) {
			R[0] = shiftRightOne(A[0]);
		} else {
			for (int i = A.length - 2; i >= 0; i--) {
				byte cur = A[i];
				byte next = A[i + 1];
				int lastBitCur = (cur & 0x0FF) & 0x01;
				next = (byte) ((lastBitCur << 7) | shiftRightOne(next)); // put
																			// on
																			// first
																			// bit
																			// of
																			// next
				R[i + 1] = next;
			}
			R[0] = shiftRightOne(A[0]);
		}
		return R;
	}

	/**
	 * Shift array 'A' 'n'-bits to the right.
	 * 
	 * @param A
	 *            array to be shifted
	 * @param n
	 *            number of shift bits
	 * @return shifted arrays
	 */
	public static byte[] shiftRight(byte[] A, int n) {
		// TODO receive 'R' as a parameter
		byte[] R = A.clone();
		for (int i = 0; i < n; i++) {
			R = shiftRightOne(R);
		}
		return R;
	}

	/**
	 * Copy array 'src' to 'dest'.
	 * 
	 * @param dest
	 *            destination
	 * @param src
	 *            source
	 */
	public static void copyFullArray(byte[] dest, byte[] src) {
		assert dest.length == src.length;
		System.arraycopy(src, 0, dest, 0, src.length);
	}

	/**
	 * Convert the byte array 'arr' to uppercase hexadecimal string
	 * representation. Each element of 'arr' is converted into two characters of
	 * the string.
	 * 
	 * @param arr
	 *            the array to convert
	 * @return uppercase hexadecimal string representation
	 */
	public static String ByteArrayToHexStr(byte[] arr) {
		char[] out = new char[arr.length * 2];
		int count = 0;

		char[] hexMap = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

		for (int i = 0; i < arr.length; i++) {
			byte b = arr[i];
			out[count++] = hexMap[(b & 0xf0) >>> 4];
			out[count++] = hexMap[(b & 0x0f)];
		}
		return String.valueOf(out);
	}

	/**
	 * Convert a hexadecimal string into a byte array
	 * 
	 * @param hexStr
	 *            string to be converted
	 * @return byte array
	 * @throws ParseException
	 *             If not a valid hexadecimal string
	 */
	public static byte[] HexStrToByteArray(String hexStr) throws ParseException {

		// remove whitespaces
		hexStr = hexStr.replaceAll(" ", "");

		if (hexStr.length() % 2 != 0)
			throw new ParseException("Invalid hexadecimal byte string");

		byte[] out = new byte[hexStr.length() / 2];

		for (int i = 0; i < hexStr.length(); i += 2) {
			char first = hexStr.charAt(i);
			char second = hexStr.charAt(i + 1);
			byte b = CryptoUtil.HexByteStrToByte(new char[] { first, second });
			out[i / 2] = b;
		}

		return out;
	}

	/**
	 * Converts the result of a BigInteger.toByteArray call to an exact
     * signed magnitude representation for any positive number.
     * 
	 * @param bigInt Big Integer to be converted.
	 * @return Big Integer converted.
	 */
	public static byte[] BigIntegerToByteArray(BigInteger bigInt) {
		byte[] result = bigInt.toByteArray();
		if (result[0] == 0) {
			byte[] tmp = new byte[result.length - 1];
			System.arraycopy(result, 1, tmp, 0, tmp.length);
			result = tmp;
		}
		return result;
	}

	/**
	 * Convert a 2-character hexadecimal string into a byte value
	 * 
	 * @param byteHexStr
	 *            the string to be converted
	 * @return the byte
	 * @throws ParseException
	 *             If not a valid hexadecimal string
	 */
	public static byte HexByteStrToByte(char[] byteHexStr)
			throws ParseException {
		char[] hexMap = new char[] { '0', '1', '2', '3', '4', '5', '6', '7',
				'8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		if (byteHexStr.length != 2)
			throw new ParseException("Invalid hexadecimal byte string");

		char first = Character.toUpperCase(byteHexStr[0]);
		char second = Character.toUpperCase(byteHexStr[1]);
		int firstVal = 0x100, secondVal = 0x100;

		for (int i = 0; i < hexMap.length; i++) {
			if (first == hexMap[i])
				firstVal = i;
			if (second == hexMap[i])
				secondVal = i;
		}
		if (firstVal == 0x100 || secondVal == 0x100) {
			throw new ParseException("Invalid hexadecimal byte string");
		} else {
			return (byte) ((firstVal << 4) | secondVal);
		}
	}

	/**
	 * Ceil function. Returns the smallest integer greater or equal than a/b.
	 * 
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @return ceil(a/b)
	 */
	public static int intDivisionCeil(int a, int b) {
		assert a >= 0 && b > 0;
		if (a % b == 0) {
			return a / b;
		} else {
			return a / b + 1;
		}
	}

	/**
	 * Ceil function. Returns the smallest integer greater or equal than a/b.
	 * 
	 * @param a
	 *            first operand
	 * @param b
	 *            second operand
	 * @return ceil(a/b)
	 */
	public static long longDivisionCeil(long a, long b) {
		assert a >= 0 && b > 0;
		if (a % b == 0) {
			return a / b;
		} else {
			return a / b + 1;
		}
	}

	/**
	 * 
	 * Compare a byte array with a byte
	 * 
	 * @param arr
	 *            the byte array
	 * @param arrOffset
	 *            the byte array offset
	 * @param b
	 *            the byte to be compared
	 * @return true if all bytes in the array are equal to the byte and false if
	 *         there are at least one byte different
	 */
	public static boolean compareArrayToByteDiffConstant(byte[] arr,
			int arrOffset, byte b) {

		byte res = 0x00;

		for (int i = 0; i < arrOffset; i++) {
			res |= (b ^ arr[i]);
		}
		return res == 0;
	}

	/**
	 * 
	 * Compare a byte array with a byte
	 * 
	 * @param arr
	 *            the byte array
	 * @param arrB
	 *            the byte array to be compared
	 * @return true if all bytes in the array are equal to the other array and
	 *         false if there are at least one byte different or the sizes are
	 *         different
	 */
	public static boolean compareArrayToArrayDiffConstant(byte[] arr,
			byte[] arrB) {

		byte res = 0x00;

		if (arr.length == arrB.length) {

			for (int i = 0; i < arr.length; i++) {
				res |= (arrB[i] ^ arr[i]);
			}
		} else {
			return false;
		}
		return res == 0;
	}

	/**
	 * Get a non-Hex String as a Byte Array
	 * 
	 * @param string
	 *            String to be converted
	 * @return The resultant byte array
	 */
	public static byte[] getBytesFromString(String string) {
		Charset utf8 = Charset.forName("UTF-8");
		CharBuffer charBuffer = CharBuffer.wrap(string);
		ByteBuffer byteBuffer = utf8.encode(charBuffer);

		int len = byteBuffer.limit();
		byte[] stringBytes = new byte[len];
		byteBuffer.get(stringBytes, 0, len);

		return stringBytes;
	}
	
	public static boolean equals(byte[] a, byte[] b) {
		int len_a = a.length;
		int len_b = b.length;
		boolean result = false;
		
		if (len_a != len_b)
			return false;
		
		for (int i = 0; i < len_a; i++) {
			result = result || (a[i] != b[i]);
		}
		
		return !result;
	}
	
	/* Calculates the number of bytes which will be copied to the buffer */
	public static int calculateRemainingBytes(int inputLen, int inputOffset, int bufferOffset, int blockLength) {
		int remainingBytes;
		remainingBytes = SecureUtil.sub_s(inputLen, inputOffset);
		remainingBytes = SecureUtil.add_s(remainingBytes, bufferOffset);
		remainingBytes = remainingBytes % blockLength;
		return remainingBytes;
	}
	
	/* Calculates the number of bytes that can be processed */
	public static int calculateFullBytes(int inputLen, int inputOffset, int bufferOffset, int remainingBytes) {
		int fullBytes;
		fullBytes = SecureUtil.sub_s(inputLen, inputOffset);
		fullBytes = SecureUtil.add_s(fullBytes, bufferOffset);
		fullBytes = SecureUtil.sub_s(fullBytes, remainingBytes);
		return fullBytes;
	}
	
	/** Converts char array to byte array. */
	public static byte[] charArrayToByteArray(char[] array) 
	{
		byte[] barray = new byte[array.length];
		
		for(int i = 0; i < array.length; i++) {
			barray[i] = (byte) array[i];
		}
		return barray;
	}
}
