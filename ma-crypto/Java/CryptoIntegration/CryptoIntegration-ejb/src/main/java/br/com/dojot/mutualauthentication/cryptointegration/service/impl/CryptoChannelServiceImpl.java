package br.com.dojot.mutualauthentication.cryptointegration.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.dojot.jcrypto.exception.ParseException;
import br.com.dojot.jcrypto.util.CryptoUtil;
import br.com.dojot.mutualauthentication.cryptointegration.beans.cache.CryptoChannelStateCache;
import br.com.dojot.mutualauthentication.cryptointegration.dao.api.CryptoChannelStateDAO;
import br.com.dojot.mutualauthentication.cryptointegration.service.api.CryptoChannelService;

@Stateless
public class CryptoChannelServiceImpl implements CryptoChannelService {
	
	@EJB
	private CryptoChannelStateDAO cryptoChannelStateDAO;

	@Override
	public void save(String sessionId, CryptoChannelStateCache cache) {
		JSONObject json = new JSONObject();
		json.put("tagLen", cache.getTagLen());
		json.put("provider", cache.getProvider());
		json.put("ivComponentToServer", CryptoUtil.ByteArrayToHexStr(cache.getIvComponentToServer()));
		json.put("ivServerToComponent", CryptoUtil.ByteArrayToHexStr(cache.getIvServerToComponent()));
		json.put("keyComponentToServer", CryptoUtil.ByteArrayToHexStr(cache.getKeyComponentToServer()));
		json.put("keyServerToComponent", CryptoUtil.ByteArrayToHexStr(cache.getKeyServerToComponent()));
		cryptoChannelStateDAO.add(sessionId, json.toString());
	}

	@Override
	public void save(String sessionId, CryptoChannelStateCache cache, Long lifespan) {
		JSONObject json = new JSONObject();
		json.put("tagLen", cache.getTagLen());
		json.put("provider", cache.getProvider());
		json.put("ivComponentToServer", CryptoUtil.ByteArrayToHexStr(cache.getIvComponentToServer()));
		json.put("ivServerToComponent", CryptoUtil.ByteArrayToHexStr(cache.getIvServerToComponent()));
		json.put("keyComponentToServer", CryptoUtil.ByteArrayToHexStr(cache.getKeyComponentToServer()));
		json.put("keyServerToComponent", CryptoUtil.ByteArrayToHexStr(cache.getKeyServerToComponent()));
		cryptoChannelStateDAO.add(sessionId, json.toString(), lifespan);
	}

	@Override
	public void remove(String sessionId) {
		cryptoChannelStateDAO.remove(sessionId);
	}

	@Override
	public CryptoChannelStateCache findBySessionId(String sessionId) {
		CryptoChannelStateCache cache = null;
		try {
			String json = cryptoChannelStateDAO.get(sessionId);
			if (json != null) {
				JSONObject jsonObject = new JSONObject(json);
				cache = new CryptoChannelStateCache();
				cache.setTagLen(jsonObject.getInt("tagLen"));
				cache.setProvider(jsonObject.getString("provider"));
				cache.setIvComponentToServer(CryptoUtil.HexStrToByteArray(jsonObject.getString("ivComponentToServer")));
				cache.setIvServerToComponent(CryptoUtil.HexStrToByteArray(jsonObject.getString("ivServerToComponent")));
				cache.setKeyComponentToServer(CryptoUtil.HexStrToByteArray(jsonObject.getString("keyComponentToServer")));
				cache.setKeyServerToComponent(CryptoUtil.HexStrToByteArray(jsonObject.getString("keyServerToComponent")));
			}
		} catch (JSONException e) {
			// TODO tratar exception
			cache = null;
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO tratar exception
			e.printStackTrace();
		}
		return cache;
	}
	
}
