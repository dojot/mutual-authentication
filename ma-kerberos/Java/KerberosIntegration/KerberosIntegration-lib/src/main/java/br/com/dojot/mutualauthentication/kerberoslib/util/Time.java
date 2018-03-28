package br.com.dojot.mutualauthentication.kerberoslib.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.TimeZone;

public final class Time {
	/**
	 * UTC time is encoded as double that is exactly 8 bytes long.
	 */
	public static final int DOUBLE_PRECISION = 8;
	
	public static byte[] getCurrentUTCTime() 
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		return doubleToByteArray((double)cal.getTimeInMillis());
	}
	
	/**
	 * Adds offset milliseconds to the start reference
	 * @param start UTC time coded in binary format
	 * @param offset Number of milliseconds to be added
	 * @return Updated UTC time
	 */
	public static byte[] getAdjustedUTC(long offset)
	{
		return doubleToByteArray(ByteArrayToDouble(getCurrentUTCTime()) + offset);
	}
	
	/**
	 * Adds period milliseconds to the start UTC time.
	 * @param start UTC time coded in binary format
	 * @param period Number of milliseconds to be added
	 * @return Updated UTC time
	 */
	public static byte[] getUTCWithOffset(byte[] start, double period)
	{		
		return doubleToByteArray(ByteArrayToDouble(start) + period);
	}
	
	/**
	 * Checks if UTC times in parameters a and b are at most maxDiff milliseconds
	 * apart.
	 * @param a binary coded UTC time
	 * @param b binary coded UTC time
	 * @param maxDiff maximum milliseconds difference
	 * @return if |a-b| < maxDiff then it returns true, else it returns false 
	 */
	public static boolean isUTCDiffLessThan(byte[] a, byte[] b, double maxDiff) 
	{
		double value = Math.abs(ByteArrayToDouble(a) - ByteArrayToDouble(b));
		return value < maxDiff;
	}
	
	/**
	 * Calculates the relative order of UTC times
	 * @param a binary encoded UTC time
	 * @param b binary encoded UTC time
	 * @return Value less than zero if a < b, zero if a = b or a value bigger than zero 
	 * if a > b.
	 */
	public static double order(byte[] a, byte[] b)
	{
		double timeA = ByteArrayToDouble(a);
		double timeB = ByteArrayToDouble(b);
		
		return timeA - timeB;
	}
	
	/**
	 * Convert double UTC time to binary format
	 * @param value double encoded UTC time
	 * @return binary encoded UTC time
	 */
	private static byte[] doubleToByteArray(double value)
	{
		byte[] bValue = new byte[DOUBLE_PRECISION];
		ByteBuffer.wrap(bValue).order(ByteOrder.BIG_ENDIAN).putDouble(value);
		return bValue;
	}
	
	/**
	 * Convert binary UTC time to double format
	 * @param value binary encoded UTC time
	 * @return double encoded UTC time
	 */
	private static double ByteArrayToDouble(byte[] value)
	{
		return ByteBuffer.wrap(value).order(ByteOrder.BIG_ENDIAN).getDouble();
	}
}
