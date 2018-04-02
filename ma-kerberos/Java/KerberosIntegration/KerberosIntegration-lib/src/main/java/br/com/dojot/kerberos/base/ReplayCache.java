package br.com.dojot.kerberos.base;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import br.com.dojot.mutualauthentication.kerberoslib.util.Time;

/**
 * Keep information about the latest authentications that were done 
 * between two specific entities. Entries are automatically inserted and purged.
 */
public class ReplayCache {
	private Map<ByteBuffer, ByteBuffer> cache;
	private long period;
	private Timer purgeTimer;
	
	/**
	 * Updates the information in cache after the given period.
	 * @param period between two updates in ms.
	 */
	public ReplayCache(long period)
	{
		this.cache = new ConcurrentHashMap<ByteBuffer, ByteBuffer>();
		this.period = period;
		/* Task responsible for purging old enough entries */
		TimerTask purgeTask = new TimerTask() {
			@Override
			public void run() {
				purge();
			}
		};
		
		/* Schedule the purge task to run periodically */
		purgeTimer = new Timer();
		purgeTimer.schedule(purgeTask, period);
	
	}
	
	/**
	 * Check if authenticator is a replay.
	 * @param id Component name
	 * @param authtime Current client time
	 * @return true if it is a replay, otherwise it returns false
	 */
	public boolean checkIfReplay(byte[] id, byte[] authtime)
	{
		boolean isReplay = false;
		ByteBuffer key = ByteBuffer.wrap(id.clone());
		ByteBuffer value = ByteBuffer.wrap(authtime.clone());
		
		/* Check if it is replay */
		if(cache.containsKey(key) && Arrays.equals(cache.get(key).array(),authtime)) {
			isReplay = true;
		}
		
		/* Add new cache entry if it is not a replay */
		if(!isReplay) {
			cache.put(key, value);
		}
		return isReplay;
	}
	
	/**
	 * Periodically removes entries from the cache. 
	 * These entries are outside the current clock skew by a security factor.
	 */
	private void purge() {
		byte[] currentTime = Time.getCurrentUTCTime();
		Set<ByteBuffer> keys = cache.keySet();
		
		for(ByteBuffer key : keys) {
			
			ByteBuffer timestamp = cache.get(key);
			boolean keep = Time.isUTCDiffLessThan(currentTime, timestamp.array(), period);
				if(!keep) {
					cache.remove(key);
				}
		}		
	}
}
