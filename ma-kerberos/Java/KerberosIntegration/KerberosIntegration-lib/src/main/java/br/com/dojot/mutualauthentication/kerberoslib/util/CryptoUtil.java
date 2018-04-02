package br.com.dojot.mutualauthentication.kerberoslib.util;

import java.math.BigInteger;

import br.com.dojot.mutualauthentication.kerberoslib.exceptions.ParseException;

public class CryptoUtil {
	
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
}
